package me.hysong.tracer.servlets;

import me.hysong.tracer.backend.Records;
import me.hysong.tracer.backend.struct.UserObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/report/get")
public class GetReport extends HttpServlet {

    // Required parameters:
    //
    // machineid: The ID of the user's machine or device.
    // idx: The index of the report to get. 0 is the latest report, oldest may be 50.
    //
    // If success:
    //   Returns the report in JSON format.
    //
    // If error:
    //   If out of index, returns ERR:002:Index out of range.
    //   If user not found, returns ERR:001:User not found! Please run registration task first.
    //   If internal error, returns ERR:000:Internal server error - <error message>.
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        try {
            UserObject o = UserObject.load(request.getParameter("machineid"));
            Records.add("User machineid:" + request.getParameter("machineid") + " requested a report.");
            if (o == null) {
                response.getWriter().println("ERR:001:User not found! Please run report task first.");
                return;
            }

            int idx = Integer.parseInt(request.getParameter("idx"));
            if (idx < 0 || idx > 50) {
                Records.add("User machineid:" + request.getParameter("machineid") + " requested report index " + idx + " which is out of range.");
                response.getWriter().println("ERR:002:Index out of range.");
                return;
            }

            response.getWriter().println(o.getReportHistory().get(idx).toJsonString());
            Records.add("User machineid:" + request.getParameter("machineid") + " requested report index " + idx + ", and was returned.");

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
