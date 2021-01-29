package com.jbproductions.liszt;

import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

public class TaskListAdapter extends ListAdapter<Task, TaskFragmentViewHolder> {

    private final TaskClickInterface mTaskClickInterface;
    private String[] listTasks;

    protected TaskListAdapter(TaskClickInterface mTaskClickInterface, @NonNull DiffUtil.ItemCallback<Task> diffCallback) {
        super(diffCallback);
        this.mTaskClickInterface = mTaskClickInterface;
    }


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */

/*    public TaskListAdapter(String[] tasks) {
        listTasks = tasks;
    }*/

/*    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.getTextView().setText(listTasks[position]);
    }*/

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public TaskFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return TaskFragmentViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(TaskFragmentViewHolder holder, int position) {
        Task current = getItem(position);
        holder.bind(current.getTask(), current.getStatus());

        holder.getCheckBox().setOnClickListener(view -> {

            String taskName = holder.getTextView().getText().toString();
            boolean status = holder.getCheckBox().isChecked();

            if(holder.getCheckBox().isChecked()) {
                Log.d("myTag", "Task: " + taskName + " -> selected.");
            } else {
                Log.d("myTag", "Task: " + taskName + " -> un-selected.");
            }

            mTaskClickInterface.OnCheckCallback(new Task(taskName, status));

        });
    }

    static class WordDiff extends DiffUtil.ItemCallback<Task> {

        @Override
        public boolean areItemsTheSame(@NonNull Task oldTask, @NonNull Task newTask) {
            return oldTask == newTask;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Task oldTask, @NonNull Task newTask) {
            return oldTask.getTask().equals(newTask.getTask());
        }
    }

}
