package com.strms.demo.DTOs;


public class ContactUpdateRequest {
    private String status;

    public ContactUpdateRequest() {
    }

    public ContactUpdateRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
