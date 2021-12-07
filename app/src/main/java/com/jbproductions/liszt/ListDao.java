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
 * DAO to provide query functionality for TaskList objects.
 */
@Dao
public interface ListDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(TaskList list);

    @Update
    void updateList(TaskList list);

    @Delete
    void deleteList(TaskList list);

    @Query("SELECT * FROM lists WHERE id = :id")
    TaskList getListById(long id);

    @Query("DELETE FROM lists WHERE id = :id")
    void deleteListById(long id);

    @Query("SELECT * FROM lists ORDER BY name")
    LiveData<List<TaskList>> getAllLists();
}
