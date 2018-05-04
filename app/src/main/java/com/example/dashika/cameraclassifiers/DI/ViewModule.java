package com.example.dashika.cameraclassifiers.DI;

import com.example.dashika.cameraclassifiers.Presenter.CameraPresenter;
import com.example.dashika.cameraclassifiers.Presenter.LoadingPresenter;

import dagger.Module;
import dagger.Provides;

@Module
class ViewModule {

    @Provides
    CameraPresenter cameraPresenter() {
        return new CameraPresenter();
    }

    @Provides
    LoadingPresenter loadingPresenter() {
        return new LoadingPresenter();
    }
}
