package com.example.traveltales;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImageViewAdapter extends RecyclerView.Adapter<ImageViewAdapter.ViewHolder> {

    private List<ImageModel> imagesList;
    private Context context;

    public ImageViewAdapter(Context context, List<ImageModel> imagesList) {
        this.context = context;
        this.imagesList = imagesList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.itemImage);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String imageUrl = imagesList.get(position).getImageUri();

        Glide.with(context).load(imageUrl).into(holder.imageView);

        holder.itemView.setOnClickListener(v -> showImageDialog(imageUrl));
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    private void showImageDialog(String imageUrl) {

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image_view);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        ImageView fullImage = dialog.findViewById(R.id.fullImage);
        ImageView closeBtn = dialog.findViewById(R.id.closeBtn);

        Glide.with(context).load(imageUrl).into(fullImage);

        closeBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}