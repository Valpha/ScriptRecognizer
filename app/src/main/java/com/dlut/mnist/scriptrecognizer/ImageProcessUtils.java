package com.dlut.mnist.scriptrecognizer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.ViewUtils;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.List;

public class ImageProcessUtils {
    public static void startProcess(String filepath) {
        ThreadUtils.executeBySingle(new CvTask(filepath));

    }

    static class CvTask extends ThreadUtils.SimpleTask<List<String>> {
        String mfilepath;
        Mat mImage;

        public CvTask(String mfilepath) {
            this.mfilepath = mfilepath;
        }

        @Override
        public List<String> doInBackground() throws Throwable {


            List<String> fileList = new ArrayList<>();


            mImage = Imgcodecs.imread(mfilepath, Imgcodecs.IMREAD_GRAYSCALE);
            int width = mImage.width();
            int height = mImage.height();
            int w = width / 7;
            int h = height / 7;
            Mat img = mImage.submat(3 * h, 4 * h, 2 * w, 5 * w);
            w = img.width();
            h = img.height();
            Mat blur = new Mat();
            Imgproc.GaussianBlur(img, blur, new Size(5, 5), 0);
            Mat otsu = new Mat();
            Imgproc.threshold(blur, otsu, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);

            Mat opening = new Mat();
            Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
            Point anchor = new Point(3, 3);
            Imgproc.morphologyEx(otsu, opening, Imgproc.MORPH_OPEN, kernel, anchor, 1);

            List<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            // Imgproc.findContours(opening, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
            Imgproc.findContours(otsu, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

            double meanWidth = 0;
            double meanHeight = 0;
            List<Integer> midWidth = new ArrayList<>();
            List<Integer> midHeight = new ArrayList<>();

            for (MatOfPoint contour : contours) {
                Rect cnt = Imgproc.boundingRect(contour);
                Moments M = Imgproc.moments(contour, true);
                meanHeight += cnt.height;
                meanWidth += cnt.width;
                midHeight.add(cnt.height);
                midWidth.add(cnt.width);
            }
            meanHeight /= contours.size();
            meanWidth /= contours.size();
            for (MatOfPoint contour : contours) {
                Rect cnt = Imgproc.boundingRect(contour);

                if (cnt.height < Math.min(h / 4, meanHeight * 0.8)) {
                    continue;

                }
                if (cnt.x + (int) cnt.width / 2 < 0.1 * w || cnt.x > 0.9 * w) {
                    continue;
                }
                if (cnt.y + (int) cnt.height / 2 < 0.08 * h || cnt.y > 0.92 * h) {
                    continue;
                }
                if (cnt.width > 1.5 * meanWidth) {
                    Imgproc.rectangle(img, new Point(cnt.x, cnt.y), new Point(cnt.x + (cnt.width >> 1), cnt.y + cnt.height), new Scalar(0, 0, 255));
                    Imgproc.rectangle(img, new Point(cnt.x + (cnt.width >> 1), cnt.y), new Point(cnt.x + cnt.width, cnt.y + cnt.height), new Scalar(0, 0, 255));
                } else {
                    Imgproc.rectangle(img,cnt,new Scalar(0,0,255));
                }
            }

            Imgcodecs.imwrite(mfilepath.replace(".jpg,","_AfterProcess.jpg"),img);

            // Imgcodecs.imwrite(mfilepath.replace(".jpg","_RAY.jpg"),mImage);
            Bitmap bmp = BitmapFactory.decodeFile(mfilepath);
            Utils.matToBitmap(img, bmp);
            ViewUtils.runOnUiThread(() -> {
                MainActivity.resultView.setImageBitmap(bmp);
                MainActivity.resultView.setVisibility(View.VISIBLE);
                MainActivity.resultView.setOnClickListener(view -> view.setVisibility(View.GONE));
            });


            return fileList;
        }

        @Override
        public void onSuccess(List<String> result) {
            ToastUtils.showShort("ImageProcess was Done!");
        }

    }
}
