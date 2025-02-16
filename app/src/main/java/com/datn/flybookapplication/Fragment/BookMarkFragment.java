package com.datn.flybookapplication.Fragment;

import android.icu.text.SimpleDateFormat;
import android.net.ParseException;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.datn.flybookapplication.Activity.MenuActivty;
import com.datn.flybookapplication.Class.BookAdapter;
import com.datn.flybookapplication.Class.BookDataClass;
import com.datn.flybookapplication.Class.BookDetailAdapter;
import com.datn.flybookapplication.Class.BookMarkAdapter;
import com.datn.flybookapplication.Class.BookMarkClass;
import com.datn.flybookapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookMarkFragment extends Fragment {
    private RecyclerView recyclerView;
    private BookMarkAdapter bookMarkAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_book_mark, container, false);

        recyclerView = view.findViewById(R.id.RecViewBookMark);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Bundle bundle = getArguments();
        if (bundle != null) {
            String userId = bundle.getString("user_id");
            Log.d("BookMarkFragment", "User ID: " + userId);
            getBookmark(userId);
        }

        return view;
    }
    public void getBookmark(String user_id) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        String url = "https://apex.oracle.com/pls/apex/tranthai312/books/BookMarkCus?user_id=" + user_id;
        Log.v("burl", url);

        List<BookMarkClass> bookMarkList = new ArrayList<>();
        bookMarkAdapter = new BookMarkAdapter(getContext(), bookMarkList);
        recyclerView.setAdapter(bookMarkAdapter);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("items");
                            int countBookmark = jsonObject.getInt("count");

                            if (jsonArray.length() > 0) {
                                bookMarkList.clear();
                                for (int i = 0; i < countBookmark; i++) {
                                    JSONObject bookObject = jsonArray.getJSONObject(i);

                                    String bm_ID = bookObject.getString("bookmark_id");
                                    String b_ID = bookObject.getString("book_id");
                                    String bm_Created = bookObject.getString("bookmark_created_at");
                                    String b_name = bookObject.getString("book_name");
                                    String b_imgBase64 = bookObject.getString("book_image");
                                    String b_type = bookObject.getString("book_type");
                                    String b_chapter = bookObject.getString("chapter_name");
                                    String b_chapter_updated = bookObject.getString("chapter_created_at");

                                    String bm_Created_TC = changeTime(bm_Created);
                                    String bm_chapter_created_TC = changeTime(b_chapter_updated);

                                    BookMarkClass bookmarkData = new BookMarkClass(user_id, bm_ID, b_ID, b_name, b_imgBase64, bm_Created_TC, b_chapter, b_type, bm_chapter_created_TC);
                                    bookMarkList.add(bookmarkData);
                                }
                                bookMarkAdapter.notifyDataSetChanged();
                                Log.d("BookmarkFragment", "Data loaded: " + bookMarkList.size());
                            } else {
                                Log.d("BookmarkFragment", "No bookmarks found.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String utf8String = new String(response.data, "UTF-8");
                    return Response.success(utf8String, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    String fallbackString = new String(response.data);
                    return Response.success(fallbackString, HttpHeaderParser.parseCacheHeaders(response));
                }
            }
        };

        requestQueue.add(stringRequest);
    }

    public String changeTime(String inputTime) {
        String formattedDate = null;

        SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        SimpleDateFormat inputFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");

        try {
            Date date;

            if (inputTime.length() > 19) {
                date = inputFormat1.parse(inputTime);
            } else {
                date = inputFormat2.parse(inputTime);
            }

            formattedDate = outputFormat.format(date);
        } catch (ParseException | java.text.ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }
}