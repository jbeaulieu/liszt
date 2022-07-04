package com.jbproductions.liszt.dao

import androidx.room.*
import com.jbproductions.liszt.models.ListModel

/**
 * DAO to provide query functionality for ListModel objects.
 */
@Dao
interface ListDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(list: ListModel)

    @Update
    suspend fun updateList(list: ListModel)

    @Query("SELECT * FROM lists WHERE id = :id")
    suspend fun getListById(id: Long): ListModel?
}