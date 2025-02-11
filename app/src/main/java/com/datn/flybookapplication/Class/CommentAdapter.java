package com.datn.flybookapplication.Class;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.flybookapplication.R;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.BookViewHolder> {
    private List<CommentDataClass> commentList;
    private Context context;

    public CommentAdapter(Context context, List<CommentDataClass> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_comment, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        CommentDataClass commentDataClass = commentList.get(position);

        holder.u_name.setText(commentDataClass.getBook_account());
        holder.u_comment.setText(commentDataClass.getComment_text());
        holder.u_created.setText(commentDataClass.getCreated_at());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public void updateData(List<CommentDataClass> newCommentList) {
        this.commentList.clear();
        this.commentList.addAll(newCommentList);
        notifyDataSetChanged(); 
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView u_name, u_book, u_created, u_comment;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            u_name = itemView.findViewById(R.id.list_username);
            u_book = itemView.findViewById(R.id.listBookID);
            u_created = itemView.findViewById(R.id.list_updated);
            u_comment = itemView.findViewById(R.id.list_comment);
        }
    }
}

