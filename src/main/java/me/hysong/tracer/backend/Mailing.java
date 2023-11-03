package me.hysong.tracer.backend;

import me.hysong.libhycore.CoreDate;
import me.hysong.libhyextended.Utils;
import me.hysong.libhyextended.environment.SubsystemEnvironment;
import me.hysong.libhyextended.mail.HEMail;
import me.hysong.libhyextended.mail.HEMailBodyType;
import me.hysong.libhyextended.mail.HEMailObject;
import me.hysong.libhyextended.mail.servers.HEGmail;
import me.hysong.tracer.backend.struct.Report;
import me.hysong.tracer.backend.struct.UserObject;

public class Mailing {

    private static void sendMail(String to, String subject, String body) {
        SubsystemEnvironment env = new SubsystemEnvironment("TracerRecord", "/opt/data/tracer");
        env.configure();
        String senderAddress = env.getConfig("smtp-addr");
        String senderPassword = env.getConfig("smtp-passwd");
        HEMail.send(new HEGmail(senderAddress, senderPassword, "Tracer System"), new HEMailObject("Tracer System", to, HEMailBodyType.TEXT, subject, body));
        Records.add("Sent email to " + to + " with subject: " + subject);
    }

    public static void sendFinalEmail(UserObject o) {
        Report r = Utils.last(o.getReportHistory());
        if (r == null) return;

        String subject = "[EMERGENCY SUSPECTED] Tracer System - Final Report";
        String body = Strings.formatter(r, o, Strings.finalEmailContent);

        String[] emails = Utils.last(o.getReportHistory()).getContacts().replace(" ", "").split(",");
        for (String email : emails) {
            sendMail(email, subject, body);
        }

        sendMail(o.getEmail(), subject, body);
    }

    public static void sendWarningEmail(UserObject o) {
        Report r = Utils.last(o.getReportHistory());
        if (r == null) return;

        String subject = "Tracer System - Warning";
        String body = Strings.formatter(r, o, Strings.warningEmailContent);
        sendMail(o.getEmail(), subject, body);
    }
}
