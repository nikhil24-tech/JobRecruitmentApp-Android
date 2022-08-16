package com.example.jobrecruitmentapp_android.models;

import java.util.List;

public class User {
    public String email;
    public String empName;
    public String empPhone = "Enter Phone";
    public boolean isBlocked = false;
    public String jsAboutMe = "Enter About Me";
    public String jsAddress;
    public String jsEduLevel = "Enter Education Level";
    public String jsImageUrl;
    public String jsJobXp = "Enter Job Experience";
    public String jsLocation;
    public String jsName;
    public String jsOccupation = "Enter Occupation";
    public String jsPhone;
    public String jsSkills = "Enter Skills";
    public String orgAddress;
    public String orgImageUrl;
    public String orgLocation;
    public String orgName;
    public String orgType;
    public String password;
    public String uid;
    public String userType;

    public String appliedFor;
    public String appliedForJobId;

    public boolean isApproved = false;

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
        if (jsName != null)
            return jsName;
        else if (empName != null)
            return empName;
        else
            return orgName;
    }

}
