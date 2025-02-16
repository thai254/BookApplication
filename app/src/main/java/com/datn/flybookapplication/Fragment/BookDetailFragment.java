package com.datn.flybookapplication.Fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.ParseException;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.datn.flybookapplication.Class.BookDetailAdapter;
import com.datn.flybookapplication.Class.ChapterDataClass;
import com.datn.flybookapplication.Class.CommentAdapter;
import com.datn.flybookapplication.Class.CommentDataClass;
import com.datn.flybookapplication.Class.SharedViewModel;
import com.datn.flybookapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookDetailFragment extends Fragment {
    private RecyclerView recyclerView, recyclerViewComment;
    private CommentAdapter commentAdapter;
    private List<ChapterDataClass> chapterDataList = new ArrayList<>();
    ImageView imgBookDetail;
    TextView txtBookName, txtBookAlias, txtAuthor, txtContentBook, txtCategories, text_sumcmt;
    EditText text_cmt;
    Button btnCmt;
    ImageButton btnBookmark;
    String bookId, userID, userAcc, bookType;
    private SharedViewModel sharedViewModel;
    SwipeRefreshLayout SRLcmt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_book_detail, container, false);

        txtBookName = view.findViewById(R.id.text_bookdetail);
        txtBookAlias = view.findViewById(R.id.text_alias);
        txtAuthor = view.findViewById(R.id.text_author);
        txtContentBook = view.findViewById(R.id.text_content);
        txtCategories = view.findViewById(R.id.text_categories);
        btnBookmark = view.findViewById(R.id.bookmark);
        btnCmt = view.findViewById(R.id.btn_sendCmt);
        text_cmt = view.findViewById(R.id.text_listcomment);
        text_sumcmt = view.findViewById(R.id.text_listsumcomment);

        imgBookDetail =view.findViewById(R.id.img_Book);

        SRLcmt = view.findViewById(R.id.SWLcomment);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        recyclerView = view.findViewById(R.id.RecViewBook);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerViewComment = view.findViewById(R.id.RecViewComment);
        recyclerViewComment.setLayoutManager(new LinearLayoutManager(getContext()));

        Bundle bundle = getArguments();
        if (bundle != null) {
            bookId = bundle.getString("bookId");
            userID = bundle.getString("userId");
            userAcc = bundle.getString("Account");
            bookType = bundle.getString("Type");

            ArrayList<String> bookmarkList = bundle.getStringArrayList("bookmarkList");
            int currentPosition = bundle.getInt("currentPosition", 0);
            if (bookmarkList != null && !bookmarkList.isEmpty()) {
                String currentBookID = bookmarkList.get(currentPosition);
                getBookDetail(currentBookID);
            }

            Log.d("boma_detail", "book_id " + bookId + " u_id: " + userID + " u_a: " + userAcc);
            getBookDetail(bookId);
            getBookComment(bookId);
        }

        btnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //UpdateBookmark(bookId, userID);
                CheckBookmarkExist(userID, bookId);
            }
        });

        btnCmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lần thực hiện đầu tiên
                String textcmt = text_cmt.getText().toString().trim();

                UpdateBookcomment(userID, userAcc, bookId, textcmt);
                Toast.makeText(getContext(), "Đã thêm bình luận!", Toast.LENGTH_LONG).show();

                SRLcmt.setRefreshing(true);

                getBookComment(bookId);

                recyclerViewComment.getAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onChanged() {
                        super.onChanged();
                        recyclerViewComment.getAdapter().unregisterAdapterDataObserver(this);
                        SRLcmt.setRefreshing(false);
                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SRLcmt.setRefreshing(true);

                        getBookComment(bookId);

                        recyclerViewComment.getAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                            @Override
                            public void onChanged() {
                                super.onChanged();
                                recyclerViewComment.getAdapter().unregisterAdapterDataObserver(this);
                                SRLcmt.setRefreshing(false);
                            }
                        });
                    }
                }, 500);
            }
        });

        return view;
    }

    public void CheckBookmarkExist(String user_id, String book_id) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        String url = "https://apex.oracle.com/pls/apex/tranthai312/books/Bookmark" +
                "?bookid="+ book_id +"&userid=" + user_id;
        Log.v("bmurl", url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int count = jsonObject.getInt("count");

                            if (count == 1) {
                                Toast.makeText(getContext(), "Đã theo dõi truyện!", Toast.LENGTH_LONG).show();
                                return;
                            }
                            UpdateBookmark(user_id, book_id);
                            Toast.makeText(getContext(), "Đã thêm truyện!", Toast.LENGTH_LONG).show();

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
                });

        requestQueue.add(stringRequest);
    }

    public void getBookDetail(String id_book) {
        Log.d("boma_detail_id", "book_id " + bookId);
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        String url = "https://apex.oracle.com/pls/apex/tranthai312/admin/getBookDetail?BOOK_DETAIL_ID=" + id_book;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            List<ChapterDataClass> chapterDataList = new ArrayList<>();
                            List<String> chapterIdList = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("items");

                            int countChapter = jsonObject.getInt("count");

                            if (jsonArray.length() > 0) {
                                JSONObject bookDetailObject = jsonArray.getJSONObject(0);
                                String b_ID = bookDetailObject.getString("book_id");
                                String b_NAME = bookDetailObject.getString("book_name");
                                String b_AUTHOR = bookDetailObject.getString("author_id");
                                String b_ALIAS = bookDetailObject.getString("alias");
                                String b_CONTENT = bookDetailObject.getString("content");
                                String b_BOOK_IMAGE = bookDetailObject.getString("book_image");

                                getBookCate(id_book);

                                txtBookName.setText(b_NAME);
                                txtBookAlias.setText("Tên khác: " + b_ALIAS);

                                if(!b_AUTHOR.equals("null")) {
                                    txtAuthor.setText("Tác giả: " + b_AUTHOR);
                                }else {
                                    txtAuthor.setText("Tác giả: Đang cập nhật");
                                }

                                txtContentBook.setText(b_CONTENT);

                                byte[] decodedString = Base64.decode(b_BOOK_IMAGE, Base64.DEFAULT);
                                Bitmap decodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                imgBookDetail.setImageBitmap(decodedImage);
                                imgBookDetail.setBackgroundResource(R.drawable.image_border);

                                ViewGroup.LayoutParams layoutParams = imgBookDetail.getLayoutParams();

                                layoutParams.width = 650;
                                layoutParams.height = 965;

                                imgBookDetail.setLayoutParams(layoutParams);

                                chapterDataList.clear();
                                for (int i = 0; i < countChapter; i++) {
                                    JSONObject chapterObject = jsonArray.getJSONObject(i);

                                    String b_BOOK_ID = bookDetailObject.getString("book_id");
                                    String b_CHAPTER_ID = chapterObject.getString("chapter_id");
                                    String b_CHAPTER_NAME = chapterObject.getString("chapter_name");
                                    String b_CREATED_AT = chapterObject.getString("created_at");

                                    String b_CREATED_AT_TC = changeTime(b_CREATED_AT);

                                    Log.v("data chapter: ", b_BOOK_ID + " - " + b_CHAPTER_ID + " - " + b_CHAPTER_NAME + " - " + b_CREATED_AT_TC);

                                    ChapterDataClass chapterDataClass = new ChapterDataClass(b_CHAPTER_ID, b_CHAPTER_NAME, b_CREATED_AT_TC, b_BOOK_ID, bookType, userID);
                                    chapterDataList.add(chapterDataClass);
                                    chapterIdList.add(b_CHAPTER_ID);
                                }
                                sharedViewModel.setChapterDataList(chapterDataList);
                                sharedViewModel.setChapterIdList(chapterIdList);

                                BookDetailAdapter bookDetailAdapter1 = new BookDetailAdapter(getContext(), chapterDataList);
                                recyclerView.setAdapter(bookDetailAdapter1);
                                Log.d("BookDetailActivity", "Chapter List Size: " + chapterDataList.size());

                            } else {
                                Log.v("ERROR BOOK", "No book details found");
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

    public void getBookCate(String id_book) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        String url = "https://apex.oracle.com/pls/apex/tranthai312/books/GetBookCate?BOOK_ID=" + id_book;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            List<ChapterDataClass> chapterDataList = new ArrayList<>();
                            List<String> chapterIdList = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("items");

                            int countCate = jsonObject.getInt("count");

                            StringBuilder cateNamesBuilder = new StringBuilder();

                            if (jsonArray.length() > 0) {
                                JSONObject bookCateObject = jsonArray.getJSONObject(0);
                                for (int i = 0; i < countCate; i++) {
                                    JSONObject chapterObject = jsonArray.getJSONObject(i);
                                    String b_Cate = chapterObject.getString("cate_name");
                                    cateNamesBuilder.append(b_Cate);

                                    if (i < countCate - 1) {
                                        cateNamesBuilder.append(", ");
                                    }
                                }
                                txtCategories.setText("Thể loại: " + cateNamesBuilder.toString());
                            } else {
                                txtCategories.setText("Thể loại: Đang cập nhật");
                                Log.v("ERROR BOOK", "No book details found");
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

    public void getBookComment(String id_book) {
        Log.d("boma_detail_cmt", "book_id " + bookId);
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        String url = "https://apex.oracle.com/pls/apex/tranthai312/books/Comments?book_id=" + id_book;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            List<CommentDataClass> commentDataList = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("items");

                            int countComment = jsonObject.getInt("count");
                            text_sumcmt.setText("Bình luận (" + countComment + ")");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject commmentObject = jsonArray.getJSONObject(i);

                                String cmt_ID = commmentObject.getString("comment_id");
                                String cmt_BOOK_ID = commmentObject.getString("book_id");
                                String cmt_USER_ID = commmentObject.getString("user_id");
                                String cmt_BOOK_ACCOUNT = commmentObject.getString("book_account");
                                String cmt_TEXT = commmentObject.getString("comment_text");
                                String cmt_CREATED_AT = commmentObject.getString("created_at");

                                String cmt_CREATED_AT_TC = changeTime2(cmt_CREATED_AT);

                                CommentDataClass commentDataClass = new CommentDataClass(cmt_ID, cmt_BOOK_ID, cmt_USER_ID,
                                        cmt_BOOK_ACCOUNT, cmt_CREATED_AT_TC, cmt_TEXT);
                                commentDataList.add(commentDataClass);
                            }

                            // Nếu Adapter đã được khởi tạo, chỉ cần gọi updateData
                            if (commentAdapter != null) {
                                commentAdapter.updateData(commentDataList);
                            } else {
                                // Nếu chưa tạo Adapter, tạo mới và gán vào RecyclerView
                                commentAdapter = new CommentAdapter(getContext(), commentDataList);
                                recyclerViewComment.setAdapter(commentAdapter);
                            }

                            Log.d("BookDetailActivity", "Comment List Size: " + commentDataList.size());

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
    public void UpdateBookmark(String user_id, String book_id) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        String url = "https://apex.oracle.com/pls/apex/tranthai312/books/Bookmark?" +
                "book_id=" + book_id + "&user_id=" + user_id;
        Log.v("url bmark = ", url);

        JSONObject postData = new JSONObject();
        try {
            postData.put("book_id", book_id);
            postData.put("user_id", user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null && !response.isEmpty()) {
                            Log.d("API Response", response);
                        } else {
                            Log.d("API Response", "Response is empty");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API Error", error.toString());
                        if (error.networkResponse != null) {
                            String errorResponse = new String(error.networkResponse.data);
                            Log.e("API Error Data", errorResponse);
                        }
                    }
                }) {
            @Override
            public byte[] getBody() {
                return postData.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };
        requestQueue.add(stringRequest);
    }

    public void UpdateBookcomment(String user_id, String book_account, String book_id, String cmt_text) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        String url = "https://apex.oracle.com/pls/apex/tranthai312/books/Comments?" +
                "book_id=" + book_id + "&user_id=" + user_id + "&book_account=" + book_account + "&cmt_text=" + cmt_text;
        Log.v("url bcmt = ", url);

        JSONObject postData = new JSONObject();
        try {
            postData.put("book_id", book_id);
            postData.put("user_id", user_id);
            postData.put("book_account", book_account);
            postData.put("cmt_text", cmt_text);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null && !response.isEmpty()) {
                            Log.d("API Response", response);
                        } else {
                            Log.d("API Response", "Response is empty");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API Error", error.toString());
                        if (error.networkResponse != null) {
                            String errorResponse = new String(error.networkResponse.data);
                            Log.e("API Error Data", errorResponse);
                        }
                    }
                }) {
            @Override
            public byte[] getBody() {
                return postData.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
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

    public String changeTime2(String inputTime) {
        String formattedDate = null;

        SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        SimpleDateFormat inputFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm dd-MM-yy");

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