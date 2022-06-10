package com.jbproductions.liszt;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.jbproductions.liszt.dao.ListDao;
import com.jbproductions.liszt.dao.TaskDao;
import com.jbproductions.liszt.models.ListModel;
import com.jbproductions.liszt.models.TaskModel;
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

    LiveData<List<TaskModel>> getTasksForList(long id, int sortKey) {
        return mTaskDao.getTasksForList(id, sortKey);
    }

    void createTask(TaskModel task) {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> mTaskDao.insert(task));
    }

    void updateTask(TaskModel task) {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> mTaskDao.updateTask(task));
    }

    TaskModel getTaskById(long id) {
        TaskModel retTask = null;
        Future<TaskModel> thisTask = TaskRoomDatabase.databaseWriteExecutor.submit(() -> mTaskDao.getTaskById(id));
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

    void updateList(ListModel list) {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> mListDao.updateList(list));
    }

    ListModel getListById(long id) {
        ListModel retList = null;
        Future<ListModel> thisList = TaskRoomDatabase.databaseWriteExecutor.submit(() -> mListDao.getListById(id));
        try {
            retList = thisList.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return retList;
    }
}
