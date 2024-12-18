// Updated TaskAdapter.kt
package com.example.safeload

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private var tasks: List<Task>, // Changed to List to allow flexibility for updates
    private val onStartClick: (Task, Int) -> Unit,
    private val onEndClick: (Task, Int) -> Unit,
    private val onTaskClick: (Task, Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.tvTaskName)
        val taskDetails: TextView = itemView.findViewById(R.id.tvTaskDetails)
        val btnStart: Button = itemView.findViewById(R.id.btnStartTask)
        val btnEnd: Button = itemView.findViewById(R.id.btnEndTask)

        init {
            btnStart.setOnClickListener {
                onStartClick(tasks[adapterPosition], adapterPosition)
            }
            btnEnd.setOnClickListener {
                onEndClick(tasks[adapterPosition], adapterPosition)
            }
            itemView.setOnClickListener {
                onTaskClick(tasks[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskName.text = task.name
        holder.taskDetails.text = "From: ${task.startingLocation}, To: ${task.endingLocation}"
    }

    override fun getItemCount(): Int = tasks.size

    // Method to update the list of tasks dynamically
    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }
}