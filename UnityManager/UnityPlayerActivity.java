package com.v1.avatar.v1.UnityManager;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.unity3d.player.UnityPlayer;

public class UnityPlayerActivity extends Activity implements View.OnTouchListener{

    public static Context mContext;
    private Handler handler=new Handler();
    private UnityPlayer mUnityPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mContext = this;
        getWindow().takeSurface(null);
        setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);
        getWindow().setFormat(PixelFormat.RGB_565);

        mUnityPlayer = new UnityPlayer(this);
        if (mUnityPlayer.getSettings ().getBoolean ("hide_status_bar", true))
            getWindow ().setFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

        int glesMode = mUnityPlayer.getSettings().getInt("gles_mode", 1);
        mUnityPlayer.init(glesMode, false);

        View playerView = mUnityPlayer.getView();


        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(750, 630);

    }

    protected void onDestroy ()
    {
        mUnityPlayer.quit();
        super.onDestroy();
    }

    protected void onPause()
    {
        super.onPause();
        mUnityPlayer.pause();
    }

    protected void onResume()
    {
        super.onResume();
        mUnityPlayer.resume();
    }

    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }

    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }

    public boolean dispatchKeyEvent(KeyEvent event)
    {
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
            return mUnityPlayer.onKeyMultiple(event.getKeyCode(), event.getRepeatCount(), event);
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }
}
