package com.jbproductions.liszt;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Fragment to display expanded task details.
 */
public class DetailsFragment extends Fragment {

    private Date dueDate;

    private CardView dueDatePicker;
    //private EditText dueDateText;
    private TextView dueDateText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        //requireActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        dueDatePicker = view.findViewById(R.id.datePickerCard);
        dueDateText = view.findViewById(R.id.due_date_text);

        dueDatePicker.setOnClickListener(this::createDatePicker);

        return view;
    }

    public void createDatePicker(View view) {
        int mYear, mMonth, mDay;
        final Calendar cal = Calendar.getInstance();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view1, year, monthOfYear, dayOfMonth) -> {
                        String month = cal.getDisplayName(monthOfYear, Calendar.LONG, Locale.US);
                        dueDateText.setText(month + " " + dayOfMonth + ", " + year);
                        }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }
}