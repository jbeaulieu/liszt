package com.jbproductions.liszt;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * Repository class to manage the ViewModel's access to data.
 * Currently, this simply routes data provided by the TaskDao. In a future implementation, this
 * class will handle deciding whether to fetch online data or rely on data cached in local storage.
 */
public class DataRepository {

    private TaskDao mTaskDao;
    private LiveData<List<Task>> mListTasks;
    private LiveData<List<Task>> mOpenTasks;
    private LiveData<List<Task>> mCompleteTasks;

    DataRepository(Application application) {
        TaskRoomDatabase db = TaskRoomDatabase.getDatabase(application);
        mTaskDao = db.taskDao();
        mListTasks = mTaskDao.getAllTasks();
        mOpenTasks = mTaskDao.getOpenTasks();
        mCompleteTasks = mTaskDao.getCompleteTasks();
    }

    LiveData<List<Task>> getListTasks() {
        return mListTasks;
    }

    LiveData<List<Task>> getOpenTasks() {
        return mOpenTasks;
    }

    LiveData<List<Task>> getCompleteTasks() {
        return mCompleteTasks;
    }

    void insert(Task task) {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> {
            mTaskDao.insert(task);
        });
    }

    void update(Task task) {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> {
            mTaskDao.updateTask(task);
        });
    }

    void delete(Task task) {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> {
            mTaskDao.deleteTask(task);
        });
    }

    void deleteTaskByID(long id) {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> {
            mTaskDao.deleteTaskByID(id);
        });
    }
}
