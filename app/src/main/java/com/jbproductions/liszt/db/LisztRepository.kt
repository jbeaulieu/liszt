package com.jbproductions.liszt.db

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.jbproductions.liszt.dao.ListDao
import com.jbproductions.liszt.dao.TaskDao
import com.jbproductions.liszt.models.ListModel
import com.jbproductions.liszt.models.TaskModel

// Declares the DAO as a private property in the constructor. We pass in the DAO
// instead of the whole database, because the repository only needs access to the DAO
class LisztRepository(private val taskDao: TaskDao, private val listDao: ListDao) {

    fun getTasksForList(id: Long, sortKey: Int): LiveData<List<TaskModel>> {
        return taskDao.getTasksForList(id, sortKey)
    }

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @WorkerThread
    suspend fun createTask(task: TaskModel) {
        taskDao.insert(task)
    }

    @WorkerThread
    suspend fun updateTask(task: TaskModel) {
        taskDao.updateTask(task)
    }

    @WorkerThread
    suspend fun getTaskById(id: Long): TaskModel? {
        return taskDao.getTaskById(id)
    }

    @WorkerThread
    suspend fun deleteTaskById(id: Long) {
        taskDao.deleteTaskById(id)
    }

    @WorkerThread
    suspend fun updateList(list: ListModel) {
        listDao.updateList(list)
    }

    @WorkerThread
    suspend fun getListById(id: Long): ListModel? {
        return listDao.getListById(id)
    }
}
