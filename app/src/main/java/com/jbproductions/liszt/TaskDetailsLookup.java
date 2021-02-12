package com.jbproductions.liszt;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

import com.jbproductions.liszt.TaskListAdapter;

public class TaskDetailsLookup extends ItemDetailsLookup<Long> {

    private RecyclerView mRecyclerView;

    public TaskDetailsLookup(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    @Nullable
    @Override
    public ItemDetails<Long> getItemDetails(@NonNull MotionEvent event) {
        View view = mRecyclerView.findChildViewUnder(event.getX(), event.getY());
        if (view != null) {
            final RecyclerView.ViewHolder viewHolder = mRecyclerView.getChildViewHolder(view);
            if (viewHolder instanceof TaskListAdapter.TaskViewHolder) {
                final TaskListAdapter.TaskViewHolder taskViewHolder = (TaskListAdapter.TaskViewHolder) viewHolder;
                return new ItemDetailsLookup.ItemDetails<Long>() {
                    @Override
                    public int getPosition() {
                        return viewHolder.getAdapterPosition();
                    }

                    @Nullable
                    @Override
                    public Long getSelectionKey() {
                        return taskViewHolder.getItemId();
                    }
                };
            }
        }
        return null;
    }
}