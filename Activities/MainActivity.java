package com.v1.avatar.v1.Activities;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.v1.avatar.v1.R;
import com.v1.avatar.v1.UnityManager.UnityPlayerActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private Button cameraButton;
    private Button albumButton;
    private Button stickerButton;
    private Button unityButton;
    private Button loginPage;
    private Button photoTaking;
    private Button avatarButton;
    private Button createAvatarWithSelfieButton;

    private String chosenImageDecodable;
    private String mCurrentPhotoPath;
    private static String[] PERMISSIONS_REQ = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private static final int REQUEST_CODE_PERMISSION = 2;

    // Temporarily preset to true to test NewAvatarActivity.
    private boolean needAvatar = true;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_PICK = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyPermissions(this);

        if(!isExternalStorageWritable()) Log.d("Main", "External storage not writable!");
        if(!isExternalStorageReadable()) Log.d("Main", "External storage not readable!");
        chosenImageDecodable = "";

        cameraButton = (Button) findViewById(R.id.loginBtn);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (cameraIntent.resolveActivity(getPackageManager()) != null){
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e){

                    }

                    if (photoFile != null){
                        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), "com.v1.fileprovider", photoFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });

        albumButton = (Button) findViewById(R.id.b2);

        albumButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, REQUEST_IMAGE_PICK);
            }
        });

        stickerButton = (Button) findViewById(R.id.b4);

        stickerButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent stickerIntent = new Intent(getApplicationContext(), StickerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("background", chosenImageDecodable);
                stickerIntent.putExtras(bundle);
                startActivity(stickerIntent);
            }
        });

        unityButton = (Button) findViewById(R.id.b6);

        unityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent unityIntent = new Intent(getApplicationContext(), UnityPlayerActivity.class);
                startActivity(unityIntent);
            }
        });


        photoTaking = (Button) findViewById(R.id.phototaking);

        photoTaking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoTakingIntent = new Intent(getApplicationContext(), ChoosePhotoActivity.class);
                startActivity(photoTakingIntent);
            }
        });


        //LoginPage is no longer used as test but is integrated in the service

        loginPage = (Button) findViewById(R.id.b7);

        loginPage.setOnClickListener (new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Redirecting...",
                        Toast.LENGTH_SHORT).show();
                Intent loginPageIntent = new Intent (getApplicationContext(), LoginPageActivity.class);
                startActivity(loginPageIntent);
            }
        });


        avatarButton = (Button) findViewById(R.id.b7);

        avatarButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent avatarIntent = new Intent(getApplicationContext(), NewAvatarActivity.class);
                Bundle bundle = new Bundle();
                avatarIntent.putExtras(bundle);
                bundle.putString("imagePath", chosenImageDecodable);
                startActivity(avatarIntent);
            }
        });

        createAvatarWithSelfieButton = (Button)findViewById(R.id.CreateAvatarWithSelfie);

        createAvatarWithSelfieButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent createAvatarIntent = new Intent(getApplicationContext(), CreateAvatarWithSelfieActivity.class);
                startActivity(createAvatarIntent);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    handleImageCapture(data);
                    if(needAvatar) {
                        Intent avatarIntent = new Intent(getApplicationContext(), NewAvatarActivity.class);
                        avatarIntent.putExtra("imagePath", mCurrentPhotoPath);
                        startActivity(avatarIntent);
                    }
                    break;
                case REQUEST_IMAGE_PICK:
                    chosenImageDecodable = handleImagePick(data);
                    if(needAvatar) {
                        Intent avatarIntent = new Intent(getApplicationContext(), NewAvatarActivity.class);
                        avatarIntent.putExtra("imagePath", chosenImageDecodable);
                        startActivity(avatarIntent);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void handleImageCapture(Intent data){
        saveImageToGallery();
    }

    private String handleImagePick(Intent data){
        Uri selectedImage = data.getData();
        String [] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String imgDecodableString = cursor.getString(columnIndex);
        cursor.close();
        if (imgDecodableString != null) Log.d("Data", imgDecodableString);
        return imgDecodableString;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void saveImageToGallery(){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private static boolean verifyPermissions(Activity activity) {
        // Check if we have write permission
        int write_permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read_persmission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int camera_permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        if (write_permission != PackageManager.PERMISSION_GRANTED ||
                read_persmission != PackageManager.PERMISSION_GRANTED ||
                camera_permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            Log.d("Main", "Requesting permissions");
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_REQ,
                    REQUEST_CODE_PERMISSION
            );
            return false;
        } else {
            return true;
        }
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}