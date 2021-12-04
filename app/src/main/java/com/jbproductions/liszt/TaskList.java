package com.jbproductions.liszt;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

/**
 * Entity class to represent a SQLite table of lists.
 */
@Entity(tableName = "lists")
public class TaskList {

    public static final int SORT_ALPHA = 0;
    public static final int SORT_DATE_DUE = 1;
    public static final int SORT_DATE_CREATED = 2;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @NonNull
    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "sort_key")
    private int sortKey;

    @ColumnInfo(name = "date_created", defaultValue = "CURRENT_TIMESTAMP")
    private Date mCreated;

    @ColumnInfo(name = "date_modified", defaultValue = "CURRENT_TIMESTAMP")
    private Date mModified;

    /**
     * Default constructor for list. DateCreated and DateModified are created by default.
     * @param name String name of list
     */
    public TaskList(@NonNull String name) {
        this.mName = name;
        this.sortKey = 0;
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

    public int getSortKey() {
        return this.sortKey;
    }

    public void setSortKey(int sortKey) {
        this.sortKey = sortKey;
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

    /**
     * Compares this Task against another Task for equality, indicating that the Tasks have identical properties.
     * respect to their properties.
     * @param otherTask A Task object to compare against
     * @return boolean indicating if the two Tasks are equal
     */
    public boolean equals(TaskList otherTask) {
        return id == otherTask.getId() && mName.equals(otherTask.mName);
    }
}
