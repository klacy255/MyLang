package com.example.katie.mylang;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;

/**
 * DictionaryAdapter.java
 * Purpose: Adapter for dictionary objects. used in search and dictionary activity.
 *
 * @author katie
 * @version 1.0 4/10/17.
 */
class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.ViewHolder> implements PopupMenu.OnMenuItemClickListener {

    private ArrayList<Dictionary> mDataSet;
    private ViewGroup parent;
    private boolean dictionaryActivity;
    private final Database database = new Database();
    private final String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String dictionaryName;

    /**
     * constructs a dictionary adapter given an arraylist of dictionary objects and a boolean
     *
     * @param dataSet - the list of items we are going to prepare
     * @param dictionaryActivity - boolean to determine what onClick action this adapter will use
     *                           true implies that the dictioanryactvity called it.
     *                           false implies the translateactivity called it.
     */
    DictionaryAdapter(ArrayList<Dictionary> dataSet, boolean dictionaryActivity) {
        mDataSet = dataSet;
        this.dictionaryActivity = dictionaryActivity;
    }

    /**
     * inflates a view object into the holder and returns that holder
     *
     * @param parent -  parent viewgroup
     * @param viewType - required from method
     * @return a Viewholder object - a dictionary item
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        final View dictionaryListItem = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.dictionary_list_item, parent, false);
        return new ViewHolder(dictionaryListItem);
    }

    /**
     * creates view and defines onClick listeners
     *
     * @param holder the holder we want to set values of
     * @param position - position in the dataset our object is
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Dictionary dictionary = mDataSet.get(position);
        holder.titleTextView.setText(dictionary.getName());
        holder.descriptionTextView.setText(dictionary.getDescription());
        switch(dictionary.getColor()) {
            case "blue":
                holder.view.setBackgroundColor(
                        ContextCompat.getColor(parent.getContext(), R.color.blue));
                break;
            case "red":
                holder.view.setBackgroundColor(
                        ContextCompat.getColor(parent.getContext(), R.color.textColor));
                break;
            case "green":
                holder.view.setBackgroundColor(
                        ContextCompat.getColor(parent.getContext(), R.color.green));
                break;
            case "orange":
                holder.view.setBackgroundColor(
                        ContextCompat.getColor(parent.getContext(), R.color.orange));
                break;
        }
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dictionaryActivity) {
                    Intent intent = new Intent(view.getContext(), DictionaryDetailActivity.class);
                    intent.putExtra("CREATOR", dictionary.getCreator());
                    intent.putExtra("ID", dictionary.getID());
                    intent.putExtra("NAME", dictionary.getName());
                    view.getContext().startActivity(intent);
                } else {
                    Intent intent = new Intent(view.getContext(), DictionaryActivity.class);
                    database.addDictionaryToUser(dictionary.getID(), dictionary.getName(), currentUserID);
                    Toast.makeText(parent.getContext(), R.string.added_dictionary_success, Toast.LENGTH_LONG).show();
                    view.getContext().startActivity(intent);
                }
            }
        });
        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(dictionaryActivity) {
                    PopupMenu popupMenu = new PopupMenu(parent.getContext(), view);
                    popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                    popupMenu.getMenu().add(R.string.delete);
                    dictionaryName = dictionary.getName();
                    popupMenu.setOnMenuItemClickListener(DictionaryAdapter.this);
                    popupMenu.show();
                }
                return false;
            }
        });
    }

    /**
     * deletes a dictionary from a user and displays a success message
     * @param item - in this case a delete button
     * @return true when task is complete
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        database.deleteDictionaryFromUser(dictionaryName, currentUserID);
        Toast.makeText(parent.getContext(), R.string.deleted_dictionary, Toast.LENGTH_SHORT).show();
        return true;
    }

    /**
     * gets the number of items in the dataset
     *
     * @return the size of mDataset
     */
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    /**
     * private class viewholder used as recyclerview. contains the constructor for a view
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        View view;

        ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            descriptionTextView = (TextView) itemView.findViewById(R.id.descriptionTextView);
        }
    }
}
