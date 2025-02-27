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
import com.example.ex2.adapters.VacationAdapter;
import com.example.ex2.models.BookingService;
import com.example.ex2.models.Vacation;
import com.google.android.gms.tasks.OnFailureListener;
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
 * Use the {@link ProviderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProviderFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView greeting;
    long timestampNow;
    private Button alterServices, goToVacation, logOut;
    private RecyclerView bookingsRecyclerView, vacationsRecyclerView;
    private BookingServiceAdapter adapter;
    private VacationAdapter vacationAdapter;
    private ArrayList<BookingService> bookingServiceArrayList;
    private ArrayList<Vacation> vacationsArrayList;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private String userId;
    private DatabaseReference userRef, bookingsRef, vacationsRef;

    public ProviderFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProviderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProviderFragment newInstance(String param1, String param2) {
        ProviderFragment fragment = new ProviderFragment();
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
        View view =  inflater.inflate(R.layout.fragment_provider, container, false);

        timestampNow = System.currentTimeMillis();

        alterServices = view.findViewById(R.id.change_services);
        goToVacation = view.findViewById(R.id.vacation);
        logOut = view.findViewById(R.id.log_out);
        vacationsRecyclerView = view.findViewById(R.id.vacations);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        userRef = database.getReference().child("users").child(userId);
        bookingsRef = database.getReference().child("bookings");
        vacationsRef = database.getReference().child("vacations");

        bookingsRecyclerView = view.findViewById(R.id.bookings);

        greeting = view.findViewById(R.id.greeting);
        setGreeting();

        vacationsArrayList = new ArrayList<Vacation>();
        bookingServiceArrayList = new ArrayList<BookingService>();
        adapter = new BookingServiceAdapter(bookingServiceArrayList,
                bookingService -> {
            editBookingService(bookingService);
        },
                bookingService -> {
            removeBookingService(bookingService);
        });
        vacationAdapter = new VacationAdapter(vacationsArrayList, vacation ->  {
           showDeleteVacationDialog(vacation);
        });

        fillBookingServices();
        fillVacations();

        bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        bookingsRecyclerView.setAdapter(adapter);

        vacationsRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        vacationsRecyclerView.setAdapter(vacationAdapter);

        alterServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.user_fragments, new ChangeAddServiceFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
        });

        goToVacation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                getParentFragmentManager().beginTransaction().replace(R.id.user_fragments, new VacationFragment())
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

        return view;
    }

    private void showDeleteVacationDialog(Vacation vacation) {
        new AlertDialog.Builder(requireContext())
                .setTitle("האם אתה בטוח?")
                .setMessage("לבטל את החופשה שנקבעה בתאריכים " + vacation.toString())
                .setPositiveButton("כן", ((dialogInterface, i) ->  {
                    vacationsRef.child(vacation.getVacationId()).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(requireContext(), "החופשה נמחקה בהצלחה.", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(requireContext(), "מחיקת החופשה נכשלה, נסה שנית מאוחר יותר.", Toast.LENGTH_LONG).show();
                                }
                            });
                }))
                .setNegativeButton("לא", ((dialogInterface, i) -> {
                    dialogInterface.dismiss();
                }))
                .show();
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

    private void editBookingService(BookingService bookingService) {
            Fragment fragment = new CreateBookingService();
            Bundle bundle = new Bundle();
            bundle.putString("type", "provider");
            bundle.putSerializable("bookingService", bookingService);
            fragment.setArguments(bundle);

            getParentFragmentManager().beginTransaction().replace(R.id.user_fragments, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    private void fillBookingServices() {
        bookingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<BookingService> newList = new ArrayList<>();
                BookingService currentBookingService;
                for(DataSnapshot data : snapshot.getChildren()) {
                    currentBookingService = data.getValue(BookingService.class);
                    currentBookingService.setBookingServiceId(data.getKey());

                    if (timestampNow > currentBookingService.getWhen().getTimestampFromDate()) {
                        data.getRef().removeValue();

                        continue;
                    }

                     if (currentBookingService.getService().getProducer().equals(userId)) {
                         newList.add(currentBookingService);
                     }
                }

                bookingServiceArrayList = newList;
                adapter.updateList(bookingServiceArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fillVacations() {
        vacationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Vacation> newList = new ArrayList<>();
                Vacation currentVacation;

                for(DataSnapshot data : snapshot.getChildren()) {
                    currentVacation = data.getValue(Vacation.class);

                    if(currentVacation.getUser().equals(userId)) {
                        currentVacation.setVacationId(data.getKey());
                        newList.add(currentVacation);
                    }
                }

                vacationsArrayList = newList;
                vacationAdapter.updateList(vacationsArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
}