package com.datn.flybookapplication.Fragment;

import static androidx.databinding.DataBindingUtil.setContentView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.datn.flybookapplication.Activity.MenuActivty;
import com.datn.flybookapplication.Class.BannerAdapter;
import com.datn.flybookapplication.Class.BannerDataClass;
import com.datn.flybookapplication.Class.BookAdapter;
import com.datn.flybookapplication.Class.BookDataClass;
import com.datn.flybookapplication.R;

import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView, recycleBanner;
    private BookAdapter bookAdapter;
    private BannerAdapter bannerAdapter;
    private List<BookDataClass> bookDataList;
    private List<BannerDataClass> bannerDataList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.RecViewBook);
        recycleBanner = view.findViewById(R.id.RecBannerBook);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleBanner.setLayoutManager(new LinearLayoutManager(getContext()));

        if (bookDataList != null) {
            bookAdapter = new BookAdapter(getContext(), bookDataList);
            recyclerView.setAdapter(bookAdapter);
        }
        if (bannerDataList != null) {
            bannerAdapter = new BannerAdapter(getContext(), bannerDataList);
            recycleBanner.setAdapter(bannerAdapter);
        }

        return view;
    }

    public void setBookDataList(List<BookDataClass> bookDataList) {
        this.bookDataList = bookDataList;

        if (recyclerView != null) { // Đảm bảo RecyclerView đã khởi tạo
            if (bookAdapter == null) {
                bookAdapter = new BookAdapter(getContext(), this.bookDataList);
                recyclerView.setAdapter(bookAdapter);
            } else {
                bookAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setBannerDataList(List<BannerDataClass> bannerDataList) {
        this.bannerDataList = bannerDataList;

        if (recycleBanner != null) { // Đảm bảo RecyclerView đã khởi tạo
            if (bannerAdapter == null) {
                bannerAdapter = new BannerAdapter(getContext(), this.bannerDataList);
                recycleBanner.setAdapter(bannerAdapter);
            } else {
                bannerAdapter.notifyDataSetChanged();
            }
        }
    }

    public void refreshData() {
        ((MenuActivty) getActivity()).getBooks();
    }
}