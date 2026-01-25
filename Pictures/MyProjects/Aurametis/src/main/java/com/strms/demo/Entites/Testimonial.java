package com.strms.demo.Entites;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "testimonials")
public class Testimonial {

	@Id
	private String id;
	private String name;
	private String role;
	private String company;
	private String content;
	private String avatar;
	@Column(name = "is_visible", nullable = false)
    private boolean isVisible;

	public Testimonial() {
	}

	public Testimonial(String id, String name, String role, String company, String content, String avatar,  boolean isVisible) {
		this.id = id;
		this.name = name;
		this.role = role;
		this.company = company;
		this.content = content;
		this.avatar = avatar;
		this.isVisible = isVisible;
	}
	
	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
}
