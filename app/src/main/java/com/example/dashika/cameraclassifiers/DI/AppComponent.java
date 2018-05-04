package com.example.dashika.cameraclassifiers.DI;

import android.content.Context;

import com.example.dashika.cameraclassifiers.Presenter.BasePresenter;
import com.example.dashika.cameraclassifiers.View.Fragment.Camera2BasicFragment;
import com.example.dashika.cameraclassifiers.View.Fragment.LoadingFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {PresenterModule.class, ViewModule.class})
public interface AppComponent {

    Context context();

    void inject(BasePresenter basePresenter);

    void inject(Camera2BasicFragment camera2BasicFragment);

    void inject(LoadingFragment loadingFragment);

}
