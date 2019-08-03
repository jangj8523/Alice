package com.v1.avatar.v1.CameraFragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.v1.avatar.v1.Adapter.MyImageAdapter;
import com.v1.avatar.v1.Helpers.BackgroundThreadHelper;
import com.v1.avatar.v1.Models.Character;
import com.v1.avatar.v1.Models.GalleryImage;
import com.v1.avatar.v1.Models.ImageProcessor;
import com.v1.avatar.v1.Interface.RecyclerViewOnClickListener;
import com.v1.avatar.v1.Models.Sticker;
import com.v1.avatar.v1.Helpers.WrapContentLinearLayoutManager;
import com.v1.avatar.v1.R;
import com.v1.avatar.v1.Activities.MainActivity;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PhotoGalleryFragment} interface
 * to handle interaction events.
 * Use the {@link PhotoGalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotoGalleryFragment extends Fragment implements RecyclerViewOnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView recyclerView;
    private Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private int index = 0;
    private BackgroundThreadHelper threadHelper = null;

    private ImageView imageView;

    private OnChooseGalleryPhotoListener takeGalleryPhoto;

    private ArrayList<GalleryImage> currentGalleryImage = null;
    private String chosenImageLocation;



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public PhotoGalleryFragment() {
        // Required empty public constructor
    }

    private static ArrayList <String> photoURIPath;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PhotoGalleryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PhotoGalleryFragment newInstance(String param1, String param2) {
        PhotoGalleryFragment fragment = new PhotoGalleryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (currentGalleryImage == null || currentGalleryImage.isEmpty()) prepareData();
    }

    @Override
    public void ItemOnClick(GalleryImage mImage, View view) {
        String absolutePath = mImage.getPath();
        if (absolutePath.equals(chosenImageLocation)) return;
        chosenImageLocation = absolutePath;
        Bitmap bm = ImageProcessor.decodePreviewImage(absolutePath, (float)imageView.getWidth(), (float)imageView.getHeight());
        imageView.setImageBitmap(bm);
    }

    @Override
    public void StickerItemOnClick(Sticker mImage, View view) {
        //unused
    }

    @Override
    public void CharacterItemOnClick(Character mCharacter, View v) {
        //unused
    }


//    @Override
//    public void onImageSelected(int position) {
//        GalleryImage imageHolder = currentGalleryImage.get(position);
//        String absolutePath = imageHolder.getPath();
//        Bitmap bm = ImageProcessor.decodePreviewImage(absolutePath, (float)imageView.getWidth(), (float)imageView.getHeight());
//        imageView.setImageBitmap(bm);
//    }

    private class photoGalleryRunnable implements Runnable {
        private boolean isUpdateUi;
        private int indexStart;
        private int count;


        photoGalleryRunnable (boolean isUpdateUi, int indexStart, int count) {
            this.isUpdateUi = isUpdateUi;
            this.indexStart = indexStart;
            this.count = count;
        }

        @Override
        public void run() {
            if (!isUpdateUi) retrievePhotoGallery();
            else {
                //adapter.notifyItemRangeInserted(indexStart, count);
                adapter.notifyItemRangeInserted(indexStart, count);
            }
        }
    }

    HandlerThread mBackgroundThreadHelper = null;
    Handler backGroundThreadHandler = null;
    Handler mainHandler;
    private MyImageAdapter adapter;

    private void prepareData () {
        if (currentGalleryImage == null) currentGalleryImage = new ArrayList <GalleryImage>();
        if (mBackgroundThreadHelper == null) {
            mBackgroundThreadHelper = new HandlerThread("Gallery Load");
            mBackgroundThreadHelper.start();
            backGroundThreadHandler = new Handler(mBackgroundThreadHelper.getLooper());
        }
        if (mainHandler == null) {
            mainHandler = new Handler(getContext().getMainLooper());
        }

        backGroundThreadHandler.post(new photoGalleryRunnable(false, 0, 0 ));
        //retrievePhotoGallery(getActivity());


//        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
//            @Override
//            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
//                return false;
//            }
//
//            @Override
//            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
//                GalleryImage imageHolder = currentGalleryImage.get(e.getActionIndex());
//                String absolutePath = imageHolder.getPath();
//                Log.i("Click", "onTouchEvent: " + e.getActionIndex());
//                Bitmap bm = ImageProcessor.decodePreviewImage(absolutePath, (float)imageView.getWidth(), (float)imageView.getHeight());
//                imageView.setImageBitmap(bm);
//            }
//
//            @Override
//            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//            }
//        });


    }

    private void retrievePhotoGallery () {
        int index = -1;
        int startIndex = 0;
        int count = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data;

        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Files.FileColumns.MIME_TYPE};
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;

        cursor = getActivity().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (cursor.moveToNext()) {
            index++;
            count++;
            absolutePathOfImage = cursor.getString(column_index_data);
            GalleryImage newImage = new GalleryImage();
            newImage.setPath(absolutePathOfImage);
            newImage.setImage_ID(index);
            Bitmap myBitmap = ImageProcessor.decodePreviewImage(absolutePathOfImage, (float) mRecyclerViewWidth/4, (float) mRecyclerViewHeight/4);
            newImage.setBitMap(myBitmap);
            currentGalleryImage.add(newImage);
            //must find a way
            if (index == 0) {
                chosenImageLocation = absolutePathOfImage;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(ImageProcessor.decodePreviewImage(chosenImageLocation, imageView.getWidth(), imageView.getHeight()));
                    }
                });
            }

            if (count % 5 == 0) {
                mainHandler.post(new photoGalleryRunnable(true, startIndex, 5));
                startIndex = count;
            }
        }
        if (startIndex < count) mainHandler.post(new photoGalleryRunnable(true, startIndex, count-startIndex));
        cursor.close();
    }




    private int mRecyclerViewWidth;
    private int mRecyclerViewHeight;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        imageView = (ImageView) mView.findViewById(R.id.GalleryImageView);

        //RelativeLayout rl = (RelativeLayout) mView.findViewById(R.id.GalleryContainer);
        recyclerView = (RecyclerView) mView.findViewById(R.id.GalleryRecyclerView);

        mRecyclerViewWidth = container.getWidth();
        mRecyclerViewHeight = container.getHeight();

        ImageButton rightArrow = (ImageButton) mView.findViewById(R.id.FrontButton);
        rightArrow.bringToFront();
        rightArrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                takeGalleryPhoto.takeImage(chosenImageLocation);
            }
        });

        ImageButton leftArrow = (ImageButton) mView.findViewById(R.id.BackButton);
        leftArrow.bringToFront();

        leftArrow.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent (getContext(), MainActivity.class);
                startActivity (loginIntent);
            }
        });

        recyclerView.setHasFixedSize(true);

        currentGalleryImage = new ArrayList <GalleryImage>();
        RecyclerView.LayoutManager layoutManager = new WrapContentLinearLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MyImageAdapter(currentGalleryImage, this);
        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(layoutManager);
//        ArrayList<GalleryImage> createLists = prepareData();
        /**
         * leave empty
         */
        return mView;
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnChooseGalleryPhotoListener) {
            takeGalleryPhoto = (OnChooseGalleryPhotoListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    private void closeDown() {
        if (mBackgroundThreadHelper != null) {
            mBackgroundThreadHelper.quitSafely();
            mBackgroundThreadHelper = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        closeDown();
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
    public interface OnChooseGalleryPhotoListener {
        // TODO: Update argument type and name
        void takeImage (String mFileLocation);
    }

}
