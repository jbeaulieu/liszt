package com.jbproductions.liszt.models

import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.*

/**
 * Entity class to represent a SQLite table of list tasks.
 */
@Entity(tableName = "tasks")
class TaskModel(
    @field:ColumnInfo(name = "name") var name: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0

    @ColumnInfo(name = "complete")
    var complete: Boolean = false

    @ColumnInfo(name = "parent_id")
    var parentId: Long = 1

    @ColumnInfo(name = "date_due")
    var dueDate: Date? = null

    @ColumnInfo(name = "note")
    var notes = ""

    @ColumnInfo(name = "date_created", defaultValue = "CURRENT_TIMESTAMP")
    var created: Date = Date()

    @ColumnInfo(name = "date_modified", defaultValue = "CURRENT_TIMESTAMP")
    var modified: Date = Date()

    /**
     * Compares this Task against another Task for equality, indicating that the Tasks have identical properties.
     * respect to their properties.
     * @param otherTask A Task object to compare against
     * @return boolean indicating if the two Tasks are equal
     */
    fun equals(otherTask: TaskModel): Boolean {
        return (id == otherTask.id && name == otherTask.name && complete == otherTask.complete && dueDateEquals(
            otherTask.dueDate
        )
                && notes == otherTask.notes)
    }

    /**
     * Helper function to compare a Task object's due date to another date for equality. This method is null-safe for
     * both the Task's due date and the Date argument supplied. This allows for direct comparison to another Task's due
     * date by supplying the other Task's getDueDate() method as the argument for this method, even in the case when one
     * or both due dates are null.
     * @param date Date object to compare the task's due date against. May be null.
     * @return boolean indicating if the two dates are equal
     */
    fun dueDateEquals(date: Date?): Boolean {
        date ?: return dueDate == null
        return dueDate?.equals(date) ?: false
    }

}