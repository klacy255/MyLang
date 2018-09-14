package com.example.katie.mylang;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

/**
 * AddDictionaryActivity.java
 * Purpose: activity dealing adding a dictionary to firebase
 *
 * @author katie
 * @version 1.0 4/10/17.
 */
public class AddDictionaryActivity extends AppCompatActivity {

    RadioGroup radioColorGroup;
    TextView dictionaryTitle;
    EditText dictionaryName;
    EditText dictionaryDescription;
    TextView buttonInstructions;
    Button createDictionary;
    Database database;
    final String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dictionary);

        //set up toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //set up and find views
        createDictionary = (Button) findViewById(R.id.btn_createDictionary);
        dictionaryTitle = (TextView) findViewById(R.id.createDictionaryTextView);
        dictionaryName = (EditText) findViewById(R.id.dictionaryName);
        dictionaryDescription = (EditText) findViewById(R.id.dictionaryDescription);
        buttonInstructions = (TextView) findViewById(R.id.colorDescription);
        radioColorGroup = (RadioGroup) findViewById(R.id.radioColors);

        //set typefaces
        final Typeface font = Typeface.createFromAsset(getAssets(), "bebas_neue/TTF's/BebasNeue Regular.ttf");
        final Typeface font2 = Typeface.createFromAsset(getAssets(), "roboto/Roboto-Regular.ttf");
        dictionaryTitle.setTypeface(font);
        buttonInstructions.setTypeface(font);
        dictionaryName.setTypeface(font2);
        dictionaryDescription.setTypeface(font2);

        //create a new Database object
        database = new Database();

        //set onclick listener to create a dictionary
        createDictionary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()) {
                    String name = dictionaryName.getText().toString();
                    String description = dictionaryDescription.getText().toString();
                    String color;
                    int selectedId= radioColorGroup.getCheckedRadioButtonId();
                    switch(selectedId) {
                        case R.id.radioOrange:
                            color = getString(R.string.orange);
                            break;
                        case R.id.radioBlue:
                            color = getString(R.string.blue);
                            break;
                        case R.id.radioRed:
                            color = getString(R.string.red);
                            break;
                        case R.id.radioGreen:
                            color = getString(R.string.green);
                            break;
                        default:
                            color = "";
                    }
                    //create our dictionary and display it in dictionaryActivity
                    createDictionary(name, description, color);
                    Intent intent = new Intent(view.getContext(), DictionaryActivity.class);
                    view.getContext().startActivity(intent);
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.correctErrors), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    /**
     * inflates the menu at the top of the app
     * @param menu menu to inflate
     * @return true if enu was inflated
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * starts a new activity depending on the menu item that was clicked
     * @param item menu item that was selected
     * @return a boolean that an activity was started
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_translate:
                startActivity(new Intent(this, TranslateActivity.class));
                return true;
            case R.id.action_dictionaries:
                startActivity(new Intent(this, DictionaryActivity.class));
                return true;
            case R.id.action_search:
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            case R.id.signout:
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                Intent intent = new Intent(AddDictionaryActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * returns if the user has given the dictionary a name
     * @return true or false if information is valid
     */
    public boolean validate() {
        String name = dictionaryName.getText().toString();
        if (name.isEmpty()) {
            dictionaryName.setError(getString(R.string.dictionaryError));
            return false;
        }
        return true;
    }

    /**
     * creates a dicitonary object in our dictionary database
     * @param name dicitonary name
     * @param description description of the dictionary object
     * @param color color of the dictionary
     */
    public void createDictionary(String name, String description, String color) {
        final Dictionary dictionary = new Dictionary(name, description, color);
        dictionary.setCreator(currentUserID);
        if (!validate()) {
            Toast.makeText(getBaseContext(), getString(R.string.correctErrors), Toast.LENGTH_LONG).show();
            return;
        }
        database.addDictionaryToFirebase(dictionary, currentUserID);
        database.addDictionaryToUser(dictionary.getID(), dictionary.getName(), currentUserID);
    }

}
