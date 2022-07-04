package com.jbproductions.liszt.viewmodels

import androidx.lifecycle.*
import com.jbproductions.liszt.db.LisztRepository
import com.jbproductions.liszt.models.ListModel
import com.jbproductions.liszt.models.TaskModel
import kotlinx.coroutines.launch

class LisztViewModel(private val repository: LisztRepository) : ViewModel() {

    private val _sortKey = MutableLiveData(1)
    private val sortKey: LiveData<Int> = _sortKey

    private lateinit var activeList: ListModel
    var selectedTask: TaskModel? = null

    var tasks = Transformations.switchMap(sortKey) { repository.getTasksForList(1, sortKey.value!!) }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun createTask(task: TaskModel) = viewModelScope.launch {
        repository.createTask(task)
    }

    fun updateTask(task: TaskModel) = viewModelScope.launch {
        repository.updateTask(task)
    }

    fun deleteTaskById(id: Long) = viewModelScope.launch {
        repository.deleteTaskById(id)
    }

    fun updateList(list: ListModel) = viewModelScope.launch {
        repository.updateList(list)
    }

    /**
     * Sets the sort key of the active list. The chosen key is saved both in the ViewModel's savedStateHandle
     * and to the active list's database entry.
     * @param key int value representing the sort method to be used. See TaskList class for values
     */
    fun setSortKey(key: Int) {
        _sortKey.value = key
        activeList.sortKey = key
        updateList(activeList)
    }

    fun setSelectedTask(id: Long) = viewModelScope.launch {
        selectedTask = repository.getTaskById(id)
    }

    init {
        viewModelScope.launch {
            activeList = repository.getListById(1)!!
            _sortKey.value = activeList.sortKey
        }
    }
}

class LisztViewModelFactory(private val repository: LisztRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LisztViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LisztViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
