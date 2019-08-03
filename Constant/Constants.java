package com.v1.avatar.v1.Constant;

import android.util.SparseIntArray;
import android.view.Surface;

public class Constants {
    public static final  String CAMERA_PREVIEW_THREAD_ID = "Camera Preview ID";
//    public static final

    public static final int isWrite = 1;
    public static final int isWriteBitmap = 2;

    public static final SparseIntArray ORIENTATIONS = new SparseIntArray(4);

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    public static final int SELECT_STICKER_STAGE = 1;
    public static final int SELECT_CHARACTER_STAGE = 2;
    public static final int SELECT_CLOTHES_STAGE = 3;

    public static final String isGenerateAvatarParameter = "Param1";



}
