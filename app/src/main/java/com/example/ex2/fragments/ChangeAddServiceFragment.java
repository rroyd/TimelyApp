package com.example.ex2.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.ex2.R;
import com.example.ex2.activities.UserActivity;
import com.example.ex2.adapters.ServiceAdapter;
import com.example.ex2.models.Range;
import com.example.ex2.models.Service;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangeAddServiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangeAddServiceFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static Map<Integer, String> DAYS;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private Map<String, Range> newServiceAvailability;
    private String mParam2;
    private EditText newServiceName, servicePrice;
    private AutoCompleteTextView currency;
    private Integer currentDurationHourSelected, currentDurationMinuteSelected;
    private boolean isSetStartHour, isSetEndHour;
    private Map<String, String> hebrewMap = Map.of(
            "saturday", "ש'",
            "friday", "ו'",
            "thursday", "ה'",
             "wendesday","ד'",
             "tuesday","ג'",
             "monday","ב'",
             "sunday", "א'"
    );
    private NumberPicker newServiceDurationHour, newServiceDurationMinute;
    private int selectedStartHour, selectedStartMinute, selectedEndHour, selectedEndMinute;
    private Button setStartHour, setEndHour, setAllDaysRanges, clearRange;
    private Button createNewService, cancelNewService;
    private Button addNewService, goBack;
    private RecyclerView servicesRecyclerView;
    private ServiceAdapter adapter;
    private ArrayList<Service> services;
    private FirebaseAuth mAuth;
    private String selectedDay;
    private FirebaseDatabase database;
    private TabLayout selectedDayTabLayout;
    private String userId;
    private DatabaseReference userRef, servicesRef, bookingsRef;

    public ChangeAddServiceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddServiceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChangeAddServiceFragment newInstance(String param1, String param2) {
        ChangeAddServiceFragment fragment = new ChangeAddServiceFragment();
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
        View view = inflater.inflate(R.layout.fragment_add_service, container, false);

        DAYS = Map.of(
                0, "saturday",
                1, "friday",
                2, "thursday",
                3, "wendesday",
                4, "tuesday",
                5, "monday",
                6, "sunday"
        );

        servicesRecyclerView = view.findViewById(R.id.services);
        addNewService = view.findViewById(R.id.new_service);
        goBack = view.findViewById(R.id.go_back);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        userRef = database.getReference().child("users").child(userId);
        servicesRef = database.getReference().child("services");
        bookingsRef = database.getReference().child("bookings");

        adapter = new ServiceAdapter(new ArrayList<Service>(), new ServiceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Service service) {
                editService(service);
            }
        },
                new ServiceAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Service service) {
                        removeService(service);
                    }
                });

        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        servicesRecyclerView.setAdapter(adapter);

        setServices();

        addNewService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateServiceDialog();
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.user_fragments, new ProviderFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
        });

        return view;
    }

    private void editService(Service service) {

    }

    private void removeService(Service service) {
        new AlertDialog.Builder(requireActivity())
                .setTitle("האם אתה בטוח?")
                .setMessage(String.format("האם אתה בטוח שברצונך לבטל את השירות '%s'? (כל התורים לשירות זה יבוטלו כתוצאה מכך!)", service.getServiceName()))
                .setPositiveButton("כן", ((dialogInterface, i) -> {
                    List<Task<Void>> tasks = new ArrayList<>();

                    tasks.add(servicesRef.child(service.getServiceId()).removeValue());

                    bookingsRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                String serviceId = data.child("service").child("serviceId").getValue(String.class);

                                if(serviceId.equals(service.getServiceId())) {
                                    tasks.add(data.getRef().removeValue());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Tasks.whenAllComplete(tasks)
                            .addOnSuccessListener(new OnSuccessListener<List<Task<?>>>() {
                                @Override
                                public void onSuccess(List<Task<?>> tasks) {
                                    Toast.makeText(requireContext(), "השירות נמחק, וגם כל התורים לשירות זה, בהצלחה." , Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(requireContext(), "מחיקת השירות נכשלה, " + e , Toast.LENGTH_LONG).show();
                                }
                            });

                    dialogInterface.dismiss();
                }))
                .setNegativeButton("לא", ((dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })).show();
    }

    public void setServices() {
        servicesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Service> itemList = new ArrayList<>();

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    String producer = itemSnapshot.child("producer").getValue(String.class);
                    Service service;

                    if (producer != null && producer.equals(userId)) {
                        service = itemSnapshot.getValue(Service.class);
                        service.setServiceId(itemSnapshot.getKey());

                        itemList.add(service);
                    }
                }

                services = itemList;
                adapter.updateList(itemList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initializeTime() {
        newServiceAvailability = Map.of(
                Objects.requireNonNull(DAYS.get(0)), new Range(),
                Objects.requireNonNull(DAYS.get(1)), new Range(),
                Objects.requireNonNull(DAYS.get(2)), new Range(),
                Objects.requireNonNull(DAYS.get(3)), new Range(),
                Objects.requireNonNull(DAYS.get(4)), new Range(),
                Objects.requireNonNull(DAYS.get(5)), new Range(),
                Objects.requireNonNull(DAYS.get(6)), new Range()
        );

        newServiceAvailability = new HashMap<>(newServiceAvailability);
    }

    private void showCreateServiceDialog() {
        initializeTime();

        Dialog dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.new_service_dialog);

        List<String> currencies = Arrays.asList("₪", "$", "€", "£");
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(requireActivity(), R.layout.currency_dropdown, currencies);

        newServiceName = dialog.findViewById(R.id.service_name);
        currency = dialog.findViewById(R.id.currency_dropdown);
        currency.setAdapter(currencyAdapter);
        servicePrice = dialog.findViewById(R.id.service_price);
        clearRange = dialog.findViewById(R.id.clear_range);
        setAllDaysRanges = dialog.findViewById(R.id.set_for_all_days);
        selectedDayTabLayout = dialog.findViewById(R.id.selected_day);
        newServiceDurationHour = dialog.findViewById(R.id.service_duartion_hour);

        newServiceDurationHour.setValue(0);
        newServiceDurationHour.setMinValue(0);
        newServiceDurationHour.setMaxValue(12);
        newServiceDurationMinute = dialog.findViewById(R.id.service_duartion_minute);

        newServiceDurationMinute.setValue(0);
        newServiceDurationMinute.setMinValue(0);
        newServiceDurationMinute.setMaxValue(59);

        createNewService = dialog.findViewById(R.id.add_service);
        cancelNewService = dialog.findViewById(R.id.cancel_new_service);
        setStartHour = dialog.findViewById(R.id.select_start_time);
        setEndHour = dialog.findViewById(R.id.select_end_time);

        selectedDayTabLayout.getTabAt(0).select();
        selectedDay = DAYS.get(0);

        selectedDayTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedDay = DAYS.get(tab.getPosition());

                if(newServiceAvailability.get(selectedDay).getIsSetStart()) {
                    setStartHour.setText(String.format("%02d:%02d (לחץ לשינויי)",
                            newServiceAvailability.get(selectedDay).getStartHour(),
                            newServiceAvailability.get(selectedDay).getStartMinute()));
                }
                else {
                    setStartHour.setText("בחר שעת התחלה");
                }

                if(newServiceAvailability.get(selectedDay).getIsSetEnd()) {
                    setEndHour.setText(String.format("%02d:%02d (לחץ לשינויי)",
                            newServiceAvailability.get(selectedDay).getEndHour(),
                            newServiceAvailability.get(selectedDay).getEndMinute()));
                }
                else {
                    setEndHour.setText("בחר שעת סיום");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        cancelNewService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        setStartHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpStartTimePicker();
            }
        });

        setEndHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpEndTimePicker();
            }
        });

        currency.setOnClickListener(v -> currency.showDropDown());


        createNewService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    validateServiceForCreation();

                    String serviceName = newServiceName.getText().toString();
                    String duration = String.format("%02d:%02d", newServiceDurationHour.getValue(), newServiceDurationMinute.getValue());
                    Map<String, Range> availability = newServiceAvailability;
                    String currencyStr = currency.getText().toString();
                    Double servicePriceDouble = Double.parseDouble(servicePrice.getText().toString());
                    String serviceId = UUID.randomUUID().toString();

                    Service service = new Service(serviceName, duration, availability, servicePriceDouble, currencyStr);

                    List<Task<Void>> tasks = new ArrayList<>();

                    Task<Void> task1 =  servicesRef.child(serviceId).setValue(service);
                    Task<Void> task2 = servicesRef.child(serviceId).child("producer").setValue(userId);

                    Tasks.whenAll(tasks)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(requireContext(), "השירות נוצר בהצלחה!", Toast.LENGTH_LONG).show();

                                    requireActivity().startActivity(new Intent(requireActivity(), UserActivity.class));
                                    requireActivity().finish();
                                }}).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(requireContext(), String.format("יצירת השירות נכשלה, %s", e.getMessage()), Toast.LENGTH_LONG).show();
                                }
                            });
                }
                catch (IllegalArgumentException e) {
                    showErrorDialog(e.getMessage());
                }
            }
        });

        setAllDaysRanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Range currentRange = newServiceAvailability.get(selectedDay);

                for(Map.Entry<String, Range> entry : newServiceAvailability.entrySet()) {
                    newServiceAvailability.put(entry.getKey(), new Range(currentRange));
                }

                Toast.makeText(requireContext(),  "כל הימים הוגדרו לטווח זמן של יום זה", Toast.LENGTH_LONG).show();
            }
        });

        clearRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newServiceAvailability.put(selectedDay, new Range());

                setStartHour.setText("בחר שעת התחלה");
                setEndHour.setText("בחר שעת סיום");
            }
        });

        dialog.show();
    }

    private void popUpStartTimePicker() {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                try {
                    if (newServiceAvailability.get(selectedDay).getIsSetEnd()) {
                        validateStartHour(selectedHour, selectedMinute);
                    }

                    newServiceAvailability.get(selectedDay).setStartTime(selectedHour, selectedMinute);
                    setStartHour.setText(String.format("%02d:%02d (לחץ לשינויי)", selectedHour, selectedMinute));

                    newServiceAvailability.get(selectedDay).setIsSetStart(true);
                }
                catch (IllegalArgumentException e) {
                    showErrorDialog(e.getMessage());
                }
            }
        };

        int style = AlertDialog.THEME_HOLO_LIGHT;

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireActivity(), style, onTimeSetListener, selectedStartHour, selectedStartMinute, true);

        timePickerDialog.setTitle("שעת התחלת השירות");

        timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "אישור", timePickerDialog);
        timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "ביטול", timePickerDialog);

        timePickerDialog.show();
    }

    private void validateStartHour(int hour, int minute) {
        boolean isValid = true;
        String message = "";

        if (hour == newServiceAvailability.get(selectedDay).getEndHour()) {
            if (minute >= newServiceAvailability.get(selectedDay).getEndMinute()) {
                isValid = false;
            }
        }
        else if (hour > newServiceAvailability.get(selectedDay).getEndHour()) {
            isValid = false;
        }

        if(!isValid) {
            message = "לא ניתן לשים שעה יותר מאוחרת מזמו הסיום.";
            throw new IllegalArgumentException(message);
        }
    }

    private void popUpEndTimePicker() {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                try {
                    if (newServiceAvailability.get(selectedDay).getIsSetStart()) {
                        validateEndHour(selectedHour, selectedMinute);
                    }

                    newServiceAvailability.get(selectedDay).setEndTime(selectedHour, selectedMinute);

                    setEndHour.setText(String.format("%02d:%02d (לחץ לשינויי)", selectedHour, selectedMinute));

                    newServiceAvailability.get(selectedDay).setIsSetEnd(true);
                }
                catch (IllegalArgumentException e) {
                    showErrorDialog(e.getMessage());
                }
            }
        };

        int style = AlertDialog.THEME_HOLO_LIGHT;

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireActivity(),style, onTimeSetListener, selectedEndHour, selectedEndMinute, true);

        timePickerDialog.setTitle("שעת סיום השירות");

        timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "אישור", timePickerDialog);
        timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "ביטול", timePickerDialog);

        timePickerDialog.show();
    }

    private void validateEndHour(int hour, int minute) throws IllegalArgumentException {
        boolean isValid = true;
        String message = "";

        if (hour == newServiceAvailability.get(selectedDay).getStartHour()) {
            if (minute <= newServiceAvailability.get(selectedDay).getStartMinute()) {
                isValid = false;
            }
        }
        else if (hour < newServiceAvailability.get(selectedDay).getStartHour()) {
            isValid = false;
        }

        if(!isValid) {
            message = "לא ניתן לשים שעה יותר מוקדמת מזמו ההתחלה.";
            throw new IllegalArgumentException(message);
        }
    }

    public void showErrorDialog(String errorMessage) {
        new AlertDialog.Builder(requireContext())
                .setTitle("שגיאה")
                .setMessage(errorMessage)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("הבנתי", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void validateServiceForCreation() throws IllegalArgumentException {
        String message = null;
        boolean hasAtleastOneDayWorking = false;

        if (newServiceDurationMinute.getValue() == 0 && newServiceDurationHour.getValue() == 0) {
            message = "השירות חייב להיות לפחות דקה.";
        }

        Range range;

        for(Map.Entry<String, Range> entry : newServiceAvailability.entrySet()) {
            range = entry.getValue();

            if (range.getIsSetStart() == range.getIsSetEnd() && range.getIsSetStart()) {
                hasAtleastOneDayWorking = true;
            }

            if (range.getIsSetStart() != range.getIsSetEnd()) {
                if(!range.getIsSetStart()) {
                    message = String.format("יום %s נמצא ללא זמן התחלת השירות", hebrewMap.get(entry.getKey()));
                }
                else {
                    message = String.format("יום %s נמצא ללא זמן סיום השירות", hebrewMap.get(entry.getKey()));
                }
            }
        }

        if(servicePrice.getText().toString().isEmpty()) {
            message = "אנא הזן מחיר לשירות";
        }

        if(Double.parseDouble(servicePrice.getText().toString()) < 0) {
            message = "מחיר השירות לא יכול להיות שלילי";
        }

        if(!hasAtleastOneDayWorking) {
            message = "השירות חייב להכיל לפחות יום עבודה אחד בשבוע";
        }

        if (newServiceName.getText().toString().isEmpty()) {
            message = "אנא הזן שם שירות";
        }

        if (message != null) {
            throw new IllegalArgumentException(message);
        }
    }
}