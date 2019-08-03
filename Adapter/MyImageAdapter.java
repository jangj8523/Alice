package com.v1.avatar.v1.Adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.v1.avatar.v1.Models.GalleryImage;
import com.v1.avatar.v1.Interface.RecyclerViewOnClickListener;
import com.v1.avatar.v1.R;

import java.util.ArrayList;

public class MyImageAdapter extends RecyclerView.Adapter<MyImageAdapter.ViewHolder> {
    private RecyclerViewOnClickListener mRecyclerUpdateListener;


    private ArrayList<GalleryImage> galleryList;
    private int indexPosition;

    public MyImageAdapter(ArrayList<GalleryImage> galleryList, RecyclerViewOnClickListener mRecyclerViewOnClickListener) {
        this.galleryList = galleryList;
        this.mRecyclerUpdateListener = mRecyclerViewOnClickListener;


//        if (context instanceof OnGalleryListener) {
//            mOnGalleryListener = (OnGalleryListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//        mOnGalleryListener = listener;
    }

    @NonNull
    @Override
    public MyImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_imageview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //holder.title.setText(galleryList.get(position).getImage_title());
        int indexPosition = position;
        holder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.img.setImageBitmap((galleryList.get(position).getBitMap()));
        holder.bind(galleryList.get(indexPosition), mRecyclerUpdateListener);
    }



    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView img;
        private View view;



        public void bind (final GalleryImage mImage, final RecyclerViewOnClickListener mRecyclerUpdateListener) {
            view.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceType")
                @Override
                public void onClick(View v) {
                    mRecyclerUpdateListener.ItemOnClick(mImage, v);
                    //GRAY

                }
            });
        }



        public ViewHolder(View view) {
            super(view);
            this.view = view;
            //title = (TextView)view.findViewById(R.id.title);
            img = (ImageView) view.findViewById(R.id.img);
        }


    }
}
