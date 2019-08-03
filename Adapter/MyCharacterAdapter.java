package com.v1.avatar.v1.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.v1.avatar.v1.Interface.RecyclerViewOnClickListener;
import com.v1.avatar.v1.Models.Character;
import com.v1.avatar.v1.R;

import java.util.ArrayList;

public class MyCharacterAdapter extends RecyclerView.Adapter<MyCharacterAdapter.CharacterViewHolder> {

    private ArrayList<Character> characterList;
    private RecyclerViewOnClickListener mRecyclerViewOnClickListener;
    private int devicewidth;
    private int deviceheight;

    private int characterWidth;
    private int characterHeight;

    @NonNull
    @Override
    public CharacterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_character, parent, false);
        return new CharacterViewHolder(view);
    }

    public MyCharacterAdapter (ArrayList<Character> characterList, RecyclerViewOnClickListener mRecyclerViewOnClickListener, int devicewidth, int deviceheight) {
        this.characterList = characterList;
        this.mRecyclerViewOnClickListener = mRecyclerViewOnClickListener;
        this.deviceheight = deviceheight;
        this.devicewidth = devicewidth;
        characterWidth = devicewidth/2;
        characterHeight = deviceheight/2;

    }

    @Override
    public void onBindViewHolder(@NonNull CharacterViewHolder holder, int position) {
        int indexPosition = position;
        Character mChar = characterList.get(position);

        holder.profileImg.getLayoutParams().width = devicewidth;

        //if you need same height as width you can set devicewidth in holder.image_view.getLayoutParams().height
        holder.profileImg.getLayoutParams().height = deviceheight;

        holder.characterImg.getLayoutParams().width = characterWidth;
        holder.characterImg.getLayoutParams().height = characterHeight;

        holder.profileImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.profileImg.setImageBitmap(mChar.getProfileImage());
        holder.characterImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.characterImg.setImageBitmap(mChar.getCharacterImage());

        holder.mTextId.setText("@" + mChar.getCharacterId());

        holder.bind(characterList.get(indexPosition));
    }

    @Override
    public int getItemCount() {
        return characterList.size();
    }

    public class CharacterViewHolder extends RecyclerView.ViewHolder {
        private ImageView profileImg;
        private ImageView characterImg;
        private View view;
        private TextView mTextId;



        public void bind (final Character mCharacter) {
            view.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceType")
                @Override
                public void onClick(View v) {
                    mRecyclerViewOnClickListener.CharacterItemOnClick(mCharacter, v);

                }
            });
        }



        public CharacterViewHolder(View view) {
            super(view);
            this.view = view;
//            title = (TextView)view.findViewById(R.id.title);
            profileImg = (ImageView) view.findViewById(R.id.ProfileView);
            characterImg = (ImageView) view.findViewById(R.id.CharacterView);
            mTextId = (TextView) view.findViewById(R.id.CharacterID);
        }


    }
}
