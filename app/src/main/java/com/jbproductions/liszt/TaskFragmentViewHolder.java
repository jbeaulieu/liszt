package com.jbproductions.liszt;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

// Provides a direct reference for each view in a Task
// Caches potentially expensive view.findViewById(int) results
class TaskFragmentViewHolder extends RecyclerView.ViewHolder {

    //// Member Attributes

    private final TextView textView;
    private final CheckBox checkBox;

    //// Constructor Methods

    private TaskFragmentViewHolder(View view) {
        // Stores the view in a public final variable that can be used to access
        // the context from any TaskFragmentViewHolder instance.
        super(view);

        // Initialize member attributes for each view in the Task fragment.
        textView = (TextView) view.findViewById(R.id.task_text);
        checkBox = (CheckBox) view.findViewById(R.id.task_checkbox);
    }

    static TaskFragmentViewHolder create(ViewGroup parent) {
        // Obtain the LayoutInflater from the parent's context and use it to
        // inflate a view hierarchy from the fragment_task xml to obtain a
        // reference to the LinearLayout View. Pass View to ViewHolder constructor.
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_task, parent, false);
        return new TaskFragmentViewHolder(view);
    }

    //// Getter & Setter & Bind Methods

    public void bind(String text, boolean status) {
        textView.setText(text);
        checkBox.setChecked(status);
    }


    public TextView getTextView() {
        return textView;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }


}
