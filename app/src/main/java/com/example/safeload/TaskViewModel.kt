package com.example.safeload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class TaskViewModel : ViewModel() {
    private val _tasks = MutableLiveData<MutableList<Task>>(mutableListOf())
    val tasks: LiveData<MutableList<Task>> get() = _tasks

    private val _hydrationProgress = MutableLiveData<Int>(0) // 0 to 100
    private val _restProgress = MutableLiveData<Int>(0) // 0 to 100
    private val _currentTask = MutableLiveData<Task?>() // Tracks the currently running task

    val hydrationProgress: LiveData<Int> get() = _hydrationProgress
    val restProgress: LiveData<Int> get() = _restProgress
    val currentTask: LiveData<Task?> get() = _currentTask

    private var hydrationJob: Job? = null
    private var restJob: Job? = null

    fun addTask(task: Task) {
        val currentTasks = _tasks.value ?: mutableListOf()
        currentTasks.add(task)
        _tasks.value = currentTasks
    }

    fun removeTask(position: Int) {
        val currentTasks = _tasks.value ?: mutableListOf()
        val removedTask = currentTasks.removeAt(position)
        _tasks.value = currentTasks

        // Stop progress if the removed task is the currently running task
        if (removedTask == _currentTask.value) {
            stopProgressUpdates()
            _currentTask.value = null
        }
    }

    fun startTask(task: Task): Boolean {
        // Check if another task is running
        if (_currentTask.value != null && _currentTask.value != task) {
            return false // Indicate that another task is already running
        }

        _currentTask.value = task
        startProgressUpdates()
        return true
    }

    fun stopTask() {
        stopProgressUpdates()
        _currentTask.value = null
    }

    private fun startProgressUpdates() {
        // Hydration progress coroutine
        hydrationJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                for (i in 1..100) {
                    delay(1200L) // Smooth progress over 2 minutes
                    _hydrationProgress.postValue(i)
                }
                _hydrationProgress.postValue(0) // Reset after completion
            }
        }

        // Rest progress coroutine
        restJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                for (i in 1..100) {
                    delay(600L) // Smooth progress over 1 minute
                    _restProgress.postValue(i)
                }
                _restProgress.postValue(0) // Reset after completion
            }
        }
    }

    private fun stopProgressUpdates() {
        hydrationJob?.cancel()
        restJob?.cancel()
        _hydrationProgress.postValue(0)
        _restProgress.postValue(0)
    }
}
