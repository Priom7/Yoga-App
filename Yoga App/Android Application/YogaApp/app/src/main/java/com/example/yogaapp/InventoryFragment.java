package com.example.yogaapp;

import static com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK;

import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InventoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InventoryFragment extends Fragment {
    View rootView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InventoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InventoryFragment newInstance(String param1, String param2) {
        InventoryFragment fragment = new InventoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public InventoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(10)
                .setTitleText("Select Class time")
                .build();
        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Respond to positive button click.
                int hour = picker.getHour();
                int minute = picker.getMinute();

                // Create a Calendar instance to convert to Date
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);

                // Get the Date object
                Date selectedTime = calendar.getTime();

                // Format the Date object to a specific time format
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                String formattedTime = sdf.format(selectedTime);

                // Now, formattedTime contains the selected time in "hh:mm AM/PM" format
                Log.d("SelectedTime", formattedTime);
            }
        });

        picker.show(getParentFragmentManager(), "tag");
//        final MaterialButton timePickerButton = rootView.findViewById(R.id.timePickerButton);
//
//        timePickerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int hour = 12; // Initial time (you can change this)
//                int minute = 0; // Initial time (you can change this)
//
//                TimePickerDialog timePickerDialog = new TimePickerDialog(
//                        getActivity(),
//                        new TimePickerDialog.OnTimeSetListener() {
//                            @Override
//                            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
//                                // Handle the selected time (selectedHour and selectedMinute)
//                            }
//                        },
//                        hour,
//                        minute,
//                        false // 24-hour format (true for 24-hour format)
//                );
//
//                timePickerDialog.show();
//            }
//        });
        rootView = inflater.inflate(R.layout.fragment_inventory, container, false);
        return rootView;
    }
}