package com.datn.flybookapplication.Class;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.flybookapplication.Fragment.Book2ContentFragment;
import com.datn.flybookapplication.Fragment.BookContentFragment;
import com.datn.flybookapplication.Fragment.BookDetailFragment;
import com.datn.flybookapplication.Fragment.ComicFragment;
import com.datn.flybookapplication.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookDetailAdapter extends RecyclerView.Adapter<BookDetailAdapter.BookViewHolder>{
    private List<ChapterDataClass> chapterDataList;
    private Context context;

    public BookDetailAdapter(Context context, List<ChapterDataClass> chapterDataList) {
        this.context = context;
        this.chapterDataList = chapterDataList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_chapter, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        ChapterDataClass chapterDataClass = chapterDataList.get(position);

        holder.chapterNAME.setText(chapterDataClass.getChapterName());
        holder.chapterID.setText(chapterDataClass.getChapterId());
        holder.createdAt.setText(chapterDataClass.getCreatedAt());

        Log.v("chapter ID: ", chapterDataClass.getChapterId());

        // Lấy danh sách các chapterID từ chapterDataList
        List<String> chapterIdList = new ArrayList<>();
        for (ChapterDataClass chapter : chapterDataList) {
            chapterIdList.add(chapter.getChapterId());
        }

        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();

            String book_id = chapterDataClass.getBookId();
            String user_id = chapterDataClass.getUserId();
            String book_type = chapterDataClass.getBookType();

            if(book_type.equals("0")) {
                bundle.putStringArrayList("chapterList", new ArrayList<>(chapterIdList));
                bundle.putInt("currentPosition", position);
                bundle.putString("bookId", book_id);
                bundle.putString("userId", user_id);

                BookContentFragment fragment = new BookContentFragment();
                fragment.setArguments(bundle);

                FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }else if(book_type.equals("1")) {
                bundle.putStringArrayList("chapterList", new ArrayList<>(chapterIdList));
                bundle.putInt("currentPosition", position);
                bundle.putString("bookId", book_id);
                bundle.putString("userId", user_id);

                Book2ContentFragment fragment = new Book2ContentFragment();
                fragment.setArguments(bundle);

                FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return chapterDataList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView chapterNAME, chapterID, createdAt, bookID;
        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            chapterNAME = itemView.findViewById(R.id.listChapter);
            chapterID = itemView.findViewById(R.id.listChapterID);
            createdAt = itemView.findViewById(R.id.listUpdated);
        }
    }

}
