package com.example.companytest.Data;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.companytest.Model.User;
import com.example.companytest.R;


@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class UserDataBase extends RoomDatabase {
    private static UserDataBase appDatabase;

    public abstract UserDao getUserDao();
    public synchronized static UserDataBase getAppDatabase(Context context) {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(context.getApplicationContext(), UserDataBase.class, context.getResources().getString(R.string.app_name)).allowMainThreadQueries()

                    /*
                     *  Don't do this on a real app! Always use AsyncTask or in Kotlin use Coroutines some other background thread to get and put data in database
                     * I use this because time is short and have not much data.....
                     * */
                    // allow queries on the main thread.
//                    .allowMainThreadQueries()
                    .build();
        }
        return appDatabase;
    }
}


