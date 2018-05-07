/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.dashika.cameraclassifiers.View.CameraView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

/**
 * A {@link TextureView} that can be adjusted to a specified aspect ratio.
 */
public class AutoFitTextureView extends TextureView {

    int maxwidth = 0;
    int maxheight = 0;
    private int mRatioWidth = 0;
    private int mRatioHeight = 0;
    private Size previewSize;

    public AutoFitTextureView(Context context) {
        this(context, null);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Sets the aspect ratio for this view. The size of the view will be measured based on the ratio
     * calculated from the parameters. Note that the actual sizes of parameters don't matter, that
     * is, calling setAspectRatio(2, 3) and setAspectRatio(4, 6) make the same result.
     *
     * @param width  Relative horizontal size
     * @param height Relative vertical size
     */
    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
    }

    public void setAspectRatio(int width, int height, int maxwidth, int maxheight, Size preview) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        mRatioWidth = width;
        mRatioHeight = height;
        this.maxwidth = maxwidth;
        this.maxheight = maxheight;
        this.previewSize = preview;
        enterTheMatrix();
        requestLayout();
    }

    private void adjustAspectRatio(int previewWidth,
                                   int previewHeight,
                                   int rotation) {
        Matrix txform = new Matrix();
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        RectF rectView = new RectF(0, 0, viewWidth, viewHeight);
        float viewCenterX = rectView.centerX();
        float viewCenterY = rectView.centerY();
        RectF rectPreview = new RectF(0, 0, previewHeight, previewWidth);
        float previewCenterX = rectPreview.centerX();
        float previewCenterY = rectPreview.centerY();

        if (Surface.ROTATION_90 == rotation ||
                Surface.ROTATION_270 == rotation) {
            rectPreview.offset(viewCenterX - previewCenterX,
                    viewCenterY - previewCenterY);

            txform.setRectToRect(rectView, rectPreview,
                    Matrix.ScaleToFit.FILL);

            float scale = Math.max((float) viewHeight / previewHeight,
                    (float) viewWidth / previewWidth);

            txform.postScale(scale, scale, viewCenterX, viewCenterY);
            txform.postRotate(90 * (rotation - 2), viewCenterX,
                    viewCenterY);
        } else {
            if (Surface.ROTATION_180 == rotation) {
                txform.postRotate(180, viewCenterX, viewCenterY);
            }
        }

       // if (LollipopCamera.type == 1) {
     //       txform.postScale(-1, 1, viewCenterX, viewCenterY);
     //   }

        setTransform(txform);
    }

    private void enterTheMatrix() {
        if (previewSize != null) {
            adjustAspectRatio(mRatioWidth,
                    mRatioHeight,
                    ((Activity) getContext()).getWindowManager().getDefaultDisplay().getRotation());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        boolean isFullBleed = true;
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
        } else {
            setMeasuredDimension(height * mRatioWidth / mRatioHeight,height);
        }

    }

}
