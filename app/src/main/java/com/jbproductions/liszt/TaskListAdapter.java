package com.jbproductions.liszt;

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

    //// Member Attributes
    private SelectionTracker<Long> mSelectionTracker;
    private final ItemCheckListener itemCheckListener;

    //// Constructor Methods

    protected TaskListAdapter(ItemCheckListener itemCheckListener,
                              @NonNull DiffUtil.ItemCallback<Task> diffCallback) {
        super(diffCallback);
        this.itemCheckListener = itemCheckListener;
        setHasStableIds(true);
    }

    /**
     * Simple interface to pass a callback to the Adapter's parent Fragment when a list item is checked/unchecked.
     */
    interface ItemCheckListener {
        void onTaskChecked(Task task);
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
            ((TaskViewHolder) viewHolder).bind(task, isSelected, itemCheckListener);
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
            return oldTask.getId() == newTask.getId()
                    && oldTask.getName().equals(newTask.getName())
                    && oldTask.dueDateEquals(newTask.getDueDate());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Task oldTask, @NonNull Task newTask) {
            return oldTask.equals(newTask);
        }
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {

        //// Member Attributes
        private final LinearLayout taskLayout;
        private final TextView taskCardTitle;
        private final TextView taskCardSubtitle;
        private final CheckBox checkBox;

        //// Constructor Methods

        public TaskViewHolder(View view) {
            // Stores the view in a public final variable that can be used to access
            // the context from any TaskFragmentViewHolder instance.
            super(view);

            // Initialize member attributes for each view in the Task fragment.
            taskLayout = view.findViewById(R.id.task_layout);
            taskCardTitle = view.findViewById(R.id.task_text);
            taskCardSubtitle = view.findViewById(R.id.task_subtext);
            checkBox = view.findViewById(R.id.task_checkbox);
        }

        private void bind(Task task, boolean isSelected, ItemCheckListener itemCheckListener) {
            taskCardTitle.setText(task.getName());
            checkBox.setChecked(task.getComplete());
            taskLayout.setActivated(isSelected);

            if (task.getDueDate() == null) {
                taskCardSubtitle.setVisibility(View.GONE);
            } else {
                taskCardSubtitle.setVisibility(View.VISIBLE);
                taskCardSubtitle.setText(DetailsFragment.getReadableDate(task.getDueDate()));
            }

            checkBox.setOnClickListener(view -> {
                boolean status = checkBox.isChecked();
                task.setComplete(status);

                itemCheckListener.onTaskChecked(task);
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
