package com.strms.demo.Entites;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "products")
public class Products {

    @Id
    private String id;   // e.g. "rag-chat"

    @Column(nullable = false)
    private String name;

    private String version;    // v2.1

    private String status;     // Alpha, Beta, Stable, etc.

    @Column(length = 1000)
    private String description;

    private boolean isVisible;

    @ElementCollection
    @CollectionTable(
        name = "product_tags",
        joinColumns = @JoinColumn(name = "product_id")
    )
    @Column(name = "tag")
    private List<String> tags;

    // 🔹 No-arg constructor
    public Products() {}

    // 🔹 All-args constructor
    public Products(String id, String name, String version, String status,
                   String description, boolean isVisible, List<String> tags) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.status = status;
        this.description = description;
        this.isVisible = isVisible;
        this.tags = tags;
    }

    // 🔹 Getters & Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
