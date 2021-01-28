package com.jbproductions.liszt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

class TaskViewHolder extends RecyclerView.ViewHolder {
    private final TextView textView;

    private TaskViewHolder(View view) {
        super(view);
        textView = (TextView) view.findViewById(R.id.checkbox_text);
    }

    public void bind(String text) { textView.setText(text); }

    public TextView getTextView() {
        return textView;
    }

    static TaskViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new TaskViewHolder(view);
    }
}
