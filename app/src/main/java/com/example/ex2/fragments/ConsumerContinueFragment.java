package com.example.ex2.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConsumerContinueFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConsumerContinueFragment extends RegisterUser {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private FirebaseAuth mAuth;
    private String emailStr;
    private String passwordStr;
    private String phoneNumberStr;
    private String userTypeStr;
    private String fullNameStr;
    private String companyStr;
    private String categoryStr;
    private FirebaseDatabase database;
    private EditText nickname;
    private Button searchBtn, goBackBtn, registerBtn;
    private RecyclerView recyclerView;
    private String nicknameStr;
    private String mParam1;
    private String mParam2;
    private TextView error;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private final String TYPE_NAME = "customer";

    public ConsumerContinueFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConsumerContinueFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConsumerContinueFragment newInstance(String param1, String param2) {
        ConsumerContinueFragment fragment = new ConsumerContinueFragment();
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
        View view = inflater.inflate(R.layout.fragment_consumer_continue, container, false);

        registerBtn = view.findViewById(R.id.register);
        goBack = view.findViewById(R.id.back);
        error = view.findViewById(R.id.error);
        nickname = view.findViewById(R.id.nickname);
        emailStr = getArguments().getString("EMAIL");
        passwordStr = getArguments().getString("PASSWORD");
        fullNameStr = getArguments().getString("FULL_NAME");
        phoneNumberStr = getArguments().getString("PHONE_NUMBER");
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference().getDatabase();

        registerBtn.setOnClickListener(view1 -> {
            nicknameStr = nickname.getText().toString();

            try {
                    mAuth.createUserWithEmailAndPassword(emailStr, passwordStr)
                            .addOnCompleteListener(requireActivity(), task -> {
                                if (task.isSuccessful()) {
                                    String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

                                    List<Task<Void>> listOfTasks = new ArrayList<>();

                                    Task<Void> task1 = database.getReference("users").child(userId).child("type").setValue(TYPE_NAME);
                                    Task<Void> task2 = database.getReference("users").child(userId).child("fullName").setValue(fullNameStr);
                                    Task<Void> task3 = database.getReference("users").child(userId).child("phoneNumber").setValue(phoneNumberStr);
                                    Task<Void> task4 =  database.getReference("users").child(userId).child("companyName").setValue(companyStr);
                                    Task<Void> task5 = database.getReference("users").child(userId).child("category").setValue(categoryStr);
                                    Task<Void> task6 = null;

                                    listOfTasks.add(task1);
                                    listOfTasks.add(task2);
                                    listOfTasks.add(task3);
                                    listOfTasks.add(task4);
                                    listOfTasks.add(task5);

                                    if (!nicknameStr.isEmpty()) {
                                        task6 =  database.getReference("users").child(userId).child("nickname").setValue(nicknameStr);
                                    }

                                    if(task6 != null) {
                                        listOfTasks.add(task5);
                                    }

                                    Tasks.whenAll(listOfTasks)
                                                    .addOnSuccessListener(aVoid -> {
                                                        Toast.makeText(requireActivity(), "ברוך הבא, " + fullNameStr, Toast.LENGTH_SHORT).show();

                                                        startActivity(new Intent(requireActivity() , UserActivity.class));
                                                        requireActivity().finish();
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
            catch (IllegalArgumentException e)
            {
                error.setText(e.getMessage());
            }
        });


        return view;
    }
}