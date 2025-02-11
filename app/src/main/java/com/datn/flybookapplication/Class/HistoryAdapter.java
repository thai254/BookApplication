package com.datn.flybookapplication.Class;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.lifecycle.ViewModelProvider;
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
import com.datn.flybookapplication.Fragment.HistoryFragment;
import com.datn.flybookapplication.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.BookViewHolder> {
    private List<HistoryDataClass> historyDataList;
    private Context context;
    public HistoryAdapter(Context context, List<HistoryDataClass> historyDataList) {
        this.context = context;
        this.historyDataList = historyDataList;
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
        HistoryDataClass historyData = historyDataList.get(position);

        holder.userID.setText(historyData.getUserID());
        holder.bookID.setText(historyData.getHistoryID());
        holder.bookName.setText(historyData.getBookName());
        holder.BM_created_at.setText(historyData.getCreatedAT());
        holder.chapterName.setText("Đọc tiếp " + historyData.getChapterName());

        String base64Image = historyData.getBookImage();
        if (base64Image != null && !base64Image.isEmpty()) {
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.bookmarkImage.setImageBitmap(decodedByte);
        } else {
            holder.bookmarkImage.setImageResource(R.drawable.logo_book);
        }

        List<String> chapterIdList = new ArrayList<>();
        for (HistoryDataClass history : historyDataList) {
            chapterIdList.add(history.getBookID());
        }

        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();

            bundle.putString("bookId", historyData.getBookID());
            bundle.putString("userId", historyData.getUserID());
            bundle.putStringArrayList("bookmarkList", new ArrayList<>(chapterIdList));
            bundle.putInt("currentPosition", position);

            BookDetailFragment fragment = new BookDetailFragment();
            fragment.setArguments(bundle);

            FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        holder.chapterName.setOnClickListener(v -> {
            String chapterID = historyData.getChapterID();

            SharedViewModel sharedViewModel = new ViewModelProvider((AppCompatActivity) context).get(SharedViewModel.class);
            List<String> chapterIdList1 = sharedViewModel.getChapterIdList().getValue();

            if (chapterIdList1 != null && !chapterIdList1.isEmpty() && chapterIdList1.contains(chapterID)) {
                int currentPosition = chapterIdList1.indexOf(chapterID);

                Bundle bundle = new Bundle();
                bundle.putString("bookId", historyData.getBookID());
                bundle.putString("userId", historyData.getUserID());
                bundle.putInt("currentPosition", currentPosition);
                bundle.putStringArrayList("chapterList", new ArrayList<>(chapterIdList1));

                BookContentFragment fragment = new BookContentFragment();
                fragment.setArguments(bundle);

                ((AppCompatActivity) context).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                Log.v("HistoryAdapter", "chapterIdList1 không có dữ liệu hoặc không chứa chapterID, chuyển sang BookDetailFragment");

                Bundle bundle1 = new Bundle();
                bundle1.putString("bookId", historyData.getBookID());
                bundle1.putString("userId", historyData.getUserID());
                bundle1.putStringArrayList("bookmarkList", new ArrayList<>(chapterIdList));
                bundle1.putInt("currentPosition", position);

                BookDetailFragment fragment1 = new BookDetailFragment();
                fragment1.setArguments(bundle1);

                FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment1);
                transaction.addToBackStack(null);
                transaction.commit();

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    ((AppCompatActivity) context).getSupportFragmentManager().popBackStack();

                    List<String> updatedChapterIdList = sharedViewModel.getChapterIdList().getValue();

                    if (updatedChapterIdList != null && updatedChapterIdList.contains(chapterID)) {
                        int currentPosition = updatedChapterIdList.indexOf(chapterID);

                        Bundle bundle = new Bundle();
                        bundle.putString("bookId", historyData.getBookID());
                        bundle.putString("userId", historyData.getUserID());
                        bundle.putInt("currentPosition", currentPosition); // Vị trí hiện tại
                        bundle.putStringArrayList("chapterList", new ArrayList<>(updatedChapterIdList)); // Danh sách chương

                        BookContentFragment fragment = new BookContentFragment();
                        fragment.setArguments(bundle);

                        ((AppCompatActivity) context).getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack(null)
                                .commit();
                    } else {
                        Log.e("HistoryAdapter", "Chapter ID không tồn tại trong danh sách!");
                    }
                }, 1000);
            }
    });

    }

    @Override
    public int getItemCount() {
        return historyDataList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView historyID, bookID, userID, chapterID, bookName, BM_created_at;
        Button chapterName;
        ImageView bookmarkImage;
        ImageButton imgDeleteBM;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookID = itemView.findViewById(R.id.listBookIDBM);
            userID = itemView.findViewById(R.id.listBookuseridBM);
            bookName = itemView.findViewById(R.id.listBookNameBM);
            chapterName = itemView.findViewById(R.id.listChapterBM);
            bookmarkImage = itemView.findViewById(R.id.listImageBM);
            BM_created_at = itemView.findViewById(R.id.listUpdatedBM);
            imgDeleteBM = itemView.findViewById(R.id.imageButtonDelete);
        }
    }

    private void refreshFragmentWithUserId(String userId) {
        AppCompatActivity activity = (AppCompatActivity) context;

        Bundle bundle = new Bundle();
        bundle.putString("user_id", userId);

        HistoryFragment currentFragment = new HistoryFragment();
        currentFragment.setArguments(bundle);

        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, currentFragment)
                .addToBackStack(null)
                .commit();
    }

    private void deleteHistoryItem(String hisID) {
        String url = "https://apex.oracle.com/pls/apex/tranthai312/books/Histories?historyID=" + hisID;

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
