package com.jbproductions.liszt;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

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

    @ColumnInfo(name = "date_created", defaultValue = "CURRENT_TIMESTAMP")
    private Date mCreated;

    @ColumnInfo(name = "date_modified", defaultValue = "CURRENT_TIMESTAMP")
    private Date mModified;

    public Task(@NonNull String task, boolean status) {this.mTask = task; this.mStatus = status; this.mCreated = new Date(); this.mModified = new Date(); }

    public String getTask(){ return this.mTask; }
    public boolean getStatus(){ return this.mStatus; }
    public Date getCreated(){ return this.mCreated; }
    public Date getModified(){ return this.mModified; }

    public void setCreated(Date created) {
        this.mCreated = created;
    }

    public void setModified(Date modified) {
        this.mModified = modified;
    }

    public boolean equals(Task otherTask) {
        return mTask.equals(otherTask.mTask) && mStatus == otherTask.mStatus;
    }
}
