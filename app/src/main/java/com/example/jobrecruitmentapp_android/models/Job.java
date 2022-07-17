package com.example.jobrecruitmentapp_android.models;

import com.google.firebase.firestore.DocumentId;

public class Job {
    @DocumentId
    public String jobId;
    public String jobName;
    public String location;
    public String address;
    public String jobDescription;
    public String requirements;
    public String contactEmail;
    public String salary;
    public String phone;
    public String orgAddress;
}
