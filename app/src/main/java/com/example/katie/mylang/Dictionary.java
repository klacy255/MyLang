package com.example.katie.mylang;


/**
 * Dictionary.java
 * Purpose: represents a Dictionary object
 *
 * @author katie
 * @version 1.0 4/10/17.
 */
public class Dictionary {

    private String description;
    private String creator;
    private String name;
    private String ID;
    private String color;

    /**
     * constructor for a dictionary object that is required by recycler view but which shouldnt be called
     */
    public Dictionary() {
        this.name = "";
        this.description = "";
        this.color = "";
        this.creator = "";
    }

    /**
     * constructor for a dictionary object that takes a name description and color
     *
     * @param name - a string name
     * @param description - a string description
     * @param color - a string color
     */
    Dictionary(String name, String description, String color) {
        this.name = name;
        this.description = description;
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    String getID() {
        return ID;
    }

    void setID(String ID) {
        this.ID = ID;
    }

    String getDescription() {
        return description;
    }

    String getCreator() {
        return creator;
    }

    void setCreator(String creator) {
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
