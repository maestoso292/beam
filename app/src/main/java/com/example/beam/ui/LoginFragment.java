package com.example.beam.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.beam.R;
import com.example.beam.SavedStateModel;

public class LoginFragment extends Fragment {
    public static String LOGIN_SUCCESSFUL = "LOGIN_SUCCESSFUL";

    private SavedStateModel savedStateModel;
    private SavedStateHandle savedStateHandle;

    private Button button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        savedStateModel = new ViewModelProvider(requireActivity()).get(SavedStateModel.class);
        /*
        savedStateHandle = Navigation.findNavController(view)
                .getPreviousBackStackEntry().getSavedStateHandle();
        //savedStateHandle.set(LOGIN_SUCCESSFUL, false);
         */

        button = view.findViewById(R.id.login_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    // TODO Authenticate user
    private void login(){
        // savedStateHandle.set(LOGIN_SUCCESSFUL, true);
        savedStateModel.setAuthentication(true);
        NavHostFragment.findNavController(this).popBackStack();
    }
}
