package com.jbproductions.liszt;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class TaskListAdapter extends ListAdapter<Task, TaskListAdapter.TaskViewHolder> {

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
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_task, parent, false);
        return new TaskViewHolder(view);
    }

    // Set Task attributes based on stored data
    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {

        Task current = getItem(position);
        holder.textView.setText(current.getTask());
        holder.checkBox.setChecked(current.getStatus());

        holder.checkBox.setOnClickListener(view -> {
            String taskName = holder.textView.getText().toString();
            boolean status = holder.checkBox.isChecked();

            if (holder.checkBox.isChecked()) {
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

    static class TaskViewHolder extends RecyclerView.ViewHolder {

        //// Member Attributes

        private final TextView textView;
        private final CheckBox checkBox;

        //// Constructor Methods

        public TaskViewHolder(View view) {
            // Stores the view in a public final variable that can be used to access
            // the context from any TaskFragmentViewHolder instance.
            super(view);

            // Initialize member attributes for each view in the Task fragment.
            textView = (TextView) view.findViewById(R.id.task_text);
            checkBox = (CheckBox) view.findViewById(R.id.task_checkbox);
        }
    }
}
