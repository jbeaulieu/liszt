package com.jbproductions.liszt;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity class to represent a SQLite table of list tasks
 * Currently, each task only contains a String with the task's name
 */
@Entity(tableName = "task_table")
public class Task {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "task")
    private String mTask;

    public Task(@NonNull String task) {this.mTask = task;}

    public String getTask(){ return this.mTask; }
}
