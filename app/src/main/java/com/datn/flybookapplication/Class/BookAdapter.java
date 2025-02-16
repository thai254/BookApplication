package com.datn.flybookapplication.Class;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
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

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private List<BookDataClass> bookList;
    private Context context;

    public BookAdapter(Context context, List<BookDataClass> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_item, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        BookDataClass bookData = bookList.get(position);

        holder.bookName.setText(bookData.getBookName());
        holder.chapterName.setText(bookData.getChapterName());
        holder.authorName.setText(bookData.getAuthorId());

        String createdAt = bookData.getCreatedAt();
        String timeAgo = TimeAgo(createdAt);
        holder.createdAt.setText(timeAgo);

        // Xử lý ảnh Base64
        String base64Image = bookData.getBookImage();
        if (base64Image != null && !base64Image.isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.bookImage.setImageBitmap(decodedByte);
            } catch (Exception e) {
                e.printStackTrace();
                holder.bookImage.setImageResource(R.drawable.logo_book);
            }
        } else {
            holder.bookImage.setImageResource(R.drawable.logo_book);
        }

        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("bookId", bookData.getBookId());
            bundle.putString("userId", bookData.getUserID());
            bundle.putString("Account", bookData.getUserAccount());
            bundle.putString("Type", bookData.getBookType());

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
        return bookList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView bookName, chapterName, authorName, createdAt;
        ImageView bookImage, imgBanner;
        Button txtNameBanner;
        TextView txtContentBanner;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookName = itemView.findViewById(R.id.listBookName);
            chapterName = itemView.findViewById(R.id.listChapter);
            authorName = itemView.findViewById(R.id.listAuthor);
            createdAt = itemView.findViewById(R.id.listUpdated);
            bookImage = itemView.findViewById(R.id.listImage);

            txtNameBanner = itemView.findViewById(R.id.textName);
            txtContentBanner = itemView.findViewById(R.id.textContent);
            imgBanner = itemView.findViewById(R.id.bannerBackground);
        }
    }

    public String TimeAgo(String inputTime) {
        String formattedDate = null;

        SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        SimpleDateFormat inputFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");

        try {
            Date date;

            // Kiểm tra độ dài của chuỗi để quyết định sử dụng định dạng nào
            if (inputTime != null) {
                if (inputTime.length() > 19) {
                    date = inputFormat1.parse(inputTime);
                } else {
                    date = inputFormat2.parse(inputTime);
                }
                formattedDate = outputFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Trả về ngày đã được định dạng hoặc giá trị mặc định nếu không thể phân tích
        return formattedDate != null ? formattedDate : inputTime;
    }
}
