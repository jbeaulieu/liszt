package com.jbproductions.liszt.dao

import com.jbproductions.liszt.models.TaskModel
import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * DAO to provide query functionality for Task Entities.
 */
@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(task: TaskModel?)

    @Update
    fun updateTask(task: TaskModel?)

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTaskById(id: Long): TaskModel?

    @Query("DELETE FROM tasks WHERE id = :id")
    fun deleteTaskById(id: Long)

    @Query(
        "SELECT * FROM tasks WHERE parent_id = :id ORDER BY complete, "
                + "CASE WHEN :sortKey LIKE 0 THEN name "
                + "WHEN :sortKey LIKE 1 THEN COALESCE(date_due, '2100-12-31') "
                + "WHEN :sortKey LIKE 2 THEN id "
                + "ELSE id COLLATE NOCASE END"
    )
    fun getTasksForList(id: Long, sortKey: Int): LiveData<List<TaskModel?>?>?
}