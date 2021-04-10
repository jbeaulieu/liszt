package com.jbproductions.liszt;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Fragment to display expanded task details.
 */
public class DetailsFragment extends Fragment {

    private EditText taskNameEditText;
    private CheckBox taskStatusCheckBox;
    private TextView dueDateTextView;
    private ImageButton removeDueDateButton;
    private TextInputLayout taskNotesLayout;
    private TextInputEditText taskNotesEditText;
    private ViewModel viewModel;

    private Task referenceTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register a callback to intercept back button presses and check for unsaved changes to the Task
        OnBackPressedCallback checkUnsavedChangesCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (hasUnsavedChanges()) {
                    // Prompt the user before discarding changes
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity())
                            .setTitle(R.string.discard_changes_confirmation)
                            .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                                NavHostFragment.findNavController(DetailsFragment.this).popBackStack();
                            })
                            .setNegativeButton(R.string.no, (dialogInterface, i) -> {});
                    builder.show();
                } else {
                    // No changes have been made, go back
                    NavHostFragment.findNavController(DetailsFragment.this).popBackStack();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, checkUnsavedChangesCallback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        taskNameEditText = view.findViewById(R.id.task_name_text);
        taskStatusCheckBox = view.findViewById(R.id.task_checkbox);
        dueDateTextView = view.findViewById(R.id.due_date_text);
        taskNotesLayout = view.findViewById(R.id.task_notes_layout);
        taskNotesEditText = view.findViewById(R.id.task_notes_text);
        removeDueDateButton = view.findViewById(R.id.remove_due_date_button);

        // Set the ActionBar title and show the up/back button
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(R.string.details_fragment_title);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get a reference to the app's ViewModel, so that we can access data for the selected task
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);

        // Get a clean reference to the task we are editing. This reference will be used to compare for unsaved changes.
        referenceTask = viewModel.getTaskById(viewModel.selectedTask.getId());

        // Set the fragments fields based on the task's details
        taskNameEditText.setText(viewModel.selectedTask.getName());
        taskStatusCheckBox.setChecked(viewModel.selectedTask.getComplete());
        if (!(viewModel.selectedTask.getDueDate() == null)) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(viewModel.selectedTask.getDueDate());
            String monthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
            String dateText = monthName + " " + cal.get(Calendar.DAY_OF_MONTH) + ", " + cal.get(Calendar.YEAR);
            dueDateTextView.setText(dateText);
            removeDueDateButton.setVisibility(View.VISIBLE);
        }
        taskNotesEditText.setText(viewModel.selectedTask.getNotes());

        // Set listeners on the name field to maintain focus and show/hide the cursor & keyboard appropriately
        taskNameEditText.setOnClickListener(v -> taskNameEditText.setCursorVisible(true));
        taskNameEditText.setOnFocusChangeListener((v, hasFocus) -> taskNameEditText.setCursorVisible(hasFocus));
        taskNameEditText.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                taskNameEditText.setCursorVisible(false);
                InputMethodManager imm = (InputMethodManager)
                        requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                handled = true;
            }
            return handled;
        });

        // Set click listener on due date field to open the DatePickerDialog
        CardView dueDatePicker = view.findViewById(R.id.datePickerCard);
        dueDatePicker.setOnClickListener(v -> createDatePicker());

        // Set a listener on the "x" button next to the due date to remove the task's due date when pressed
        removeDueDateButton.setOnClickListener(v -> {
            viewModel.selectedTask.setDueDate(null);
            dueDateTextView.setText(R.string.due_date_label_default);
            removeDueDateButton.setVisibility(View.GONE);
        });

        // Set a listener on the notes field to hide the character counter when the field is empty
        taskNotesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                taskNotesLayout.setCounterEnabled(editable.toString().length() != 0);
            }
        });

        // Set listener on the save button to push updates to the db and go back to the list
        FloatingActionButton saveButton = view.findViewById(R.id.fab);
        saveButton.setOnClickListener(v -> {
            Task newDetailsTask = viewModel.selectedTask;
            String newName = taskNameEditText.getText().toString();
            if (!newName.equals("")) {
                newDetailsTask.setName(newName);
            }
            newDetailsTask.setComplete(taskStatusCheckBox.isChecked());
            newDetailsTask.setNotes(taskNotesEditText.getText().toString());
            newDetailsTask.setModified(new Date());
            viewModel.update(newDetailsTask);
            NavHostFragment.findNavController(this).popBackStack();
        });

        return view;
    }

    void createDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        if (viewModel.selectedTask.getDueDate() != null) {
            calendar.setTime(viewModel.selectedTask.getDueDate());
        }
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year, month, day) -> {
            calendar.set(year, month, day, 0, 0, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            String monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
            String dueDate = monthName + " " + day + ", " + year;
            dueDateTextView.setText(dueDate);
            removeDueDateButton.setVisibility(View.VISIBLE);
            viewModel.selectedTask.setDueDate(calendar.getTime());
        }, currentYear, currentMonth, currentDay);

        datePickerDialog.show();
    }

    /**
     * Helper function to check if there are unsaved changes to the task.
     * Currently only used in the OnBackPressedCallback that is set up in DetailFragment's OnCreate(). This check has
     * been isolated to a separate function to maintain readability.
     * @return boolean value indicating the presence of unsaved changes.
     */
    boolean hasUnsavedChanges() {

        // If any of the name, status, or notes values differ between the VM and ref. task, there are unsaved changes
        return !(taskNameEditText.getText().toString().equals(referenceTask.getName())
                && taskStatusCheckBox.isChecked() == referenceTask.getComplete()
                && viewModel.selectedTask.dueDateEquals(referenceTask.getDueDate())
                && taskNotesEditText.getText().toString().equals(referenceTask.getNotes()));
    }
}