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

    @Query("SELECT * FROM tasks WHERE id = :id")
    Task getTaskById(long id);

    @Query("DELETE FROM tasks WHERE id = :id")
    void deleteTaskById(long id);

    @Query("SELECT * FROM tasks WHERE parent_id = :id ORDER BY complete, "
            + "CASE WHEN :sortKey LIKE 0 THEN name "
            + "WHEN :sortKey LIKE 1 THEN COALESCE(date_due, '2100-12-31') "
            + "WHEN :sortKey LIKE 2 THEN id "
            + "ELSE id COLLATE NOCASE END")
    LiveData<List<Task>> getTasksForList(long id, int sortKey);
}
