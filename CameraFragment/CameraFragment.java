package com.v1.avatar.v1.CameraFragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.v1.avatar.v1.Constant.Constants;
import com.v1.avatar.v1.Helpers.BackgroundThreadHelper;
import com.v1.avatar.v1.Models.ImageProcessor;
import com.v1.avatar.v1.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static com.v1.avatar.v1.Constant.Constants.CAMERA_PREVIEW_THREAD_ID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CameraFragment.OnTakePhotoListener} interface
 * to handle interaction events.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends Fragment {

    private static final int CAMERA_REQUEST = 1888;
    private static final int GALLERY_REQUEST = 1889;

    private OnTakePhotoListener mImageCaptureListener;
    private TextureView mTextureHolder;
    private Size mCameraPreviewSize;

    private String mCameraId;
    private CaptureRequest.Builder mPreviewCaptureRequestBuilder;
    private CaptureRequest mPreviewCaptureRequest;
    private CameraCaptureSession mCameraCaptureSession;
    private BackgroundThreadHelper threadHelper = null;

    private CameraCaptureSession.CaptureCallback photoCaptureCallback;

    private String mFileLocation;
    private int cameraScreenOrientation;
    private CameraCharacteristics cameraCharacteristics;


    private ImageReader mImageReader;
    private ImageReader.OnImageAvailableListener mOnImageAvailableListener;
    private Surface mPreviewSurface;

    private CameraDevice.StateCallback mCameraDeviceCallBack;
    private CameraCaptureSession.StateCallback mCameraPreviewStateCallBack;
    private TextureView.SurfaceTextureListener mSurfaceTextureListener;
    private CameraCaptureSession.CaptureCallback mCameraCaptureSessionCallBack;

    private CameraDevice mCameraDevice = null;
    private CameraManager mCameraManager = null;


    private boolean isPhotoCaptured = false;
    private ImageButton lensOrientationButton;
    private TextView faceFitView;

    private ImageButton.OnClickListener mLensOrientationListener;
    private boolean isSelfieMode = false;
    boolean isSelfieToCreateAvatar = false;

    public CameraFragment() {

        // Required empty public constructor
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment Camerafragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CameraFragment newInstance(boolean isSelfieToCreateAvatar) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putBoolean(Constants.isGenerateAvatarParameter, isSelfieToCreateAvatar);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.isSelfieToCreateAvatar  = getArguments().getBoolean(Constants.isGenerateAvatarParameter);
        }
    }



    public void reset() {
        isPhotoCaptured = false;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private ImageView testView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View cameraLayoutView = inflater.inflate(R.layout.fragment_camerapreview, container, false);
        lensOrientationButton = (ImageButton) cameraLayoutView.findViewById (R.id.CameraSwitchButton);
        faceFitView = (TextView) cameraLayoutView.findViewById(R.id.FaceBox);
        mTextureHolder = (TextureView) cameraLayoutView.findViewById(R.id.CameraContainer);
        ImageButton mPhotoButton = (ImageButton) cameraLayoutView.findViewById(R.id.PhotoButton);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();

            }
        });
        lensOrientationButton.setOnClickListener(mLensOrientationListener);
        if (isSelfieToCreateAvatar) changeCameraLayout();


        initCallbacks();
        initListeners();




        /**
         * the ordering of initalizing the mTextureHolder is important
         */

        return cameraLayoutView;
    }

    private void changeCameraLayout() {
        isSelfieMode = true;
        lensOrientationButton.setClickable(false);
        lensOrientationButton.setVisibility(View.INVISIBLE);
        faceFitView.setVisibility(View.VISIBLE);
    }

    private void initListeners()  {
        mLensOrientationListener  = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSelfieMode) {
                    isSelfieMode = false;
                } else {
                    isSelfieMode = true;
                }
                //closeDown();
                /**
                 * rebuild the entire camera. Too much load on the UI
                 */
                setUpCamera();
                configureCamera();

            }
        };


        mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                if (!threadHelper.isAlive()) {
                    threadHelper.closeBackgroundThreadHelper();
                    threadHelper = new BackgroundThreadHelper("Image Saver");
                }
                Image mImage = reader.acquireNextImage();
                threadHelper.getBackgroundHelper().post(new ImageProcessor(mImage, mFileLocation, Constants.isWrite));
                mImageCaptureListener.captureImage(mFileLocation);
            }
        };

        mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                setUpCamera();
                configureCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                closeDown();
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        };

    }

    private void closeDown() {
        if (mCameraCaptureSession != null) {
            mCameraCaptureSession.close();
            mCameraCaptureSession = null;
        }
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
        if (mImageReader != null) {
            mImageReader.close();
            mImageReader = null;
        }
        if(mPreviewSurface != null) {
            mPreviewSurface.release();
            mPreviewSurface = null;
        }
        if(threadHelper != null) threadHelper.closeBackgroundThreadHelper();

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mImageCaptureListener != null) {

        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public CameraCharacteristics mGetCameraCharacteristics() {
        return cameraCharacteristics;
    }

    private void setUpCamera() {
        mCameraManager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] CameraIDs = mCameraManager.getCameraIdList();
            for (String Id : CameraIDs) {
                cameraCharacteristics = mCameraManager.getCameraCharacteristics(Id);
                int lensOrientation = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                if (isSelfieMode && (lensOrientation != CameraCharacteristics.LENS_FACING_FRONT))
                    continue;
                else if (!isSelfieMode && (lensOrientation == CameraCharacteristics.LENS_FACING_FRONT))
                    continue;
                StreamConfigurationMap textureMapping = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                //WE should be getting the largest image that we can take
                mImageReader = ImageReader.newInstance(mTextureHolder.getWidth(), mTextureHolder.getHeight(), ImageFormat.JPEG, 1);
                mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, threadHelper.getBackgroundHelper());
                mCameraId = Id;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    private void configureCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST);
                return;
            }
            if (mCameraDevice != null) {
                mCameraDevice.close();
            }
            mCameraManager.openCamera(mCameraId, mCameraDeviceCallBack, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createPhotoImage() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "JPG_" + timestamp;
        File fileDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        try {
            File image = File.createTempFile(fileName, ".jpg ", fileDirectory);
            mFileLocation = image.getAbsolutePath();
            Log.i("create path", "in CameraFragment:  " + mFileLocation, null);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public void takePhoto() {
        createPhotoImage();
        CameraCharacteristics cameraCharacteristics = null;
        try {
            cameraCharacteristics = mCameraManager.getCameraCharacteristics(mCameraId);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            return;
        }
//
        int deviceOrientation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        int sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);


        int correctedOrientation = ImageProcessor.getJpegOrientation(sensorOrientation, deviceOrientation, isSelfieMode);
        try {
            mCameraCaptureSession.stopRepeating();
            mCameraCaptureSession.abortCaptures();
            /**
             * Test
             */
//            Bitmap capPic = mTextureHolder.getBitmap();
//            mImageView.setImageBitmap(capPic);
//            return null;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
//        CaptureRequest.Builder capturePhotoRequestBuilder = null;
//        try {
//            capturePhotoRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
//            capturePhotoRequestBuilder.addTarget(mImageReader.getSurface());
//            capturePhotoRequestBuilder.set (CaptureRequest.JPEG_ORIENTATION, correctedOrientation);
//
//
//            //Handler is null because this process is already running on the background thread anyway.
//            mCameraCaptureSession.capture(capturePhotoRequestBuilder.build(), photoCaptureCallback, null);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }

        /**
         * Test Code
         */
//        if (!threadHelper.isAlive()) {
//            threadHelper.closeBackgroundThreadHelper();
//            threadHelper = new BackgroundThreadHelper("Image Saver");
//        }
        Bitmap bm = mTextureHolder.getBitmap();
        ImageProcessor.savePreviewImage(mFileLocation, bm);
        mImageCaptureListener.captureImage(mFileLocation);

    }



    private void initCallbacks() {
        mCameraDeviceCallBack = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                mCameraDevice = camera;
                openCameraPreview();
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice camera) {
                closeDown();
            }

            @Override
            public void onError(@NonNull CameraDevice camera, int error) {
                closeDown();
                Toast.makeText(getActivity(), "onError Camera open: " + error, Toast.LENGTH_LONG).show();
            }
        };

        mCameraPreviewStateCallBack = new CameraCaptureSession.StateCallback() {

            @Override
            public void onConfigured(@NonNull CameraCaptureSession session) {
                if (mCameraDevice == null) {

                    return;
                }
                mCameraCaptureSession = session;
                mPreviewCaptureRequest = mPreviewCaptureRequestBuilder.build();
                try {
                    mCameraCaptureSession.setRepeatingRequest(mPreviewCaptureRequest, mCameraCaptureSessionCallBack, threadHelper.getBackgroundHelper());
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                Toast.makeText(getActivity(), "onConfiguredFailed Camera open", Toast.LENGTH_LONG).show();

            }
        };

        mCameraCaptureSessionCallBack = new CameraCaptureSession.CaptureCallback() {
            @Override
            public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                super.onCaptureCompleted(session, request, result);
            }

            @Override
            public void onCaptureFailed (CameraCaptureSession session, CaptureRequest request, CaptureFailure result) {
                super.onCaptureFailed(session, request, result);
            }

            @Override
            public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                super.onCaptureStarted(session, request, timestamp, frameNumber);
            }
        };

        photoCaptureCallback = new CameraCaptureSession.CaptureCallback() {

            @Override
            public void onCaptureStarted (CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
                super.onCaptureStarted(session, request, timestamp, frameNumber);
            }

            @Override
            public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                super.onCaptureCompleted(session, request, result);
                isPhotoCaptured = true;
            }
        };
    }

    private void openCameraPreview() {
        //mTextureHolder.setSurfaceTextureListener(mSurfaceTextureListener);
        SurfaceTexture surfaceTexture = mTextureHolder.getSurfaceTexture();
        Log.e("5", "openCameraPreview: ");
        surfaceTexture.setDefaultBufferSize(mTextureHolder.getWidth(), mTextureHolder.getHeight());
        mPreviewSurface = new Surface(surfaceTexture);
        try{
            mPreviewCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewCaptureRequestBuilder.addTarget(mPreviewSurface);

            //Create Session
            mCameraDevice.createCaptureSession(Arrays.asList(mPreviewSurface, mImageReader.getSurface()), mCameraPreviewStateCallBack, threadHelper.getBackgroundHelper());
        } catch(CameraAccessException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onResume () {
        super.onResume();
        if (threadHelper == null) threadHelper = new BackgroundThreadHelper (CAMERA_PREVIEW_THREAD_ID);
        if (mTextureHolder.isAvailable()) {
            setUpCamera();
            configureCamera();
            Log.e("4", "onResume: ");
        } else {
            mTextureHolder.setSurfaceTextureListener(mSurfaceTextureListener);
            Log.e("3", "onResume: ");

        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTakePhotoListener) {
            mImageCaptureListener = (OnTakePhotoListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public String getmFileLocation() {
        return mFileLocation;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTakePhotoListener {
        // TODO: Update argument type and name
        void captureImage (String mFileLocation);
    }




}