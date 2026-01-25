package com.strms.demo.Entites;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "projects")
public class Projects {

    @Id
    private String id;   // e.g., "p1"

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    private String category;   // Web App, Mobile App, etc.

    private String image;      // image URL

    private String videoUrl;   // video URL

    private boolean isVisible;

    @ElementCollection
    @CollectionTable(
        name = "project_tech_stack",
        joinColumns = @JoinColumn(name = "project_id")
    )
    @Column(name = "tech")
    private List<String> techStack;

    // 🔹 No-arg constructor (required by JPA)
    public Projects() {}

    // 🔹 All-args constructor
    public Projects(String id, String title, String description, String category,
                   String image, String videoUrl, boolean isVisible,
                   List<String> techStack) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.image = image;
        this.videoUrl = videoUrl;
        this.isVisible = isVisible;
        this.techStack = techStack;
    }

    // 🔹 Getters & Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public List<String> getTechStack() {
        return techStack;
    }

    public void setTechStack(List<String> techStack) {
        this.techStack = techStack;
    }
}
