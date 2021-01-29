package com.jbproductions.liszt;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ViewModel extends AndroidViewModel {

    private DataRepository dataRepository;

    private final LiveData<List<Task>> taskList;

    public ViewModel(Application application) {
        super(application);
        dataRepository = new DataRepository(application);
        taskList = dataRepository.getListTasks();
    }

    LiveData<List<Task>> getTaskList() { return taskList; }

    // Create wrapper methods so that the implementation is segmented from the UI
    public void insert(Task task) { dataRepository.insert(task); }
    public void update(Task task) { dataRepository.update(task); }
    public void delete(Task task) { dataRepository.delete(task); }
}
