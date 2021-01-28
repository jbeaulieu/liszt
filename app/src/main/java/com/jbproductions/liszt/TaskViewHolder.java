package com.jbproductions.liszt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

class TaskViewHolder extends RecyclerView.ViewHolder {
    private final TextView textView;
    private final CheckBox checkBox;

    private TaskViewHolder(View view) {
        super(view);
        textView = (TextView) view.findViewById(R.id.task_text);
        checkBox = (CheckBox) view.findViewById(R.id.task_checkbox);
    }

    public void bind(String text) { textView.setText(text); }

    public TextView getTextView() {
        return textView;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    static TaskViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_task, parent, false);
        return new TaskViewHolder(view);
    }
}
