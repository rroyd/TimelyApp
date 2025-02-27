package com.example.ex2.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ex2.R;
import com.example.ex2.activities.UserActivity;
import com.example.ex2.adapters.DateAdapter;
import com.example.ex2.adapters.ServiceForCustomerAdapter;
import com.example.ex2.models.BookingService;
import com.example.ex2.models.Date;
import com.example.ex2.models.Range;
import com.example.ex2.models.Service;
import com.example.ex2.models.Vacation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateBookingService#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateBookingService extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Map<Integer, String> map = DAYS = Map.of(
            0, "saturday",
            1, "friday",
            2, "thursday",
            3, "wendesday",
            4, "tuesday",
            5, "monday",
            6, "sunday"
    );
    private RecyclerView availableDates;
    private String serviceDuration;
    private ArrayList<Date> dates;
    private DateAdapter adapter;
    private Service serviceToBook;
    private String producerId;
    private static Map<Integer, String> DAYS;

    // TODO: Rename and change types of parameters
    private TextView serviceName, subtitle;
    private Button bookService, cancel;
    private Date currentDateSelected;
    private Map<String, Range> newServiceAvailability;
    private EditText searchText;
    private ArrayList<BookingService> allBookingsToService;
    private RecyclerView servicesForCustomer;
    private Integer currentDurationHourSelected, currentDurationMinuteSelected;
    private boolean isSetStartHour, isSetEndHour;
    private CalendarView bookingDate;
    private Map<String, String> hebrewMap = Map.of(
            "saturday", "ש'",
            "friday", "ו'",
            "thursday", "ה'",
            "wendesday","ד'",
            "tuesday","ג'",
            "monday","ב'",
            "sunday", "א'"
    );
    private Button searchBtn, goBack;
    private RecyclerView servicesRecyclerView;
    private String type;
    private ArrayList<Service> services;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private String userId;
    private BookingService bookingServiceToEdit;
    private DatabaseReference userRef,vacationsRef, serviceRef, producerRef, allBookingsRef, allBookingsToServiceRef;

    public CreateBookingService() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateBookingService.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateBookingService newInstance(String param1, String param2) {
        CreateBookingService fragment = new CreateBookingService();
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
            type = (String)getArguments().getString("type");

            if (type.equals("provider")) {
                bookingServiceToEdit = (BookingService) getArguments().getSerializable("bookingService");
                serviceToBook = bookingServiceToEdit.getService();
            }
            else {
                serviceToBook = (Service) getArguments().getSerializable("service");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_booking_service, container, false);

        DAYS = Map.of(
                7, "saturday",
                6, "friday",
                5, "thursday",
                4, "wendesday",
                3, "tuesday",
                2, "monday",
                1, "sunday"
        );

        allBookingsToService = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        userRef = database.getReference().child("users").child(userId);
        serviceRef = database.getReference().child("services").child(serviceToBook.getServiceId());
        producerRef = database.getReference().child("users").child(serviceToBook.getProducer());
        allBookingsRef = database.getReference().child("bookings");
        vacationsRef = database.getReference().child("vacations");

        allBookingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()) {
                    BookingService bookingService = data.getValue(BookingService.class);

                    if (bookingService.getService().getServiceId().equals(serviceToBook.getServiceId())) {
                        allBookingsToService.add(bookingService);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        subtitle = view.findViewById(R.id.subtitle);
        bookingDate = view.findViewById(R.id.booking_date);
        serviceName = view.findViewById(R.id.service_name);
        availableDates = view.findViewById(R.id.available_dates);
        goBack = view.findViewById(R.id.go_back);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment;

                if(type.equals("consumer")) {
                    fragment = new BookServiceFragment();
                }
                else {
                    fragment = new ProviderFragment();
                }

                getParentFragmentManager().beginTransaction().replace(R.id.user_fragments, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
        });

        long today = System.currentTimeMillis();
        bookingDate.setMinDate(today);
        bookingDate.setDate(today, false, true);

        dates = new ArrayList<>();
        adapter = new DateAdapter(dates, new DateAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Date date) {
                if(type.equals("consumer")) {
                    selectDate(date);
                }
                else {
                    selectDateToEdit(date);
                }
            }
        });

        availableDates.setLayoutManager(new LinearLayoutManager(requireActivity()));
        availableDates.setAdapter(adapter);

        bookingDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                dates = new ArrayList<>();

                if (checkIfProducerInVacation(year, month, day)) {
                    subtitle.setText("בעל העסק נמצא בחופשה ביום זה.");
                }
                else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day);
                    int dayOfTheWeek = calendar.get(Calendar.DAY_OF_WEEK);

                    String duration = serviceToBook.getDuration(); // "05:00"
                    String dayChosen = DAYS.get(dayOfTheWeek);
                    Range rangeOfServiceAtDay = serviceToBook.getAvailability().get(dayChosen);
                    Integer durationHours = Integer.parseInt(duration.split(":")[0]);
                    Integer durationMinutes = Integer.parseInt(duration.split(":")[1]);
                    Boolean isSetStart = rangeOfServiceAtDay.getIsSetStart();
                    Boolean isSetEnd = rangeOfServiceAtDay.getIsSetEnd();
                    Integer currentHour, currentMinute;

                    if (!isSetStart && !isSetEnd) {
                        subtitle.setText("אין תורים זמינים ביום זה.");
                    } else {
                        subtitle.setText("מועדים פנויים:");

                        currentHour = rangeOfServiceAtDay.getStartHour();
                        currentMinute = rangeOfServiceAtDay.getStartMinute();

                        boolean isInRange = !(currentHour > rangeOfServiceAtDay.getEndHour() ||
                                    (currentHour == rangeOfServiceAtDay.getEndHour() &&
                                            currentMinute > rangeOfServiceAtDay.getEndMinute()) ||
                                    currentHour < rangeOfServiceAtDay.getStartHour() ||
                                    (currentHour == rangeOfServiceAtDay.getStartHour() &&
                                            currentMinute < rangeOfServiceAtDay.getStartMinute()));

                        while (isInRange) {
                            Date date = new Date(year, month, day, currentHour, currentMinute);
                            Boolean isAvailable = checkIfBookingAvailable(date);

                            if (isAvailable && date.getTimestampFromDate() > System.currentTimeMillis()) {
                                dates.add(date);
                            }

                            currentHour = (currentHour + durationHours) % 24;

                            if (currentMinute + durationMinutes > 59) {
                                currentHour = (currentHour + 1) % 24;
                            }

                            currentMinute = (currentMinute + durationMinutes) % 60;

                            isInRange = !(currentHour > rangeOfServiceAtDay.getEndHour() ||
                                        (currentHour == rangeOfServiceAtDay.getEndHour() &&
                                                currentMinute > rangeOfServiceAtDay.getEndMinute()) ||
                                        currentHour < rangeOfServiceAtDay.getStartHour() ||
                                        (currentHour == rangeOfServiceAtDay.getStartHour() &&
                                                currentMinute < rangeOfServiceAtDay.getStartMinute()));
                            }
                        }

                    if (dates.size() == 0) {
                        subtitle.setText("אין תורים זמינים ביום זה.");
                    }
                }

                adapter.updateList(dates);
            }
        });

        return view;
    }

    private void selectDateToEdit(Date date) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.edit_booking_service_dialog);

        TextView info = dialog.findViewById(R.id.info);
        Button cancel, book;

        cancel = dialog.findViewById(R.id.cancel);
        book = dialog.findViewById(R.id.book);

        info.setText(String.format("לערוך תור מתאריך %s לתאריך %s לשירות `%s`?",bookingServiceToEdit.getWhen().toString() , date.toString(), serviceToBook.getServiceName()));

        cancel.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          dialog.dismiss();
                                      }
                                  }
        );

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allBookingsRef.child(bookingServiceToEdit.getBookingServiceId()).child("when").setValue(date)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(requireContext(), "השירות נערך בהצלחה!", Toast.LENGTH_LONG).show();

                                dialog.dismiss();
                                requireActivity().startActivity(new Intent(requireActivity(), UserActivity.class));
                                requireActivity().finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(requireContext(), e.getMessage() + "לא ניתן לערוך שירות", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        dialog.show();
    }

    private boolean checkIfProducerInVacation(int year, int month, int day) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Task<DataSnapshot> dataSnapshotTask = vacationsRef.get();

        try {
            return executorService.submit(() -> {
                DataSnapshot dataSnapshot = Tasks.await(dataSnapshotTask);
                boolean isInVacationAtThisDate = false;
                Date dateChosen = new Date(year,month,day);
                Vacation currentVacation;

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    currentVacation = data.getValue(Vacation.class);

                    if (currentVacation.getUser().equals(serviceToBook.getProducer())) {
                        boolean dateInBetween = dateChosen.getTimestampFromDate() >= currentVacation.getFrom().getTimestampFromDate()
                                && dateChosen.getTimestampFromDate() <= currentVacation.getTo().getTimestampFromDate();

                        if (dateInBetween)
                        {
                            isInVacationAtThisDate = true;

                            break;
                    }
                }
                }

                return isInVacationAtThisDate;
            }).get();
        }
        catch (ExecutionException | InterruptedException e) {
            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
        finally {
            executorService.shutdown();
        }
    }

    private boolean checkIfBookingAvailable(Date date) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Task<DataSnapshot> dataSnapshotTask =  allBookingsRef.orderByChild("service/serviceId").equalTo(serviceToBook.getServiceId()).get();

        try {
            return executorService.submit(() -> {
                DataSnapshot dataSnapshot = Tasks.await(dataSnapshotTask);
                boolean serviceAvailable = true;

                for (DataSnapshot booking : dataSnapshot.getChildren()) {
                    BookingService bookingService = booking.getValue(BookingService.class);

                    if(bookingService != null && bookingService.getWhen().equals(date)) {
                        serviceAvailable = false;
                        break;
                    }
                }

                return serviceAvailable;
            }).get();
        }
        catch (ExecutionException | InterruptedException e) {
            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT);
            return false;
        }
        finally {
            executorService.shutdown();
        }
    }

    private void selectDate(Date date) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.book_new_service_for_customer_dialog);

        TextView info = dialog.findViewById(R.id.info);
        Button cancel, book;

        cancel = dialog.findViewById(R.id.cancel);
        book = dialog.findViewById(R.id.book);

        info.setText(String.format("לקבוע תור לתאריך %s לשירות `%s`?", date.toString(), serviceToBook.getServiceName()));

        cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                dialog.dismiss();
            }
            }
        );

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BookingService bookingService = new BookingService(serviceToBook, date);
                bookingService.setCustomerId(userId);
                String bookingServiceId = UUID.randomUUID().toString();

                allBookingsRef.child(bookingServiceId).setValue(bookingService).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(requireContext(), "השירות נקבע בהצלחה!", Toast.LENGTH_LONG).show();

                        dialog.dismiss();

                        requireActivity().startActivity(new Intent(requireActivity(), UserActivity.class));
                        requireActivity().finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        dialog.show();
    }
}