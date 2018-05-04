package com.example.dashika.cameraclassifiers.snpe.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.dashika.cameraclassifiers.ApplicationCameraClassifiers;
import com.example.dashika.cameraclassifiers.Model.SNPEModel;
import com.example.dashika.cameraclassifiers.Util.Events;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class LoadModelsTask extends AsyncTask<Void, Void, Set<SNPEModel>> {

    private static final String MODEL_DLC_FILE_NAME = "model.dlc";
    private static final String MODEL_MEAN_IMAGE_FILE_NAME = "mean_image.bin";
    private static final String LABELS_FILE_NAME = "labels.txt";
    private static final String IMAGES_FOLDER_NAME = "images";
    private static final String RAW_EXT = ".raw";
    private static final String JPG_EXT = ".jpg";
    private static final String LOG_TAG = LoadModelsTask.class.getSimpleName();

    private WeakReference<Context> mContext;

    public LoadModelsTask(Context context) {
        mContext = new WeakReference<>(context);
    }

    @Override
    protected Set<SNPEModel> doInBackground(Void... params) {
        final Set<SNPEModel> result = new LinkedHashSet<>();
        final File modelsRoot = mContext.get().getExternalFilesDir("models");
        if (modelsRoot != null) {
            result.addAll(createModels(modelsRoot));
        }
        return result;
    }

    @Override
    protected void onPostExecute(Set<SNPEModel> models) {
        ApplicationCameraClassifiers.get().bus().send(new Events.onModelLoaded(models));
    }

    private Set<SNPEModel> createModels(File modelsRoot) {
        final Set<SNPEModel> models = new LinkedHashSet<>();
        for (File child : modelsRoot.listFiles()) {
            if (!child.isDirectory()) {
                continue;
            }
            try {
                models.add(createModel(child));
            } catch (IOException e) {
                Log.e(LOG_TAG, "Failed to load model from model directory.", e);
            }
        }
        return models;
    }

    private SNPEModel createModel(File modelDir) throws IOException {
        final SNPEModel model = new SNPEModel();
        model.name = modelDir.getName();
        model.file = new File(modelDir, MODEL_DLC_FILE_NAME);
        model.meanImage = new File(modelDir, MODEL_MEAN_IMAGE_FILE_NAME);
        final File images = new File(modelDir, IMAGES_FOLDER_NAME);
        if (images.isDirectory()) {
            model.rawImages = images.listFiles(file -> file.getName().endsWith(RAW_EXT));
            model.jpgImages = images.listFiles(file -> file.getName().endsWith(JPG_EXT));
        }
        model.labels = loadLabels(new File(modelDir, LABELS_FILE_NAME));
        return model;
    }

    private String[] loadLabels(File labelsFile) throws IOException {
        final List<String> list = new LinkedList<>();
        final BufferedReader inputStream = new BufferedReader(
                new InputStreamReader(new FileInputStream(labelsFile)));
        String line;
        while ((line = inputStream.readLine()) != null) {
            list.add(line);
        }
        return list.toArray(new String[list.size()]);
    }
}
