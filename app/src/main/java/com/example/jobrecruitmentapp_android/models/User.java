package com.example.jobrecruitmentapp_android.models;

import java.util.List;

public class User {
    public String empName;
    public String orgImageUrl;
    public String empPhone;
    public String orgType;
    public String orgName;
    public String orgAddress;

    public String jsName;
    public String email;
    public String name;
    public String uid;
    public String jsAddress;
    public String jsLocation;
    public String jsPhone;
    public String password;
    public String jsEduLevel;
    public String jsSkills;
    public String jsAboutMe;
    public String jsJobXp;
    public String jsImageUrl;
    public String jsOccupation;

    public List<String> savedJobs;
    public List<String> appliedJobs;
    public List<String> postedJobs;
    public String appliedFor;
    public String appliedForJobId;

    public String userType;
    public boolean isBlocked = false;

    public String getAddress() {
        if (jsAddress != null)
            return jsAddress;
        else if (jsLocation != null)
            return jsLocation;
        else if (orgAddress != null)
            return orgAddress;
        else return null;
    }

    public String getName() {
        if (name != null)
            return name;
        else if (jsName != null)
            return jsName;
        else if (empName != null)
            return empName;
        else
            return orgName;
    }
}
