package com.jbproductions.liszt;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ListAdapter implementation to dynamically display a single task list.
 */
public class TaskListAdapter extends ListAdapter<Task, RecyclerView.ViewHolder> {

    private final TaskClickInterface mTaskClickInterface;
    //// Member Attributes
    private SelectionTracker<Long> mSelectionTracker;

    //// Constructor Methods

    protected TaskListAdapter(TaskClickInterface taskClickInterface,
                              @NonNull DiffUtil.ItemCallback<Task> diffCallback) {
        super(diffCallback);
        this.mTaskClickInterface = taskClickInterface;
        setHasStableIds(true);
    }

    public void setSelectionTracker(SelectionTracker<Long> selectionTracker) {
        this.mSelectionTracker = selectionTracker;
    }

    // Create direct references for Task subviews (invoked by the layout manager)
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        switch (viewType) {
            case ViewTypes.Divider:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.task_archive_header, parent, false);
                return new DividerViewHolder(view);
            case ViewTypes.TaskView:
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_task, parent, false);
                return new TaskViewHolder(view);
        }
    }

    // Set Task attributes based on stored data
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        Task task = getItem(position);

        if (getItemViewType(position) == ViewTypes.TaskView) {
            boolean isSelected = mSelectionTracker.isSelected((long) task.getId());
            ((TaskViewHolder) viewHolder).bind(task, isSelected, mTaskClickInterface);
        }
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).getId() == -1) {
            return ViewTypes.Divider;
        } else {
            return ViewTypes.TaskView;
        }
    }

    // Compares whether two Tasks visual representations are the same
    static class TaskDiff extends DiffUtil.ItemCallback<Task> {

        @Override
        public boolean areItemsTheSame(@NonNull Task oldTask, @NonNull Task newTask) {
            return oldTask.getId() == newTask.getId() && oldTask.getName().equals(newTask.getName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Task oldTask, @NonNull Task newTask) {
            return oldTask.equals(newTask);
        }
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {

        //// Member Attributes
        private final LinearLayout taskLayout;
        private final TextView textView;
        private final CheckBox checkBox;

        //// Constructor Methods

        public TaskViewHolder(View view) {
            // Stores the view in a public final variable that can be used to access
            // the context from any TaskFragmentViewHolder instance.
            super(view);

            // Initialize member attributes for each view in the Task fragment.
            taskLayout = (LinearLayout) view.findViewById(R.id.task_layout);
            textView = (TextView) view.findViewById(R.id.task_text);
            checkBox = (CheckBox) view.findViewById(R.id.task_checkbox);
        }

        private void bind(Task task, boolean isSelected, TaskClickInterface clickInterface) {
            textView.setText(task.getName());
            checkBox.setChecked(task.getComplete());
            taskLayout.setActivated(isSelected);

            checkBox.setOnClickListener(view -> {
                String taskName = textView.getText().toString();
                boolean status = checkBox.isChecked();
                task.setComplete(status);

                if (checkBox.isChecked()) {
                    Log.d("myTag", "Task: " + taskName + " -> selected.");
                } else {
                    Log.d("myTag", "Task: " + taskName + " -> un-selected.");
                }

                clickInterface.onCheckCallback(task);
            });
        }
    }

    static class DividerViewHolder extends RecyclerView.ViewHolder {

        public DividerViewHolder(View view) {
            super(view);
        }

    }

    private static class ViewTypes {
        public static final int TaskView = 1;
        public static final int Divider = 2;
    }
}
