package com.example.dashika.cameraclassifiers.Presenter;

import android.content.Context;
import android.widget.Toast;

import com.example.dashika.cameraclassifiers.ApplicationCameraClassifiers;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public abstract class BasePresenter implements Presenter {

    @Inject
    Context context;

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

    public void showError(Throwable e) {
        Toast.makeText(context, "Happend error", Toast.LENGTH_LONG).show();
    }
}
