package com.honeychain.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {

    private boolean success;
    private String token;
    private String role;
    private String message;

    public LoginResponse() {
    }

    public LoginResponse(boolean success, String token, String role) {
        this.success = success;
        this.token = token;
        this.role = role;
    }

    public LoginResponse(boolean success, String token, String role, String message) {
        this.success = success;
        this.token = token;
        this.role = role;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static LoginResponse of(String token, String role) {
        return new LoginResponse(true, token, role);
    }

    public static LoginResponse of(String token, String role, String message) {
        return new LoginResponse(true, token, role, message);
    }
}
