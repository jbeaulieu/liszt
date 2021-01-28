package com.jbproductions.liszt;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class TaskListAdapter extends ListAdapter<Item, TaskViewHolder> {

    private String[] listItems;

    protected TaskListAdapter(@NonNull DiffUtil.ItemCallback<Item> diffCallback) {
        super(diffCallback);
    }


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */

/*    public TaskListAdapter(String[] items) {
        listItems = items;
    }*/

/*    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.getTextView().setText(listItems[position]);
    }*/

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return TaskViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Item current = getItem(position);
        holder.bind(current.getItem());

        holder.getCheckBox().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if(holder.getCheckBox().isChecked())
                {
                    Log.d("myTag", "Task: " + holder.getTextView().getText() + " -> selected.");
                }
                else
                {
                    Log.d("myTag", "Task: " + holder.getTextView().getText() + " -> un-selected.");
                }

            }

        });
    }

    static class WordDiff extends DiffUtil.ItemCallback<Item> {

        @Override
        public boolean areItemsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
            return oldItem.getItem().equals(newItem.getItem());
        }
    }

}
