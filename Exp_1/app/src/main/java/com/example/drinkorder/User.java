package com.example.drinkorder;

/** 用户实体类 — 对应User表 */
public class User {
    private int id;
    private String username;
    private String password;
    private String role;
    private String nickname;

    public User() {
    }

    public User(int id, String username, String password, String role, String nickname) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.nickname = nickname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
