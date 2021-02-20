package com.jbproductions.liszt;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class TaskListAdapter extends ListAdapter<Task, TaskListAdapter.TaskViewHolder> {

    //// Member Attributes
    private SelectionTracker<Long> mSelectionTracker;
    private final TaskClickInterface mTaskClickInterface;

    //// Constructor Methods

    protected TaskListAdapter(TaskClickInterface mTaskClickInterface, @NonNull DiffUtil.ItemCallback<Task> diffCallback) {
        super(diffCallback);
        this.mTaskClickInterface = mTaskClickInterface;
        setHasStableIds(true);
    }

    public void setSelectionTracker(SelectionTracker<Long> selectionTracker) {
        this.mSelectionTracker = selectionTracker;
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
    public void onBindViewHolder(TaskViewHolder viewHolder, int position) {

        Task task = getItem(position);

        viewHolder.textView.setText(task.getName());
        viewHolder.checkBox.setChecked(task.getStatus());
        viewHolder.textView.setActivated(mSelectionTracker.isSelected((long) task.getId()));

        viewHolder.checkBox.setOnClickListener(view -> {
            String taskName = viewHolder.textView.getText().toString();
            boolean status = viewHolder.checkBox.isChecked();
            task.setStatus(status);

            if (viewHolder.checkBox.isChecked()) {
                Log.d("myTag", "Task: " + taskName + " -> selected.");
            } else {
                Log.d("myTag", "Task: " + taskName + " -> un-selected.");
            }

            mTaskClickInterface.OnCheckCallback(task);
        });
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    // Compares whether two Tasks visual representations are the same
    static class TaskDiff extends DiffUtil.ItemCallback<Task> {

        @Override
        public boolean areItemsTheSame(@NonNull Task oldTask, @NonNull Task newTask) {
            return oldTask.getName().equals(newTask.getName());
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
