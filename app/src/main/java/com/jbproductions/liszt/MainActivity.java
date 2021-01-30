package com.jbproductions.liszt;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    TaskClickInterface mTaskClickInterface;
    private ImageButton addTaskButton;
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
                Task newTask = new Task(newTaskText.getText().toString(), false);
                mViewModel.insert(newTask);
                Log.d("myTag", "Button Press Captured: " + newTaskText.getText());
            }
        });

        newTaskText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    Task newTask = new Task(newTaskText.getText().toString(), false);
                    mViewModel.insert(newTask);
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
}