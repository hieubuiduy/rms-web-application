package com.example.springbootmvcsecurity.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "Admins")
public class Admin implements Serializable {

    private static final long serialVersionUID = -2054386655979281969L;

    @Id
    @Column(name = "User_Name", length = 25, nullable = false)
    String userName;

    @Column(name = "Password", length = 100, nullable = false)
    String password;

    @Column(name = "Active", length = 1, nullable = false)
    boolean isActive;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
    @Override
    public String toString() {
        return "[" +
                this.userName   + ", " +
                this.password   + ", " +
                this.isActive   +
                "]";
    }
}
