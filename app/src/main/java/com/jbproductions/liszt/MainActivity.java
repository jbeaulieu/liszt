package com.jbproductions.liszt;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.Selection;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StableIdKeyProvider;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    SelectionTracker<Long> mSelectionTracker;
    SelectionTracker<Long> mSelectionTracker2;
    TaskClickInterface mTaskClickInterface;
    private ImageButton addTaskButton;
    private ImageButton editTaskButton;
    private ImageButton deleteTaskButton;
    private EditText newTaskText;
    private ViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        RecyclerView ListItemRecyclerView = findViewById(R.id.ListItemRecyclerView);
        RecyclerView ArchiveRecyclerView = findViewById(R.id.ArchiveRecyclerView);
        setSupportActionBar(toolbar);
        addTaskButton = (ImageButton) findViewById(R.id.add_task_button);
        editTaskButton = (ImageButton) findViewById(R.id.edit_task_button);
        deleteTaskButton = (ImageButton) findViewById(R.id.delete_task_button);

        newTaskText = (EditText) findViewById(R.id.newTaskText);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task thisTask = new Task(newTaskText.getText().toString(), false);
                mViewModel.insert(thisTask);
                newTaskText.getText().clear();
                Log.d("myTag", "Button Press Captured: " + newTaskText.getText());
            }
        });

        editTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Selection<Long> selectedItems = mSelectionTracker.getSelection();

                Iterator<Long> iterator = selectedItems.iterator();
                while(iterator.hasNext()) {
                    Log.d("NEXT", String.valueOf(iterator.next()));
                }
            }
        });

        deleteTaskButton.setOnClickListener(view -> {
            Selection<Long> selectedItems = mSelectionTracker.getSelection();
            for (Long item : selectedItems) {
                mViewModel.deleteTaskByID(item);
            }
            mSelectionTracker.clearSelection();
        });

        newTaskText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    Task thisTask = new Task(newTaskText.getText().toString(), false);
                    mViewModel.insert(thisTask);
                    newTaskText.getText().clear();
                    Log.d("myTag", "Keyboard Enter Captured: " + newTaskText.getText());
                    return true;
                }
                return false;
            }
        });

        mViewModel = new ViewModelProvider(this).get(ViewModel.class);

        mTaskClickInterface = new TaskClickInterface() {
            @Override
            public void OnCheckCallback(Task task) {
                mViewModel.update(task);
                Log.d("HERE", "HERE");
            }
        };

        final TaskListAdapter adapter = new TaskListAdapter(mTaskClickInterface, new TaskListAdapter.TaskDiff());
        ListItemRecyclerView.setAdapter(adapter);
        ListItemRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mViewModel.getOpenTasks().observe(this, tasks -> {
            adapter.submitList(tasks);
        });

        mSelectionTracker = new SelectionTracker.Builder<Long>(
                "selection-id",
                ListItemRecyclerView,
                new TaskKeyProvider(ListItemRecyclerView),
                new TaskDetailsLookup(ListItemRecyclerView),
                StorageStrategy.createLongStorage()
        ).withSelectionPredicate(SelectionPredicates.<Long>createSelectAnything()).build();

        adapter.setSelectionTracker(mSelectionTracker);

        final TaskListAdapter archiveAdapter = new TaskListAdapter(mTaskClickInterface, new TaskListAdapter.TaskDiff());
        ArchiveRecyclerView.setAdapter(archiveAdapter);
        ArchiveRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mViewModel.getCompleteTasks().observe(this, tasks -> {
            archiveAdapter.submitList(tasks);
        });

        mSelectionTracker2 = new SelectionTracker.Builder<Long>(
                "selection-id",
                ArchiveRecyclerView,
                new TaskKeyProvider(ArchiveRecyclerView),
                new TaskDetailsLookup(ArchiveRecyclerView),
                StorageStrategy.createLongStorage()
        ).withSelectionPredicate(SelectionPredicates.<Long>createSelectAnything()).build();

        archiveAdapter.setSelectionTracker(mSelectionTracker2);

        mSelectionTracker.addObserver(new SelectionTracker.SelectionObserver<Long>() {
            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();
                int selectionSize = mSelectionTracker.getSelection().size();
                switch (selectionSize) {
                    case 0:
                        deleteTaskButton.setVisibility(View.GONE);
                        editTaskButton.setVisibility(View.GONE);
                        break;
                    case 1:
                        deleteTaskButton.setVisibility(View.VISIBLE);
                        editTaskButton.setVisibility(View.VISIBLE);
                        break;
                    default:
                        deleteTaskButton.setVisibility(View.VISIBLE);
                        editTaskButton.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mSelectionTracker.hasSelection()) {
            mSelectionTracker.clearSelection();
        } else {
            super.onBackPressed();
        }
    }

    private static class TaskKeyProvider extends ItemKeyProvider<Long> {

        private final RecyclerView mRecyclerView;

        public TaskKeyProvider(RecyclerView recyclerView) {
            super(SCOPE_CACHED);
            this.mRecyclerView = recyclerView;
        }

        @Nullable
        @Override
        public Long getKey(int position) {
            return mRecyclerView.getAdapter().getItemId(position);
        }

        @Override
        public int getPosition(@NonNull Long key) {
            RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForItemId(key);
            return viewHolder.getLayoutPosition();
        }
    }

    private static class TaskDetailsLookup extends ItemDetailsLookup<Long> {

        private final RecyclerView mRecyclerView;

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
}