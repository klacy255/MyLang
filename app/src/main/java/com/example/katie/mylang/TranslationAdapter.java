package com.example.katie.mylang;

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

/**
 * TranslationAdapter.java
 * Purpose: Adapter for Translation objects. used in dictionary detail activity.
 *
 * @author katie
 * @version 1.0 4/10/17.
 */

class TranslationAdapter extends RecyclerView.Adapter<TranslationAdapter.ViewHolder> implements PopupMenu.OnMenuItemClickListener {

    private ArrayList<Translation> mDataSet;
    private final Database database = new Database();
    private ViewGroup parent;
    private Translation deleted;
    private String dictionaryID;
    private boolean hasPermission;

    /**
     * translation adpater constructor assigns dataset
     *
     * @param dataSet - arraylist of translations
     */
    TranslationAdapter(ArrayList<Translation> dataSet) {
        mDataSet = dataSet;
    }

    /**
     * sets hasPermission instance variable
     *
     * @param permission - boolean representing if a user has permission to write to teh database
     */
    void setHasPermission(boolean permission) {
        hasPermission = permission;
    }

    /**
     * sets dictionaryID instance variable
     *
     * @param dictionaryID - string representing the dicitonaryID translation objects exist in
     */
    void setDictionaryID(String dictionaryID) {
        this.dictionaryID = dictionaryID;
    }



    /**
     * inflates a view object into the holder and returns that holder
     *
     * @param parent -  parent viewgroup
     * @param viewType - required from method
     * @return a TranslationAdapter View holder
     */
    @Override
    public TranslationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        final View translationListItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.translation_list_item, parent, false);
        return new ViewHolder(translationListItem);
    }

    /**
     * creates the view and sets values to translation objects values
     *
     * @param holder the holder we want to set values of
     * @param position - position in the dataset our object is
     */
    @Override
    public void onBindViewHolder(TranslationAdapter.ViewHolder holder, int position) {
        final Translation translation = mDataSet.get(position);
        holder.originalTextView.setText(translation.getInitial());
        holder.translatedTextView.setText(translation.getTranslated());
        //on a long click a user is prompted to delete a translation
        //if they have permission then they can delete
        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(hasPermission) {
                    PopupMenu popupMenu = new PopupMenu(parent.getContext(), view);
                    popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                    popupMenu.getMenu().add(R.string.delete);
                    deleted = translation;
                    popupMenu.setOnMenuItemClickListener(TranslationAdapter.this);
                    popupMenu.show();
                }
                return false;
            }
        });
    }

    /**
     * this is called when a user presses the delete option. it deletes the translation from the dictionary
     *
     * @param item - a singular item representing a delete operation
     * @return a success value
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        database.deleteTranslationFromFirebase(deleted, dictionaryID);
        Toast.makeText(parent.getContext(), R.string.deleted_translation, Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }



    /**
     * viewholder class to represent the view of a translation object
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView originalTextView;
        TextView translatedTextView;
        View view;

        ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            originalTextView = (TextView) itemView.findViewById(R.id.originalTextView);
            translatedTextView = (TextView) itemView.findViewById(R.id.translatedTextView);

        }
    }
}
