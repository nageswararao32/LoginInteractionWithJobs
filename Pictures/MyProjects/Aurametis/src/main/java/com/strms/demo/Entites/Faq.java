package com.strms.demo.Entites;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "faqs")
public class Faq {

    @Id
    private String id; // "faq1", "faq2", etc.

    @Column(nullable = false)
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    @Column(nullable = false)
    private boolean isVisible;
    
    public Faq(){
    	
    }

	public Faq(String id, String question, String answer, boolean isVisible) {
		super();
		this.id = id;
		this.question = question;
		this.answer = answer;
		this.isVisible = isVisible;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
    
    
}

