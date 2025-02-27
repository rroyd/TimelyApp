package com.example.ex2.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ex2.R;
import com.example.ex2.activities.UserActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerLogin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerLogin extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Button registerBtn, loginBtn;
    private EditText email, password;
    private String emailStr, passwordStr;
    private TextView error;
    private FirebaseAuth mAuth;

    public CustomerLogin() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BuyerLogin.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerLogin newInstance(String param1, String param2) {
        CustomerLogin fragment = new CustomerLogin();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
        View view = inflater.inflate(R.layout.fragment_customer_login, container, false);

        error = view.findViewById(R.id.error);
        registerBtn = view.findViewById(R.id.btn_register);
        loginBtn = view.findViewById(R.id.btn_login);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();


        registerBtn.setOnClickListener(viewClick -> {
            getParentFragmentManager().beginTransaction().replace(R.id.login_fragments, new RegisterUser())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        });

        loginBtn.setOnClickListener(viewClick -> {
            emailStr = email.getText().toString();
            passwordStr = password.getText().toString();

            try {
                if (validateInputs()) {
                    mAuth.signInWithEmailAndPassword(emailStr, passwordStr)
                            .addOnCompleteListener(requireActivity(), task -> {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(requireActivity(), UserActivity.class));
                                    requireActivity().finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(requireActivity(), "ההתחברות נכשלה, נסה שנית", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
            catch (IllegalArgumentException e){
                error.setText(e.getMessage());
            }
        });

        return view;
    }

    private Boolean validateInputs() {
        if (emailStr.isEmpty() || passwordStr.isEmpty()) {
            throw new IllegalArgumentException("מלא שדות ריקות");
        }

        return !emailStr.isEmpty() && !passwordStr.isEmpty();
    }
}

