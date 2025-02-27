package com.example.ex2.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ex2.R;
import com.example.ex2.activities.LoginActivity;
import com.example.ex2.activities.UserActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private EditText password, email, category;
    private ImageView editEmail, editPassword, editCategory;
    private Button deleteUser;
    private String mParam2;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private String userId;
    private DatabaseReference userRef, servicesRef, bookingsRef;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);


        deleteUser = view.findViewById(R.id.delete_user);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        editEmail = view.findViewById(R.id.edit_email);
        editPassword = view.findViewById(R.id.edit_password);

        email.setEnabled(false);
        password.setEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        currentUser = mAuth.getCurrentUser();
        userRef = database.getReference().child("users").child(userId);
        servicesRef = database.getReference().child("services");
        bookingsRef = database.getReference().child("bookings");

        email.setText(currentUser.getEmail());

        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditDialog("email");
            }
        });

        editPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditDialog("password");
            }
        });

        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteUserDialog();
            }
        });

        return view;
    }

    private void showDeleteUserDialog() {

    }

    private void openEditDialog(String field) {
        Dialog dialog = new Dialog(requireContext());

        EditText newField, confirmField, oldField;
        Button finalEdit;

        switch (field) {
            case "email":
                dialog.setContentView(R.layout.edit_email_dialog);

                oldField = dialog.findViewById(R.id.old_mail);
                newField = dialog.findViewById(R.id.new_mail);
                confirmField = dialog.findViewById(R.id.confirm_new_mail);
                finalEdit = dialog.findViewById(R.id.edit_final);

                finalEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       try {
                           verifyEmail(dialog);
                       }
                       catch (IllegalArgumentException e) {
                           Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                       }
                    }
                });

                break;
            case "password":
                dialog.setContentView(R.layout.edit_password_dialog);

                newField = dialog.findViewById(R.id.new_password);
                confirmField = dialog.findViewById(R.id.confirm_new_password);
                finalEdit = dialog.findViewById(R.id.edit_final);

                finalEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            verifyPassword(dialog);
                        }
                        catch (IllegalArgumentException e) {
                            Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

                break;
        }

        dialog.show();
    }

    private void verifyPassword(Dialog dialog) {
        EditText newPassword = dialog.findViewById(R.id.new_password);
        EditText confirmPassword = dialog.findViewById(R.id.confirm_new_password);

        String newPasswordStr = newPassword.getText().toString();
        String confirmPasswordStr = confirmPassword.getText().toString();

        if(!(newPasswordStr.length() > 6 &&  hasLowercase(newPasswordStr) &&  hasOneUpper(newPasswordStr) && hasNumber(newPasswordStr))) {
            throw new IllegalArgumentException("חייבת להכיל לפחות אות גדולה, אות קטנה ומספר, ואורך 6 לפחות");
        }
        else {
            if (newPasswordStr.equals(confirmPasswordStr)) {
                currentUser.updatePassword(newPasswordStr)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(requireContext(), "הסיסמא שונתה בהצלחה!", Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(requireActivity(), UserActivity.class));
                                    requireActivity().finish();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                throw new IllegalArgumentException("שינויי הסיסמא נכשלה." + e.getMessage());
                            }
                        });
            }
            else {
                throw new IllegalArgumentException("הסיסמאות לא תואמות");
            }
        }

    }

    private void verifyEmail(Dialog dialog) {
        EditText oldMail = dialog.findViewById(R.id.old_mail);
        EditText newEmail = dialog.findViewById(R.id.new_mail);
        EditText confirmEmail = dialog.findViewById(R.id.confirm_new_mail);
        String currentMail = currentUser.getEmail();

        String oldMailStr = oldMail.getText().toString();
        String newEmailStr = newEmail.getText().toString();
        String confirmEmailStr = confirmEmail.getText().toString();

        if(!oldMailStr.equals(currentMail)) {
            throw new IllegalArgumentException("המייל הישן אינו תואם.");
        }
        else if (!newEmailStr.isEmpty() && newEmailStr.equals(confirmEmailStr)) {
            currentUser.updateEmail(newEmailStr)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(requireContext(), "המייל שונה בהצלחה!", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(requireActivity(), UserActivity.class));
                                requireActivity().finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            throw new IllegalArgumentException("שינויי המייל נכשלה." + e.getMessage());
                        }
                    });
        }
    }

    private Boolean hasOneUpper(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (Character.isUpperCase(string.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    private Boolean hasNumber(String string) {
        for (int i = 0; i< string.length(); i++) {
            if (Character.isDigit(string.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    private Boolean hasLowercase(String string) {
        for (int i = 0; i< string.length(); i++) {
            if (Character.isLowerCase(string.charAt(i))) {
                return true;
            }
        }

        return false;
    }
}