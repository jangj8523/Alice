package com.v1.avatar.v1.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.v1.avatar.v1.CameraFragment.CameraFragment;
import com.v1.avatar.v1.Constant.Constants;
import com.v1.avatar.v1.R;

public class CreateAvatarWithSelfieActivity extends AppCompatActivity implements CameraFragment.OnTakePhotoListener {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_avatar_with_selfie);


        /**boolean isTakingPhotoToCreateAvatar
         * This flag determines whether the camera interface should include the "fit face here" interface
         * and the camera lens orientation button
         */
        boolean isTakingPhotoToCreateAvatar = true;

        CameraFragment mCameraFragment = new CameraFragment();
        mCameraFragment = new CameraFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.isGenerateAvatarParameter, isTakingPhotoToCreateAvatar);
        mCameraFragment.setArguments(bundle);
        mCameraFragment.setRetainInstance(true);


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.SelfieCameraOption, mCameraFragment);
        ft.commit();

    }

    @Override
    public void captureImage(String mFileLocation) {
        Intent avatarIntent = new Intent(getApplicationContext(), NewAvatarActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("imagePath", mFileLocation);
        avatarIntent.putExtras(bundle);
        startActivity(avatarIntent);
    }
}
