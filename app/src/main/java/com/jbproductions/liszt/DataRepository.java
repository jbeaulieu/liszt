package com.jbproductions.liszt;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Repository class to manage the ViewModel's access to data.
 * Currently, this simply routes data provided by the TaskDao. In a future implementation, this
 * class will handle deciding whether to fetch online data or rely on data cached in local storage.
 */
public class DataRepository {

    private final TaskDao mTaskDao;
    private final ListDao mListDao;

    DataRepository(Application application) {
        TaskRoomDatabase db = TaskRoomDatabase.getDatabase(application);
        mTaskDao = db.taskDao();
        mListDao = db.listDao();
    }

    LiveData<List<Task>> getTasksForList(long id, int sortKey) {
        return mTaskDao.getTasksForList(id, sortKey);
    }

    void createTask(Task task) {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> mTaskDao.insert(task));
    }

    void updateTask(Task task) {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> mTaskDao.updateTask(task));
    }

    Task getTaskById(long id) {
        Task retTask = null;
        Future<Task> thisTask = TaskRoomDatabase.databaseWriteExecutor.submit(() -> mTaskDao.getTaskById(id));
        try {
            retTask = thisTask.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return retTask;
    }

    void deleteTaskById(long id) {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> mTaskDao.deleteTaskById(id));
    }

    void updateList(TaskList taskList) {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> mListDao.updateList(taskList));
    }

    TaskList getListById(long id) {
        TaskList retList = null;
        Future<TaskList> thisList = TaskRoomDatabase.databaseWriteExecutor.submit(() -> mListDao.getListById(id));
        try {
            retList = thisList.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return retList;
    }
}
