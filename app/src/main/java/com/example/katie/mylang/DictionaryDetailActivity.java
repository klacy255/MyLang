package com.example.katie.mylang;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * DictionaryDetailActivity.java
 * Purpose: activity dealing with vieiwing translations in a specific dictionary.
 *
 * @author katie
 * @version 1.0 4/10/17.
 */
public class DictionaryDetailActivity extends AppCompatActivity {

    DatabaseReference translationDataBase;
    DatabaseReference mDatabase;
    Database database;
    private TranslationAdapter translationAdapter;
    private ArrayList<Translation> translationAdapterItems;
    private RecyclerView mRecyclerView;

    TextView dictionariesTitle;
    ImageButton addTranslation;
    final String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary_detail);
        final Typeface font = Typeface.createFromAsset(getAssets(), "bebas_neue/TTF's/BebasNeue Regular.ttf");

        //finding view and setting them to string extras passed from dictionary Activity
        dictionariesTitle = (TextView) findViewById(R.id.dictionaryTitle);
        final String ID = getIntent().getStringExtra("ID");
        final String name = getIntent().getStringExtra("NAME");
        final String creator = getIntent().getStringExtra("CREATOR");
        dictionariesTitle.setText(name);
        dictionariesTitle.setTypeface(font);
        addTranslation = (ImageButton) findViewById(R.id.btn_addNewTranslation);

        //boolean used in adapter to tell if this user has write permissions
        final boolean hasPermission = creator.equals(currentUserID);

        //passing the dictionary name and ID as extras and starting translate activity
        addTranslation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hasPermission) {
                    Intent intent = new Intent(DictionaryDetailActivity.this, TranslateActivity.class);
                    intent.putExtra("DICTIONARY_NAME", name);
                    intent.putExtra("DICTIONARY_ID", ID);
                    startActivity(intent);
                } else {
                    Toast.makeText(getBaseContext(), "Can not add if you are not the creator", Toast.LENGTH_SHORT).show();
                }

            }
        });
        //set up recycler view and dictionary adapter
        translationAdapterItems = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewTranslations);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        translationAdapter = new TranslationAdapter(translationAdapterItems);
        mRecyclerView.setAdapter(translationAdapter);
        translationAdapter.setDictionaryID(ID);
        translationAdapter.setHasPermission(hasPermission);

        //set up toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //setUpFirebase
        database = new Database();
        mDatabase = database.mDatabase;
        translationDataBase = database.mDatabase.child(getString(R.string.dictionaries_endpoint))
                .child(ID).child(getString(R.string.translations_endpoint));

        //populate recyclerview with translation objects
        translationDataBase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    try{
                        Translation model = dataSnapshot.getValue(Translation.class);
                        translationAdapterItems.add(model);
                        mRecyclerView.scrollToPosition(translationAdapterItems.size() - 1);
                        translationAdapter.notifyItemInserted(translationAdapterItems.size() - 1);
                    } catch (Exception ex) {
                        Log.e("ERROR", ex.getMessage());
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //reload data after deletion
                Intent intent = new Intent(DictionaryDetailActivity.this, DictionaryDetailActivity.class);
                startActivity(intent);
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
                Intent intent = new Intent(DictionaryDetailActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
