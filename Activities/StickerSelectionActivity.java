package com.v1.avatar.v1.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.v1.avatar.v1.Adapter.MyCharacterAdapter;
import com.v1.avatar.v1.Adapter.MyStickerAdapter;
import com.v1.avatar.v1.Constant.Constants;
import com.v1.avatar.v1.Interface.RecyclerViewOnClickListener;
import com.v1.avatar.v1.Models.GalleryImage;
import com.v1.avatar.v1.Models.ImageProcessor;
import com.v1.avatar.v1.Models.Sticker;
import com.v1.avatar.v1.Models.Character;
import com.v1.avatar.v1.Helpers.WrapContentLinearLayoutManager;
import com.v1.avatar.v1.R;

import java.util.ArrayList;


public class StickerSelectionActivity extends AppCompatActivity implements RecyclerViewOnClickListener {


    private String mFileLocation;
    private Bitmap bm;
    private CoordinatorLayout cl;
    private ImageView imageView;
    private ConstraintLayout selectedStickerLayout;


    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private float prevScaleFactor = 1.0f;

    private ArrayList <Sticker> stickerList = null;
    private ArrayList <Character> characterList = null;

    private int xDelta;
    private int yDelta;
    private ViewGroup background;
    private int mRecyclerViewWidth;
    private int mRecyclerViewHeight;
    private RecyclerView recyclerView;
    HandlerThread mBackgroundThreadHelper = null;
    Handler mBackgroundThreadHandler = null;
    Handler mainHandler;
    private MyStickerAdapter stickerAdapter;
    private MyCharacterAdapter characterAdapter;

    private OnSetUpListener setupListener;

    private int currentStage;

    private Sticker previewSticker;
    private Character previewCharacter;

    private ImageView selectedCharacter;
    private ImageView selectedSticker;
    private RelativeLayout imageScreen;
    private ImageButton deleteButton;
    private ConstraintLayout characterLayoutContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sticker_selection);
        Intent intent = getIntent();
        currentStage = Constants.SELECT_STICKER_STAGE;
        mFileLocation = (String) intent.getStringExtra("mFileLocation");
        imageView = (ImageView) findViewById(R.id.GalleryImageView);
        background = (ViewGroup) findViewById(R.id.ViewContainer);

        /**
         * Test
         */
        deleteButton = (ImageButton) findViewById(R.id.deleteImage);
        deleteButton.setVisibility(View.INVISIBLE);


        stickerList = new ArrayList <Sticker>();
        characterList = new ArrayList<Character> ();

        previewSticker = new Sticker();
        previewCharacter = new Character();

        recyclerView = (RecyclerView) findViewById(R.id.ItemRecyclerView);

        cl = (CoordinatorLayout) findViewById(R.id.ViewContainer);
        characterLayoutContainer = (ConstraintLayout) findViewById(R.id.SelectedCharacterContainer);

        imageScreen = (RelativeLayout) findViewById(R.id.ImageScreen);


        selectedSticker = (ImageView) findViewById(R.id.SelectedSticker);
        selectedCharacter = (ImageView) findViewById(R.id.SelectedCharacter);
        selectedStickerLayout = (ConstraintLayout) findViewById(R.id.SelectedStickerContainer);
        selectedStickerLayout.setVisibility(View.INVISIBLE);
        selectedStickerLayout.setClickable(false);


        setupListener = new OnSetUpListener() {
            @Override
            public void prepareStickerData() {
                prepareStickerList();
            }
        };

        initializeLayouts();
    }

    private void prepareList() {
        if (mBackgroundThreadHelper == null) {
            mBackgroundThreadHelper = new HandlerThread("Gallery Load");
            mBackgroundThreadHelper.start();
            mBackgroundThreadHandler = new Handler(mBackgroundThreadHelper.getLooper());
        }
        if (mainHandler == null) {
            mainHandler = new Handler(getMainLooper());
        }
        mBackgroundThreadHandler.post(new HelperRunnable(false, 0, 0));
    }


    private void initializeLayouts() {
        ViewTreeObserver viewTreeObserver = cl.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int viewHeight = cl.getHeight();
                    int width = cl.getWidth();
                    int height = cl.getHeight();
                    Bitmap bm = ImageProcessor.decodePreviewImage(mFileLocation, width, height);
                    imageView.setImageBitmap(bm);

                    if (viewHeight != 0)
                        cl.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                }
            });
        }




        ViewTreeObserver recyclerViewObserver = recyclerView.getViewTreeObserver();
        if (recyclerViewObserver.isAlive()) {
            recyclerViewObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mRecyclerViewWidth = recyclerView.getWidth();
                    mRecyclerViewHeight = recyclerView.getHeight();
                    recyclerView.setHasFixedSize(true);

                    RecyclerView.LayoutManager layoutManager = new WrapContentLinearLayoutManager(getApplicationContext(), 3);
                    recyclerView.setLayoutManager(layoutManager);

                    stickerAdapter = new MyStickerAdapter(stickerList, StickerSelectionActivity.this);
                    recyclerView.setAdapter(stickerAdapter);
                    setupListener.prepareStickerData();
                    if (mRecyclerViewHeight != 0) recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }


        ImageButton rightButton = (ImageButton) findViewById(R.id.FrontButton);
        rightButton.bringToFront();
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add a different Recycler

                clearRecyclerView();
                if (currentStage == Constants.SELECT_STICKER_STAGE) {
                    currentStage = Constants.SELECT_CHARACTER_STAGE;
                    prepareCharacterList();
                }
                else if (currentStage == Constants.SELECT_CHARACTER_STAGE) currentStage = Constants.SELECT_CLOTHES_STAGE;
            }
        });

        ImageButton leftButton = (ImageButton) findViewById(R.id.BackButton);
        leftButton.bringToFront();
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                clearRecyclerView();
                if (currentStage == Constants.SELECT_STICKER_STAGE) {
                    Intent photoTakingIntent = new Intent(getApplicationContext(), ChoosePhotoActivity.class);
                    startActivity(photoTakingIntent);
                } else if (currentStage == Constants.SELECT_CHARACTER_STAGE) {
                    currentStage = Constants.SELECT_STICKER_STAGE;
                    prepareStickerList();
                }

            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                previewSticker.setisPreviewed(false);
                selectedStickerLayout.setVisibility(View.INVISIBLE);
                selectedStickerLayout.setClickable(false);
            }
        });
    }

    private void prepareStickerList () {
        stickerList = new ArrayList<Sticker>();
        stickerAdapter = new MyStickerAdapter(stickerList, StickerSelectionActivity.this);
        recyclerView.setAdapter(stickerAdapter);
        prepareList();
    }

    private void prepareCharacterList() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        //if you need three fix imageview in width
        int devicewidth = displaymetrics.widthPixels / 3;

        //if you need 4-5-6 anything fix imageview in height
        int deviceheight = devicewidth;
        characterAdapter = new MyCharacterAdapter(characterList, StickerSelectionActivity.this, devicewidth, deviceheight);
        recyclerView.setAdapter(characterAdapter);
        prepareList();
    }

    private void clearRecyclerView (){
        recyclerView.removeAllViews();
        stickerList.clear();
        characterList.clear();
        stickerAdapter.notifyItemRangeRemoved(0, stickerList.size());
        recyclerView.invalidate();
    }


    public interface OnSetUpListener {
        void prepareStickerData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void retrieveCharacterFromDb () {
        int startIndex = 0;
        int count = 0;

        for (int i = 0; i < characterId.length; i++) {
            count++;
            Character mCharacter = new Character();
            mCharacter.setCharacterId(String.valueOf(characterId[i]));

            Bitmap characterbitmap = ImageProcessor.decodeSampledBitmapFromResource(getResources(), characterId[i], (float)mRecyclerViewWidth/3, (float)mRecyclerViewHeight/3);
            Bitmap profilebitmap = ImageProcessor.decodeSampledBitmapFromResource(getResources(), userId[i], (float)mRecyclerViewWidth/3, (float)mRecyclerViewHeight/3);

            mCharacter.setCharacterImage(characterbitmap);
            mCharacter.setProfileImage(profilebitmap);
            characterList.add(mCharacter);

            if (count % 5 == 0 && count != 1 ) {
                mainHandler.post(new HelperRunnable(true, startIndex, 5));
                startIndex = count;
            }
        }
        if (startIndex < count) mainHandler.post(new HelperRunnable(true, startIndex, count-startIndex));
    }


    private void retrieveStickerFromDB() {
        //Test
        int startIndex = 0;
        int count = 0;
        for (int i = 0; i < idArray.length; i++) {
            count++;
            Sticker mSticker = new Sticker();
            mSticker.setStickerId(idArray[i]);
            Bitmap bitmap = ImageProcessor.decodeSampledBitmapFromResource(getResources(), idArray[i], (float)mRecyclerViewWidth/3, (float)mRecyclerViewHeight/3);
            mSticker.setStickerBitMap(bitmap);
            stickerList.add(mSticker);
// convert bitmap to drawable
            //Drawable d = new BitmapDrawable(bitmap);

            if (count % 5 == 0 && count != 1 ) {
                mainHandler.post(new HelperRunnable(true, startIndex, 5));
                startIndex = count;
            }
        }
        if (startIndex < count) mainHandler.post(new HelperRunnable(true, startIndex, count-startIndex));
    }

    @Override
    public void ItemOnClick(GalleryImage mImage, View view) {
        //Unused in this activity
    }


    private void activateSticker () {
        RelativeLayout.LayoutParams layoutparams = (RelativeLayout.LayoutParams) selectedStickerLayout.getLayoutParams();
//        layoutparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//        layoutparams.addRule(RelativeLayout.CENTER_VERTICAL);


        float defaultWidth = imageScreen.getWidth() /3;
        float defaultHeight = imageScreen.getHeight() /3;

        float widthOfXButton = defaultWidth/4;
        float heightOfXButton = defaultHeight/4;

        deleteButton.setX(widthOfXButton);
        deleteButton.setY(heightOfXButton);

        layoutparams.width = (int) defaultWidth;
        layoutparams.height = (int) defaultHeight;
        selectedStickerLayout.setX(defaultWidth);
        selectedStickerLayout.setY(defaultHeight);


        selectedStickerLayout.setVisibility(View.VISIBLE);
        selectedStickerLayout.setClickable(true);
        selectedStickerLayout.bringToFront();
        selectedSticker.setVisibility(View.VISIBLE);

        ConstraintLayout.LayoutParams layoutparams2 = (ConstraintLayout.LayoutParams) characterLayoutContainer.getLayoutParams();
//        layoutparams2.width = (int) (defaultWidth * 0.75);
//        layoutparams2.height = (int) (defaultHeight * 0.75);
        layoutparams2.width = (int) (defaultWidth * 0.5);
        layoutparams2.height = (int) (defaultHeight * 0.5);
        characterLayoutContainer.setLayoutParams(layoutparams2);


        characterLayoutContainer.setVisibility(View.VISIBLE);
        characterLayoutContainer.bringToFront();


        selectedCharacter.setVisibility(View.VISIBLE);
        selectedCharacter.bringToFront();

//        layoutparams.removeRule(RelativeLayout.CENTER_VERTICAL);
//        layoutparams.removeRule(RelativeLayout.CENTER_HORIZONTAL);
        selectedStickerLayout.setOnTouchListener(new StickerTouchListener());
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

    }


    @Override
    public void StickerItemOnClick(Sticker mImage, View view) {
        if (!previewSticker.getisPreviewed()) {
            activateSticker();
            Bitmap bm = mImage.getBitMap();
            previewSticker.setStickerId(mImage.getStickerId());
            previewSticker.setStickerBitMap(bm);
            previewSticker.setisPreviewed(true);

            selectedSticker.setImageBitmap(bm);
            return;
        } else if (previewSticker.getStickerId().equals(mImage.getStickerId())) return;
        else {
            Bitmap bm = mImage.getBitMap();
            previewSticker.setStickerId(mImage.getStickerId());
            previewSticker.setStickerBitMap(bm);
            previewSticker.setisPreviewed(true);
            selectedSticker.setImageBitmap(bm);
        }
    }

    @Override
    public void CharacterItemOnClick(Character mCharacter, View v) {
        selectedCharacter.setImageBitmap(mCharacter.getCharacterImage());
    }

    private final class StickerTouchListener implements View.OnTouchListener{
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            final int x = (int) motionEvent.getRawX();
            final int y = (int) motionEvent.getRawY();
            if (motionEvent.getPointerCount() == 2) {
                mScaleGestureDetector.onTouchEvent(motionEvent);
                background.invalidate();
                return true;
            }

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

    private class HelperRunnable implements Runnable {
        private boolean isUpdateUi;
        private int indexStart;
        private int count;
        private MyCharacterAdapter mCharacterAdapter;


        HelperRunnable (boolean isUpdateUi, int indexStart, int count) {
            this.isUpdateUi = isUpdateUi;
            this.indexStart = indexStart;
            this.count = count;
        }

//        HelperRunnable (boolean isUpdateUi, MyCharacterAdapter adapter, int indexStart, int count) {
//            this.isUpdateUi = isUpdateUi;
//            this.indexStart = indexStart;
//            mCharacterAdapter = adapter;
//            this.count = count;
//        }
//

        @Override
        public void run() {
            if (currentStage == Constants.SELECT_STICKER_STAGE) {
                if (!isUpdateUi) retrieveStickerFromDB();
                else stickerAdapter.notifyItemRangeInserted(indexStart, count);
            } else if (currentStage == Constants.SELECT_CHARACTER_STAGE) {
                if (!isUpdateUi) retrieveCharacterFromDb();
                else characterAdapter.notifyItemRangeInserted(indexStart, count);
            }

        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f,
                    Math.min(mScaleFactor, 10.0f));
//            if (Math.abs(mScaleFactor - prevScaleFactor) > 0.1) {
//                mScaleFactor = prevScaleFactor;
//                return true;
//            }
            selectedStickerLayout.setScaleX(mScaleFactor);
            selectedStickerLayout.setScaleY(mScaleFactor);
            prevScaleFactor = mScaleFactor;
            return true;
        }
    }




    private Integer [] characterId = {
            R.drawable.yeon,
            R.drawable.hye,
            R.drawable.yeon2,
            R.drawable.min
    };

    private Integer[] idArray = {
            R.drawable.ewha,
            R.drawable.namsan,
            R.drawable.dotz,
            R.drawable.cancun,
            R.drawable.gucc,
            R.drawable.gucci,
            R.drawable.flower,
            R.drawable.tryst,
            R.drawable.schoolevent,
            R.drawable.grad,
            R.drawable.celeb
    };

    private Integer [] userId = {
            R.drawable.rinah,
            R.drawable.hyeyoung,
            R.drawable.sehyun,
            R.drawable.dable
    };
}
