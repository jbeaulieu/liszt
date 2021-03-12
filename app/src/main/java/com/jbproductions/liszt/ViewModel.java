package com.jbproductions.liszt;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

/**
 * ViewModel class to manage interactions with the application's data.
 */
public class ViewModel extends AndroidViewModel {

    private final LiveData<List<Task>> taskList;
    private final LiveData<List<Task>> openTasks;
    private final LiveData<List<Task>> completeTasks;
    private final DataRepository dataRepository;

    /**
     * Default constructor for application ViewModel.
     * @param application Application to base ViewModel instance around
     */
    public ViewModel(Application application) {
        super(application);
        dataRepository = new DataRepository(application);
        taskList = dataRepository.getListTasks();
        openTasks = dataRepository.getOpenTasks();
        completeTasks = dataRepository.getCompleteTasks();
    }

    LiveData<List<Task>> getAllTasks() {
        return taskList;
    }

    LiveData<List<Task>> getOpenTasks() {
        return openTasks;
    }

    LiveData<List<Task>> getCompleteTasks() {
        return completeTasks;
    }

    // Create wrapper methods so that the implementation is segmented from the UI
    public void insert(Task task) {
        dataRepository.insert(task);
    }

    public void update(Task task) {
        dataRepository.update(task);
    }

    public void delete(Task task) {
        dataRepository.delete(task);
    }

    public Task getTaskById(long id) {
        return dataRepository.getTaskById(id);
    }

    public void deleteTaskById(long id) {
        dataRepository.deleteTaskById(id);
    }
}
