package com.example.katie.mylang;

/**
 * Translation.java
 * Purpose: representation of a Translation object
 * dictionaries
 *
 * @author katie
 * @version 1.0 4/10/17.
 */

public class Translation {

    private String initial;
    private String translated;
    private String language;
    private String ID;

    public Translation(String init, String trans, String language) {
        initial = init;
        translated = trans;
        this.language = language;
    }

    public Translation() {
        initial = "";
        translated = "";
        this.language = "";
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTranslated() {
        return translated;
    }

    public void setTranslated(String translated) {
        this.translated = translated;
    }

}
