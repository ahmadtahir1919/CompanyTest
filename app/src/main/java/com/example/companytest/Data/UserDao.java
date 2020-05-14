package com.example.companytest.Data;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.companytest.Model.User;

import java.util.List;


@Dao
public interface UserDao {
    @Query("SELECT * FROM User where userName= :userName and password= :password")
    LiveData<List<User>> getUser(String userName, String password);

    @Query("SELECT * FROM User where userName= :userName")
    User checkAlreadyUserExist(String userName);

    @Insert
    void insert(User user);

}
