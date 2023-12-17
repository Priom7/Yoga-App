package com.example.yogaapp;

import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WeekdayDateValidator implements CalendarConstraints.DateValidator {

    private final int targetDayOfWeek;

    public WeekdayDateValidator(int targetDayOfWeek) {
        this.targetDayOfWeek = targetDayOfWeek;
    }

    @Override
    public boolean isValid(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == targetDayOfWeek;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {

    }

    public static MaterialDatePicker<Long> createDatePicker(int targetDayOfWeek) {
        WeekdayDateValidator validator = new WeekdayDateValidator(targetDayOfWeek);

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(validator);

        return MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Class Date")
                .setCalendarConstraints(constraintsBuilder.build())
                .build();
    }

    public static void setupDatePickerClickListener(MaterialButton datePickerButton, TextInputEditText classDateEditText, int targetDayOfWeek, FragmentManager fragmentManager) {
        datePickerButton.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = createDatePicker(targetDayOfWeek);

            datePicker.addOnPositiveButtonClickListener(selection -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String formattedDate = sdf.format(new Date(selection));

                classDateEditText.setText(formattedDate);
            });

            datePicker.show(fragmentManager, "DatePicker");
        });
    }
}
