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

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @NonNull
    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "status")
    private boolean mStatus;

    @ColumnInfo(name = "date_created", defaultValue = "CURRENT_TIMESTAMP")
    private Date mCreated;

    @ColumnInfo(name = "date_modified", defaultValue = "CURRENT_TIMESTAMP")
    private Date mModified;

    public Task(@NonNull String name, boolean status) {
        this.mName = name;
        this.mStatus = status;
        this.mCreated = new Date();
        this.mModified = new Date();
    }

    public long getId(){ return this.id; }
    public String getName(){ return this.mName; }
    public boolean getStatus(){ return this.mStatus; }
    public Date getCreated(){ return this.mCreated; }
    public Date getModified(){ return this.mModified; }

    public void setId(long id) { this.id = id; }
    public void setStatus(boolean status) { this.mStatus = status; }
    public void setCreated(Date created) { this.mCreated = created; }
    public void setModified(Date modified) { this.mModified = modified; }

    public boolean equals(Task otherTask) {
        return mName.equals(otherTask.mName) && mStatus == otherTask.mStatus;
    }
}
