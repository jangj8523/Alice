package com.v1.avatar.v1.Models;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class GalleryImage {
//    private String image_title;
    private Integer img;
    private ImageView mImageView;
    private String path;
    private Bitmap bm;

//    public String getImage_title() {
//        return image_title;
//    }
//
//    public void setImage_title(String android_version_name) {
//        this.image_title = android_version_name;
//    }

    public Integer getImage_ID() {
        return img;
    }

    public void setImage_ID(Integer android_image_url) {
        this.img = android_image_url;
    }

    public void setBitMap (Bitmap bm) {
        this.bm = bm;
    }

    public Bitmap getBitMap () {
        return bm;
    }

    public void setPath (String path) {
        this.path = path;
    }

    public String getPath () {
        return path;
    }



}
