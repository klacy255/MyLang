package com.example.katie.mylang;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;


import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * TranslateActivity.java
 * Purpose: activity dealing with creating translations and adding them to a users dictionary
 *
 * @author katie
 * @version 1.0 4/10/17.
 */

public class TranslateActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    Database database;
    DatabaseReference databaseDictionaries;
    TextView translateTitle;
    EditText originalText;
    TextView translatedText;
    Button translate;
    Button addToDictionary;
    Spinner originalLang;
    Spinner translatedLang;
    String translatedTextFinal;
    Translation translation;
    final Handler textViewHandler = new Handler();
    final ArrayList<String> dictionaryNames = new ArrayList<>();
    HashMap<String, String> dictionaryIDs = new HashMap<>();
    private API_KEY api = new API_KEY();
    String ID;
    String name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        //set up toolbar and assign current user id
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        final String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //find views and assign typefaces
        translateTitle = (TextView) findViewById(R.id.translateTitleView);
        final Typeface font = Typeface.createFromAsset(getAssets(), "bebas_neue/TTF's/BebasNeue Regular.ttf");
        translateTitle.setTypeface(font);
        originalText = (EditText) findViewById(R.id.originalEditText);
        translatedText = (TextView) findViewById(R.id.translatedTextView);
        translate = (Button) findViewById(R.id.btn_translate);
        translate.setOnClickListener(this);
        translate.setTypeface(font);
        originalLang = (Spinner) findViewById(R.id.originalLanguage);
        translatedLang = (Spinner) findViewById(R.id.translatedLanguage);

        //assign adapters to hold the array of strings in Language Enum
        originalLang.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Language.LANGUAGE_NAMES));
        translatedLang.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Language.LANGUAGE_NAMES));
        addToDictionary = (Button) findViewById(R.id.btn_addToDictionary);
        addToDictionary.setOnClickListener(this);
        addToDictionary.setTypeface(font);

        //set add to dictionary not clickable until a user has translated
        addToDictionary.setClickable(false);
        //if a user pressed translate form a dictionary detail the intent will have been passed
        // a dictionary ID and name
        ID = getIntent().getStringExtra("DICTIONARY_ID");
        name = getIntent().getStringExtra("DICTIONARY_NAME");

        //set up database and endpoints
        database = new Database();
        databaseDictionaries = database.dictionaryCloudEndPoint;

        //set value event listener to get all a users dictionary names and IDs
        databaseDictionaries.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Dictionary model = dataSnapshot.getValue(Dictionary.class);
                if(model.getCreator().equals(currentUserID)) {
                    String dictionaryName = model.getName();
                    dictionaryNames.add(dictionaryName);
                    dictionaryIDs.put(dictionaryName, model.getID());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * private class for creating translations. takes the first string in params as the string to
     * translate and the second string in the params as the language key found from the Language ENUM value
     * uses a handler to update the translated text view to display translated text.
     */
    private class GoogleTranslate extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            TranslateOptions options = TranslateOptions.newBuilder()
                    .setApiKey(api.API_KEY)
                    .build();
            Translate translate = options.getService();
            final com.google.cloud.translate.Translation translation =
                    translate.translate(params[0], Translate.TranslateOption.targetLanguage(params[1]));
            textViewHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (translatedText != null) {
                        translatedText.setText(translation.getTranslatedText());
                    }
                }
            });
            return null;
        }
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
                Intent intent = new Intent(TranslateActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * handles translate button click and add to dictionary click.
     * translate button click will create a translation object from the originalText view and the
     * translatedText view as well as the translated language spinner. upon click it will make add
     * to dictionary active.
     *
     * add to dictionary creates a popupMenu of the available dictionaries to add to. if we have a
     * name and ID from string extras it will only display this dictionary to add to (that was the
     * original intent)
     *
     * @param view - the view that has been clicked
     */
    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btn_translate) {
            String original = originalText.getText().toString();
            String language = translatedLang.getSelectedItem().toString();
            language = Language.valueOf(language).toString();
            new GoogleTranslate().execute(original, language);
            translatedTextFinal = translatedText.getText().toString();
            translation = new Translation(original, translatedTextFinal, language);
            addToDictionary.setBackgroundColor
                    (ContextCompat.getColor(TranslateActivity.this, R.color.textColor));
            addToDictionary.setClickable(true);

        } else if (i == R.id.btn_addToDictionary) {
            PopupMenu popupMenu = new PopupMenu(TranslateActivity.this, view);
            popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
            for(int j = 0; j < dictionaryNames.size(); j++) {
                popupMenu.getMenu().add(dictionaryNames.get(j));
            }
            if(name != null && ID != null) {
                popupMenu.getMenu().clear();
                popupMenu.getMenu().add(name);
            }
            popupMenu.setOnMenuItemClickListener(TranslateActivity.this);
            popupMenu.show();
        }
    }

    /**
     * this method takes in a menuItem, this item contains a dictionary name, from there it will add
     * a translation object to the specified dictionary name. afterwards it will set fields to blank
     * and set the addToDictionary button to its original unclickable state.
     *
     * @param item - clicked menu item
     * @return true if the task was completed
     */
    public boolean onMenuItemClick(MenuItem item) {
        translation.setTranslated(translatedText.getText().toString());
        String dictionaryName = item.toString();
        String dictionaryID = dictionaryIDs.get(dictionaryName);
        database.addTranslationtoFirebase(translation, dictionaryID);

        originalText.setText("");
        translatedText.setText("");
        Toast.makeText(TranslateActivity.this, R.string.added_success, Toast.LENGTH_SHORT).show();
        addToDictionary.setClickable(false);
        addToDictionary.setBackgroundColor(ContextCompat.getColor
                (TranslateActivity.this, R.color.colorPrimary));
        return true;
    }

}
