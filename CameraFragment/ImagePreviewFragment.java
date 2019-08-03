package com.v1.avatar.v1.CameraFragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.v1.avatar.v1.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ImagePreviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImagePreviewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mFileLocation;
    private int mScreenOrientation;
    private float widthSize;
    private float heightSize;

//    public ImagePreviewFragment() {
//        // Required empty public constructor
//    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mFileLocation filelocation of the temporary data
     * @return A new instance of fragment ImagePreviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ImagePreviewFragment newInstance(String mFileLocation) {
        ImagePreviewFragment fragment = new ImagePreviewFragment();
        Bundle args = new Bundle();
        args.putString("mFileLocation", mFileLocation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFileLocation = getArguments().getString("mFileLocation");
            mScreenOrientation = getArguments().getInt("mCameraScreenOrientation");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View previewLayout = inflater.inflate(R.layout.fragment_image_preview, container, false);
        ImageView previewScreen = (ImageView) previewLayout.findViewById(R.id.PreviewImage);

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mFileLocation, bmOptions);

        //NIge's code
        int imageHeight = bmOptions.outHeight;
        int imageWidth = bmOptions.outWidth;
//        int scaleFactor = Math.min(imageWidth/widthSize, imageHeight/heightSize);
//        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inJustDecodeBounds = false;

        Bitmap myBitmap = BitmapFactory.decodeFile(mFileLocation);
////        //myBitmap = Bitmap.createScaledBitmap(myBitmap,400,400,false);
////
////        /**
////         * need more robust orientation checking
////         *
////         */
////
        Matrix matrix = new Matrix();
//         // setup rotation degree
        matrix.setScale(widthSize/imageWidth, heightSize/imageHeight);
        matrix.postRotate(mScreenOrientation);
        Bitmap newBitmap = Bitmap.createBitmap(myBitmap, 0,0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);
        //Bitmap newBitmap = Bitmap.createBitmap(myBitmap);
//
        previewScreen.setImageBitmap(newBitmap);
        return previewLayout;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
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

}
