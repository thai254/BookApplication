package com.datn.flybookapplication.Class;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.flybookapplication.Fragment.BookDetailFragment;
import com.datn.flybookapplication.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BookViewHolder> {
    private List<BannerDataClass> bannerList;
    private Context context;

    public BannerAdapter(Context context, List<BannerDataClass> bannerList) {
        this.context = context;
        this.bannerList = bannerList;
    }
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_banner, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        BannerDataClass bannerData = bannerList.get(position);

        holder.txtNameBanner.setText(bannerData.getTxtContentBanner());
        holder.txtContentBanner.setText(bannerData.getTxtNameBanner());

        // Xử lý ảnh Base64
        String base64Image = bannerData.getImgBanner();
        if (base64Image != null && !base64Image.isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.imgBanner.setImageBitmap(decodedByte);
            } catch (Exception e) {
                e.printStackTrace();
                holder.imgBanner.setImageResource(R.drawable.logo_book);
            }
        } else {
            holder.imgBanner.setImageResource(R.drawable.logo_book);
        }

        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("bookId", bannerData.getBookId());
            bundle.putString("userId", bannerData.getUserID());
            bundle.putString("Account", bannerData.getUserAccount());

            BookDetailFragment fragment = new BookDetailFragment();
            fragment.setArguments(bundle);

            FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }


    @Override
    public int getItemCount() {
        return bannerList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBanner;
        Button txtNameBanner;
        TextView txtContentBanner;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNameBanner = itemView.findViewById(R.id.textName);
            txtContentBanner = itemView.findViewById(R.id.textContent);
            imgBanner = itemView.findViewById(R.id.bannerBackground);
        }
    }
}
