package com.jbproductions.liszt.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jbproductions.liszt.dao.ListDao
import com.jbproductions.liszt.dao.TaskDao
import com.jbproductions.liszt.models.ListModel
import com.jbproductions.liszt.models.TaskModel
import com.jbproductions.liszt.util.DateTypeConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [TaskModel::class, ListModel::class], version = 1, exportSchema = false)
@TypeConverters(DateTypeConverter::class)
abstract class LisztDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun listDao(): ListDao

    private class LisztDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val taskDao = database.taskDao()
                    val listDao = database.listDao()

                    val inbox = ListModel("inbox")
                    inbox.id = 1
                    listDao.insert(inbox)

                    val task1 = TaskModel("Apples")
                    val task2 = TaskModel("Oranges")
                    taskDao.insert(task1)
                    taskDao.insert(task2)
                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: LisztDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): LisztDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LisztDatabase::class.java,
                    "liszt_database"
                )
                    .addCallback(LisztDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
