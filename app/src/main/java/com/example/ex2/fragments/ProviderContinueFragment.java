package com.example.ex2.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ex2.R;
import com.example.ex2.activities.UserActivity;
import com.example.ex2.adapters.JobAdapter;
import com.example.ex2.services.JobDataService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProviderContinueFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProviderContinueFragment extends RegisterUser {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private FirebaseAuth mAuth;
    private String emailStr;
    private String passwordStr;
    private String phoneNumberStr;
    private String userTypeStr;
    private String fullNameStr;
    private String companyStr;
    private String categoryStr;
    private FirebaseDatabase database;
    private ArrayList<String> jobsStrs;
    private JobAdapter adapter;
    private EditText businessName, categoryName;
    private TextView error;
    private Button searchBtn, goBackBtn, registerBtn;
    private RecyclerView recyclerView;
    private String mParam1;
    private String mParam2;
    private final String TYPE_NAME = "provider";

    public ProviderContinueFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProviderContinueFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProviderContinueFragment newInstance(String param1, String param2) {
        ProviderContinueFragment fragment = new ProviderContinueFragment();
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
        View view =  inflater.inflate(R.layout.fragment_provider_continue, container, false);

        recyclerView = view.findViewById(R.id.jobs);
        businessName = view.findViewById(R.id.business_name);
        categoryName = view.findViewById(R.id.category);
        searchBtn = view.findViewById(R.id.search_jobs);
        registerBtn = view.findViewById(R.id.register);
        error = view.findViewById(R.id.error);
        goBackBtn = view.findViewById(R.id.go_back);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference().getDatabase();


        adapter = new JobAdapter(new ArrayList<>(), selectedText -> {
            categoryName.setText(selectedText);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(adapter);

        searchBtn.setOnClickListener(view1 -> {
            try {
                if(!categoryName.getText().toString().isEmpty()) {
                    JobDataService jobDataService = new JobDataService();

                    jobDataService.searchJobsWithQuery(categoryName.getText().toString());
                    adapter.updateList(jobDataService.getJobs());

                    Log.e("UPDATE", adapter.getJobList().toString());
                }
            }
            catch (Exception e) {
                Log.e("ERROR",e.getMessage().toString());
            }
        });

        emailStr = getArguments().getString("EMAIL");
        passwordStr = getArguments().getString("PASSWORD");
        fullNameStr = getArguments().getString("FULL_NAME");
        phoneNumberStr = getArguments().getString("PHONE_NUMBER");

        registerBtn.setOnClickListener(view1 -> {
            companyStr = businessName.getText().toString();
            categoryStr = categoryName.getText().toString();

            try {
                if (verifyInputs()) {
                     mAuth.createUserWithEmailAndPassword(emailStr, passwordStr)
                             .addOnCompleteListener(requireActivity(), task -> {
                                 if (task.isSuccessful()) {
                                     List<Task<Void>> tasks = new ArrayList<>();

                                     String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                                     Task<Void> task1 = database.getReference("users").child(userId).child("type").setValue(TYPE_NAME);
                                     Task<Void> task2 = database.getReference("users").child(userId).child("fullName").setValue(fullNameStr);
                                     Task<Void> task3 = database.getReference("users").child(userId).child("phoneNumber").setValue(phoneNumberStr);
                                     Task<Void> task4 =  database.getReference("users").child(userId).child("companyName").setValue(companyStr);
                                     Task<Void> task5 = database.getReference("users").child(userId).child("category").setValue(categoryStr);

                                     tasks.add(task1);
                                     tasks.add(task2);
                                     tasks.add(task3);
                                     tasks.add(task4);
                                     tasks.add(task5);

                                     Tasks.whenAll(tasks)
                                             .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                         @Override
                                                         public void onSuccess(Void unused) {
                                                             Toast.makeText(requireActivity(), "ברוך הבא, " + fullNameStr, Toast.LENGTH_SHORT).show();

                                                             startActivity(new Intent(requireActivity() ,UserActivity.class));
                                                             requireActivity().finish();
                                                         }
                                             })
                                                     .addOnFailureListener(new OnFailureListener() {
                                                         @Override
                                                         public void onFailure(@NonNull Exception e) {
                                                             Toast.makeText(requireActivity(), "ההירשמות נכשלה, נסה שנית", Toast.LENGTH_SHORT).show();
                                                         }
                                                     });
                                 }
                                 else {
                                        throw new IllegalArgumentException("ההירשמות נכשלה, נסה שנית מאוחר יותר");
                                 }
                             });
                }
            }
            catch (IllegalArgumentException e)
            {
                error.setText(e.getMessage());
            }
        });

        return view;
    }

    public Boolean verifyInputs() {
        String companyName = businessName.getText().toString();
        String category = categoryName.getText().toString();

        if(companyName.isEmpty() || category.isEmpty()) {
            throw new IllegalArgumentException("מלא שדות ריקות");
        }

        return !companyName.isEmpty() && !category.isEmpty();
    }
}