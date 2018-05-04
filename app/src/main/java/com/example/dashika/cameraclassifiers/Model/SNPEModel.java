package com.example.dashika.cameraclassifiers.Model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

import paperparcel.PaperParcel;

@PaperParcel
public class SNPEModel implements Parcelable {

    public static final Uri MODELS_URI = Uri.parse("content://snpe/models");

    public static final String INVALID_ID = "null";
    public static final Creator<SNPEModel> CREATOR = new Creator<SNPEModel>() {
        @Override
        public SNPEModel createFromParcel(Parcel in) {
            return new SNPEModel(in);
        }

        @Override
        public SNPEModel[] newArray(int size) {
            return new SNPEModel[size];
        }
    };
    public File file;
    public String[] labels;
    public File[] rawImages;
    public File[] jpgImages;
    public String name;
    public File meanImage;

    protected SNPEModel(Parcel in) {
        name = in.readString();
        file = new File(in.readString());

        final String[] rawPaths = new String[in.readInt()];
        in.readStringArray(rawPaths);
        rawImages = fromPaths(rawPaths);

        final String[] jpgPaths = new String[in.readInt()];
        in.readStringArray(jpgPaths);
        jpgImages = fromPaths(jpgPaths);

        meanImage = new File(in.readString());

        labels = new String[in.readInt()];
        in.readStringArray(labels);
    }

    public SNPEModel() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(file.getAbsolutePath());
        dest.writeInt(rawImages.length);
        dest.writeStringArray(toPaths(rawImages));
        dest.writeInt(jpgImages.length);
        dest.writeStringArray(toPaths(jpgImages));
        dest.writeString(meanImage.getAbsolutePath());
        dest.writeInt(labels.length);
        dest.writeStringArray(labels);
    }

    private File[] fromPaths(String[] paths) {
        final File[] files = new File[paths.length];
        for (int i = 0; i < paths.length; i++) {
            files[i] = new File(paths[i]);
        }
        return files;
    }

    private String[] toPaths(File[] files) {
        final String[] paths = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            paths[i] = files[i].getAbsolutePath();
        }
        return paths;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return name.toUpperCase();
    }
}
