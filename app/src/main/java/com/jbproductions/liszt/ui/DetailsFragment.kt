package com.jbproductions.liszt.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.jbproductions.liszt.R
import com.jbproductions.liszt.ViewModel
import com.jbproductions.liszt.databinding.FragmentDetailsBinding
import com.jbproductions.liszt.models.TaskModel
import com.jbproductions.liszt.util.getReadableDate
import java.util.*

/**
 * Fragment to display expanded task details.
 */
class DetailsFragment : Fragment() {

    // Using long convention so variable can be set to null later in onDestroyView.
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ViewModel
    private lateinit var selectedTask: TaskModel
    private var dueDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register a callback to intercept back button presses and check for unsaved changes to the Task
        val checkUnsavedChangesCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (hasUnsavedChanges(binding)) {
                        // Prompt the user before discarding changes
                        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
                            .setTitle(R.string.discard_changes_confirmation)
                            .setPositiveButton(R.string.yes) { _, _ ->
                                NavHostFragment.findNavController(this@DetailsFragment).popBackStack()
                            }
                            .setNegativeButton(R.string.no) { _, _ -> }
                        builder.show()
                    } else {
                        // No changes have been made, go back
                        NavHostFragment.findNavController(this@DetailsFragment).popBackStack()
                    }
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(this, checkUnsavedChangesCallback)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {

        // Inflate the layout for this fragment
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)

        //Get a reference to the app's ViewModel, and then a clean reference to the task we are editing
        viewModel = ViewModelProvider(requireActivity())[ViewModel::class.java]
        selectedTask = viewModel.selectedTask

        // Set the fragments fields based on the task's details
        binding.taskNameText.setText(selectedTask.name)
        binding.taskCheckbox.isChecked = selectedTask.complete
        dueDate = selectedTask.dueDate
        if (dueDate != null) {
            binding.dueDateText.text = resources.getString(
                R.string.due_date_string,
                getReadableDate(dueDate!!)
            )
            binding.removeDueDateButton.visibility = View.VISIBLE
        }
        binding.taskNotesText.setText(selectedTask.notes)

        // Set listeners on the name field to maintain focus and show/hide the cursor & keyboard appropriately
        binding.taskNameText.setOnClickListener {
            binding.taskNameText.isCursorVisible = true
        }
        binding.taskNameText.onFocusChangeListener =
            OnFocusChangeListener { _: View?, hasFocus: Boolean ->
                binding.taskNameText.isCursorVisible = hasFocus
            }
        binding.taskNameText.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                binding.taskNameText.isCursorVisible = false
                val imm: InputMethodManager =
                    requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view?.windowToken, 0)
                handled = true
            }
            handled
        }

        // Set click listener on due date field to open the DatePickerDialog
        binding.datePickerCard.setOnClickListener { createDatePicker() }

        // Set a listener on the "x" button next to the due date to remove the task's due date when pressed
        binding.removeDueDateButton.setOnClickListener {
            dueDate = null
            binding.dueDateText.setText(R.string.due_date_label_default)
            binding.removeDueDateButton.visibility = View.GONE
        }

        // Set a listener on the notes field to hide the character counter when the field is empty
        binding.taskNotesText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                binding.taskNotesLayout.isCounterEnabled = editable.toString().isNotEmpty()
            }
        })

        // Set listener on the save button to push updates to the db and go back to the list fragment
        binding.fab.setOnClickListener {
            val newName = binding.taskNameText.text.toString()
            if (newName != "") {
                selectedTask.name = newName
            }
            selectedTask.complete = binding.taskCheckbox.isChecked
            selectedTask.dueDate = dueDate
            if (binding.taskNotesText.text != null) {
                selectedTask.notes = binding.taskNotesText.text.toString()
            }
            selectedTask.modified = Date()
            viewModel.updateTask(selectedTask)
            NavHostFragment.findNavController(this).popBackStack()
        }

        return binding.root
    }

    private fun createDatePicker() {
        val calendar: Calendar = Calendar.getInstance()
        if (dueDate != null) {
            calendar.time = dueDate!!
        }
        val currentYear = calendar[Calendar.YEAR]
        val currentMonth = calendar[Calendar.MONTH]
        val currentDay = calendar[Calendar.DAY_OF_MONTH]

        val datePickerDialog = DatePickerDialog(requireContext(),
            { _: DatePicker?, year: Int, month: Int, day: Int ->
                calendar[year, month, day, 0, 0] = 0
                calendar[Calendar.MILLISECOND] = 0
                val dueDateText =
                    "Due " + getReadableDate(calendar.time)
                binding.dueDateText.text = dueDateText
                binding.removeDueDateButton.visibility = View.VISIBLE
                dueDate = calendar.time
            }, currentYear, currentMonth, currentDay
        )

        datePickerDialog.show()
    }

    /**
     * Helper function to check if there are unsaved changes to the task. Used in the OnBackPressedCallback that is
     * set up in this Fragment's OnCreate().
     */
    private fun hasUnsavedChanges(binding: FragmentDetailsBinding): Boolean {
        // If any of the name, status, or notes values differ between the VM and ref. task, there are unsaved changes
        return !(
                Objects.requireNonNull(binding.taskNameText.text.toString()) == selectedTask.name
                && Objects.requireNonNull(binding.taskCheckbox.isChecked) == selectedTask.complete
                && selectedTask.dueDateEquals(dueDate)
                && Objects.requireNonNull(binding.taskNotesText.text).toString() == selectedTask.notes
                )
    }

    /**
     * Release the view when the fragment is destroyed
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
