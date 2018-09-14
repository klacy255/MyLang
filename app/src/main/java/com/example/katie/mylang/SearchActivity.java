package com.example.katie.mylang;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * SearchActivity.java
 * Purpose: activity dealing searching all dictionaries in firebase and adding them to a users
 * dictionaries
 *
 * @author katie
 * @version 1.0 4/10/17.
 */

public class SearchActivity extends AppCompatActivity {

    TextView searchtitle;
    SearchView search;
    Database database;
    DatabaseReference dictionaryEndPoint;
    String currentUserID;
    private RecyclerView mRecyclerView;
    private ArrayList<Dictionary> dictionaryAdapterItems;
    private DictionaryAdapter dictionaryAdapter;
    boolean childrenReturned = false;

    private final String ERROR = "ERROR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //set up toolbar and current User ID
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //instantiate database and get references
        database = new Database();
        dictionaryEndPoint = database.dictionaryCloudEndPoint;
        dictionaryAdapterItems = new ArrayList<>();

        //set up recycler view adapter
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewSearchDictionaries);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dictionaryAdapter = new DictionaryAdapter(dictionaryAdapterItems, false);
        mRecyclerView.setAdapter(dictionaryAdapter);

        //find views and assign their typefaces
        searchtitle = (TextView) findViewById(R.id.searchTitleTextView);
        final Typeface font = Typeface.createFromAsset(getAssets(), "bebas_neue/TTF's/BebasNeue Regular.ttf");
        searchtitle.setTypeface(font);
        search = (SearchView) findViewById(R.id.searchView);

        //search query for search view. finds all children of dictionary database with title matching a query name
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                final String finalQuery = query;

                dictionaryEndPoint.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        childrenReturned = true;
                        if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                            try{
                                Dictionary model = dataSnapshot.getValue(Dictionary.class);
                                if(model.getName().toUpperCase().equals(finalQuery.toUpperCase())) {
                                    dictionaryAdapterItems.add(model);
                                    mRecyclerView.scrollToPosition(dictionaryAdapterItems.size() - 1);
                                    dictionaryAdapter.notifyItemInserted(dictionaryAdapterItems.size() - 1);
                                }
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

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                if(!childrenReturned) {
                    Toast.makeText(SearchActivity.this, "no results returned", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
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
                Intent intent = new Intent(SearchActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
