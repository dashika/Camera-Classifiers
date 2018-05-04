package com.example.dashika.cameraclassifiers;

import android.app.Application;

import com.example.dashika.cameraclassifiers.DI.AppComponent;
import com.example.dashika.cameraclassifiers.DI.DaggerAppComponent;
import com.example.dashika.cameraclassifiers.DI.PresenterModule;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class ApplicationCameraClassifiers extends Application {

    private static ApplicationCameraClassifiers instance;
    private static AppComponent component;
    private RxBus bus;

    public static ApplicationCameraClassifiers get() {
        return instance;
    }

    public static AppComponent getComponent() {
        return component;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        component = buildComponent();

        bus = new RxBus();
    }

    protected AppComponent buildComponent() {
        return DaggerAppComponent.builder()
                .presenterModule(new PresenterModule(this))
                .build();
    }

    public RxBus bus() {
        return bus;
    }

    public static final class RxBus {
        private final Subject<Object, Object> bus = new SerializedSubject<>(PublishSubject.create());

        public void send(Object o) {
            bus.onNext(o);
        }

        public Observable<Object> toObservable() {
            return bus;
        }

    }


}
