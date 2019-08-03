package com.v1.avatar.v1.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.TextureView;
import android.widget.ImageView;

import com.v1.avatar.v1.CameraFragment.CameraFragment;
import com.v1.avatar.v1.CameraFragment.PhotoGalleryFragment;
import com.v1.avatar.v1.Constant.Constants;
import com.v1.avatar.v1.R;


public class ChoosePhotoActivity extends AppCompatActivity implements CameraFragment.OnTakePhotoListener, PhotoGalleryFragment.OnChooseGalleryPhotoListener{

    /**Variables used for Camera Preview Setup */
    private CameraFragment mCameraFragment;
    private PhotoGalleryFragment mPhotoGalleryFragment;
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photolibrary);

        /**
         * Setting up the fragments on the layouts
         */

        boolean isTakingPhotoToCreateAvatar = false;

        mCameraFragment = new CameraFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.isGenerateAvatarParameter, isTakingPhotoToCreateAvatar);
        mCameraFragment.setArguments(bundle);
        mCameraFragment.setRetainInstance(true);
        mPhotoGalleryFragment = new PhotoGalleryFragment ();
        mPhotoGalleryFragment.setRetainInstance(true);

        makeFragmentTransaction(R.id.CameraOrGalleryFragmentOption, mCameraFragment);
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.add(R.id.CameraOrGalleryFragmentOption, mPhotoGalleryFragment).addToBackStack("PhotoGallery").commit();

        navigationView = (BottomNavigationView) findViewById(R.id.NavigationBar);
        navigationView.setSelectedItemId(R.id.action_camera);
        initializeNavigationViewListener(navigationView);


    }

    private void makeFragmentTransaction (int containerId, Fragment attachFrag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(containerId, attachFrag);
        ft.commit();
    }



    private void initializeNavigationViewListener(final BottomNavigationView navigationView ) {
        navigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_video:
                                //makeFragmentTransaction(R.id.CameraOrGalleryFragmentOption, new CameraFragment());
                                break;
                            case R.id.action_camera:
                                if (navigationView.getSelectedItemId() == R.id.action_camera) break;
                                makeFragmentTransaction(R.id.CameraOrGalleryFragmentOption, mCameraFragment);
                                break;
                            case R.id.action_library:
                                if (navigationView.getSelectedItemId() == R.id.action_library) break;
                                makeFragmentTransaction(R.id.CameraOrGalleryFragmentOption, mPhotoGalleryFragment);
                                break;

                        }

                        return true;

                    }
                });
    }


    @Override
    public void captureImage(String mFileLocation) {
        TextureView screen = (TextureView) findViewById(R.id.CameraContainer);
        int width = screen.getWidth();
        int height = screen.getHeight();
        Intent intent = new Intent(getApplicationContext(), StickerSelectionActivity.class);
        intent.putExtra("mFileLocation", mFileLocation);
        intent.putExtra("width", width);
        intent.putExtra("height", height);
        startActivity(intent);
    }


    @Override
    public void takeImage(String mFileLocation) {
        ImageView screen = (ImageView) findViewById(R.id.GalleryImageView);
        int width = screen.getWidth();
        int height = screen.getHeight();
        Log.e("size", "takeImage: size of container " + width + " " + height);
        Intent intent = new Intent(getApplicationContext(), StickerSelectionActivity.class);
        intent.putExtra("mFileLocation", mFileLocation);
        intent.putExtra("width", width);
        intent.putExtra("height", height);
        startActivity(intent);
    }
}
