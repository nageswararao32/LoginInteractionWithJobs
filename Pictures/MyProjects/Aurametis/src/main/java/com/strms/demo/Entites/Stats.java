package com.strms.demo.Entites;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "stats")
public class Stats {

    @Id
    private String id = "stats"; // Single record, fixed id

    private String projectCount;
    private String retentionRate;

    public Stats() {}

    public Stats(String projectCount, String retentionRate) {
        this.projectCount = projectCount;
        this.retentionRate = retentionRate;
    }

    public String getId() {
        return id;
    }

    public String getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(String projectCount) {
        this.projectCount = projectCount;
    }

    public String getRetentionRate() {
        return retentionRate;
    }

    public void setRetentionRate(String retentionRate) {
        this.retentionRate = retentionRate;
    }
}

