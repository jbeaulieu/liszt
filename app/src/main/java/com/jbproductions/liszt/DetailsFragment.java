package com.jbproductions.liszt;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private EditText taskNotesEditText;
    private ViewModel viewModel;

    private Task referenceTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register a callback to intercept back button presses and confirm that the user wants to discard changes
        OnBackPressedCallback checkUnsavedChangesCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                // Check if there are unsaved changes
                if (taskNameEditText.getText().toString().equals(referenceTask.getName())
                        && taskStatusCheckBox.isChecked() == referenceTask.getComplete()
                        && viewModel.selectedTask.getDueDate().toString().equals(referenceTask.getDueDate().toString())
                        && taskNotesEditText.getText().toString().equals(referenceTask.getNotes())) {
                    // No changes have been made, go back
                    NavHostFragment.findNavController(DetailsFragment.this).popBackStack();
                } else {
                    // There are unsaved changes, prompt the user before discarding
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity())
                            .setTitle("Discard Changes?")
                            .setPositiveButton("YES", (dialogInterface, i) -> {
                                NavHostFragment.findNavController(DetailsFragment.this).popBackStack();
                            })
                            .setNegativeButton("NO", (dialogInterface, i) -> {});

                    builder.show();
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
        taskNotesEditText = view.findViewById(R.id.task_notes_text);

        // Set the ActionBar title and show the up/back button
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Edit");
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set click listener on due date field to open the DatePickerDialog
        CardView dueDatePicker = view.findViewById(R.id.datePickerCard);
        dueDatePicker.setOnClickListener(v -> createDatePicker());

        //Get a reference to the app's ViewModel, so that we can access data for the selected task
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);

        // Get a clean reference to the task we are editing. This reference will be used to compare for unsaved changes.
        referenceTask = viewModel.getTaskById(viewModel.selectedTask.getId());

        // Set the fragments fields based on the task's details
        taskNameEditText.setText(viewModel.selectedTask.getName());
        taskStatusCheckBox.setChecked(viewModel.selectedTask.getComplete());
        Calendar cal = Calendar.getInstance();
        cal.setTime(viewModel.selectedTask.getDueDate());
        String monthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        String dateText = monthName + " " + cal.get(Calendar.DAY_OF_MONTH) + ", " + cal.get(Calendar.YEAR);
        dueDateTextView.setText(dateText);
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

    public void createDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        if (viewModel.selectedTask.getDueDate() != null) {
            calendar.setTime(viewModel.selectedTask.getDueDate());
        }
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year, month, day) -> {
            calendar.set(year, month, day, 0, 0, 0);
            String monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
            String dueDate = monthName + " " + day + ", " + year;
            dueDateTextView.setText(dueDate);
            viewModel.selectedTask.setDueDate(calendar.getTime());
        }, currentYear, currentMonth, currentDay);

        datePickerDialog.show();
    }
}