package com.example.photoeditor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.photoeditor.R;
import com.example.photoeditor.model.ImageModel;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.VH> {

    private final Context ctx;
    private final List<ImageModel> list;
    private OnPhotoClickListener listener;

    public interface OnPhotoClickListener {
        void onPhotoClick(int pos, String uri);
    }

    public PhotoAdapter(Context ctx, List<ImageModel> list) {
        this.ctx = ctx;
        this.list = list;
    }

    public void setOnPhotoClickListener(OnPhotoClickListener l) {
        this.listener = l;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int pos) {
        ImageModel img = list.get(pos);
        Glide.with(ctx).load(img.getUri()).centerCrop().into(holder.iv);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPhotoClick(pos, img.getUri());
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        ImageView iv;
        public VH(@NonNull View v) {
            super(v);
            iv = v.findViewById(R.id.item_image);
        }
    }
}