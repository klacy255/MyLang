package com.example.katie.mylang;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

/**
 * Databse.java
 * Purpose: class dealing with all database interaction (CRUD operations)
 *
 * @author katie
 * @version 1.0 4/10/17.
 */
class Database {

    DatabaseReference mDatabase;
    DatabaseReference dictionaryCloudEndPoint;
    DatabaseReference userCloudEndpoint;

    /**
     * constructor for a database object
     */
    Database() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        dictionaryCloudEndPoint = mDatabase.child("dictionaries");
        userCloudEndpoint = mDatabase.child("users");
    }

    /**
     * adds a user object to our users database. sets the key to this as thier auth UID
     *
     * @param user a user object to add
     */
    void addUserToFirebase(User user) {
        String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
        user.setID(key);
        userCloudEndpoint.child(key).setValue(user);
        userCloudEndpoint.child(key).child("password").setValue("");

    }

    /**
     * adds a dictionary id and name key-value to a user
     *
     * @param dictionaryID - dictionary ID to add
     * @param dictionaryName - dictionary name to add
     * @param userID - userID to add dictionary too
     */
    void addDictionaryToUser(String dictionaryID, String dictionaryName, String userID) {
        String path = "/users/" + userID;
        String pathToDictionary = userID + "/dictionaries/";

        userCloudEndpoint.child(pathToDictionary).push();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/dictionaries/" + dictionaryName, dictionaryID);
        mDatabase.child(path).updateChildren(childUpdates);

    }

    /**
     * this method updates a userID to the current authID
     *
     * @param user - userID
     * @param newUserID -user AuthID
     */
    void updateUserID(String user, String newUserID) {
        userCloudEndpoint.child(user).setValue(newUserID);
        userCloudEndpoint.child(newUserID).child("id").setValue(newUserID);
    }


    /**
     * this method adds a dictionary object to the dictionaries database/table
     *
     * @param dictionary - a dictionary object we are adding to our database
     */
    void addDictionaryToFirebase(Dictionary dictionary, String creator) {
        String key = dictionaryCloudEndPoint.push().getKey();
        dictionary.setID(key);
        dictionary.setCreator(creator);
        dictionaryCloudEndPoint.child(key).setValue(dictionary);
    }

    /**
     * this method removes a dictionary object from the dictionaries database/table
     *
     * @param dictionary - a dictionary object we are removing from our database
     */
    void deleteDictionaryFromFirebase(Dictionary dictionary) {
        dictionaryCloudEndPoint.child(dictionary.getID()).removeValue();
    }

    /**
     * this method removes a dictionary object from the user dictionaries database
     *
     * @param dictionaryName - a dictionary object we are removing from our users dictionaries
     */
    void deleteDictionaryFromUser(String dictionaryName, String userID) {
        userCloudEndpoint.child(userID).child("dictionaries").child(dictionaryName).removeValue();
    }

    /**
     * this method updates a value in dictionary.
     *
     * @param service    - uses an integer to indicate what piece of the dictionary we are updating
     * @param updated    - the updated string
     * @param dictionary - the dictionary we are updating
     */
    void editDictionaryInFirebase(int service, String updated, Dictionary dictionary) {
        if (service == 1) {
            dictionaryCloudEndPoint.child(dictionary.getID() + "/name").setValue(updated);
        } else if (service == 2) {
            dictionaryCloudEndPoint.child(dictionary.getID() + "/description").setValue(updated);
        } else if (service == 3) {
            dictionaryCloudEndPoint.child(dictionary.getID() + "/color").setValue(updated);
        }
    }

    /**
     * this method adds a translation object to the translation data table.
     *
     * @param translation  -a translation object to add to translation database
     * @param dictionaryID -the ID of the dictionary we want to notify of a translation addition
     */
    void addTranslationtoFirebase(Translation translation, String dictionaryID) {
        String path = "/dictionaries/" + dictionaryID;
        String pathToTranslation = dictionaryID + "/translations/";

        String key = dictionaryCloudEndPoint.child(pathToTranslation).push().getKey();
        translation.setID(key);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/translations/" + key, translation);
        mDatabase.child(path).updateChildren(childUpdates);
    }

    /**
     * this method deletes a translation object from the database.
     *
     * @param translation - the translationID to delete from translation list in dictionary
     */
    public void deleteTranslationFromFirebase(Translation translation, String dictionaryID) {
        String path = dictionaryID + "/translations/" + translation.getID();
        dictionaryCloudEndPoint.child(path).removeValue();
    }

}
