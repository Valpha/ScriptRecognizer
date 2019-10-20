package com.dlut.mnist.scriptrecognizer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.ViewUtils;
import com.google.firebase.ml.common.FirebaseMLException;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * @author Valpha
 */
public class ImageProcessUtils {
    public static void startProcess(String filepath) {
        ThreadUtils.executeBySingle(new CvTask(filepath));

    }

    static class CvTask extends ThreadUtils.SimpleTask<List<String>> {
        private static final String TAG = "ImageProcess";
        String mfilepath;
        Mat mImage;

        CvTask(String mfilepath) {
            this.mfilepath = mfilepath;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public List<String> doInBackground() {


            List<String> fileList = new ArrayList<>();
            TFLiteManager tfLite = TFLiteManager.getInstance();
            Random random = new Random();
            String dirpath = mfilepath.replace(".jpg", "/");
            FileUtils.createDir(dirpath);

            mImage = Imgcodecs.imread(mfilepath, Imgcodecs.IMREAD_ANYCOLOR);
            int width = mImage.width();
            int height = mImage.height();
            int w = width / 7;
            int h = height / 7;
            Mat mImageColored = mImage.submat(3 * h, 4 * h, 2 * w, 5 * w);
            Mat img = new Mat();
            Imgproc.cvtColor(mImageColored, img, Imgproc.COLOR_BGR2GRAY);
            w = img.width();
            h = img.height();

            Imgcodecs.imwrite(dirpath.concat("origin.jpg"), img);


            Mat blur = new Mat();
            Imgproc.GaussianBlur(img, blur, new Size(5, 5), 0);
            Imgcodecs.imwrite(dirpath.concat("blur.jpg"), blur);

            Mat otsu = new Mat();
            Imgproc.threshold(blur, otsu, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
            Imgcodecs.imwrite(dirpath.concat("otsu.jpg"), otsu);

            Mat opening = new Mat();
            Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
            Point anchor = new Point(3, 3);
            Imgproc.morphologyEx(otsu, opening, Imgproc.MORPH_OPEN, kernel, anchor, 1);
            Imgcodecs.imwrite(dirpath.concat("opening.jpg"), opening);


            List<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(opening, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

            double meanWidth = 0;
            double meanHeight = 0;
            List<Integer> midWidth = new ArrayList<>();
            List<Integer> midHeight = new ArrayList<>();

            for (MatOfPoint contour : contours) {
                Rect cnt = Imgproc.boundingRect(contour);
                // Moments M = Imgproc.moments(contour, true);
                if (Imgproc.contourArea(contour) >= 100) {

                    meanHeight += cnt.height;
                    meanWidth += cnt.width;
                    midHeight.add(cnt.height);
                    midWidth.add(cnt.width);
                }
            }
            meanHeight /= contours.size();
            meanWidth /= contours.size();
            int i = 0;
            Collections.sort(contours, (o1, o2) -> {
                Rect rect1 = Imgproc.boundingRect(o1);
                Rect rect2 = Imgproc.boundingRect(o2);

                // double total = rect1.tl().y/rect2.tl().y;
                // if (total >= 0.9 && total <= 1.4) {
                //     result = Double.compare(rect1.tl().x, rect2.tl().x);
                // }
                return Double.compare(rect1.tl().x, rect2.tl().x);
            });

            List<Rect> rects = new ArrayList<>();
            List<String> fileNameList = new ArrayList<>();
            String string;
            for (MatOfPoint contour : contours) {
                Rect cnt = Imgproc.boundingRect(contour);
                //  滤除过于短小的像素点
                if (cnt.height < min(h / 5, meanHeight * 0.8)) {
                    continue;
                }
                //  滤除左右边缘
                if (cnt.x + (cnt.width >> 1) < 0.1 * w || cnt.x > 0.9 * w) {
                    continue;
                }
                //  滤除上下边缘
                if (cnt.y + (cnt.height >> 1) < 0.08 * h || cnt.y > 0.92 * h) {
                    continue;
                }
                // if (cnt.width > 1.5 * meanWidth) {
                //     Rect rect1 = new Rect(new Point(cnt.x, cnt.y), new Point(cnt.x + (cnt.width >> 1), cnt.y + cnt.height));
                //     string = dirpath.concat(i++ +".jpg");
                //     fileNameList.add(string);
                //     Imgcodecs.imwrite(string, new Mat(img, rect1));
                //     Imgproc.rectangle(mImageColored, rect1, new Scalar(0, 0, 255));
                //
                //     rects.add(rect1);
                //     Rect rect2 = new Rect(new Point(cnt.x + (cnt.width >> 1), cnt.y), new Point(cnt.x + cnt.width, cnt.y + cnt.height));
                //     string = dirpath.concat(i++ +".jpg");
                //     fileNameList.add(string);
                //     Imgcodecs.imwrite(string, new Mat(img, rect2));
                //     Imgproc.rectangle(mImageColored, rect2, new Scalar(0, 0, 255));
                //     rects.add(rect2);
                //
                // } else {
                Rect rect;
                if (cnt.width < meanWidth) {
                    rect = new Rect(new Point(max(cnt.x + (cnt.width >> 1) - meanWidth / 2, 0), max(cnt.y - 10, 0)), new Point(min(cnt.x + (cnt.width >> 1) + meanWidth / 2, w), min(cnt.y + 10 + cnt.height, h)));

                } else {
                    rect = new Rect(new Point(max(cnt.x - 10, 0), max(cnt.y - 10, 0)), new Point(min(cnt.x + 10 + cnt.width, w), min(cnt.y + 10 + cnt.height, h)));

                }
                rects.add(rect);
                string = dirpath.concat(i++ + String.valueOf(random.nextInt(50000)) + ".jpg");
                fileNameList.add(string);
                Imgcodecs.imwrite(string, new Mat(img, rect));
                Imgproc.rectangle(mImageColored, rect, new Scalar(0, 0, 255));
                // i++;
                // }
            }
            Imgcodecs.imwrite(dirpath.concat("img.jpg"), mImageColored);

            // Imgcodecs.imwrite(mfilepath.replace(".jpg","_RAY.jpg"),mImage);
            Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
            Utils.matToBitmap(mImageColored, bmp);
            ViewUtils.runOnUiThread(() -> {
                Activity mainActivity = MyActivityManager.getInstance().getCurrentActivity();
                ImageView ivResult = mainActivity.findViewById(R.id.iv_result);
                ivResult.setImageBitmap(bmp);
                ivResult.setVisibility(View.VISIBLE);
                ivResult.setOnClickListener(view -> view.setVisibility(View.GONE));
                EditText etNumber = mainActivity.findViewById(R.id.et_stunumber);
                etNumber.setText("");


            });

            //  predict part
            boolean isEnd;
            Log.d(TAG, "doInBackground: 开始预测");
            for (int i1 = 0; i1 < fileNameList.size(); i1++) {
                String filename = fileNameList.get(i1);

                Log.d(TAG, "doInBackground: 本次预测的顺序为" + i + "---文件名为" + filename);

                Bitmap bitmap = BitmapFactory.decodeFile(filename);
                try {
                    isEnd = i1 == (fileNameList.size() - 1);
                    tfLite.predict(bitmap, isEnd);
                } catch (FirebaseMLException e) {
                    e.printStackTrace();
                }
            }


            return fileList;
        }

        @Override
        public void onSuccess(List<String> result) {
            ToastUtils.showShort("ImageProcess was Done!");
        }

    }
}
