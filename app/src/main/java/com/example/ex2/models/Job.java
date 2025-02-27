package com.example.ex2.models;

public class Job {
    private String jobName;

    public Job(String name) {
        jobName = name;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobName() { return jobName; }
}
