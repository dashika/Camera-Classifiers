package com.example.dashika.cameraclassifiers.View.Fragment;

import android.support.v4.app.Fragment;

import com.example.dashika.cameraclassifiers.Presenter.Presenter;

public abstract class BaseFragment extends Fragment {

    protected abstract Presenter getPresenter();

    @Override
    public void onStop() {
        super.onStop();
        if (getPresenter() != null) {
            getPresenter().onStop();
        }
    }

}

