package com.example.ex2.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.ex2.R;
import com.example.ex2.adapters.ServiceAdapter;
import com.example.ex2.adapters.ServiceForCustomerAdapter;
import com.example.ex2.models.Range;
import com.example.ex2.models.Service;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookServiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookServiceFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static Map<Integer, String> DAYS;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private TextView title, priceAndProducer;
    private Button bookService, cancel;
    private Map<String, Range> newServiceAvailability;
    private String mParam2;
    private EditText searchText;
    private RecyclerView servicesForCustomer;
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
    private Button searchBtn, goBack;
    private RecyclerView servicesRecyclerView;
    private ServiceForCustomerAdapter adapter;
    private ArrayList<Service> services;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private String userId;
    private DatabaseReference userRef, servicesRef, bookingsRef;

    public BookServiceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookServiceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookServiceFragment newInstance(String param1, String param2) {
        BookServiceFragment fragment = new BookServiceFragment();
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
        View view = inflater.inflate(R.layout.fragment_book_service, container, false);

        servicesForCustomer = view.findViewById(R.id.services_for_customer);
        searchBtn = view.findViewById(R.id.search);
        searchText = view.findViewById(R.id.serach_text);
        goBack = view.findViewById(R.id.go_back);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.user_fragments,
                                new ConsumerFragment())
                        .addToBackStack("book_service")
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
        });

        services = new ArrayList<>();
        adapter = new ServiceForCustomerAdapter(services, service -> {
            openBookServiceDialog(service);
        });

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        userRef = database.getReference().child("users").child(userId);
        servicesRef = database.getReference().child("services");
        bookingsRef = database.getReference().child("bookings");

        servicesForCustomer.setLayoutManager(new LinearLayoutManager(requireActivity()));
        servicesForCustomer.setAdapter(adapter);

        setServices();

        return view;
    }

    public void setServices() {
        servicesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Service> newList = new ArrayList<>();

                Service service;
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    service = itemSnapshot.getValue(Service.class);

                    assert service != null;
                    service.setServiceId(itemSnapshot.getKey());

                    newList.add(service);
                }

                adapter.updateList(newList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void openBookServiceDialog(Service service) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.book_service_dialog);

        title = dialog.findViewById(R.id.title);
        priceAndProducer = dialog.findViewById(R.id.producer);
        bookService = dialog.findViewById(R.id.book);
        cancel = dialog.findViewById(R.id.cancel);

        DatabaseReference producerRef = database.getReference().child("users").child(service.getProducer());

        producerRef.child("fullName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fullName = snapshot.getValue(String.class);

                title.setText(service.getServiceName());
                priceAndProducer.setText(String.format("%s - %s%s", fullName, service.getPrice(), service.getServiceCurrency()));

                bookService.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        validateCustomer(dialog, service);
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void validateCustomer(Dialog dialog, Service service) {
        bookingsRef.addValueEventListener(new ValueEventListener() {
            boolean foundBooking = false;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String customerId = dataSnapshot.child("customerId").getValue(String.class);
                    String serviceId= dataSnapshot.child("service").child("serviceId").getValue(String.class);

                    if (userId.equals(customerId) && service.getServiceId().equals(serviceId))
                    {
                        foundBooking = true;
                    }
                }

                if (!foundBooking) {
                    Fragment fragment = new CreateBookingService();
                    Bundle bundle = new Bundle();

                    bundle.putString("type", "consumer");
                    bundle.putSerializable("service", service);
                    fragment.setArguments(bundle);

                    if(!isAdded()) {
                        return;
                    }

                    getParentFragmentManager().beginTransaction().replace(R.id.user_fragments, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();

                    dialog.dismiss();
                }
                else {
                    showErrorDialog("כבר קבעת תור לשירות זה.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void showErrorDialog(String message) {
        if (!isAdded()) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("הערה")
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("הבנתי", (dialog, which) -> dialog.dismiss())
                .show();
    }
}