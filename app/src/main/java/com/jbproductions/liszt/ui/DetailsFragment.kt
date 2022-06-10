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
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.jbproductions.liszt.R
import com.jbproductions.liszt.models.TaskModel
import com.jbproductions.liszt.ViewModel
import com.jbproductions.liszt.util.getReadableDate
import java.util.*

/**
 * Fragment to display expanded task details.
 */
class DetailsFragment : Fragment() {

    private lateinit var taskNameEditText: EditText
    private lateinit var taskStatusCheckBox: CheckBox
    private lateinit var dueDateTextView: TextView
    private lateinit var removeDueDateButton: ImageButton
    private lateinit var taskNotesLayout: TextInputLayout
    private lateinit var taskNotesEditText: TextInputEditText
    private lateinit var viewModel: ViewModel

    private lateinit var selectedTask: TaskModel
    private var dueDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register a callback to intercept back button presses and check for unsaved changes to the Task
        val checkUnsavedChangesCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (hasUnsavedChanges()) {
                    // Prompt the user before discarding changes
                    val builder = AlertDialog.Builder(requireActivity())
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

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_details, container, false)
        taskNameEditText = view.findViewById(R.id.task_name_text)
        taskStatusCheckBox = view.findViewById(R.id.task_checkbox)
        dueDateTextView = view.findViewById(R.id.due_date_text)
        taskNotesLayout = view.findViewById(R.id.task_notes_layout)
        taskNotesEditText = view.findViewById(R.id.task_notes_text)
        removeDueDateButton = view.findViewById(R.id.remove_due_date_button)

        //Get a reference to the app's ViewModel, and then a clean reference to the task we are editing
        viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
        selectedTask = viewModel.selectedTask

        // Set the fragments fields based on the task's details
        taskNameEditText.setText(selectedTask.name)
        taskStatusCheckBox.isChecked = selectedTask.complete
        dueDate = selectedTask.dueDate
        if (dueDate != null) {
            dueDateTextView.text = resources.getString(
                R.string.due_date_string,
                getReadableDate(dueDate!!)
            )
            removeDueDateButton.visibility = View.VISIBLE
        }
        taskNotesEditText.setText(selectedTask.notes)

        // Set listeners on the name field to maintain focus and show/hide the cursor & keyboard appropriately
        taskNameEditText.setOnClickListener {
            taskNameEditText.isCursorVisible = true
        }
        taskNameEditText.onFocusChangeListener =
            OnFocusChangeListener { _: View?, hasFocus: Boolean ->
                taskNameEditText.isCursorVisible = hasFocus
            }
        taskNameEditText.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                taskNameEditText.isCursorVisible = false
                val imm =
                    requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                handled = true
            }
            handled
        }

        // Set click listener on due date field to open the DatePickerDialog
        val dueDatePicker: CardView = view.findViewById(R.id.datePickerCard)
        dueDatePicker.setOnClickListener { createDatePicker() }

        // Set a listener on the "x" button next to the due date to remove the task's due date when pressed
        removeDueDateButton.setOnClickListener {
            dueDate = null
            dueDateTextView.setText(R.string.due_date_label_default)
            removeDueDateButton.visibility = View.GONE
        }

        // Set a listener on the notes field to hide the character counter when the field is empty
        taskNotesEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                taskNotesLayout.isCounterEnabled = editable.toString().isNotEmpty()
            }
        })

        // Set listener on the save button to push updates to the db and go back to the list
        val saveButton: FloatingActionButton = view.findViewById(R.id.fab)
        saveButton.setOnClickListener {
            val newName = taskNameEditText.text.toString()
            if (newName != "") {
                selectedTask.name = newName
            }
            selectedTask.complete = taskStatusCheckBox.isChecked
            selectedTask.dueDate = dueDate
            if (taskNotesEditText.text != null) {
                selectedTask.notes = taskNotesEditText.text.toString()
            }
            selectedTask.modified = Date()
            viewModel.updateTask(selectedTask)
            NavHostFragment.findNavController(this).popBackStack()
        }

        return view
    }

    private fun createDatePicker() {
        val calendar = Calendar.getInstance()
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
                dueDateTextView.text = dueDateText
                removeDueDateButton.visibility = View.VISIBLE
                dueDate = calendar.time
            }, currentYear, currentMonth, currentDay
        )

        datePickerDialog.show()
    }

    /**
     * Helper function to check if there are unsaved changes to the task. Used in the OnBackPressedCallback that is
     * set up in this Fragment's OnCreate().
     */
    private fun hasUnsavedChanges(): Boolean {
        // If any of the name, status, or notes values differ between the VM and ref. task, there are unsaved changes
        return !(taskNameEditText.text.toString() == selectedTask.name
                && taskStatusCheckBox.isChecked == selectedTask.complete
                && selectedTask.dueDateEquals(dueDate)
                && Objects.requireNonNull(taskNotesEditText.text).toString() == selectedTask.notes)
    }
}
