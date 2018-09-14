package com.example.katie.mylang;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by katie on 4/18/17.
 */
public class DatabaseTest {
    Database database;
    @Before
    public void setUp() throws Exception {
        database = new Database();
    }

    @Test
    public void addDictionaryToFirebase() throws Exception {
        Dictionary dictionary = new Dictionary("arabic verbs", "this is a list of common arabic verbs", "green");
        database.addDictionaryToFirebase(dictionary, "");
        assertTrue(database.dictionaryCloudEndPoint.child(dictionary.getID()) != null);
    }

    @Test
    public void deleteDictionaryFromFirebase() throws Exception {
        Dictionary dictionary = new Dictionary("arabic verbs", "this is a list of common arabic verbs", "green");
        database.addDictionaryToFirebase(dictionary, "");
        assertTrue(database.dictionaryCloudEndPoint.child(dictionary.getID()) != null);
        database.deleteDictionaryFromFirebase(dictionary);
        assertTrue(database.dictionaryCloudEndPoint.child(dictionary.getID()) == null);
    }

    @Test
    public void editDictionaryInFirebase() throws Exception {

    }

    @Test
    public void addTranslationtoFirebase() throws Exception {

    }

    @Test
    public void deleteTranslationFromFirebase() throws Exception {

    }

}