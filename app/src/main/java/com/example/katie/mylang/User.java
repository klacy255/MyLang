package com.example.katie.mylang;

/**
 * User.java
 * Purpose: representation of a User object
 *
 * @author katie
 * @version 1.0 4/10/17.
 */

public class User {

    private String email;
    private String password;
    private String ID;

    /**
     * a constructor for a user object that takes an email and password
     *
     * @param email
     * @param password
     */
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

}
