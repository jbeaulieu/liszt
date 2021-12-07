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

    @ColumnInfo(name = "parent_id")
    private long parentId;

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
        this.dueDate = null;
        this.notes = "";
        this.mCreated = new Date();
        this.mModified = new Date();
        this.parentId = 1;
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

    public long getParentId() {
        return this.parentId;
    }

    public void setParentId(long id) {
        this.parentId = id;
    }

    /**
     * Compares this Task against another Task for equality, indicating that the Tasks have identical properties.
     * respect to their properties.
     * @param otherTask A Task object to compare against
     * @return boolean indicating if the two Tasks are equal
     */
    public boolean equals(Task otherTask) {
        return id == otherTask.getId()
                && mName.equals(otherTask.mName)
                && mComplete == otherTask.mComplete
                && dueDateEquals(otherTask.dueDate)
                && notes.equals(otherTask.notes);
    }

    /**
     * Helper function to compare a Task object's due date to another date for equality. This method is null-safe for
     * both the Task's due date and the Date argument supplied. This allows for direct comparison to another Task's due
     * date by supplying the other Task's getDueDate() method as the argument for this method, even in the case when one
     * or both due dates are null.
     * @param date Date object to compare the task's due date against. May be null.
     * @return boolean indicating if the two dates are equal
     */
    public boolean dueDateEquals(Date date) {
        if (this.dueDate == null ^ date == null) {
            // Check if this Task's due date XOR the parameter date are null (not both)
            return false;
        } else if (this.dueDate != null && date != null) {
            // If neither date is null, check that the two are equal via Date's built-in equals function
            return this.dueDate.equals(date);
        }
        return true;
    }
}
