package com.example.dashika.cameraclassifiers.Presenter;

import com.example.dashika.cameraclassifiers.ApplicationCameraClassifiers;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public abstract class BasePresenter implements Presenter {

    @Inject
    CompositeSubscription compositeSubscription;

    BasePresenter() {
        ApplicationCameraClassifiers.getComponent().inject(this);
    }

    void addSubscription(Subscription subscription) {
        compositeSubscription.add(subscription);
    }

    @Override
    public void onStop() {
        compositeSubscription.clear();
    }

}
