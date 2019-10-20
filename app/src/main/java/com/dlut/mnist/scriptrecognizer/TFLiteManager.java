package com.dlut.mnist.scriptrecognizer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.blankj.utilcode.util.ViewUtils;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.common.modeldownload.FirebaseLocalModel;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions;

class TFLiteManager {
    private static final TFLiteManager ourInstance = new TFLiteManager();
    private static final String TAG = "TFLite";

    private TFLiteManager() {
    }

    static TFLiteManager getInstance() {
        return ourInstance;
    }

    void init() {
        Log.d(TAG, "init: Enter");
        FirebaseLocalModel localModel = new FirebaseLocalModel.Builder("my_mnist_model")
                .setAssetFilePath("manifest.json")
                .build();
        FirebaseModelManager.getInstance().registerLocalModel(localModel);
        Log.d(TAG, "init: set Local Model");
        Log.d(TAG, "init: 初始化完成");
    }

    void predict(Bitmap inputBitmap, boolean isEnd) throws FirebaseMLException {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(inputBitmap);
        FirebaseVisionOnDeviceAutoMLImageLabelerOptions labelerOptions =
                new FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder()
                        .setLocalModelName("my_mnist_model")
                        .setConfidenceThreshold(0)
                        .build();
        FirebaseVisionImageLabeler labeler =
                FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(labelerOptions);
        labeler.processImage(image)
                .addOnSuccessListener(labels -> {
                    String text = labels.get(0).getText();
                    float confidence = labels.get(0).getConfidence();
                    Log.d(TAG, "onSuccess: the Text is ---" + text);
                    Log.d(TAG, "onSuccess: the confidence is ---" + confidence);
                    ViewUtils.runOnUiThread(() -> {
                        Activity mainActivity = MyActivityManager.getInstance().getCurrentActivity();
                        EditText etNumber = mainActivity.findViewById(R.id.et_stunumber);
                        String prefix = etNumber.getText().toString();
                        etNumber.setText(prefix + text);
                        if (isEnd) {
                            etNumber.onEditorAction(EditorInfo.IME_ACTION_DONE);
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Log.d(TAG, "onFailure: something wrong...");
                });
    }

}
