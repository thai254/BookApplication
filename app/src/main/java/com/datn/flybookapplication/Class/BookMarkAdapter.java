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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.datn.flybookapplication.Fragment.BookContentFragment;
import com.datn.flybookapplication.Fragment.BookDetailFragment;
import com.datn.flybookapplication.Fragment.BookMarkFragment;
import com.datn.flybookapplication.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookMarkAdapter extends RecyclerView.Adapter<BookMarkAdapter.BookViewHolder> {
    private List<BookMarkClass> bookMarkList;
    private Context context;
    public BookMarkAdapter(Context context, List<BookMarkClass> bookMarkList) {
        this.context = context;
        this.bookMarkList = bookMarkList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_bookmark, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        BookMarkClass bookMarkData = bookMarkList.get(position);

        holder.userID.setText(bookMarkData.getUser_id());
        holder.bookID.setText(bookMarkData.getBookmark_id());
        holder.bookmarkName.setText(bookMarkData.getBook_name());
        holder.BM_created_at.setText(bookMarkData.getChapter_updated());
        holder.chapterNameBM.setText("Cập nhật " + bookMarkData.getChapter_name());

        String book_type = bookMarkData.getBook_type();

        String base64Image = bookMarkData.getBook_image();
        if (base64Image != null && !base64Image.isEmpty()) {
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.bookmarkImage.setImageBitmap(decodedByte);
        } else {
            holder.bookmarkImage.setImageResource(R.drawable.logo_book);
        }

        List<String> chapterIdList = new ArrayList<>();
        for (BookMarkClass bookmark : bookMarkList) {
            chapterIdList.add(bookmark.getBook_id());
        }

        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();

            bundle.putString("Type", book_type);
            bundle.putStringArrayList("bookmarkList", new ArrayList<>(chapterIdList));
            bundle.putInt("currentPosition", position);

            BookDetailFragment fragment = new BookDetailFragment();
            fragment.setArguments(bundle);

            FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        holder.imgDeleteBM.setOnClickListener(v -> {
            String bookId = bookMarkData.getBook_id();
            String userID = bookMarkData.getUser_id();
            String bookmarkID = bookMarkData.getBookmark_id();
            Log.v("delbm", "b_id = " + bookId + " - u_id = " + userID);

            Toast.makeText(context, "Bookmark deleted: " + bookMarkData.getBook_name(), Toast.LENGTH_LONG).show();
            deleteBookmarkItem(bookmarkID);
            bookMarkList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, bookMarkList.size());
            refreshFragmentWithUserId(userID);
        });
    }

    @Override
    public int getItemCount() {
        return bookMarkList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView bookID, userID, bookmarkName, BM_created_at;
        Button chapterNameBM;
        ImageView bookmarkImage;
        ImageButton imgDeleteBM;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookID = itemView.findViewById(R.id.listBookIDBM);
            userID = itemView.findViewById(R.id.listBookuseridBM);
            bookmarkName = itemView.findViewById(R.id.listBookNameBM);
            chapterNameBM = itemView.findViewById(R.id.listChapterBM);
            bookmarkImage = itemView.findViewById(R.id.listImageBM);
            BM_created_at = itemView.findViewById(R.id.listUpdatedBM);
            imgDeleteBM = itemView.findViewById(R.id.imageButtonDelete);
        }
    }

    private void refreshFragmentWithUserId(String userId) {
        AppCompatActivity activity = (AppCompatActivity) context;

        Bundle bundle = new Bundle();
        bundle.putString("user_id", userId);

        BookMarkFragment currentFragment = new BookMarkFragment();
        currentFragment.setArguments(bundle);

        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, currentFragment) // R.id.fragment_container là ID của container
                .addToBackStack(null) // Thêm vào BackStack nếu muốn
                .commit();
    }
    private void deleteBookmarkItem(String bookmarkId) {
        String url = "https://apex.oracle.com/pls/apex/tranthai312/books/Bookmark?bookmarkID=" + bookmarkId;

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("DeleteResponse", "Bookmark deleted successfully: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("DeleteError", "Error deleting bookmark: " + error.getMessage());
                    }
                }) {
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
