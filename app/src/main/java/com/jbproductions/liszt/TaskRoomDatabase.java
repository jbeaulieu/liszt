package com.jbproductions.liszt;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * RoomDatabase class to work with TaskDao and perform asynchronous queries.
 */
@Database(entities = {Task.class, TaskList.class}, version = 1, exportSchema = false)
@TypeConverters(DateTypeConverter.class)
public abstract class TaskRoomDatabase extends RoomDatabase {

    private static final int NUMBER_OF_THREADS = 2;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static volatile TaskRoomDatabase INSTANCE;
    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                TaskDao taskDao = INSTANCE.taskDao();
                ListDao listDao = INSTANCE.listDao();

                Task task1 = new Task("Apples", false);
                Task task2 = new Task("Oranges", false);
                taskDao.insert(task1);
                taskDao.insert(task2);

                TaskList inbox = new TaskList("inbox");
                listDao.insert(inbox);
            });
        }
    };

    static TaskRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TaskRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), TaskRoomDatabase.class,
                            "task_database")
                            .addCallback(sRoomDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract TaskDao taskDao();

    public abstract ListDao listDao();
}