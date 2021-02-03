package com.jbproductions.liszt;

import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

public class TaskListAdapter extends ListAdapter<Task, TaskFragmentViewHolder> {

    //// Member Attributes

    private final TaskClickInterface mTaskClickInterface;

    //// Constructor Methods

    protected TaskListAdapter(TaskClickInterface mTaskClickInterface, @NonNull DiffUtil.ItemCallback<Task> diffCallback) {
        super(diffCallback);
        this.mTaskClickInterface = mTaskClickInterface;
    }

    // Create direct references for Task subviews (invoked by the layout manager)
    @NonNull
    @Override
    public TaskFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return TaskFragmentViewHolder.create(parent);
    }

    // Set Task attributes based on stored data
    @Override
    public void onBindViewHolder(TaskFragmentViewHolder holder, int position) {
        Task current = getItem(position);
        holder.bind(current.getTask(), current.getStatus());

        holder.getCheckBox().setOnClickListener(view -> {

            String taskName = holder.getTextView().getText().toString();
            boolean status = holder.getCheckBox().isChecked();

            if (holder.getCheckBox().isChecked()) {
                Log.d("myTag", "Task: " + taskName + " -> selected.");
            } else {
                Log.d("myTag", "Task: " + taskName + " -> un-selected.");
            }

            mTaskClickInterface.OnCheckCallback(new Task(taskName, status));

        });
    }

    // Compares whether two Tasks visual representations are the same
    static class TaskDiff extends DiffUtil.ItemCallback<Task> {

        @Override
        public boolean areItemsTheSame(@NonNull Task oldTask, @NonNull Task newTask) {
            return oldTask.getTask().equals(newTask.getTask());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Task oldTask, @NonNull Task newTask) {
            return oldTask.equals(newTask);
        }
    }

}
