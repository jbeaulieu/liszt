package com.jbproductions.liszt;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

/**
 * DAO to provide query functionality for TaskList objects.
 */
@Dao
public interface ListDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(TaskList list);

    @Update
    void updateList(TaskList list);

    @Query("SELECT * FROM lists WHERE id = :id")
    TaskList getListById(long id);

}
