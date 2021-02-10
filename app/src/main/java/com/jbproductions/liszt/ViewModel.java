package com.jbproductions.liszt;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

public class ViewModel extends AndroidViewModel {

    private DataRepository dataRepository;

    private final LiveData<List<Task>> taskList;
    private final LiveData<List<Task>> openTasks;
    private final LiveData<List<Task>> completeTasks;

    private final List<Task> listOfTasksToDelete= new ArrayList<>();

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

    private void clearDeletedSelection() { listOfTasksToDelete.clear(); }
    public void addToDeleteSelection(Task task) {listOfTasksToDelete.add(task);}
    public void removeFromDeleteSelection(Task task) {listOfTasksToDelete.remove(task);}
    // public boolean isInDeleteSelection(Task task) {return listOfTasksToDelete.contains(task);}
    public void sizeOfDeleteSelection(Task task) {listOfTasksToDelete.size();}
    public void emptyDeleteSelection() {
        for (Task thisTask : listOfTasksToDelete) {
            delete(thisTask);
        }
        clearDeletedSelection();
    }
    public boolean isInDeleteSelection(Task task) {
        for (Task thisTask : listOfTasksToDelete) {
            if (task.getTask() == thisTask.getTask()) {
                return true;
            }
        }
        return false;
    }
    public void printDeletedSelection() {
        for (Task thisTask : listOfTasksToDelete){
            System.out.println(thisTask.getTask());
        }
    }
}
