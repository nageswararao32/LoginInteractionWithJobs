package com.strms.demo.Entites;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    private String id;
    private String title;
    private String department;
    private String location;
    private String type;
    private String description;
    private boolean isVisible;

    public Job() {}

    public Job(String id, String title, String department, String location, String type, String description, boolean isVisible) {
        this.id = id;
        this.title = title;
        this.department = department;
        this.location = location;
        this.type = type;
        this.description = description;
        this.isVisible = isVisible;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isVisible() { return isVisible; }
    public void setVisible(boolean visible) { isVisible = visible; }
}
