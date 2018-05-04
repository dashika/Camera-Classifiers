package com.example.dashika.cameraclassifiers.Presenter;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.dashika.cameraclassifiers.ApplicationCameraClassifiers;
import com.example.dashika.cameraclassifiers.MainActivity;
import com.example.dashika.cameraclassifiers.Model.SNPEModel;
import com.example.dashika.cameraclassifiers.Util.Events;
import com.example.dashika.cameraclassifiers.View.Fragment.Camera2BasicFragment;

import java.util.Set;

import rx.android.schedulers.AndroidSchedulers;

public class LoadingPresenter extends BasePresenter {

    public void create() {
        addEventListener();
    }

    private void addEventListener() {
        addSubscription(ApplicationCameraClassifiers.get().bus().toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::onModelLoaded
                ));
    }

    private void onModelLoaded(Object obj) {
        if (obj instanceof Events.onModelLoaded) {
            Set<SNPEModel> snpeModels = ((Events.onModelLoaded) obj).getSNPEModel();
            SNPEModel snpeModel = snpeModels.iterator().next();

            Fragment camera2BasicFragment = new Camera2BasicFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("SNPEModel", snpeModel);
            camera2BasicFragment.setArguments(bundle);
            MainActivity.replaceFragment(camera2BasicFragment, true, "camera");
        }
    }
}
