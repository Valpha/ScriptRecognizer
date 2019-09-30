package com.dlut.mnist.scriptrecognizer;


import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.dlut.mnist.scriptrecognizer.DAO.DataManager;
import com.dlut.mnist.scriptrecognizer.View.CameraView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends CameraActivity {

    private String TAG = getClass().getSimpleName();
    private CameraView cameraView;
    public static ImageView resultView;
    private ListView lvDataBoard;
    private Button btSave;
    private ImageView ivCankaokuang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  保持屏幕不锁定
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        resultView = (ImageView) findViewById(R.id.imageView2);
        cameraView = (CameraView) findViewById(R.id.cameraView);
        ivCankaokuang = (ImageView) findViewById(R.id.iv_cankaokuang);
        cameraView.setVisibility(SurfaceView.VISIBLE);


        cameraView.setCvCameraViewListener(new CvCameraViewListener2() {
            @Override
            public void onCameraViewStarted(int width, int height) {
                Bitmap cankaokuang = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(cankaokuang);
                Paint paint = new Paint();
                paint.setColor(Color.CYAN);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3);
                canvas.drawRect(width/7*2,height/7*3,width/7*5,height/7*4,paint);
                ivCankaokuang.setImageBitmap(cankaokuang);
            }

            @Override
            public void onCameraViewStopped() {

            }

            @Override
            public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
                return inputFrame.rgba();
            }
        });
        // TODO: 在这里添加代码

        try {
            DataManager.getInstance().readCsv(PathUtils.getExternalStoragePath() + "/ScriptRecognizer/score.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // DatabaseUtils.setContext(this);
        lvDataBoard = (ListView) findViewById(R.id.lv_databoard);
        ListAdapter adapter = new DataListAdapter(this);
        lvDataBoard.setAdapter(adapter);
        btSave = (Button) findViewById(R.id.bt_save);
        btSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    DataManager.getInstance().writeCsv(PathUtils.getExternalStoragePath()+"/ScriptRecognizer/score.csv");
                    ToastUtils.showShort("保存成功");
                } catch (IOException e) {

                    e.printStackTrace();
                    ToastUtils.showShort("保存出错！");
                }

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraView != null) {
            cameraView.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(cameraView);
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.cameraView:
                    LogUtils.d("onClick: cameraView was clicked!");

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                    String currentDateandTime = sdf.format(new Date());
                    FileUtils.createDir(PathUtils.getExternalStoragePath() + "/ScriptRecognizer");
                    String fileName = PathUtils.getExternalStoragePath() + "/ScriptRecognizer" + "/sample_picture_" + currentDateandTime + ".jpg";

                    cameraView.takePicture(fileName);
                    ToastUtils.showShort(fileName + " saved");
                    break;

                default:
                    break;


            }
        }
    };
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    cameraView.enableView();
                    cameraView.setOnClickListener(onClickListener);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };


}
