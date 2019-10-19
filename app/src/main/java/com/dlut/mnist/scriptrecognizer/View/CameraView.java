package com.dlut.mnist.scriptrecognizer.View;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.dlut.mnist.scriptrecognizer.ImageProcessUtils;

import org.opencv.android.JavaCameraView;

import java.io.FileOutputStream;
import java.util.List;

public class CameraView extends JavaCameraView implements PictureCallback {

    private static final String TAG = "Sample::Tutorial3View";
    private String mPictureFileName;

    public CameraView(Context context, AttributeSet attrs) {

        super(context, attrs);

    }

    public List<Size> getResolutionList() {
        return mCamera.getParameters().getSupportedPreviewSizes();
    }

    public void setResolution(Size resolution) {
        disconnectCamera();
        mMaxHeight = resolution.height;
        mMaxWidth = resolution.width;
        connectCamera(getWidth(), getHeight());
    }

    public Size getResolution() {
        return mCamera.getParameters().getPreviewSize();
    }

    public void takePicture(final String fileName) {
        LogUtils.i("Taking picture");
        Camera.Parameters parameters = mCamera.getParameters();
        // parameters.setPreviewSize(1280, 720);
        //   mCamera.setParameters(parameters);
        try {
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            //非常罕见的情况
            //个别机型在SupportPreviewSizes里汇报了支持某种预览尺寸，但实际是不支持的，设置进去就会抛出RuntimeException.
            e.printStackTrace();
            try {
                //遇到上面所说的情况，只能设置一个最小的预览尺寸
                parameters.setPreviewSize(1920, 1080);
                mCamera.setParameters(parameters);
            } catch (Exception e1) {
                //到这里还有问题，就是拍照尺寸的锅了，同样只能设置一个最小的拍照尺寸
                e1.printStackTrace();
                try {
                    parameters.setPictureSize(1920, 1080);
                    mCamera.setParameters(parameters);
                } catch (Exception ignored) {
                }
            }
        }

        this.mPictureFileName = fileName;
        // Postview and jpeg are sent in the same buffers if the queue is not empty when performing a capture.
        // Clear up buffers to avoid mCamera.takePicture to be stuck because of a memory issue
        mCamera.setPreviewCallback(null);


        // PictureCallback is implemented by the current class
        mCamera.takePicture(null, null, this);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.i(TAG, "Saving a bitmap to file");
        // The camera preview was automatically stopped. Start it again.
        mCamera.startPreview();
        mCamera.setPreviewCallback(this);

        // Write the image in a file (in jpeg format)
        try {
            FileOutputStream fos = new FileOutputStream(mPictureFileName);

            fos.write(data);
            fos.close();

        } catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }
        ImageProcessUtils.startProcess(mPictureFileName);

    }
}
