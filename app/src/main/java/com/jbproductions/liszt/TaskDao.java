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
 * DAO to provide query functionality for Task Entities.
 */
@Dao
public interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Task task);

    @Update
    void updateTask(Task task);

    @Delete
    void deleteTask(Task task);

    @Query("SELECT * FROM tasks WHERE id = :id")
    Task getTaskById(long id);

    @Query("SELECT * FROM tasks WHERE parent_id = :id")
    LiveData<List<Task>> getTasksForList(long id);

    @Query("DELETE FROM tasks WHERE id = :id")
    void deleteTaskById(long id);

    @Query("SELECT * FROM tasks ORDER BY complete, "
            + "CASE WHEN :sort LIKE 0 THEN name "
            + "WHEN :sort LIKE 1 THEN date_due "
            + "WHEN :sort LIKE 2 THEN id "
            + "ELSE id END")
    LiveData<List<Task>> getAllTasks(int sort);

    @Query("SELECT * FROM tasks WHERE parent_id = :id ORDER BY complete, "
            + "CASE WHEN :sortKey LIKE 0 THEN name "
            + "WHEN :sortKey LIKE 1 THEN date_due "
            + "WHEN :sortKey LIKE 2 THEN id "
            + "ELSE id END")
    LiveData<List<Task>> getTasksForListSorted(long id, int sortKey);
}
