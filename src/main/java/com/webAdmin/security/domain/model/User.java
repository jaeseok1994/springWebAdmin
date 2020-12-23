package com.webAdmin.security.domain.model;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * author:james
 * day:2020-09-10
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id = UUID.randomUUID().toString();

    private String username;

    private String password;

    private String salt;

    private String email;

    private boolean disabled;

    private Date createTime;

    private Date lastTime;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public boolean isRoot(){
        return "root".equals(username);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", email='" + email + '\'' +
                ", disabled=" + disabled +
                ", createTime=" + createTime +
                ", lastTime=" + lastTime +
                '}';
    }
}
