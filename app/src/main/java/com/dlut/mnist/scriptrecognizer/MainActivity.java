package com.dlut.mnist.scriptrecognizer;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.SPUtils;
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
import java.util.Objects;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * @author Valpha
 */
public class MainActivity extends CameraActivity {

    private static final int WRITE_PERMISSION_REQUEST_CODE = 220;
    private String TAG = getClass().getSimpleName();
    private CameraView cameraView;
    private ImageView ivRefRect;
    private String SPKEY_FILE = "file";
    private String SPNAME = "ScriptRecognizer";
    private boolean gotFile = false;
    private TextView tvFile;
    private DataManager dataManager;
    private EditText etStuNumber;
    private boolean predictFinishedFlag = false;
    private DataListAdapter adapter;

    @Override
    protected void onStart() {
        super.onStart();
        // LogUtils.dTag(TAG, "OnStart");
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.cameraView:
                    LogUtils.d("onClick: cameraView was clicked!");

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                    String currentDateandTime = sdf.format(new Date());
                    FileUtils.createDir(PathUtils.getExternalAppFilesPath() + "/ScriptRecognizer");
                    String fileName = PathUtils.getExternalAppFilesPath() + "/ScriptRecognizer" + "/" + currentDateandTime + ".jpg";

                    cameraView.takePicture(fileName);
                    ToastUtils.showShort(fileName + " saved");
                    break;

                default:
                    break;


            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.dTag(TAG, "OnCreate");
        setContentView(R.layout.activity_main);

        //  保持屏幕不锁定
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        dataManager = new DataManager();
        initView();
        TFLiteManager tfLite = TFLiteManager.getInstance();
        tfLite.init();
        requestWritePermission();

    }

    private void shareCsvFile() {
        String filepath = SPUtils.getInstance(SPNAME).getString(SPKEY_FILE);
        LogUtils.dTag(TAG, filepath);
        if ("".equals(filepath)) {
            ToastUtils.showShort("未指定文件！请先导入文件");
        } else {
            ShareFileUtils.shareFile(this, filepath);
        }
    }

    public void openSystemFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择文件!"), 1);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "请安装文件管理器", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        tvFile = findViewById(R.id.tv_file);
        ivRefRect = findViewById(R.id.iv_reference);
        cameraView = findViewById(R.id.cameraView);
        cameraView.setVisibility(SurfaceView.VISIBLE);
        cameraView.setCvCameraViewListener(new CvCameraViewListener2() {
            @Override
            public void onCameraViewStarted(int width, int height) {
                Bitmap refRect = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(refRect);
                Paint paint = new Paint();
                paint.setColor(Color.CYAN);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3);
                canvas.drawRect(2 * width / 7, 3 * height / 7, 5 * width / 7, 4 * height / 7, paint);
                ivRefRect.setImageBitmap(refRect);
            }

            @Override
            public void onCameraViewStopped() {

            }

            @Override
            public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
                return inputFrame.rgba();
            }
        });
        ListView lvDataBoard = findViewById(R.id.lv_databoard);
        adapter = new DataListAdapter(this, dataManager);
        lvDataBoard.setAdapter(adapter);
        Button btSave = findViewById(R.id.bt_save);
        btSave.setOnClickListener(v -> {
            try {
                String filepath = SPUtils.getInstance(SPNAME).getString(SPKEY_FILE);
                if ("".equals(filepath)) {
                    throw new IOException("None saved filepath.");
                } else {
                    dataManager.writeCsv(filepath);
                }
                ToastUtils.showShort("保存成功");
            } catch (IOException e) {
                e.printStackTrace();
                ToastUtils.showShort("保存出错！");
            }
        });
        Button btImport = findViewById(R.id.bt_import);
        btImport.setOnClickListener(v -> openSystemFile());
        Button btShare = findViewById(R.id.bt_export);
        btShare.setOnClickListener(v -> shareCsvFile());

        etStuNumber = findViewById(R.id.et_stunumber);
        etStuNumber.setOnEditorActionListener((v, actionId, event) -> {
            int number = dataManager.getDataCount();
            LogUtils.dTag(TAG, "count is :" + number);
            if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE
                    || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                int mark = 0;
                LogUtils.dTag(TAG, "11111");
                for (int StuId = 0; StuId < number; StuId++) {

                    String stunum = dataManager.getDataByOrder(StuId).getStunum();
                    String etStunum = etStuNumber.getText().toString().trim();
                    if (stunum.equals(etStunum)) {
                        EditText score = lvDataBoard.getChildAt(StuId).findViewById(R.id.et_score);
                        LogUtils.dTag(TAG, "fuck");
                        score.requestFocus();
                        InputMethodManager inputManager = (InputMethodManager) score.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert inputManager != null;
                        inputManager.showSoftInput(score, 0);
                        mark = 1;
                    }

                }
                if (mark != 1) {
                    ToastUtils.showShort("学号不存在！");
                }
            }
            return false;
        });
    }

    private void requestWritePermission() {
        if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST_CODE);
        } else {
            onWritePermissionGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_PERMISSION_REQUEST_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onWritePermissionGranted();
        }
    }

    private void onWritePermissionGranted() {
        Log.d(TAG, "onWritePermissionGranted: Got Write Permission");
        switch (FileUtils.createDir(PathUtils.getExternalAppFilesPath() + "/ScriptRecognizer")) {
            case FileUtils.FLAG_SUCCESS:
                LogUtils.i("ScriptRecognizer 目录创建成功。");
                break;
            case FileUtils.FLAG_EXISTS:
                LogUtils.i("ScriptRecognizer 目录已存在");
                break;
            case FileUtils.FLAG_FAILED:
                LogUtils.e("ScriptRecognizer 目录创建失败！");
                break;
            default:
                break;
        }
        loadCsvFile();
    }

    private void loadCsvFile() {
        SPUtils spUtils = SPUtils.getInstance(SPNAME);

        String filepath = spUtils.getString(SPKEY_FILE);
        LogUtils.dTag(TAG, filepath);
        if (!Objects.equals(filepath, "")) {
            try {
                dataManager.clearData();
                dataManager.readCsv(filepath);
                // gotFile = true;
                tvFile.setText(filepath);
                adapter.notifyDataSetChanged();
            } catch (IOException e) {
                e.printStackTrace();
                // gotFile = false;
                ToastUtils.showLong("CSV文件打开失败，请检查文件路径是否正确");
            }
        } else {
            try {
                dataManager.writeCsv(PathUtils.getExternalAppFilesPath() + "/ScriptRecognizer" + "/templete.csv");
                // gotFile = false;
            } catch (IOException e) {
                e.printStackTrace();
            }

            ToastUtils.showLong("未指定文件，请参考模板手动加载CSV文件！");
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        // LogUtils.dTag(TAG, "OnStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // LogUtils.dTag(TAG, "OnPause");
        if (cameraView != null) {
            cameraView.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // LogUtils.dTag(TAG, "OnResume");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            // Get the Uri of the selected file
            Uri uri = data.getData();
            LogUtils.dTag("MainActivity", uri);

            assert uri != null;
            String filepath = FileUtils.getFilePathByUri(this, uri);
            LogUtils.dTag("MainActivity", filepath);
            SPUtils.getInstance(SPNAME).put(SPKEY_FILE, filepath);
            loadCsvFile();

        }
    }

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
