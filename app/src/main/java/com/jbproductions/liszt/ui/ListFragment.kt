package com.jbproductions.liszt.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.SelectionTracker.SelectionPredicate
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jbproductions.liszt.LisztApplication
import com.jbproductions.liszt.R
import com.jbproductions.liszt.databinding.FragmentListBinding
import com.jbproductions.liszt.models.ListModel
import com.jbproductions.liszt.models.TaskModel
import com.jbproductions.liszt.ui.TaskListAdapter.ItemCheckListener
import com.jbproductions.liszt.ui.TaskListAdapter.TaskDiff
import com.jbproductions.liszt.viewmodels.LisztViewModel
import com.jbproductions.liszt.viewmodels.LisztViewModelFactory

/**
 * Host fragment for viewing a list. This fragment provides the main view for the application.
 */
class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var selectionTracker: SelectionTracker<Long>
    private var itemCheckListener: ItemCheckListener? = null

    private var singleItemSelected = false
    private var multipleItemsSelected = false

    private val viewModel: LisztViewModel by activityViewModels {
        LisztViewModelFactory(LisztApplication.repository!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val deleteItem = menu.findItem(R.id.action_delete)
        val editItem = menu.findItem(R.id.action_edit)
        deleteItem.isVisible = singleItemSelected || multipleItemsSelected
        editItem.isVisible = singleItemSelected
    }

    /**
     * Handles processing of Options Menu items. For ListFragment, this includes list sorting, as well as
     * editing and deleting items when one or more tasks are selected via long press.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                val selectedItems = selectionTracker.selection
                for (itemId in selectedItems) {
                    viewModel.deleteTaskById(itemId)
                }
                selectionTracker.clearSelection()
                true
            }
            R.id.action_edit -> {
                val selectedTaskId = selectionTracker.selection.iterator().next()
                viewModel.setSelectedTask(selectedTaskId)
                selectionTracker.clearSelection()
                NavHostFragment.findNavController(this@ListFragment)
                    .navigate(R.id.action_ListFragment_to_DetailsFragment)
                true
            }
            R.id.sort_alpha -> {
                viewModel.setSortKey(ListModel.SORT_ALPHA)
                true
            }
            R.id.sort_due -> {
                viewModel.setSortKey(ListModel.SORT_DATE_DUE)
                true
            }
            R.id.sort_default -> {
                viewModel.setSortKey(ListModel.SORT_DATE_CREATED)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentListBinding.inflate(inflater, container, false)

        binding.newTaskText.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                val thisTask = TaskModel(binding.newTaskText.text.toString())
                viewModel.createTask(thisTask)
                // mViewModel.createTask(thisTask)
                binding.newTaskText.text.clear()
                binding.newTaskText.requestFocus()
                handled = true
            }
            handled
        }

        binding.fab.setOnClickListener {
            binding.newTaskText.requestFocus()
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.newTaskText, InputMethodManager.SHOW_IMPLICIT)
        }

        itemCheckListener = object : ItemCheckListener {
            override fun onTaskChecked(task: TaskModel) {
                viewModel.updateTask(task)
            }
        }

        val adapter = TaskListAdapter(
            itemCheckListener as ItemCheckListener,
            TaskDiff() as DiffUtil.ItemCallback<TaskModel?>
        )
        binding.listRecyclerView.adapter = adapter
        binding.listRecyclerView.layoutManager = LinearLayoutManager(activity)

        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            val list = tasks.toMutableList()
            val index = list.indexOfFirst { it.complete }
            if (index != -1) {
                val divider = TaskModel("")
                divider.id = -1
                list.add(index, divider)
            }
            // Submit the list to the recyclerView adapter
            adapter.submitList(list as List<TaskModel>)
        }

        val noDividerSelection: SelectionPredicate<Long> = object : SelectionPredicate<Long>() {
            override fun canSetStateForKey(key: Long, nextState: Boolean): Boolean {
                return key != -1L
            }

            override fun canSetStateAtPosition(position: Int, nextState: Boolean): Boolean {
                return true
            }

            override fun canSelectMultiple(): Boolean {
                return true
            }
        }

        selectionTracker = SelectionTracker.Builder(
            "selection-id",
            binding.listRecyclerView,
            TaskKeyProvider(binding.listRecyclerView),
            TaskDetailsLookup(binding.listRecyclerView),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(noDividerSelection).build()

        adapter.setSelectionTracker(selectionTracker)

        selectionTracker.addObserver(object : SelectionTracker.SelectionObserver<Long?>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                when (selectionTracker.selection.size()) {
                    0 -> {
                        singleItemSelected = false
                        multipleItemsSelected = false
                    }
                    1 -> {
                        singleItemSelected = true
                        multipleItemsSelected = false
                    }
                    else -> {
                        singleItemSelected = false
                        multipleItemsSelected = true
                    }
                }
                requireActivity().invalidateOptionsMenu()
            }
        })

        return binding.root
    }

    private class TaskKeyProvider(private val mRecyclerView: RecyclerView) :
        ItemKeyProvider<Long>(SCOPE_CACHED) {

        override fun getKey(position: Int): Long = mRecyclerView.adapter!!.getItemId(position)
        override fun getPosition(key: Long): Int =
            mRecyclerView.findViewHolderForItemId(key).layoutPosition
    }

    private class TaskDetailsLookup(private val mRecyclerView: RecyclerView) :
        ItemDetailsLookup<Long>() {
        override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
            val view = mRecyclerView.findChildViewUnder(event.x, event.y)
            if (view != null) {
                val holder = mRecyclerView.getChildViewHolder(view)
                return object : ItemDetails<Long>() {
                    override fun getPosition(): Int = holder.adapterPosition
                    override fun getSelectionKey(): Long = holder.itemId
                }
            }
            return null
        }
    }
}