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
@Entity(tableName = "tasks")
public class Task {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private long id;

    @NonNull
    @ColumnInfo(name = "name")
    private String mName;

    @NonNull
    @ColumnInfo(name = "complete")
    private boolean mComplete;

    @ColumnInfo(name = "date_created", defaultValue = "CURRENT_TIMESTAMP")
    private Date mCreated;

    @ColumnInfo(name = "date_modified", defaultValue = "CURRENT_TIMESTAMP")
    private Date mModified;

    public Task(@NonNull String name, @NonNull boolean complete) {
        this.mName = name;
        this.mComplete = complete;
        this.mCreated = new Date();
        this.mModified = new Date();
    }

    public long getId(){ return this.id; }
    public String getName(){ return this.mName; }
    public boolean getComplete(){ return this.mComplete; }
    public Date getCreated(){ return this.mCreated; }
    public Date getModified(){ return this.mModified; }

    public void setId(long id) { this.id = id; }
    public void setName(String name) { this.mName = name; }
    public void setComplete(boolean complete) { this.mComplete = complete; }
    public void setCreated(Date created) { this.mCreated = created; }
    public void setModified(Date modified) { this.mModified = modified; }

    public boolean equals(Task otherTask) {
        return mName.equals(otherTask.mName) && mComplete == otherTask.mComplete;
    }
}
