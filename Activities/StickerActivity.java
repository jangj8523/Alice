package com.v1.avatar.v1.Activities;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.v1.avatar.v1.R;

import java.io.File;

public class StickerActivity extends AppCompatActivity {

    private ImageView sticker;
    private ViewGroup background;
    private int xDelta;
    private int yDelta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle b = getIntent().getExtras();
        String backgroundImageDecodable = b.getString("background");
        Uri backgroundUri = Uri.fromFile(new File(backgroundImageDecodable));

        sticker = (ImageView) findViewById(R.id.sticker_image);
        background = (ViewGroup) findViewById(R.id.sticker_background);

//        File f = new File(getRealPathFromURI(backgroundUri));
//        Drawable d = Drawable.createFromPath(f.getAbsolutePath());
//        background.setBackground(d);

        RelativeLayout.LayoutParams layoutParams
                = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sticker.setLayoutParams(layoutParams);
        sticker.setOnTouchListener(new StickerTouchListener());

    }

    private final class StickerTouchListener implements View.OnTouchListener{
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            final int x = (int) motionEvent.getRawX();
            final int y = (int) motionEvent.getRawY();
            switch(motionEvent.getAction() & MotionEvent.ACTION_MASK){
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_DOWN:
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    xDelta = x - layoutParams.leftMargin;
                    yDelta = y - layoutParams.topMargin;
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    layoutParams1.leftMargin = x - xDelta;
                    layoutParams1.topMargin = y - yDelta;
                    view.setLayoutParams(layoutParams1);
                    break;
            }

            background.invalidate();
            return true;
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
}