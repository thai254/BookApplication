package com.datn.flybookapplication.Class;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.flybookapplication.Fragment.BookDetailFragment;
import com.datn.flybookapplication.Fragment.SearchFragment;
import com.datn.flybookapplication.R;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.BookViewHolder>{
    private List<SearchDataClass> searchList;
    private Context context;
    private Dialog dialog;
    public SearchAdapter(Context context, List<SearchDataClass> searchList, Dialog dialog) {
        this.context = context;
        this.searchList = searchList;
        this.dialog = dialog;
    }
    public SearchAdapter(Context context, List<SearchDataClass> searchList) {
        this.context = context;
        this.searchList = searchList;
    }
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_search, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        SearchDataClass searchData = searchList.get(position);

        holder.bookID.setText(searchData.getBookId());
        holder.bookName.setText(searchData.getBookName());
        holder.chapterName.setText(searchData.getChapterName());

        String base64Image = searchData.getBookImage();
        if (base64Image != null && !base64Image.isEmpty()) {
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.bookImage.setImageBitmap(decodedByte);
        } else {
            holder.bookImage.setImageResource(R.drawable.logo_book);
        }

        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("bookId", searchData.getBookId());

            BookDetailFragment fragment = new BookDetailFragment();
            fragment.setArguments(bundle);

            FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

            if (dialog != null) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView bookID, bookName, chapterName;
        ImageView bookImage;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookID = itemView.findViewById(R.id.listBookIDSearch);
            bookName = itemView.findViewById(R.id.listBookNameSearch);
            chapterName = itemView.findViewById(R.id.listChapterSearch);
            bookImage = itemView.findViewById(R.id.listImageSearch);
        }
    }
}
