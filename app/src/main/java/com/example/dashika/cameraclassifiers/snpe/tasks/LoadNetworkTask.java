package com.example.dashika.cameraclassifiers.snpe.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.dashika.cameraclassifiers.ApplicationCameraClassifiers;
import com.example.dashika.cameraclassifiers.Model.SNPEModel;
import com.example.dashika.cameraclassifiers.Util.Events;
import com.qualcomm.qti.snpe.NeuralNetwork;
import com.qualcomm.qti.snpe.SNPE;

import java.io.File;
import java.io.IOException;

public class LoadNetworkTask extends AsyncTask<File, Void, NeuralNetwork> {

    private static final String LOG_TAG = LoadNetworkTask.class.getSimpleName();

    private final SNPEModel mModel;

    private final NeuralNetwork.Runtime mTargetRuntime;

    public LoadNetworkTask(final SNPEModel model, NeuralNetwork.Runtime targetRuntime) {
        mModel = model;
        mTargetRuntime = targetRuntime;
    }

    @Override
    protected NeuralNetwork doInBackground(File... params) {
        NeuralNetwork network = null;
        try {
            final SNPE.NeuralNetworkBuilder builder = new SNPE.NeuralNetworkBuilder(ApplicationCameraClassifiers.get())
                    .setDebugEnabled(false)
                    .setRuntimeOrder(mTargetRuntime)
                    .setModel(mModel.file);
            network = builder.build();
        } catch (IllegalStateException | IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        return network;
    }

    @Override
    protected void onPostExecute(NeuralNetwork neuralNetwork) {
        super.onPostExecute(neuralNetwork);
        if (neuralNetwork != null) {
            if (!isCancelled()) {
                ApplicationCameraClassifiers.get().bus().send(new Events.onNetworkLoaded(neuralNetwork));
            } else {
                neuralNetwork.release();
            }
        } else {
            if (!isCancelled()) {
                // mController.onNetworkLoadFailed();
            }
        }
    }
}
