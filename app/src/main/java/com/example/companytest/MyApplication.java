package com.example.companytest;

import android.app.Application;

public class MyApplication extends Application {
    private RxBus rxBus;

    @Override
    public void onCreate() {
        super.onCreate();
        //RX java this in small work instead of this we can we multiple other things
        rxBus = new RxBus();

    }
    public RxBus getRxBus() {
        return rxBus;
    }
}
