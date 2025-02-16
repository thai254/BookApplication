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
import com.datn.flybookapplication.Class.BookMarkAdapter;
import com.datn.flybookapplication.Class.BookMarkClass;
import com.datn.flybookapplication.Class.HistoryAdapter;
import com.datn.flybookapplication.Class.HistoryDataClass;
import com.datn.flybookapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.RecViewBookHis);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Bundle bundle = getArguments();
        if (bundle != null) {
            String userId = bundle.getString("user_id");
            Log.d("HF", "User ID: " + userId);
            getBookHistory(userId);
        }

        return view;
    }

    public void getBookHistory(String user_id) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        String url = "https://apex.oracle.com/pls/apex/tranthai312/books/Histories?user_id=" + user_id;
        Log.v("burl", url);

        List<HistoryDataClass> historyList = new ArrayList<>();
        HistoryAdapter historyAdapter = new HistoryAdapter(getContext(), historyList);
        recyclerView.setAdapter(historyAdapter);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("items");
                            int countHis = jsonObject.getInt("count");

                            if (jsonArray.length() > 0) {
                                historyList.clear();
                                for (int i = 0; i < countHis; i++) {
                                    JSONObject bookObject = jsonArray.getJSONObject(i);

                                    String bh_ID = bookObject.getString("history_id");
                                    String b_ID = bookObject.getString("book_id");
                                    String bh_Created = bookObject.getString("history_created_at");
                                    String b_name = bookObject.getString("book_name");
                                    String b_imgBase64 = bookObject.getString("book_image");
                                    String b_chapter_id = bookObject.getString("chapter_id");
                                    String b_chapter = bookObject.getString("chapter_name");
                                    String b_type = bookObject.getString("book_type");

                                    String bm_Created_TC = changeTime(bh_Created);

                                    HistoryDataClass historyDataClass = new HistoryDataClass(bh_ID, b_ID, user_id, b_chapter_id,
                                            b_chapter, bm_Created_TC, b_name, b_type, b_imgBase64);
                                    historyList.add(historyDataClass);
                                }
                                historyAdapter.notifyDataSetChanged();
                            } else {
                                Log.d("HisFragment", "No data found.");
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