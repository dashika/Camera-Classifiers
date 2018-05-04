package com.example.dashika.cameraclassifiers.snpe.tasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Pair;

import com.example.dashika.cameraclassifiers.ApplicationCameraClassifiers;
import com.example.dashika.cameraclassifiers.Model.SNPEModel;
import com.example.dashika.cameraclassifiers.Util.Events;
import com.qualcomm.qti.snpe.FloatTensor;
import com.qualcomm.qti.snpe.NeuralNetwork;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ClassifyImageTask extends AsyncTask<Bitmap, Void, String[]> {

    private static final String OUTPUT_LAYER = "softmax:0";
    private static final int FLOAT_SIZE = 4;
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;
    private final NeuralNetwork mNeuralNetwork;

    private final SNPEModel mModel;

    private final Bitmap mImage;


    public ClassifyImageTask(NeuralNetwork network, Bitmap image, SNPEModel model) {
        mNeuralNetwork = network;
        mImage = image;
        mModel = model;
    }

    @Override
    protected String[] doInBackground(Bitmap... params) {
        final List<String> result = new LinkedList<>();

        final FloatTensor tensor = mNeuralNetwork.createFloatTensor(
                mNeuralNetwork.getInputTensorsShapes().get("Mul:0"));

        final FloatBuffer meanImage = loadMeanImageIfAvailable(mModel.meanImage, tensor.getSize());
        if (meanImage.remaining() != tensor.getSize()) {
            return new String[0];
        }

        writeRgbBitmapAsFloat(mImage, tensor);

        final Map<String, FloatTensor> inputs = new HashMap<>();
        inputs.put("Mul:0", tensor);

        final Map<String, FloatTensor> outputs = mNeuralNetwork.execute(inputs);
        for (Map.Entry<String, FloatTensor> output : outputs.entrySet()) {
            if (output.getKey().equals(OUTPUT_LAYER)) {
                for (Pair pair : topK(output.getValue())) {
                    result.add(mModel.labels[(int) pair.first]);
                    result.add(String.valueOf(pair.second));
                }
            }
        }
        return result.toArray(new String[result.size()]);
    }

    @Override
    protected void onPostExecute(String[] labels) {
        super.onPostExecute(labels);
        if (labels.length > 0) {
            ApplicationCameraClassifiers.get().bus().send(new Events.onClassificationResult(labels));
        } else {
            ApplicationCameraClassifiers.get().bus().send(new Events.onClassificationResult(new String[]{""}));
        }
    }

    private void writeRgbBitmapAsFloat(Bitmap image, FloatTensor tensor) {
        final int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getPixels(pixels, 0, image.getWidth(), 0, 0,
                image.getWidth(), image.getHeight());
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                final int rgb = pixels[y * image.getWidth() + x];
                float b = (((rgb) & 0xFF) - IMAGE_MEAN) / IMAGE_STD;
                float g = (((rgb >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD;
                float r = (((rgb >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD;
                float[] pixelFloats = {b, g, r};
                tensor.write(pixelFloats, 0, pixelFloats.length, y, x);
            }
        }
    }

    private FloatBuffer loadMeanImageIfAvailable(File meanImage, final int imageSize) {
        ByteBuffer buffer = ByteBuffer.allocate(imageSize * FLOAT_SIZE)
                .order(ByteOrder.nativeOrder());
        if (!meanImage.exists()) {
            return buffer.asFloatBuffer();
        }
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(meanImage);
            final byte[] chunk = new byte[1024];
            int read;
            while ((read = fileInputStream.read(chunk)) != -1) {
                buffer.put(chunk, 0, read);
            }
            buffer.flip();
        } catch (IOException e) {
            buffer = ByteBuffer.allocate(imageSize * FLOAT_SIZE);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    // Do thing
                }
            }
        }
        return buffer.asFloatBuffer();
    }

    private Pair[] topK(FloatTensor tensor) {
        final float[] array = new float[tensor.getSize()];
        tensor.read(array, 0, array.length);

        final boolean[] selected = new boolean[tensor.getSize()];
        final Pair[] topK = new Pair[1];
        int count = 0;
        while (count < 1) {
            final int index = top(array, selected);
            selected[index] = true;
            topK[count] = new Pair<>(index, array[index]);
            count++;
        }
        return topK;
    }

    private int top(float[] array, boolean[] selected) {
        int index = 0;
        float max = -1.f;
        for (int i = 0; i < array.length; i++) {
            if (selected[i]) {
                continue;
            }
            if (array[i] > max) {
                max = array[i];
                index = i;
            }
        }
        return index;
    }
}
