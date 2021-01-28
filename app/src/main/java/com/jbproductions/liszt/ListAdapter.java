package com.jbproductions.liszt;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private String[] listItems;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textView = (TextView) view.findViewById(R.id.task_text);
            checkBox = (CheckBox) view.findViewById(R.id.task_checkbox);
        }

        public TextView getTextView() {
            return textView;
        }
        public CheckBox getCheckBox() {
            return checkBox;
        }

    }

    public ListAdapter(String[] items) {
        listItems = items;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.getTextView().setText(listItems[position]);

        viewHolder.getCheckBox().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if(viewHolder.getCheckBox().isChecked())
                {
                    Log.d("myTag", "Item " + viewHolder.getTextView().getText() + " is checked");
                }
                else
                {
                    Log.d("myTag", "Item " + viewHolder.getTextView().getText() + " is NOT checked");
                }

            }

        });

    }

    @Override
    public int getItemCount() {
        return listItems.length;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_task, viewGroup, false);

        return new ViewHolder(view);
    }

}
