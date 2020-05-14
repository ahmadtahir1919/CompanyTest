package com.example.companytest;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class RxBus {

    public RxBus() {
    }

    private PublishSubject<File> UserImage = PublishSubject.create();

    public void sendUserImage(File imageURI) {
        UserImage.onNext(imageURI);
    }

    public Observable<File> observeUserImage() {
        return UserImage;
    }



}
