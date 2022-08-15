package com.example.jobrecruitmentapp_android.models;

import com.google.firebase.firestore.DocumentId;

public class AppliedJob {

    public String empEmail;
    public String empPhone;
    public Boolean isApproved;
    public String jobDescription;
    public String jobID;
    public String jobLocation;
    public String jobName;
    public String jsAboutMe;
    public String jsAddress;
    public String jsEmail;
    public String jsExperience;
    public String jsImageUrl;
    public String jsName;
    public String jsPhone;
    public String jsSkills;
    public String orgAddress;
    public String orgType;
    public String requirements;
    public String salaryPerHr;

    @DocumentId
    public String docID;
    public String uid;

    public Job toJob() {
        Job job = new Job();
        job.docID = jobID;
        job.jobName = jobName;
        job.jobLocation = jobLocation;
        job.jobDescription = jobDescription;
        job.jobRequirements = requirements;
        job.empEmail = empEmail;
        job.salaryPerHr = salaryPerHr;
        job.empPhone = empPhone;
        job.orgType = orgType;
        job.jobAddress = orgAddress;
        return job;
    }

    public User toUser() {
        User user = new User();
        user.uid = uid;
        user.jsName = jsName;
        user.email = jsEmail;
        user.jsAddress = jsAddress;
        user.jsAboutMe = jsAboutMe;
        user.jsSkills = jsSkills;
        user.jsJobXp = jsExperience;
        user.jsImageUrl = jsImageUrl;
        user.jsPhone = jsPhone;
        user.appliedFor = jobName;
        user.appliedForJobId = docID;
        user.isApproved = isApproved != null && isApproved;
        return user;
    }
}
