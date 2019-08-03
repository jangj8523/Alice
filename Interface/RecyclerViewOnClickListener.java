package com.v1.avatar.v1.Interface;

import android.view.View;

import com.v1.avatar.v1.Models.GalleryImage;
import com.v1.avatar.v1.Models.Sticker;
import com.v1.avatar.v1.Models.Character;

public interface RecyclerViewOnClickListener {
    void ItemOnClick (GalleryImage mImage, View view);
    void StickerItemOnClick (Sticker mImage, View view);
    void CharacterItemOnClick(Character mCharacter, View v);
}
