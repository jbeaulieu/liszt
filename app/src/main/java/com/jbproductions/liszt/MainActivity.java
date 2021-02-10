package com.jbproductions.liszt;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
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

public class MainActivity extends AppCompatActivity {

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
                mViewModel.printDeletedSelection();
                Log.d("myTag", "Button Press Captured: List of Selected Tasks...");
            }
        });

        deleteTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.emptyDeleteSelection();
                Log.d("myTag", "Button Press Captured: Selected Tasks Deleted");
            }
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

        final TaskListAdapter archiveAdapter = new TaskListAdapter(mTaskClickInterface, new TaskListAdapter.TaskDiff());
        ArchiveRecyclerView.setAdapter(archiveAdapter);
        ArchiveRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mViewModel.getCompleteTasks().observe(this, tasks -> {
            archiveAdapter.submitList(tasks);
        });

        ListItemRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                ListItemRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                //Values are passing to activity & to fragment as well
                Toast.makeText(MainActivity.this, "Single Click on position        :" + position,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                String thisTaskName = mViewModel.getOpenTasks().getValue().get(position).getTask();
                Boolean thisTaskStatus = mViewModel.getOpenTasks().getValue().get(position).getStatus();
                Task thisTask = new Task(thisTaskName, thisTaskStatus);
                if(mViewModel.isInDeleteSelection(thisTask)) {
                    view.setBackgroundColor(0xFF018786);
                    mViewModel.removeFromDeleteSelection(thisTask);
                }
                else{
                    view.setBackgroundColor(0xFF00FF00);
                    mViewModel.addToDeleteSelection(thisTask);
                }
                Log.d("TestCodeTag", "Value: " + mViewModel.getOpenTasks().getValue().get(position).getTask());
                Toast.makeText(MainActivity.this, "Long press on position :" + position,
                        Toast.LENGTH_LONG).show();
            }
        }));
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

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener) {

            this.clicklistener = clicklistener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recycleView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clicklistener != null) {
                        clicklistener.onLongClick(child, recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clicklistener != null && gestureDetector.onTouchEvent(e)) {
                clicklistener.onClick(child, rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}