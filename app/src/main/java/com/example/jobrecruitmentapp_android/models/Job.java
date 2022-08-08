package com.example.jobrecruitmentapp_android.models;

import com.google.firebase.firestore.DocumentId;

public class Job {
    @DocumentId
    public String docID;
    public String jobName;
    public String jobAddress;
    public String jobLocation;
    public String jobDescription;
    public String jobRequirements;
    public String empEmail;
    public String salaryPerHr;
    public String empPhone;
    public String orgAddress;
    public String orgType;
    public String orgName;
    public String orgImageUrl;
    public String createdBy;
}
