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
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    private ImageButton addTaskButton;
    private EditText newTaskText;

    private ViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        RecyclerView ListItemRecyclerView = findViewById(R.id.ListItemRecyclerView);
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
                Log.d("myTag", "Button Press Captured: " + newTaskText.getText());
            }
        });
        String[] strDays = new String[4];

        strDays[0] = "Monday";
        strDays[1] = "Tuesday";
        strDays[2] = "Wednesday";
        strDays[3] = "Thursday";

        ListItemRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final TaskListAdapter adapter = new TaskListAdapter(new TaskListAdapter.WordDiff());
        ListItemRecyclerView.setAdapter(adapter);

        mViewModel = new ViewModelProvider(this).get(ViewModel.class);

        mViewModel.getTaskList().observe(this, tasks -> {
            adapter.submitList(tasks);
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