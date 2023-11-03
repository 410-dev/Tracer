package me.hysong.tracer.servlets;

import me.hysong.libhycore.CoreDate;
import me.hysong.libhyextended.Utils;
import me.hysong.libhyextended.environment.SubsystemEnvironment;
import me.hysong.tracer.backend.Mailing;
import me.hysong.tracer.backend.Records;
import me.hysong.tracer.backend.struct.Report;
import me.hysong.tracer.backend.struct.UserObject;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.ArrayList;

@WebListener
public class Worker implements ServletContextListener {

    public Thread workerThread = null;
    public boolean workerRunning = false;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Your code here.

        SubsystemEnvironment env = new SubsystemEnvironment("TracerRecord", "/opt/data/tracer");
        env.configure();
        env.initConfig("smtp-addr", "");
        env.initConfig("smtp-passwd", "");

        if (env.getConfig("smtp-addr").equals("") || env.getConfig("smtp-passwd").equals("")) {
            System.out.println("SMTP server not configured. Please configure it in the TracerRecord subsystem located: " + env.realpath("/"));
            Records.add("Currently running on: " + env.getHostOS());
            Records.add("SMTP server not configured. Please configure it in the TracerRecord subsystem located: " + env.realpath("/"));
            Records.add("Worker will not run.");
            return;
        }else{
            Records.add("SMTP server configured.");
        }

        workerThread = new Thread(() -> {
            workerRunning = true;
            while (workerRunning) {
                Records.add("Worker started to check for reports.");
                try {
                    ArrayList<String> list = env.list("/Reports");
                    Records.add("Found " + list.size() + " report files.");
                    int count = 1;
                    for (String s : list) {
                        Records.add("Checking record file " + s + " (" + count + "/" + list.size() + ").");
                        try {
                            UserObject o = UserObject.loadFromFile(env, "/" + s);
                            if (o == null) continue;

                            // Read most recent report
                            Report r = Utils.last(o.getReportHistory());
                            if (r == null) {
                                Records.add("User " + s + " has no report history.");
                                continue;
                            }else{
                                Records.add("User " + s + " has report history at " + r.getReportTime());
                            }

                            // Check if report is older than expectation
                            final long serverReportTime = Long.parseLong(r.getReportServerTime());
                            final long serverCurrentTime = CoreDate.secondsSince1970();
                            final long offsetUntilNextReport = Long.parseLong(r.getSUntilNext());
                            final int reportStage = Integer.parseInt(r.getReportStage());
                            final int fStage = Integer.parseInt(r.getFStage());

                            // Convert stage by time elapsed.
                            // For example, if the report is 10 minutes old, and the offset is 5 minutes, then the report stage is 2.
                            // If the report is 20 minutes old, and the offset is 5 minutes, then the report stage is 4.
                            int newReportStage = (int) ((serverCurrentTime - serverReportTime) / offsetUntilNextReport);
                            Records.add("User " + s + " has report stage " + newReportStage + " (old: " + reportStage + ").");

                            if (reportStage == -1) {
                                // Final report is already made, do nothing.
                                Records.add("User " + s + " has already made the final report.");
                                continue;
                            }

                            if (newReportStage != reportStage) {
                                Records.add("Stage has changed! (" + reportStage + " -> " + newReportStage + " | " + fStage + ")");
                                if (newReportStage >= fStage) {
                                    // Report is too old, and we have reached the final stage.
                                    Records.add("User " + s + " has reached the final stage. (" + reportStage + " -> " + newReportStage + " | " + fStage + ") Sending final mail.");
                                    Mailing.sendFinalEmail(o);
                                    newReportStage = -1;
                                    Records.add("Setting user " + s + " report stage to -1 internally.");
                                } else {
                                    // Report is too old, but we have not reached the final stage.
                                    Records.add("User " + s + " has not reached the final stage. (" + reportStage + " -> " + newReportStage + " | " + fStage + ") Sending warning mail.");
                                    Mailing.sendWarningEmail(o);
                                }

                                // Update report stage
                                r.setReportStage(String.valueOf(newReportStage));
                                o.save();
                                Records.add("User " + s + " report stage updated from " + reportStage + " to " + Utils.last(o.getReportHistory()).getReportStage() + ".");
                            }else{
                                Records.add("User " + s + " report stage is still " + reportStage + ".");
                                Records.add("Skipping user " + s + ".");
                            }

                        }catch (Exception e) {
                            Records.add("Error while processing user " + s + ": " + e.getMessage());
                        }
                    }
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    Records.add("Worker interrupted.");
                }
            }
        });

        workerThread.start();
        Records.add("Worker started.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cleanup code if needed
        System.out.println("Application destroyed");
    }
}