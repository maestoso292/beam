package com.example.beam;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.example.beam.ui.LoginFragment;

public class SavedStateModel extends ViewModel {
    // TODO SavedStateHandle needs to persist after death
    private SavedStateHandle savedStateHandle;
    SavedStateModel() {
        super();
    }
    public SavedStateModel(SavedStateHandle savedStateHandle) {
        super();
        this.savedStateHandle = savedStateHandle;
        if (!this.savedStateHandle.contains(LoginFragment.LOGIN_SUCCESSFUL)) {
            this.savedStateHandle.set(LoginFragment.LOGIN_SUCCESSFUL, false);
        }

    }

    public LiveData<Boolean> getAuthentication() {
        return savedStateHandle.getLiveData(LoginFragment.LOGIN_SUCCESSFUL, false);
    }

    public void setAuthentication(boolean authentication) {
        savedStateHandle.set(LoginFragment.LOGIN_SUCCESSFUL, authentication);
    }
}
