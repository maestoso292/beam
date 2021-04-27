package com.example.beam.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.beam.R;

/**
 * Fragment subclass with functional sign in button that navigates to Login Fragment
 */
public class SigninFragment extends Fragment {

    private NavController navController;
    private Button signinBtn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.signin_fragment, container, false);
    }

    /**
     * On view created, add functionality to sign in button.
     * @param view XML view of Fragment
     * @param savedInstanceState Previous saved instance of Fragment
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        signinBtn= view.findViewById(R.id.signinBtn);

        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // On press, navigate to LoginFragment
                NavHostFragment.findNavController(SigninFragment.this).navigate(R.id.navigateToLoginFragment);
            }
        });

    }
}