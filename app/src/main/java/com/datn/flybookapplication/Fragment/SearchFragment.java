package com.datn.flybookapplication.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.datn.flybookapplication.Activity.MenuActivty;
import com.datn.flybookapplication.Class.BookAdapter;
import com.datn.flybookapplication.Class.BookDataClass;
import com.datn.flybookapplication.Class.SearchAdapter;
import com.datn.flybookapplication.Class.SearchDataClass;
import com.datn.flybookapplication.R;

import java.util.List;

public class SearchFragment extends Fragment {
    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    private List<SearchDataClass> searchDataList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.RecViewBook);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//        if (searchDataList != null) {
//            searchAdapter = new SearchAdapter(getContext(), searchDataList);
//            recyclerView.setAdapter(searchAdapter);
//        }
//
        return view;

    }

    public void setSearchDataList(List<SearchDataClass> searchDataList) {
        this.searchDataList = searchDataList;
        if (searchAdapter != null) {
            searchAdapter.notifyDataSetChanged();
        }
    }
    public void refreshData() {
        ((MenuActivty) getActivity()).getBooks();
    }
}