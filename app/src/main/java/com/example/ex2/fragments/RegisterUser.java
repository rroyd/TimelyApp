package com.example.ex2.fragments;

import static java.lang.System.in;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.ex2.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterUser#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterUser extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    RadioGroup radioGroup;
    Button goBack;
    TextView validationError;
    Button continueBtn;
    Fragment continueFragment;
    Bundle bundle;
    EditText email, fullName, password, rePassword, phoneNumber;
    private String mParam1;
    private String mParam2;

    public RegisterUser() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterUser newInstance(String param1, String param2) {
        RegisterUser fragment = new RegisterUser();
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
        View view = inflater.inflate(R.layout.activity_register, container, false);

        bundle = new Bundle();
        email = view.findViewById(R.id.email);
        fullName = view.findViewById(R.id.full_name);
        password = view.findViewById(R.id.password);
        rePassword = view.findViewById(R.id.repassword);
        phoneNumber = view.findViewById(R.id.phone_number);
        validationError = view.findViewById(R.id.validation_error);

        radioGroup = view.findViewById(R.id.user_type);

        final String[] typeChosen = {"consumer"};
        radioGroup.check(R.id.consumer);
        continueFragment = new ConsumerContinueFragment();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.consumer) {
                    typeChosen[0] = "consumer";
                    continueFragment = new ConsumerContinueFragment();
                }
                else {
                    typeChosen[0] = "provider";
                    continueFragment = new ProviderContinueFragment();
                }
            }});

        goBack = view.findViewById(R.id.go_back);

        continueBtn = view.findViewById(R.id.continue_btn);

        goBack.setOnClickListener(thisView -> {
            getParentFragmentManager().beginTransaction().replace(R.id.login_fragments, new CustomerLogin())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        });

        continueBtn.setOnClickListener(thisView -> {
            try {
                if(validateInputs()) {
                    bundle.putString("FULL_NAME", fullName.getText().toString());
                    bundle.putString("EMAIL", email.getText().toString());
                    bundle.putString("PASSWORD", password.getText().toString());
                    bundle.putString("PHONE_NUMBER", phoneNumber.getText().toString());
                    bundle.putString("TYPE", typeChosen[0]);

                    continueFragment.setArguments(bundle);

                    getParentFragmentManager().beginTransaction().replace(R.id.login_fragments, continueFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();
                }
            }
            catch (IllegalArgumentException e) {
                validationError.setText(e.getMessage());
//                outlineEmptyField();
            }

        });

        return view;
    }

    public Boolean validateInputs() {
        String emailStr = email.getText().toString();
        String passwordStr = password.getText().toString();
        String repasswordStr = rePassword.getText().toString();
        String phoneNumberStr = phoneNumber.getText().toString();
        String fullNameStr = fullName.getText().toString();

        if (!passwordStr.equals(repasswordStr)) {
            throw new IllegalArgumentException("הסיסמאות לא תואמות");
        }
        else if (emailStr.isEmpty() || phoneNumberStr.isEmpty() || passwordStr.isEmpty() || fullNameStr.isEmpty()) {
            throw new IllegalArgumentException("מלא שדות חובה");
        }
        else if (!(passwordStr.length() > 6 &&  hasLowercase(passwordStr) &&  hasOneUpper(passwordStr) && hasNumber(passwordStr))) {
            throw new IllegalArgumentException("הסיסמא לא תואמת לתנאים הנדרשמים");
        }

        return passwordStr.equals(repasswordStr)
                && !emailStr.isEmpty()
                && !passwordStr.isEmpty()
                && passwordStr.length() > 6
                && hasLowercase(passwordStr)
                && hasOneUpper(passwordStr)
                && hasNumber(passwordStr)
                && !phoneNumberStr.isEmpty()
                && !fullNameStr.isEmpty();
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