package com.jbproductions.liszt.dao

import androidx.room.*
import com.jbproductions.liszt.models.ListModel

/**
 * DAO to provide query functionality for TaskList objects.
 */
@Dao
interface ListDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(list: ListModel)

    @Update
    fun updateList(list: ListModel)

    @Query("SELECT * FROM lists WHERE id = :id")
    fun getListById(id: Long): ListModel
}