package com.dlut.mnist.scriptrecognizer;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.TimeUtils;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.opencv.android.CameraBridgeViewBase.*;

public class Listener implements CvCameraViewListener2{


    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        return inputFrame.rgba();
    }

}
