package com.example.companytest.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.companytest.Data.UserDataBase;
import com.example.companytest.Model.User;

import java.util.List;

public class UserViewModel extends AndroidViewModel {

    private LiveData<List<User>> allData;
    UserDataBase appDatabase;
    private MutableLiveData<Boolean> userExist;

    public UserViewModel(@NonNull Application application) {
        super(application);

        appDatabase = UserDataBase.getAppDatabase(application.getApplicationContext());
    }

    public LiveData<List<User>> getAllData(String userName, String password) {
        return allData = appDatabase.getUserDao().getUser(userName, password);
    }

    public MutableLiveData<Boolean> checkAlreadyUserExist(String userName) {
        User user = appDatabase.getUserDao().checkAlreadyUserExist(userName);
        userExist = new MutableLiveData<>();
        if (user != null) {

            userExist.setValue(true);
            return userExist;

        } else {
            userExist.setValue(false);
            return userExist;

        }
    }

    public void setUserData(User user) {
        appDatabase.getUserDao().insert(user);
    }


}