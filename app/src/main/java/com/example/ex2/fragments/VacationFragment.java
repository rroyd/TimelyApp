package com.example.ex2.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ex2.R;
import com.example.ex2.models.Date;
import com.example.ex2.models.Service;
import com.example.ex2.models.Vacation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VacationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VacationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Date fromDate;
    private Date toDate;
    private Button pickDates, goVacation, goBack;
    private TextView chosenVacation;
    private ArrayList<Service> services;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private String userId;
    private DatabaseReference userRef, servicesRef, bookingsRef, vacationsRef;

    public VacationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VacationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VacationFragment newInstance(String param1, String param2) {
        VacationFragment fragment = new VacationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_vacation, container, false);

        pickDates = view.findViewById(R.id.pick_dates);
        goVacation = view.findViewById(R.id.go_vacation);
        goBack = view.findViewById(R.id.go_back);
        chosenVacation = view.findViewById(R.id.vacation);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        userRef = database.getReference().child("users").child(userId);
        servicesRef = database.getReference().child("services");
        bookingsRef = database.getReference().child("bookings");
        vacationsRef = database.getReference().child("vacations");

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.user_fragments, new ProviderFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
        });

        pickDates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarConstraints.Builder calendar = new CalendarConstraints.Builder();
                calendar.setStart(System.currentTimeMillis());

                MaterialDatePicker datePicker = MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText("בחר תאריכי חופשה")
                        .setTheme(R.style.ThemeMaterialCalendar)
                        .setCalendarConstraints(calendar.build())
                        .setSelection(Pair.create(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds()))
                        .build();

                datePicker.show(getActivity().getSupportFragmentManager(), "TAG");

                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        Pair<Long, Long> range = (Pair) selection;
                        Long from = range.first;
                        Long to = range.second;

                        fromDate = convertTimeToDateObject(from);
                        toDate = convertTimeToDateObject(to);

                        chosenVacation.setText(fromDate.showDate() + " - " + toDate.showDate());
                    }
                });

                datePicker.addOnNegativeButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        datePicker.dismiss();
                    }
                });
            }
        });

        goVacation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    validateDates(fromDate, toDate);
                }
                catch (IllegalArgumentException e) {
                    showErrorDialog(e.getMessage());
                }
            }
        });

        return view;
    }

    private void validateDates(Date from, Date to) throws IllegalArgumentException {
        if(fromDate == null || toDate == null) {
            throw new IllegalArgumentException("נא לבחור תאריכים.");
        }

        if (from.getTimestampFromDate() < System.currentTimeMillis() || to.getTimestampFromDate() < System.currentTimeMillis()) {
            throw new IllegalArgumentException("לא ניתן לבחור תאריך עבר.");
        }

        vacationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    String currentUserId;
                    Boolean validVacation = true;

                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Date currentFrom = dataSnapshot.child("from").getValue(Date.class);
                        Date currentTo = dataSnapshot.child("to").getValue(Date.class);
                        currentUserId = dataSnapshot.child("user").getValue(String.class);

                        if(currentUserId.equals(userId)) {
                            validVacation = (currentTo.getTimestampFromDate() <= from.getTimestampFromDate()) || (currentFrom.getTimestampFromDate() >= to.getTimestampFromDate());

                            if(!validVacation) {
                                throw new IllegalArgumentException("כבר הגדרת חופשה בטווח זה, נא לבטל את החופשה שהוגדה ולבצע הגדרה מחדש.");
                            }
                        }
                    }

                    if(validVacation) {
                        vacationsRef.child(UUID.randomUUID().toString()).setValue(new Vacation(fromDate, toDate, userId)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(requireContext(), "החופשה הוגדרה בהצלחה! תהנה בחופשה :)", Toast.LENGTH_SHORT).show();

                                getParentFragmentManager().beginTransaction().replace(R.id.user_fragments, new ProviderFragment())
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                        .commit();
                            }
                        });
                    }
                }
                catch (IllegalArgumentException e) {
                    showErrorDialog(e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private String convertTimeToDate(long timestamp) {
        Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utc.setTimeInMillis(timestamp);

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return format.format(utc.getTime());
    }

    private Date convertTimeToDateObject(long timestamp) {
        Calendar utc = Calendar.getInstance();
        utc.setTimeInMillis(timestamp);

        return new Date(utc.get(Calendar.YEAR), utc.get(Calendar.MONTH), utc.get(Calendar.DAY_OF_MONTH));
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle("הערה")
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("הבנתי", (dialog, which) -> dialog.dismiss())
                .show();
    }
}