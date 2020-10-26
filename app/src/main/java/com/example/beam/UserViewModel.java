package com.example.beam;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {
    private LiveData<String> user = new MutableLiveData<>();

    public LiveData<String> getUser() {
        return user;
    }

    public void setUser(LiveData<String> user) {
        this.user = user;
    }
}
