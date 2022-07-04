package com.jbproductions.liszt.models

import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.*

/**
 * Entity class to represent a SQLite table of lists.
 */
@Entity(tableName = "lists")
class ListModel(@field:ColumnInfo(name = "name") var name: String) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0

    @ColumnInfo(name = "sort_key")
    var sortKey: Int = 0

    @ColumnInfo(name = "date_created", defaultValue = "CURRENT_TIMESTAMP")
    var created: Date = Date()

    @ColumnInfo(name = "date_modified", defaultValue = "CURRENT_TIMESTAMP")
    var modified: Date = Date()

    /**
     * Compares this Task against another Task for equality, indicating that the Tasks have identical properties.
     * respect to their properties.
     * @param otherList A Task object to compare against
     * @return boolean indicating if the two Tasks are equal
     */
    fun equals(otherList: ListModel): Boolean {
        return id == otherList.id && name == otherList.name
    }

    companion object {
        const val SORT_ALPHA = 0
        const val SORT_DATE_DUE = 1
        const val SORT_DATE_CREATED = 2
    }

}