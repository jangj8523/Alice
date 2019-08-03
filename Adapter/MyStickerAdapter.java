package com.v1.avatar.v1.Adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.v1.avatar.v1.Interface.RecyclerViewOnClickListener;
import com.v1.avatar.v1.Models.Sticker;
import com.v1.avatar.v1.R;

import java.util.ArrayList;

public class MyStickerAdapter extends RecyclerView.Adapter<MyStickerAdapter.StickerViewHolder> {

    private ArrayList<Sticker> stickerList;
    private RecyclerViewOnClickListener mRecyclerViewOnClickListener;

    @NonNull
    @Override
    public StickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_imageview, parent, false);
        return new StickerViewHolder(view);
    }

    public MyStickerAdapter(ArrayList<Sticker> stickerList, RecyclerViewOnClickListener mRecyclerViewOnClickListener) {
        this.stickerList = stickerList;

//        if (context instanceof OnGalleryListener) {
//            mOnGalleryListener = (OnGalleryListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//        mOnGalleryListener = listener;
        this.mRecyclerViewOnClickListener = mRecyclerViewOnClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull StickerViewHolder holder, int position) {
        int indexPosition = position;
        holder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.img.setImageBitmap((stickerList.get(position).getBitMap()));
        holder.bind(stickerList.get(indexPosition));
    }


    @Override
    public int getItemCount() {
        return stickerList.size();
    }

    public class StickerViewHolder extends RecyclerView.ViewHolder{
        private ImageView img;
        private View view;



        public void bind (final Sticker mSticker) {
            view.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceType")
                @Override
                public void onClick(View v) {
                    mRecyclerViewOnClickListener.StickerItemOnClick(mSticker, v);

                }
            });
        }



        public StickerViewHolder(View view) {
            super(view);
            this.view = view;
            //title = (TextView)view.findViewById(R.id.title);
            img = (ImageView) view.findViewById(R.id.img);
        }


    }

}
