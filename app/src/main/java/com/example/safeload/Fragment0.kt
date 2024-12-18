package com.example.safeload

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Fragment0 : Fragment(R.layout.fragment0) {

    private val taskViewModel: TaskViewModel by activityViewModels()
    private lateinit var recyclerViewTasks: RecyclerView
    private lateinit var alarmManager: AlarmManager
    private lateinit var adapter: TaskAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnAddTask = view.findViewById<Button>(R.id.btnAddTask)
        recyclerViewTasks = view.findViewById(R.id.recyclerViewTasks)
        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        recyclerViewTasks.layoutManager = LinearLayoutManager(context)
        adapter = TaskAdapter(emptyList(), this::onTaskStart, this::onTaskEnd, this::openTaskDetails)
        recyclerViewTasks.adapter = adapter

        taskViewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            adapter.updateTasks(tasks)
        }

        btnAddTask.setOnClickListener {
            val intent = Intent(requireContext(), TaskDetailsActivity::class.java)
            addTaskLauncher.launch(intent)
        }
    }

    private val addTaskLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val startingLocation = data?.getStringExtra("startingLocation")
            val endingLocation = data?.getStringExtra("endingLocation")

            if (!startingLocation.isNullOrEmpty() && !endingLocation.isNullOrEmpty()) {
                val task = Task(
                    name = "Task ${taskViewModel.tasks.value?.size?.plus(1) ?: 1}",
                    startingLocation = startingLocation,
                    endingLocation = endingLocation
                )
                taskViewModel.addTask(task)
                Toast.makeText(requireContext(), "Task added successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onTaskStart(task: Task, position: Int) {
        if (!taskViewModel.startTask(task)) {
            Toast.makeText(requireContext(), "Another task is already running. Stop it first.", Toast.LENGTH_SHORT).show()
            return
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            scheduleReminders(task, position)
            Toast.makeText(requireContext(), "Task started: ${task.name}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun scheduleReminders(task: Task, position: Int) {
        fun createPendingIntent(message: String, requestCode: Int): PendingIntent {
            val intent = Intent(requireContext(), ReminderReceiver::class.java).apply {
                putExtra("message", message)
            }
            return PendingIntent.getBroadcast(
                requireContext(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val currentTime = System.currentTimeMillis()

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            currentTime + 60 * 1000, // First reminder in 1 hour 60 * 60*1000
            60 * 1000, // Repeat every 1 hour 60 * 60 * 1000
            createPendingIntent("Time to hydrate for ${task.name}!", position * 2)
        )

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            currentTime + 2 * 60 * 1000, // First reminder in 2 hours   2 * 60 * 60 * 1000
            2 * 60 * 1000, // Repeat every 2 hours  2 * 60 * 60 * 1000
            createPendingIntent("Time to take a rest for ${task.name}!", position * 2 + 1)
        )
    }
    private fun onTaskEnd(task: Task, position: Int) {
        if (task != taskViewModel.currentTask.value) {
            Toast.makeText(requireContext(), "Cannot end a task that is not running.", Toast.LENGTH_SHORT).show()
            return
        }

        taskViewModel.stopTask()
        cancelReminders(position)
        Toast.makeText(requireContext(), "Task ended: ${task.name}", Toast.LENGTH_SHORT).show()

        taskViewModel.removeTask(position)
    }


    private fun cancelReminders(position: Int) {
        fun createPendingIntent(requestCode: Int): PendingIntent {
            val intent = Intent(requireContext(), ReminderReceiver::class.java)
            return PendingIntent.getBroadcast(
                requireContext(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        alarmManager.cancel(createPendingIntent(position * 2))
        alarmManager.cancel(createPendingIntent(position * 2 + 1))
    }

    private fun openTaskDetails(task: Task, position: Int) {
        val intent = Intent(requireContext(), TaskDetailsActivity::class.java).apply {
            putExtra("startingLocation", task.startingLocation)
            putExtra("endingLocation", task.endingLocation)
            putExtra("taskIndex", position)
        }
        taskDetailsLauncher.launch(intent)
    }

    private val taskDetailsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(requireContext(), "Task details updated", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(requireContext(), "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }
}