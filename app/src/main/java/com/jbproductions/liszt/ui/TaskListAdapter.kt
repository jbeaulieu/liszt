package com.jbproductions.liszt.ui

import com.jbproductions.liszt.util.getReadableDate
import androidx.recyclerview.widget.DiffUtil
import com.jbproductions.liszt.models.TaskModel
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.selection.SelectionTracker
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.jbproductions.liszt.R
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.CheckBox
import androidx.recyclerview.widget.ListAdapter

/**
 * ListAdapter implementation to dynamically display a single task list.
 */
class TaskListAdapter internal constructor(
    private val itemCheckListener: ItemCheckListener,
    diffCallback: DiffUtil.ItemCallback<TaskModel?>
) : ListAdapter<TaskModel?, RecyclerView.ViewHolder>(diffCallback) {
    //// Member Attributes
    private var mSelectionTracker: SelectionTracker<Long>? = null

    /**
     * Simple interface to pass a callback to the Adapter's parent Fragment when a list item is checked/unchecked.
     */
    interface ItemCheckListener {
        fun onTaskChecked(task: TaskModel)
    }

    fun setSelectionTracker(selectionTracker: SelectionTracker<Long>?) {
        mSelectionTracker = selectionTracker
    }

    // Create direct references for Task subviews (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        return when (viewType) {
            ViewTypes.Divider -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.task_archive_header, parent, false)
                DividerViewHolder(view)
            }
            ViewTypes.TaskView -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_task, parent, false)
                TaskViewHolder(view)
            }
            else -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_task, parent, false)
                TaskViewHolder(view)
            }
        }
    }

    // Set Task attributes based on stored data
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val task = getItem(position)
        if (getItemViewType(position) == ViewTypes.TaskView) {
            val isSelected = mSelectionTracker!!.isSelected(task!!.id)
            (viewHolder as TaskViewHolder).bind(task, isSelected, itemCheckListener)
        }
    }

    override fun getItemId(position: Int): Long {
        return getItem(position)!!.id
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position)!!.id == -1L) {
            ViewTypes.Divider
        } else {
            ViewTypes.TaskView
        }
    }

    // Compares whether two Tasks visual representations are the same
    internal class TaskDiff : DiffUtil.ItemCallback<TaskModel>() {
        override fun areItemsTheSame(oldTask: TaskModel, newTask: TaskModel): Boolean {
            return oldTask.id == newTask.id && oldTask.name == newTask.name && oldTask.dueDateEquals(
                newTask.dueDate
            )
        }

        override fun areContentsTheSame(oldTask: TaskModel, newTask: TaskModel): Boolean {
            return oldTask.equals(newTask)
        }
    }

    internal class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //// Member Attributes
        private val taskLayout: LinearLayout = view.findViewById(R.id.task_layout)
        private val taskCardTitle: TextView = view.findViewById(R.id.task_text)
        private val taskCardSubtitle: TextView = view.findViewById(R.id.task_subtext)
        private val checkBox: CheckBox = view.findViewById(R.id.task_checkbox)
        fun bind(task: TaskModel?, isSelected: Boolean, itemCheckListener: ItemCheckListener) {
            taskCardTitle.text = task!!.name
            checkBox.isChecked = task.complete
            taskLayout.isActivated = isSelected
            if (task.dueDate == null) {
                taskCardSubtitle.visibility = View.GONE
            } else {
                taskCardSubtitle.visibility = View.VISIBLE
                taskCardSubtitle.text = getReadableDate(task.dueDate!!)
            }
            checkBox.setOnClickListener {
                val status = checkBox.isChecked
                task.complete = status
                itemCheckListener.onTaskChecked(task)
            }
        }
    }

    internal class DividerViewHolder(view: View?) : RecyclerView.ViewHolder(
        view!!
    )

    private object ViewTypes {
        const val TaskView = 1
        const val Divider = 2
    }

    //// Constructor Methods
    init {
        setHasStableIds(true)
    }
}