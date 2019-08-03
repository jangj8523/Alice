package com.v1.avatar.v1.Models;

import android.graphics.Bitmap;

public class Sticker {
    private String img;
    private String keyword;
    private Integer id;
    private Bitmap stickerImg;
    private Bitmap clothesImg;
    private String expression;
    private String position;
    private float xCoordinatePic;
    private float yCoordinatePic;
    private Character mChosenCharacter;
    private boolean isChosen = false;


    private float xCoordinateChar;
    private float yCoordinateChar;

    private boolean isPreviewed = false;

    public Sticker () {
        mChosenCharacter = new Character();
    }

    public void setXCoordinatePic(float x) {
        xCoordinatePic = x;
    }

    public float getXCoordinatePic () {
        return xCoordinatePic;
    }

    public void setYCoordinatePic(float y) {
        yCoordinatePic = y;
    }

    public float getYCoordinatePic () {
        return yCoordinatePic;
    }

    public boolean getisPreviewed () {
        return isPreviewed;
    }

    public void setisPreviewed(boolean bool) {
        isPreviewed = bool;
    }

    public String getStickerKeyWord() {
        return img;
    }

    public void setStickerKeyWord(String android_image_url) {
        this.img = android_image_url;
    }

    public void setStickerId(Integer id) {
        this.id = id;
    }

    public Integer getStickerId () {
        return id;
    }

    public void setStickerBitMap (Bitmap bm) {stickerImg = bm; }

    public Bitmap getBitMap () {
        return stickerImg;
    }

    public void setClothesImg (Bitmap bm) {
        this.clothesImg = bm;
    }

    public Bitmap getPath () {
        return clothesImg;
    }

    public void setCharacter (Bitmap bm, String characterId, float xCoor, float yCoor) {
        mChosenCharacter.setCharacterId(characterId);
        mChosenCharacter.setCharacterImage(bm);
        yCoordinateChar = yCoor;
        xCoordinateChar = xCoor;
        isChosen = true;
    }


}
