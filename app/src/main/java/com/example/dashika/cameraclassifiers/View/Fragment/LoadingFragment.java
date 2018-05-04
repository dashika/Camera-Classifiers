package com.example.dashika.cameraclassifiers.View.Fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dashika.cameraclassifiers.ApplicationCameraClassifiers;
import com.example.dashika.cameraclassifiers.Model.SNPEModel;
import com.example.dashika.cameraclassifiers.Presenter.LoadingPresenter;
import com.example.dashika.cameraclassifiers.Presenter.Presenter;
import com.example.dashika.cameraclassifiers.R;
import com.example.dashika.cameraclassifiers.snpe.ModelExtractionService;
import com.example.dashika.cameraclassifiers.snpe.tasks.LoadModelsTask;

import javax.inject.Inject;

public class LoadingFragment extends BaseFragment {

    private final ContentObserver mModelExtractionFailedObserver =
            new ContentObserver(new Handler()) {
                @Override
                public void onChange(boolean selfChange) {
                }
            };

    @Inject
    Context context;

    private final ContentObserver mModelExtractionObserver =
            new ContentObserver(new Handler()) {
                @Override
                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);
                    loadingPresenter.loadModels();
                }
            };

    @Inject
    LoadingPresenter loadingPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_loading, container, false);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        loadingPresenter.loadModels();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ApplicationCameraClassifiers.getComponent().inject(this);
        loadingPresenter.create();
        final ContentResolver contentResolver = context.getContentResolver();
        contentResolver.registerContentObserver(Uri.withAppendedPath(
                SNPEModel.MODELS_URI, SNPEModel.INVALID_ID), false, mModelExtractionFailedObserver);

        contentResolver.registerContentObserver(SNPEModel.MODELS_URI, true, mModelExtractionObserver);

        startModelsExtraction();
    }

    private void startModelsExtraction() {
        ModelExtractionService.extractModel(context, R.raw.inception_v3);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        final ContentResolver contentResolver = context.getContentResolver();
        contentResolver.unregisterContentObserver(mModelExtractionObserver);
        contentResolver.unregisterContentObserver(mModelExtractionFailedObserver);
    }

    @Override
    protected Presenter getPresenter() {
        ApplicationCameraClassifiers.getComponent().inject(this);
        return loadingPresenter;
    }
}
