package com.jbproductions.liszt;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * RoomDatabase class to work with TaskDao and perform asynchronous queries
 */
@Database(entities = {Task.class}, version = 1, exportSchema = false)
@TypeConverters(DateTypeConverter.class)
public abstract class TaskRoomDatabase extends RoomDatabase {

    public abstract TaskDao taskDao();

    private static volatile TaskRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 2;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
               TaskDao dao = INSTANCE.taskDao();

               Task task1 = new Task("Apples", false);
               Task task2 = new Task("Oranges", false);
               dao.insert(task1);
               dao.insert(task2);
            });
        }
    };

    static TaskRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TaskRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), TaskRoomDatabase.class, "task_database")
                            .addCallback(sRoomDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }
}

class DateTypeConverter {

    @TypeConverter
    public Date toDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public Long toLong(Date date) {
        if (date == null) {
            return null;
        } else {
            return date.getTime();
        }
    }

}