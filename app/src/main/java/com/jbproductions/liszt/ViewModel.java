package com.jbproductions.liszt;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ViewModel extends AndroidViewModel {

    private DataRepository dataRepository;

    private final LiveData<List<Task>> taskList;
    private final LiveData<List<Task>> openTasks;
    private final LiveData<List<Task>> completeTasks;

    public ViewModel(Application application) {
        super(application);
        dataRepository = new DataRepository(application);
        taskList = dataRepository.getListTasks();
        openTasks = dataRepository.getOpenTasks();
        completeTasks = dataRepository.getCompleteTasks();
    }

    LiveData<List<Task>> getTaskList() { return taskList; }
    LiveData<List<Task>> getOpenTasks() { return openTasks; }
    LiveData<List<Task>> getCompleteTasks() { return completeTasks; }

    // Create wrapper methods so that the implementation is segmented from the UI
    public void insert(Task task) { dataRepository.insert(task); }
    public void update(Task task) { dataRepository.update(task); }
    public void delete(Task task) { dataRepository.delete(task); }
    public Task getTaskByID(long id) { return dataRepository.getTaskByID(id); }
    public void deleteTaskByID(long id) { dataRepository.deleteTaskByID(id); }
}
