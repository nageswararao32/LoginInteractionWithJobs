package com.strms.demo.Entites;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "services")
public class Services {
	@Id
	private String id; // e.g. "design-dev"

	@Column(nullable = false)
	private String title;

	private String category; // Creative, Infrastructure, etc.

	@Column(length = 1500)
	private String description;

	private String icon; // Palette, Cloud, Brain, etc.

	private boolean isVisible;

	@ElementCollection
	@CollectionTable(name = "service_tags", joinColumns = @JoinColumn(name = "service_id"))
	@Column(name = "tag")
	private List<String> tags;

	public Services() {
	}

	/* Getters & Setters */

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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
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
