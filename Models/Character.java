package com.v1.avatar.v1.Models;

import android.graphics.Bitmap;

public class Character {
    private String characterId;
    private Bitmap characterImage;
    private Bitmap profilePic;


    public void setCharacterId(String id) {
        characterId = id;
    }

    public String getCharacterId () {
        return characterId;
    }

    public void setCharacterImage (Bitmap bm) {
        characterImage = bm;
    }

    public Bitmap getCharacterImage() {
        return characterImage;
    }

    public void setProfileImage (Bitmap bm) {
        profilePic = bm;
    }

    public Bitmap getProfileImage () {
        return profilePic;
    }

}
