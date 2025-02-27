package com.example.ex2.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ex2.R;
import com.example.ex2.activities.UserActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProviderLogin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProviderLogin extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button registerBtn, loginBtn;
    private EditText userNameEditText, passwordEditText;
    private FirebaseAuth mAuth;

    public ProviderLogin() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BarberLogin.
     */
    // TODO: Rename and change types and number of parameters
    public static ProviderLogin newInstance(String param1, String param2) {
        ProviderLogin fragment = new ProviderLogin();
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
        View view =  inflater.inflate(R.layout.fragment_provider_login, container, false);

        registerBtn = view.findViewById(R.id.btn_register);

        registerBtn.setOnClickListener(viewClick -> {
            getParentFragmentManager().beginTransaction().replace(R.id.login_fragments, new RegisterUser())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        });

        userNameEditText = view.findViewById(R.id.et_username);
        passwordEditText = view.findViewById(R.id.et_password);
        loginBtn = view.findViewById(R.id.btn_login);

        mAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(thisView -> {
            String email = userNameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireActivity(), "Enter required fields..", Toast.LENGTH_SHORT).show();
            }
            else {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity(), task -> {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(requireActivity(), UserActivity.class));
                                requireActivity().finish();
                            } else {
                                Toast.makeText(requireActivity(), "User not found.", Toast.LENGTH_SHORT).show();
                            }});
            }
        });


        return view;
    }
}