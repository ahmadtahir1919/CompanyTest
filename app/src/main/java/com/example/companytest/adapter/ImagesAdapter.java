package com.example.companytest.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.companytest.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder> {
    private OnItemDelete onItemDelete;
    Context context;
    List<File> list;

    public ImagesAdapter(Context context, List<File> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ImagesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_image, viewGroup, false);
        return new ImagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesViewHolder holder, int position) {

        Picasso.get().load(Uri.parse("file://" + list.get(position).getAbsoluteFile())).resize(500, 500).networkPolicy(NetworkPolicy.NO_CACHE)
                .into(holder.imgFullImage);
        holder.floatDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemDelete.onItemDelete(position,list.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ImagesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.float_delete)
        FloatingActionButton floatDelete;
        @BindView(R.id.img_full_image)
        AppCompatImageView imgFullImage;

        ImagesViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setonItemDelete(OnItemDelete onItemDelete) {
        this.onItemDelete = onItemDelete;
    }

    public interface OnItemDelete {
        void onItemDelete(int position, File file);
    }
}
