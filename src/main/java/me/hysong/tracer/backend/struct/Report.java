package me.hysong.tracer.backend.struct;

import lombok.Getter;
import lombok.Setter;
import me.hysong.libhycore.CoreDate;
import me.hysong.libhyextended.Utils;
import me.hysong.libhyextended.objects.DataObject;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.net.http.HttpRequest;

@Getter
public class Report extends DataObject {
    private String machineID;
    private String deviceName;
    private String hostName;
    private String hostModel;
    private String systemVersion;
    private String systemBuild;
    private String machVolume;
    private String machBrightness;
    private String displayMode;
    private String batteryLvl;
    private String isConnectedCharger;
    private String isCharging;
    private String location;
    private String reportTime;
    private String contacts;
    private String contactsPhone;
    private String message;
    private String wifiMac;
    private String wifiName;
    private String wifiIP;
    private String requestIP;
    private String reportServerTime;
    private String sUntilNext;
    @Setter private String reportStage;
    private String fStage;

    public Report(HttpServletRequest request) {
        Field[] fields = this.getClass().getDeclaredFields();
        String[] excludedFields = {"serialVersionUID", "requestIP", "reportServerTime", "reportStage"};
        for (Field field : fields) {
            if (Utils.arrayContains(excludedFields, field.getName().toLowerCase())) continue;
            try {
                field.setAccessible(true);
                field.set(this, request.getParameter(field.getName().toLowerCase()));
            } catch (Exception ignored) {}
        }
        this.reportServerTime = String.valueOf(CoreDate.secondsSince1970());
        this.requestIP = request.getRemoteAddr();
        this.reportStage = "0";

        // Safely fill in missing fields
        for (Field field : fields) {
            try {
                if (field.getType() != String.class) continue;
                field.setAccessible(true);
                if (field.get(this) == null) field.set(this, "");
            } catch (Exception ignored) {}
        }
    }

    public String getBaseReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("Machine ID: ").append(machineID).append("\n");
        sb.append("Device Name: ").append(deviceName).append("\n");
        sb.append("Host Name: ").append(hostName).append("\n");
        sb.append("Host Model: ").append(hostModel).append("\n");
        sb.append("System Version: ").append(systemVersion).append("\n");
        sb.append("System Build: ").append(systemBuild).append("\n");
        sb.append("Volume: ").append(machVolume).append("\n");
        sb.append("Brightness: ").append(machBrightness).append("\n");
        sb.append("Display Mode: ").append(displayMode).append("\n");
        sb.append("Battery Level: ").append(batteryLvl).append("\n");
        sb.append("Is Connected Charger: ").append(isConnectedCharger).append("\n");
        sb.append("Is Charging: ").append(isCharging).append("\n");
        sb.append("Location: ").append(location).append("\n");
        sb.append("Report Time: ").append(reportTime).append("\n");
        sb.append("Report Server Time: ").append(reportServerTime).append("\n");
        sb.append("WiFi MAC: ").append(wifiMac).append("\n");
        sb.append("WiFi Name: ").append(wifiName).append("\n");
        sb.append("WiFi IP: ").append(wifiIP).append("\n");
        sb.append("Request IP: ").append(requestIP).append("\n");

        return sb.toString();
    }

    public String getFullReport() {
        return getBaseReport() + "\n" + "Contacts: " + contacts + "\n" + "Contacts Phone: " + contactsPhone + "\n" + "Message: " + message;
    }

}
