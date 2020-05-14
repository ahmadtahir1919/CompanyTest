package com.example.companytest;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class RxBus {
    private static RxBus _instance;
    //RX java this in small work instead of this we can we multiple other things

    private RxBus()
    {

    }
    public static RxBus getInstance()
    {
        if (_instance == null)
        {
            _instance = new RxBus();
        }
        return _instance;
    }
    private PublishSubject<File> UserImage = PublishSubject.create();

    public void sendUserImage(File imageURI) {
        UserImage.onNext(imageURI);
    }

    public Observable<File> observeUserImage() {
        return UserImage;
    }

public void clearRx(){
        _instance=null;
}

}
