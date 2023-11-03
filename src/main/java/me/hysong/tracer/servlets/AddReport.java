package me.hysong.tracer.servlets;

import me.hysong.tracer.backend.Records;
import me.hysong.tracer.backend.struct.Report;
import me.hysong.tracer.backend.struct.UserObject;

import java.io.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/report/add")
public class AddReport extends HttpServlet {


    // Required parameters:
    //
    // machineid: The ID of the user's machine or device.
    // devicename: The name of the device.
    // hostname: The hostname of the device.
    // hostmodel: The model of the device.
    // systemversion: The version of the system or operating system.
    // systembuild: The build number of the system.
    // machvolume: The current volume of the machine.
    // machbrightness: The current brightness of the machine.
    // dispmode: The current appearance or display mode.
    // batterylvl: The current battery state or level.
    // isconnectedcharger: Indicates whether the device is connected to a charger.
    // ischarging: Indicates whether the device is currently charging.
    // location: The current location of the device.
    // reporttime: The formatted date and time of the report.
    // contacts: Information about contacts.
    // contactsphone: Phone details of contacts.
    // message: A message or additional information.
    // wifimac: The MAC address of the network connection.
    // wifiname: The name of the connected WiFi network.
    // wifiip: The current IP address of the device.
    // suntilnext: The time until the next report.
    // fstage: The number of maximum reports.
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        try {
            UserObject o = new UserObject(request.getParameter("machineid"));
            Records.add("User machineid:" + request.getParameter("machineid") + " reported a new report.");
            o.addReport(new Report(request));

            Records.add("Saving report to file...");
            o.save();

            Records.add("Report has been saved. Returning OK sign.");
            response.getWriter().println("OK");
        }catch (Exception e) {
            e.printStackTrace();
            Records.add("Error: " + e.getMessage());
            response.getWriter().println("ERR:000:Internal server error - " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request, response);
    }
}