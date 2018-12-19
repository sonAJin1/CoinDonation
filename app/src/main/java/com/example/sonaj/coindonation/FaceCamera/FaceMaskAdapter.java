package com.example.sonaj.coindonation.FaceCamera;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.sonaj.coindonation.R;
import com.example.sonaj.coindonation.Util.RecyclerViewItemClickListener;

import java.util.ArrayList;

public class FaceMaskAdapter extends RecyclerView.Adapter<FaceMaskAdapter.ViewHolder> {

    private Context context;
    private ArrayList<FaceMaskItem> faceMaskItems;
    private int lastPosition = -1;
    private RecyclerViewItemClickListener recyclerViewItemClickListener;

    public FaceMaskAdapter(ArrayList<FaceMaskItem> items, Context context) {
       this.faceMaskItems = items;
       this.context = context;
    }

    // 뷰 바인딩 부분을 한번만 하도록, ViewHolder 패턴 의무화
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        int position = 0;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.im_face_mask);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerViewItemClickListener.onItemClick(view,position);
                }
            });
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.face_mask_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.position = position;
        viewHolder.imageView.setImageResource(faceMaskItems.get(position).getImg());

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return faceMaskItems.size();
    }


    public void setOnClickListener(RecyclerViewItemClickListener recyclerViewItemClickListener){
        this.recyclerViewItemClickListener = recyclerViewItemClickListener;
    }
}
