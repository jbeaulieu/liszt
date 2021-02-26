package com.jbproductions.liszt;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * DAO to provide query functionality for Task Entities
 */
@Dao
public interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Task task);

    @Update
    void updateTask(Task task);

    @Delete
    void deleteTask(Task task);

    @Query("DELETE FROM task_table WHERE id = :id")
    void deleteTaskByID(long id);

    @Query("SELECT id, name, status FROM task_table WHERE status=0 UNION ALL SELECT id, name, status from task_table WHERE status=1")
    LiveData<List<Task>> getAllTasks();

    @Query("SELECT id, name, status FROM task_table WHERE status=0")
    LiveData<List<Task>> getOpenTasks();

    @Query("SELECT id, name, status FROM task_table WHERE status=1")
    LiveData<List<Task>> getCompleteTasks();
}
