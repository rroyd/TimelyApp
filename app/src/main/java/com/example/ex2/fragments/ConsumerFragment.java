package com.example.ex2.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ex2.R;
import com.example.ex2.activities.LoginActivity;
import com.example.ex2.activities.UserActivity;
import com.example.ex2.adapters.BookingServiceAdapter;
import com.example.ex2.adapters.BookingServiceForCustomerAdapter;
import com.example.ex2.models.BookingService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConsumerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConsumerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference userRef, bookingsRef;
    private String userId;
    private TextView greeting;
    private RecyclerView bookingsRecyclerView;
    private BookingServiceForCustomerAdapter adapter;
    private ArrayList<BookingService> bookingServiceArrayList;
    private TextView logOut;
    private Button newBooking, cancelAllBookings;

    public ConsumerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConsumerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConsumerFragment newInstance(String param1, String param2) {
        ConsumerFragment fragment = new ConsumerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_consumer, container, false);

        logOut = view.findViewById(R.id.log_out);
        greeting = view.findViewById(R.id.greeting);
        newBooking = view.findViewById(R.id.new_booking);
        bookingsRecyclerView = view.findViewById(R.id.bookings);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        userRef = database.getReference().child("users").child(userId);
        bookingsRef = database.getReference().child("bookings");

        bookingServiceArrayList = new ArrayList<>();
        adapter = new BookingServiceForCustomerAdapter(bookingServiceArrayList,
                this::removeBookingService);

        bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        bookingsRecyclerView.setAdapter(adapter);


        newBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.user_fragments, new BookServiceFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();

                Toast.makeText(requireActivity(), "צאתך לשלום!", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(requireActivity(), LoginActivity.class));
                requireActivity().finish();
            }
        });

        setGreeting();
        setBookings();

        return view;
    }

    private void removeBookingService(BookingService bookingService) {
        new AlertDialog.Builder(requireActivity())
                .setTitle("האם אתה בטוח?")
                .setMessage(String.format("לבטל את התור של השירות '%s' שנקבע %s?", bookingService.getService().getServiceName(),
                        bookingService.getWhen().toString()))
                .setPositiveButton("כן", ((dialogInterface, i) -> {
                    bookingsRef.child(bookingService.getBookingServiceId()).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText( requireContext(), "התור התבטל בהצלחה.", Toast.LENGTH_LONG).show();

                                    startActivity(new Intent(requireActivity(), UserActivity.class));
                                    requireActivity().finish();
                                }
                            });
                }))
                .setNegativeButton("לא", ((dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })).show();
    }


    public void setBookings() {
        bookingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<BookingService> newList = new ArrayList<>();

                BookingService bookingService;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                     bookingService = dataSnapshot.getValue(BookingService.class);
                     bookingService.setBookingServiceId(dataSnapshot.getKey());

                     if(bookingService.getCustomerId().equals(userId)) {
                         newList.add(bookingService);
                     }
                }

                bookingServiceArrayList = newList;
                adapter.updateList(bookingServiceArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "כשל 500", error.toException());
            }
        });
    }

    public void setGreeting() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fullName = snapshot.child("fullName").getValue(String.class);

                greeting.setText(String.format("שלום, %s", fullName));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "כשל 500", error.toException());
            }
        });
    }

    private void showAreYouSureDialog() {

    }
}