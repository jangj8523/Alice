package com.v1.avatar.v1.AIEngine;

import android.os.Environment;

import java.io.File;

/**
 * Created by darrenl on 2016/4/22.
 */
public final class Constants {

    private final static String testPath = "/Users/jaewoojang/Desktop/AvaHome Project/AvaHome/android/v1/app/src/main/res/raw";

    private Constants() {
        // Constants should be prive
    }

    /**
     * getFaceShapeModelPath
     * @return default face shape model path
     */
    public static String getFaceShapeModelPath() {
        File sdcard = Environment.getExternalStorageDirectory();
        String targetPath = sdcard.getAbsolutePath() + File.separator + "shape_predictor_68_face_landmarks.dat";
        //String targetPath = testPath + File.separator + "shape_predictor_68_face_landmarks.dat";

        return targetPath;
    }
}
