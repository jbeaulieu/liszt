package com.jbproductions.liszt;

import android.app.Application;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import java.util.List;

/**
 * ViewModel class to manage interactions with the application's data.
 */
public class ViewModel extends AndroidViewModel {

    private static final String SORT_KEY = "SORT";

    private final LiveData<List<Task>> taskList;
    private final DataRepository dataRepository;
    private final SavedStateHandle savedStateHandle;

    Task selectedTask;

    /**
     * Default constructor for application ViewModel.
     * @param application Application to base ViewModel instance around
     */
    public ViewModel(Application application, SavedStateHandle handle) {
        super(application);
        savedStateHandle = handle;
        dataRepository = new DataRepository(application);
        taskList = Transformations.switchMap(
                savedStateHandle.getLiveData(SORT_KEY, null),
                (Function<Integer, LiveData<List<Task>>>) sortKey -> dataRepository.getTasksForList(1, sortKey)
        );
    }

    public void setSortKey(int key) {
        savedStateHandle.set(SORT_KEY, key);
    }

    LiveData<List<Task>> getAllTasks() {
        return taskList;
    }

    // Create wrapper methods so that the implementation is segmented from the UI
    public void createTask(Task task) {
        dataRepository.createTask(task);
    }

    public void updateTask(Task task) {
        dataRepository.updateTask(task);
    }

    public Task getTaskById(long id) {
        return dataRepository.getTaskById(id);
    }

    public void deleteTaskById(long id) {
        dataRepository.deleteTaskById(id);
    }

    public void updateList(TaskList list) {
        dataRepository.updateList(list);
    }

    public TaskList getListById(long id) {
        return dataRepository.getListById(id);
    }
}
