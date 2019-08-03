package com.v1.avatar.v1.Models;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;

import com.v1.avatar.v1.Constant.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ImageProcessor implements Runnable {

    private File mImageFile = null;
    private Image mImage = null;
    private String mFileLocation;
    private int isWrite;
    private Bitmap bm;



    public ImageProcessor(Image image, String mFileLocation, int isWrite) {
        mImage = image;
        mImageFile = new File (mFileLocation);
        this.mFileLocation = mFileLocation;
        this.isWrite = isWrite;
    }

    public ImageProcessor(Bitmap bm, String mFileLocation, int isWriteBitmap) {
        this.bm = bm;
        this.mFileLocation = mFileLocation;
    }

    public static Bitmap getBitMapFromImage (Image image) {
        ByteBuffer rawBuffer = image.getPlanes()[0].getBuffer();
        byte [] rawBytes = new byte [rawBuffer.capacity()];
        rawBuffer.get(rawBytes);

        Bitmap  bitmap = BitmapFactory.decodeByteArray(rawBytes, 0, rawBytes.length);
        return bitmap;

    }

    private void writeImageFile() {
        ByteBuffer rawBuffer = mImage.getPlanes()[0].getBuffer();
        byte [] rawBytes = new byte [rawBuffer.capacity()];
        rawBuffer.get(rawBytes);


        FileOutputStream fileWriter = null;
        try {
            fileWriter = new FileOutputStream(mFileLocation);
            fileWriter.write(rawBytes);
            //myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileWriter);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mImage.close();
            if (fileWriter != null) {
                try {
                    fileWriter.getFD().sync();
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         float reqWidth, float reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;


        float imgRatio = actualWidth / actualHeight;
        float maxRatio = reqWidth / reqHeight;

        if (actualHeight > reqHeight || actualWidth > reqWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = reqHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) reqHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = reqWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) reqWidth;
            } else {
                actualHeight = (int) reqHeight;
                actualWidth = (int) reqWidth;
            }
        }

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeResource(res, resId, options);
        bm = createScaledBitmap(bm, actualWidth, actualHeight);
        return  bm;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth)
        {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap)
        {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public static void savePreviewImage (String absolutePathOfImage, Bitmap bmp) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(absolutePathOfImage);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap decodePreviewImage (String absolutePathOfImage, float maxWidth, float maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(absolutePathOfImage, options);
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;


        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;


        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth,
                actualHeight);


        options.inJustDecodeBounds = false;

        Bitmap bm = BitmapFactory.decodeFile(absolutePathOfImage, options);
        //return bm;
        bm = modifyOrientation(bm, absolutePathOfImage);

        bm = createScaledBitmap(bm, actualWidth, actualHeight);
        return makeSquareBitmap(bm);
    }


    private static Bitmap makeSquareBitmap (Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        int startX = 0;
        int startY = 0;
        int diff;
        if (width > height) {
            diff = width - height;
            startX = diff/2;
            return Bitmap.createBitmap(bm, startX, startY, height, height);
        } else if (height > width){
            diff = height - width;
            startY = diff/2;
            return Bitmap.createBitmap(bm, startX, startY, width, width);

        }
        return bm;
    }


    private static Bitmap createScaledBitmap(Bitmap src, int reqWidth, int reqHeight)
    {
        int width = src.getWidth();
        int height = src.getHeight();

        float ratioHeight = (float) height / (float) reqHeight;
        float ratioWidth = (float) width / (float) reqWidth;

        int scaledWidth = width;
        int scaledHeight = height;

        if (ratioHeight > 1 || ratioWidth > 1)
        {
            float maxRatio = Math.max(ratioHeight, ratioWidth);
            scaledWidth = (int) (width / maxRatio);
            scaledHeight = (int) (height / maxRatio);
        }
        return Bitmap.createScaledBitmap(src, scaledWidth, scaledHeight, true);
    }

    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) {
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(image_absolute_path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    public void run() {
        if (isWrite == Constants.isWrite) {
            writeImageFile();
        } else  {
            savePreviewImage(mFileLocation, bm);
        }

        //rotate(mFileLocation);
    }

    public static int getJpegOrientation(int sensorOrientation, int deviceRotation, boolean facingFront) {

        int surfaceRotation = Constants.ORIENTATIONS.get(deviceRotation);
        int jpegOrientation =
                (surfaceRotation + sensorOrientation + 270) % 360;
        return jpegOrientation;

//        if (deviceOrientation == android.view.OrientationEventListener.ORIENTATION_UNKNOWN) return 0;
//
//        // Round device orientation to a multiple of 90
//        deviceOrientation = (deviceOrientation + 45) / 90 * 90;
//
//        // Reverse device orientation for front-facing cameras
//        if (facingFront) deviceOrientation = -deviceOrientation;
//
//        // Calculate desired JPEG orientation relative to camera orientation to make
//        // the image upright relative to the device orientation
//        int jpegOrientation = (sensorOrientation + deviceOrientation + 270) % 360;
//
//        //If facing there's a lot to consider
//        if(facingFront) jpegOrientation = -jpegOrientation;
//        return jpegOrientation;
    }



    private static float exifToDegrees(float exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }
}
