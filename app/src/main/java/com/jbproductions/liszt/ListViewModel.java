package com.jbproductions.liszt;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ListViewModel extends AndroidViewModel {

    private TaskRepository taskRepository;

    private final LiveData<List<Task>> taskList;

    public ListViewModel (Application application) {
        super(application);
        taskRepository = new TaskRepository(application);
        taskList = taskRepository.getListTasks();
    }

    LiveData<List<Task>> getTaskList() { return taskList; }

    // Create wrapper methods so that the implementation is segmented from the UI
    public void insert(Task task) { taskRepository.insert(task); }
    public void delete(Task task) { taskRepository.delete(task); }
}
