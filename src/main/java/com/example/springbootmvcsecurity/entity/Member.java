package com.example.springbootmvcsecurity.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "Members")
public class Member implements Serializable {

    private static final long serialVersionUID = -2054386655979281969L;

    @Id
    @Column(name = "User_Name", length = 20, nullable = false)
    private String userName;

    @Column(name = "Email", length = 50, nullable = false)
    private String email;

    @Column(name = "Password", length = 100, nullable = false)
    private String password;

    @Column(name = "First_Name", length = 20)
    private String firstName;

    @Column(name = "Last_Name", length = 20)
    private String lastName;

    @Column(name = "Gender", length = 1, nullable = false)
    private boolean gender;

    @Column(name = "Country", length = 20, nullable = false)
    private String country;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean getGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "[" +
                this.userName   + ", " +
                this.email      + ", " +
                this.password   + ", " +
                this.gender     + ", " +
                this.country    + ", " +
                "]";
    }
}
