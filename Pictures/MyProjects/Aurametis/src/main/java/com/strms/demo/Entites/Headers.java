package com.strms.demo.Entites;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "headers")
public class Headers {

    @Id
    @Column(name = "id", nullable = false, length = 100)
    private String id;

    @Column(name = "label", nullable = false)
    private String label;

    @Column(name = "route_to", nullable = false)
    private String to;

    @Column(name = "is_visible", nullable = false)
    private boolean isVisible;

    @Column(name = "is_external", nullable = false)
    private boolean isExternal;

    // ✅ No-args constructor (required by JPA)
    public Headers() {
    }

    // ✅ All-args constructor
    public Headers(String id, String label, String to, boolean isVisible, boolean isExternal) {
        this.id = id;
        this.label = label;
        this.to = to;
        this.isVisible = isVisible;
        this.isExternal = isExternal;
    }

    // ✅ Getters & Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public boolean isExternal() {
        return isExternal;
    }

    public void setExternal(boolean isExternal) {
        this.isExternal = isExternal;
    }

    
}

