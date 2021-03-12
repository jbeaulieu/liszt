package com.jbproductions.liszt;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.Selection;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Main application activity.
 */
public class MainActivity extends AppCompatActivity {

    SelectionTracker<Long> mSelectionTracker;
    TaskClickInterface mTaskClickInterface;
    private ImageButton editTaskButton;
    private ImageButton deleteTaskButton;
    private EditText newTaskText;
    private ViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        editTaskButton = (ImageButton) findViewById(R.id.edit_task_button);
        deleteTaskButton = (ImageButton) findViewById(R.id.delete_task_button);

        newTaskText = (EditText) findViewById(R.id.newTaskText);

        newTaskText.setOnEditorActionListener((view, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                Task thisTask = new Task(newTaskText.getText().toString(), false);
                mViewModel.insert(thisTask);
                newTaskText.getText().clear();
                Log.d("myTag", "Keyboard Enter Captured: " + newTaskText.getText());
                newTaskText.requestFocus();
                handled = true;
            }
            return handled;
        });


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            newTaskText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(newTaskText, InputMethodManager.SHOW_IMPLICIT);
        });

        editTaskButton.setOnClickListener(view -> {
            Selection<Long> selectedItems = mSelectionTracker.getSelection();

            TextEditInterface getAlertDialogText = new TextEditInterface() {
                @Override
                public void onTextEntered(String text, long id) {
                    Task thisTask;
                    thisTask = mViewModel.getTaskById(id);
                    thisTask.setName(text);
                    mViewModel.update(thisTask);
                }
            };

            for (Long selectedItem : selectedItems) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("New Task Name");

                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                builder.setView(input);

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getAlertDialogText.onTextEntered(input.getText().toString(), selectedItem);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                mSelectionTracker.clearSelection();
            }
        });

        deleteTaskButton.setOnClickListener(view -> {
            Selection<Long> selectedItems = mSelectionTracker.getSelection();
            for (Long item : selectedItems) {
                mViewModel.deleteTaskById(item);
            }
            mSelectionTracker.clearSelection();
        });

        mViewModel = new ViewModelProvider(this).get(ViewModel.class);

        mTaskClickInterface = task -> {
            mViewModel.update(task);
            Log.d("HERE", "HERE");
        };

        RecyclerView listRecyclerView = findViewById(R.id.list_recycler_view);
        final TaskListAdapter adapter = new TaskListAdapter(mTaskClickInterface, new TaskListAdapter.TaskDiff());
        listRecyclerView.setAdapter(adapter);
        listRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mViewModel.getAllTasks().observe(this, tasks -> {
            int dividerIndex = tasks.size();
            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                if (task.getId() == -1) {
                    break;
                } else if (task.getComplete()) {
                    Task divider = new Task("", false);
                    divider.setId(-1);
                    tasks.add(i, divider);
                    break;
                }
            }

            adapter.submitList(tasks);
        });

        SelectionTracker.SelectionPredicate<Long> noDividerSelection = new SelectionTracker.SelectionPredicate<Long>() {
            @Override
            public boolean canSetStateForKey(@NonNull Long key, boolean nextState) {
                return key != -1;
            }

            @Override
            public boolean canSetStateAtPosition(int position, boolean nextState) {
                return true;
            }

            @Override
            public boolean canSelectMultiple() {
                return true;
            }
        };

        mSelectionTracker = new SelectionTracker.Builder<Long>(
                "selection-id",
                listRecyclerView,
                new TaskKeyProvider(listRecyclerView),
                new TaskDetailsLookup(listRecyclerView),
                StorageStrategy.createLongStorage()
        ).withSelectionPredicate(noDividerSelection).build();

        adapter.setSelectionTracker(mSelectionTracker);

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

    interface TextEditInterface {
        void onTextEntered(String text, long id);
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
                final RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(view);
                if (holder instanceof TaskListAdapter.TaskViewHolder) {
                    final TaskListAdapter.TaskViewHolder taskViewHolder = (TaskListAdapter.TaskViewHolder) holder;
                    return new ItemDetailsLookup.ItemDetails<Long>() {
                        @Override
                        public int getPosition() {
                            return holder.getAdapterPosition();
                        }

                        @NonNull
                        @Override
                        public Long getSelectionKey() {
                            return taskViewHolder.getItemId();
                        }
                    };
                } else if (holder instanceof TaskListAdapter.DividerViewHolder) {
                    final TaskListAdapter.DividerViewHolder taskViewHolder = (TaskListAdapter.DividerViewHolder) holder;
                    return new ItemDetailsLookup.ItemDetails<Long>() {
                        @Override
                        public int getPosition() {
                            return holder.getAdapterPosition();
                        }

                        @NonNull
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