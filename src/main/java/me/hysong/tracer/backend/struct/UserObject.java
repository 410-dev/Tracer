package me.hysong.tracer.backend.struct;

import lombok.Getter;
import lombok.Setter;
import me.hysong.libhycore.CoreSHA;
import me.hysong.libhyextended.environment.SubsystemEnvironment;
import me.hysong.libhyextended.objects.DataObject;
import me.hysong.tracer.backend.Records;

import java.io.IOException;
import java.util.ArrayList;

@Getter
public class UserObject extends DataObject {
    @Setter private String email;
    private ArrayList<String> emergencyEmails;
    private ArrayList<String> emergencyPhoneNumbers;
    private ArrayList<Report> reportHistory;
    private int MAX_REPORTS = 50;

    public UserObject(String email, String uniqueID, ArrayList<String> emergencyEmails, ArrayList<String> emergencyPhoneNumbers, ArrayList<Report> reportHistory) {
        this.email = email;
        this.emergencyEmails = emergencyEmails;
        this.emergencyPhoneNumbers = emergencyPhoneNumbers;
        this.reportHistory = reportHistory;
    }

    public UserObject(String email) {
        this();
        try {
            SubsystemEnvironment env = new SubsystemEnvironment("TracerRecord", "/opt/data/tracer");
            fromJsonString(env.readString("/Reports/" + CoreSHA.hash256(email)));
        } catch (Exception e) {
            this.email = email;
        }
    }

    public UserObject() {
        this.email = "";
        this.emergencyEmails = new ArrayList<>();
        this.emergencyPhoneNumbers = new ArrayList<>();
        this.reportHistory = new ArrayList<>();
    }

    public void addReport(Report report) {
        if (reportHistory == null) reportHistory = new ArrayList<>();
        if (reportHistory.size() >= MAX_REPORTS) reportHistory.remove(0);

        reportHistory.add(report);
    }

    public static UserObject load(String email) {
        try {
            return new UserObject(email);
        }catch (Exception e) {
            return null;
        }
    }

    public static UserObject loadFromFile(SubsystemEnvironment env, String path){
        try {
            UserObject o = new UserObject();
            o.fromJsonString(env.readString(path));
            return o;
        }catch (Exception e) {
            Records.add("Error loading user object from file: " + e.getMessage());
            return null;
        }
    }

    public boolean save() {
        SubsystemEnvironment env = new SubsystemEnvironment("TracerRecord", "/opt/data/tracer");
        env.mkdirs("/Reports");
        try {
            env.writeString("/Reports/" + CoreSHA.hash256(this.getEmail()), this.toJsonString());
            Records.add("Saved user object to file: " + CoreSHA.hash256(this.getEmail()));
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
