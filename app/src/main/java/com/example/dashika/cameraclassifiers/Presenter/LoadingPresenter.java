package com.example.dashika.cameraclassifiers.Presenter;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.dashika.cameraclassifiers.ApplicationCameraClassifiers;
import com.example.dashika.cameraclassifiers.MainActivity;
import com.example.dashika.cameraclassifiers.Model.SNPEModel;
import com.example.dashika.cameraclassifiers.Util.Events;
import com.example.dashika.cameraclassifiers.View.Fragment.Camera2BasicFragment;
import com.example.dashika.cameraclassifiers.snpe.tasks.LoadModelsTask;

import java.util.Set;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

public class LoadingPresenter extends BasePresenter {

    @Inject
    Context context;
    LoadModelsTask task;

    public void create() {
        ApplicationCameraClassifiers.getComponent().inject(this);
        addEventListener();
    }

    private void addEventListener() {
        addSubscription(ApplicationCameraClassifiers.get().bus().toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onModelLoaded, this::showError, this::onComplite)
        );
    }

    private void onComplite() {

    }

    @Override
    public void showError(Throwable e) {
        if(e instanceof NoSuchFieldError) {
            ((Activity) context).finish();
            loadModels();
        }
    }

    public void loadModels() {
        if (task != null) task.cancel(true);
        task = new LoadModelsTask(context);

        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void onModelLoaded(Object obj) {
        if (obj instanceof Events.onModelLoaded) {
            Set<SNPEModel> snpeModels = ((Events.onModelLoaded) obj).getSNPEModel();
            if(snpeModels.size() == 0) return;
            SNPEModel snpeModel = snpeModels.iterator().next();
            Fragment camera2BasicFragment = new Camera2BasicFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("SNPEModel", snpeModel);
            camera2BasicFragment.setArguments(bundle);
            MainActivity.replaceFragment(camera2BasicFragment, true, "camera");
        }
    }
}
