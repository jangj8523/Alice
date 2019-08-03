package com.v1.avatar.v1.Activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.v1.avatar.v1.R;
import com.v1.avatar.v1.AIEngine.FaceView;
//import com.v1.avatar.v1.classes.SafeFaceDetector;
import com.v1.avatar.v1.AIEngine.FileUtils;

import com.tzutalin.dlib.Constants;
import com.tzutalin.dlib.FaceDet;
import com.tzutalin.dlib.VisionDetRet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

public class NewAvatarActivity extends Activity {
    private static final String TAG = "NewAvatarActivity";
    private FaceDet mFaceDet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_avatar);
        Bundle b = getIntent().getExtras();
        String imageDecodable = b.getString("imagePath");
        Bitmap bitmap = null;
        try {
            if (imageDecodable == null) {
                InputStream stream = getResources().openRawResource(R.raw.face4);
                bitmap = BitmapFactory.decodeStream(stream);
            } else {
                Uri imageUri = Uri.fromFile(new File(imageDecodable));
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            }
        } catch (IOException e) {
        }
        if (mFaceDet == null) mFaceDet = new FaceDet(Constants.getFaceShapeModelPath());
        detectFace(bitmap);
    }

    private void detectFace(Bitmap bitmap) {
        FaceDet faceDet = new FaceDet(Constants.getFaceShapeModelPath());
        List<VisionDetRet> results = faceDet.detect(bitmap);
        for (final VisionDetRet ret : results) {
            String label = ret.getLabel();
            int rectLeft = ret.getLeft();
            int rectTop = ret.getTop();
            int rectRight = ret.getRight();
            int rectBottom = ret.getBottom();
            // Get 68 landmark points
            ArrayList<Point> landmarks = ret.getFaceLandmarks();
            for (Point point : landmarks) {
                int pointX = point.x;
                int pointY = point.y;
            }
        }
//
//        FaceView overlay = (FaceView) findViewById(R.id.faceView);
//        overlay.setContent(bitmap, faces);
        final String targetPath = Constants.getFaceShapeModelPath();
        FileUtils.copyFileFromRawToOthers(getApplicationContext(), R.raw.shape_predictor_68_face_landmarks, targetPath);
        List<VisionDetRet> faces = mFaceDet.detect(bitmap);
        Log.d("DetectFace", faces.toString());
        FaceView overlay = (FaceView) findViewById(R.id.faceView);
        overlay.setContent(bitmap, faces);
    }
}