package com.example.katie.mylang;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;


/**
 * DictionaryActivity.java
 * Purpose: activity dealing with viewing dictionaries. considered home page of the app
 *
 * @author katie
 * @version 1.0 4/10/17.
 */
public class DictionaryActivity  extends AppCompatActivity {

    DatabaseReference mDatabaseDictionaries;
    DatabaseReference mDatabase;
    DatabaseReference userDatabaseDictionaries;
    Database database;
    private DictionaryAdapter dictionaryAdapter;
    private ArrayList<Dictionary> dictionaryAdapterItems;
    private RecyclerView mRecyclerView;
    TextView dictionariesTitle;
    private static final String ERROR = "ERROR";


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionaries);

        //set up title and assign a font
        dictionariesTitle = (TextView) findViewById(R.id.dictionaryTitleView);
        dictionariesTitle.setText(R.string.action_dictionaries);
        final Typeface font = Typeface.createFromAsset(getAssets(), "bebas_neue/TTF's/BebasNeue Regular.ttf");
        dictionariesTitle.setTypeface(font);

        //set on click for creating a new dictionary
        ImageButton newDictionary = (ImageButton) findViewById(R.id.btn_addNewDictionary);
        newDictionary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DictionaryActivity.this, AddDictionaryActivity.class);
                startActivity(intent);
            }
        });

        //set up recycler view and dictionary adapter
        final String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //create dictionaryAdapter items and set up recycler view and adapter
        dictionaryAdapterItems = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewDictionaries);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dictionaryAdapter = new DictionaryAdapter(dictionaryAdapterItems, true);
        mRecyclerView.setAdapter(dictionaryAdapter);

        //set up toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //setUpFirebase
        database = new Database();
        mDatabase = database.mDatabase;
        mDatabaseDictionaries = database.dictionaryCloudEndPoint;
        userDatabaseDictionaries = database.userCloudEndpoint.child(currentUserID)
                .child(getString(R.string.dictionaries_endpoint));

        //create a query for a users dictionaries
        final Query queryRef = userDatabaseDictionaries.orderByKey();
        //handles using recyclerview and adding dictionary objects
        mDatabaseDictionaries.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                        try{
                            final Dictionary model = dataSnapshot.getValue(Dictionary.class);
                            queryRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot dictionaryName : dataSnapshot.getChildren()){
                                        if(dictionaryName.getKey().equals(model.getName())) {
                                            dictionaryAdapterItems.add(model);
                                            mRecyclerView.scrollToPosition(dictionaryAdapterItems.size() - 1);
                                            dictionaryAdapter.notifyItemInserted(dictionaryAdapterItems.size() - 1);
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        } catch (Exception ex) {
                            Log.e(ERROR, ex.getMessage());
                        }
                    }
                }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //reload data after deletion
                Intent intent = new Intent(DictionaryActivity.this, DictionaryActivity.class);
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
                Intent intent = new Intent(DictionaryActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
