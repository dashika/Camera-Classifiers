package com.example.dashika.cameraclassifiers.Presenter;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.example.dashika.cameraclassifiers.ApplicationCameraClassifiers;
import com.example.dashika.cameraclassifiers.Model.SNPEModel;
import com.example.dashika.cameraclassifiers.Util.Events;
import com.example.dashika.cameraclassifiers.View.CameraView.ICameraOperation;
import com.example.dashika.cameraclassifiers.snpe.tasks.ClassifyImageTask;
import com.example.dashika.cameraclassifiers.snpe.tasks.LoadNetworkTask;
import com.qualcomm.qti.snpe.NeuralNetwork;

import rx.android.schedulers.AndroidSchedulers;

public class CameraPresenter extends BasePresenter {

    private SNPEModel snpeModel;
    private NeuralNetwork mNeuralNetwork;
    private LoadNetworkTask mLoadTask;
    private ICameraOperation iCameraOperation;

    public void create(SNPEModel snpeModel, ICameraOperation iCameraOperation) {
        this.snpeModel = snpeModel;
        this.iCameraOperation = iCameraOperation;
        addEventListener();
    }

    public void onViewDetached() {
        if (mNeuralNetwork != null) {
            mNeuralNetwork.release();
            mNeuralNetwork = null;
        }
    }

    private void onNetworkLoaded(NeuralNetwork neuralNetwork) {
        mNeuralNetwork = neuralNetwork;
        neuralNetwork.getInputTensorsShapes();
        neuralNetwork.getOutputLayers();
        neuralNetwork.getModelVersion();
        mLoadTask = null;
        iCameraOperation.Activate();
    }

    public void onNetworkLoadFailed() {
        mLoadTask = null;
    }

    public void classify(final Bitmap bitmap) {
        final NeuralNetwork neuralNetwork = mNeuralNetwork;
        if (neuralNetwork != null) {
            final ClassifyImageTask task = new ClassifyImageTask(
                    mNeuralNetwork, bitmap, snpeModel);
            task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }
    }

    private void addEventListener() {
        addSubscription(ApplicationCameraClassifiers.get().bus().toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::onClassificationResult
                ));
    }

    private void onClassificationResult(Object obj) {
        if (obj instanceof Events.onClassificationResult) {
            String[] labels = ((Events.onClassificationResult) obj).getLabels();
            iCameraOperation.SetTextResult(labels);
        } else if (obj instanceof Events.onNetworkLoaded) {
            onNetworkLoaded(((Events.onNetworkLoaded) obj).getNeuralNetwork());
        }
    }

    public void onClassificationFailed() {
        //displayClassificationFailed();
    }

    public void loadNetwork(NeuralNetwork.Runtime targetRuntime) {
        final NeuralNetwork neuralNetwork = mNeuralNetwork;
        if (neuralNetwork != null) {
            neuralNetwork.release();
            mNeuralNetwork = null;
        }

        if (mLoadTask != null) {
            mLoadTask.cancel(false);
        }

        mLoadTask = new LoadNetworkTask(snpeModel, targetRuntime);
        mLoadTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }
}
