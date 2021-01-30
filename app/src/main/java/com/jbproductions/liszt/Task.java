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

    @ColumnInfo(name = "status")
    private boolean mStatus;

    public Task(@NonNull String task, boolean status) {this.mTask = task; this.mStatus = status;}

    public String getTask(){ return this.mTask; }
    public boolean getStatus(){ return this.mStatus; }

    public boolean equals(Task otherTask) {
        return mTask.equals(otherTask.mTask) && mStatus == otherTask.mStatus;
    }
}
