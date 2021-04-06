package com.jbproductions.liszt;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

/**
 * Entity class to represent a SQLite table of list tasks.
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

    @ColumnInfo(name = "date_due")
    private Date dueDate;

    @ColumnInfo(name = "note")
    private String notes;

    @ColumnInfo(name = "date_created", defaultValue = "CURRENT_TIMESTAMP")
    private Date mCreated;

    @ColumnInfo(name = "date_modified", defaultValue = "CURRENT_TIMESTAMP")
    private Date mModified;

    /**
     * Default constructor for task. DateCreated and DateModified are created by default.
     * @param name String name of task
     * @param complete Boolean reflecting whether a task has been marked as complete
     */
    public Task(@NonNull String name, @NonNull boolean complete) {
        this.mName = name;
        this.mComplete = complete;
        this.dueDate = new Date();
        this.notes = "";
        this.mCreated = new Date();
        this.mModified = new Date();
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return this.mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public Date getDueDate() {
        return this.dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public boolean getComplete() {
        return this.mComplete;
    }

    public void setComplete(boolean complete) {
        this.mComplete = complete;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getCreated() {
        return this.mCreated;
    }

    public void setCreated(Date created) {
        this.mCreated = created;
    }

    public Date getModified() {
        return this.mModified;
    }

    public void setModified(Date modified) {
        this.mModified = modified;
    }

    public boolean equals(Task otherTask) {
        return id == otherTask.getId() && mName.equals(otherTask.mName) && mComplete == otherTask.mComplete;
    }
}
