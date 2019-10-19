package com.dlut.mnist.scriptrecognizer;

import android.graphics.Bitmap;
import android.util.Log;

import com.blankj.utilcode.util.ToastUtils;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.common.modeldownload.FirebaseLocalModel;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelOptions;

public class TFLiteManager {
    private static final TFLiteManager ourInstance = new TFLiteManager();
    private static final String TAG = "TFLite";
    private FirebaseModelInterpreter firebaseInterpreter;
    private FirebaseModelInputOutputOptions inputOutputOptions;

    private TFLiteManager() {
        this.firebaseInterpreter = null;
        this.inputOutputOptions = null;
    }

    public static TFLiteManager getInstance() {
        return ourInstance;
    }

    public void init() throws FirebaseMLException {
        Log.d(TAG, "init: Enter");

        FirebaseLocalModel localSource =
                new FirebaseLocalModel.Builder("mnist_model")
                        .setAssetFilePath("1.lite")
                        .build();
        FirebaseModelManager.getInstance().registerLocalModel(localSource);
        Log.d(TAG, "init: set Local Model");

        FirebaseModelOptions options = new FirebaseModelOptions.Builder()
                .setLocalModelName("mnist_model")
                .build();
        Log.d(TAG, "init: set Model Options");

        firebaseInterpreter = FirebaseModelInterpreter.getInstance(options);
        Log.d(TAG, "init: set Interpreter");

        inputOutputOptions = new FirebaseModelInputOutputOptions.Builder()
                .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 28, 28, 1})
                .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 10})
                .build();
        Log.d(TAG, "init: 初始化完成");
    }

    public void predict(Bitmap inputBitmap) throws FirebaseMLException {
        Bitmap bitmap = inputBitmap;
        bitmap = Bitmap.createScaledBitmap(bitmap, 28, 28, true);

        int batchNum = 0;
        float[][][][] input = new float[1][28][28][1];
        for (int x = 0; x < 28; x++) {
            for (int y = 0; y < 28; y++) {
                int pixel = bitmap.getPixel(x, y);
                // Normalize channel values to [-1.0, 1.0]. This requirement varies by
                // model. For example, some models might require values to be normalized
                // to the range [0.0, 1.0] instead.
                input[batchNum][x][y][0] = (pixel - 127) / 128.0f;
                // input[batchNum][x][y][0] = (Color.red(pixel) - 127) / 128.0f;
                // input[batchNum][x][y][1] = (Color.green(pixel) - 127) / 128.0f;
                // input[batchNum][x][y][2] = (Color.blue(pixel) - 127) / 128.0f;
            }
        }

        FirebaseModelInputs inputs = new FirebaseModelInputs.Builder()
                .add(input)  // add() as many input arrays as your model requires
                .build();
        firebaseInterpreter.run(inputs, inputOutputOptions)
                .addOnSuccessListener(
                        result -> {
                            // ...
                            float[][] output = result.getOutput(0);
                            float[] probabilities = output[0];
                            for (int i = 0; i < probabilities.length; i++) {
                                if (probabilities[i] >= 0.9) {
                                    Log.d(TAG, "predict: 预测结果：" + i);


                                }
                            }
                            ToastUtils.showShort("预测完成！");


                        })
                .addOnFailureListener(
                        e -> {
                            ToastUtils.showShort("预测出现错误！");
                            e.printStackTrace();

                            // Task failed with an exception
                            // ...
                        });


    }
}
