package me.hysong.tracer.backend;

import me.hysong.libhycore.CoreDate;
import me.hysong.tracer.backend.struct.Report;
import me.hysong.tracer.backend.struct.UserObject;

public class Strings {
    public static final String finalEmailContent = "Hello. This is Tracer System from me.hysong.dev/tracer.\n" +
            "This email is sent because the registered machine (%EMAIL%) has not reported for more than %HOURS% hours (%DAYS% days) since the last report.\n" +
            "\n" +
            "Last available machine report:\n" +
            "%REPORT%\n" +
            "\n" +
            "This email means that the owner's device failed to report %FSTAGE% times in a row, for %TERM% hours term."+
            "Please make sure that the owner is safe.\n" +
            "The warning will not be sent again unless another report is made from the machine.\n";
    public static final String warningEmailContent = "Hello. This is Tracer System from me.hysong.dev/tracer.\n" +
            "This email is sent because the registered machine (%EMAIL%) has not reported for more than %HOURS% hours (%DAYS% days) since the last report.\n" +
            "\n" +
            "Last available machine report:\n" +
            "%REPORT%\n" +
            "\n" +
            "An immediate action is required - If a new report is not made before %HOURS% hours since the last report, the system will automatically send emergency report to the emergency contacts you've registered. (Emergency contacts: %CONTACTS%)\n";


    public static String formatter(Report r, UserObject o, String body) {

        long secondsSinceLastReport = Long.parseLong(r.getReportServerTime());
        secondsSinceLastReport = CoreDate.secondsSince1970() - secondsSinceLastReport;
        long hoursSinceLastReport = secondsSinceLastReport / 3600;
        int daysSinceLastReport = (int) (hoursSinceLastReport / 24);

        body = body.replace("%EMAIL%", o.getEmail());
        body = body.replace("%HOURS%", String.valueOf(hoursSinceLastReport));
        body = body.replace("%DAYS%", String.valueOf(daysSinceLastReport));
        body = body.replace("%REPORT%", r.getBaseReport());
        body = body.replace("%FSTAGE%", r.getFStage());
        body = body.replace("%TERM%", ((int) (Long.parseLong(r.getSUntilNext()) / 3600)) + "");
        body = body.replace("%CONTACTS%", String.join(", ", o.getEmergencyEmails()));

        return body;
    }
}
