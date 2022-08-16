package com.example.jobrecruitmentapp_android.models;

import com.google.firebase.firestore.DocumentId;

import java.util.ArrayList;
import java.util.List;

public class Job {
    public String empEmail;
    public String empPhone;
    public String jobAddress;
    public String jobDescription;
    public String jobLocation;
    public String jobName;
    public String jobRequirements;
    public String orgImageUrl;
    public String orgName;
    public String orgType;
    public String salaryPerHr;

    public List<SaveJob> jsSavedAndApplied = new ArrayList<>();

    @DocumentId
    public String docID;
}
