package org.example.emailprojectjavafx.models.User;

public class User {
    private String id;
    private String username;
    private String role;
    private String avatar;

    public User(String id, String username, String role, String avatar) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
