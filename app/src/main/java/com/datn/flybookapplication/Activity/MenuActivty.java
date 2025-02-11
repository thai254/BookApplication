package com.datn.flybookapplication.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.datn.flybookapplication.Class.BannerDataClass;
import com.datn.flybookapplication.Class.BookDataClass;
import com.datn.flybookapplication.Class.SearchAdapter;
import com.datn.flybookapplication.Class.SearchDataClass;
import com.datn.flybookapplication.Fragment.BookMarkFragment;
import com.datn.flybookapplication.Fragment.CateFragment;
import com.datn.flybookapplication.Fragment.HistoryFragment;
import com.datn.flybookapplication.Fragment.HomeFragment;
import com.datn.flybookapplication.Fragment.SettingsFragment;
import com.datn.flybookapplication.R;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MenuActivty extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    Dialog dialog, dialog_search;
    String userID, userAccount, userPass;
    EditText edtName;
    private String randomBookID;
    String txtNameBanner, txtContentBanner, imgBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_activty);

        drawerLayout = findViewById(R.id.drawer_menu_layout);

        NavigationView navigationView2 = findViewById(R.id.nav_view);
        navigationView2.setNavigationItemSelectedListener(this);

        userID = getIntent().getStringExtra("user_id");
        String userEmail = getIntent().getStringExtra("user_email");
        userAccount = getIntent().getStringExtra("user_account");
        String userImage = getIntent().getStringExtra("user_image");
        userPass = getIntent().getStringExtra("user_password");

        Log.v("ui: ", userImage +" \n " + userID);

        getBooks();

        View headerView = navigationView2.getHeaderView(0);

        TextView txtUsername = headerView.findViewById(R.id.txt_username);
        TextView txtUserEmail = headerView.findViewById(R.id.txt_useremail);
        ImageView imgUser = headerView.findViewById(R.id.img_user);
        ImageButton ibtnChange = headerView.findViewById(R.id.Change_username);

        if(userImage.equals("null")) {
            imgUser.setImageResource(R.drawable.logo_book);
        }else {
            byte[] decodedString = Base64.decode(userImage, Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            imgUser.setImageBitmap(decodedImage);
        }
        txtUsername.setText(userAccount);
        txtUserEmail.setText(userEmail);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        dialog = new Dialog(MenuActivty.this);
        dialog.setContentView(R.layout.activity_change_username_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        Button btnAccept = dialog.findViewById(R.id.btn_change);
        Button btnCancel = dialog.findViewById(R.id.btn_out);
        edtName = dialog.findViewById(R.id.edt_name);
        TextView txtError = dialog.findViewById(R.id.txtError);

        ibtnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtName.setText("");
                edtName.setHint("Nhập tên người dùng mới");
                txtError.setTextColor(ContextCompat.getColor(MenuActivty.this, R.color.red));
                txtError.setVisibility(View.INVISIBLE);
                dialog.show();
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String u_name = edtName.getText().toString().trim();

                if(u_name.length() >= 12){
                    txtError.setText("Tên mới quá dài, hãy chọn tên khác");
                    txtError.setTextColor(ContextCompat.getColor(MenuActivty.this, R.color.red));
                    txtError.setVisibility(View.VISIBLE);
                    return;
                }
                UpdateUsername( u_name, userEmail);
                Log.v("okela", "ok");
                txtError.setText("Đổi tên thành công, hãy đăng nhập lại để cập nhật!");
                txtError.setTextColor(ContextCompat.getColor(MenuActivty.this, R.color.menu_color));
                txtError.setVisibility(View.VISIBLE);
                txtUsername.setText(u_name);
                recreate();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(HomeFragment.class.getSimpleName());
            if (homeFragment != null && homeFragment.isVisible()) {
                homeFragment.refreshData();
            } else {
                // Nếu HomeFragment chưa được hiển thị, tạo mới và thay thế
                homeFragment = new HomeFragment();
                transaction.replace(R.id.fragment_container, homeFragment, HomeFragment.class.getSimpleName());
                transaction.addToBackStack(null);
                transaction.commit();
                getBooks();
            }
        } else if (itemId == R.id.nav_search) {
            // Hiển thị dialog tìm kiếm
            showSearchDialog();
        } else if (itemId == R.id.nav_category) {
            // Chuyển tới CateFragment
            CateFragment cateFragment = new CateFragment();
            transaction.replace(R.id.fragment_container, cateFragment, CateFragment.class.getSimpleName());
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (itemId == R.id.nav_bookmark) {
            // Chuyển tới BookMarkFragment
            Bundle bundle = new Bundle();
            bundle.putString("user_id", userID);
            BookMarkFragment bookMarkFragment = new BookMarkFragment();
            bookMarkFragment.setArguments(bundle);

            transaction.replace(R.id.fragment_container, bookMarkFragment, BookMarkFragment.class.getSimpleName());
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (itemId == R.id.nav_history) {
            // Chuyển tới HistoryFragment
            Bundle bundle = new Bundle();
            bundle.putString("user_id", userID);
            HistoryFragment historyFragment = new HistoryFragment();
            historyFragment.setArguments(bundle);

            transaction.replace(R.id.fragment_container, historyFragment, HistoryFragment.class.getSimpleName());
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (itemId == R.id.nav_setting) {
            Bundle bundle = new Bundle();
            bundle.putString("user_id", userID);
            bundle.putString("user_pass", userPass);
            SettingsFragment settingsFragment = new SettingsFragment();
            settingsFragment.setArguments(bundle);

            transaction.replace(R.id.fragment_container, settingsFragment, SettingsFragment.class.getSimpleName());
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (itemId == R.id.nav_logout) {
            // Chuyển về MainActivity và kết thúc Activity hiện tại
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        return true;
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void getBooks() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = "https://apex.oracle.com/pls/apex/tranthai312/admin/GetBookInfor";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            List<BookDataClass> bookDataList = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray jsonArray = jsonObject.getJSONArray("items");

                            if (jsonArray.length() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject bookObject = jsonArray.getJSONObject(i);
                                    String  u_ID = bookObject.getString("book_id");
                                    String  u_ChapterID = bookObject.getString("chapter_id");
                                    String  u_Chapter_name = bookObject.getString("chapter_name");
                                    String  u_book_name = bookObject.getString("book_name");
                                    String  u_author = bookObject.getString("author_id");
                                    String  u_created_at = bookObject.getString("created_at");
                                    String  u_imgBase64 = bookObject.getString("book_image");

                                    BookDataClass bookData = new BookDataClass(userID, userAccount, u_ID, u_ChapterID, u_book_name, u_Chapter_name, u_author, u_created_at, u_imgBase64);
                                    bookDataList.add(bookData);

                                    if (!bookDataList.isEmpty()) {
                                        Random random = new Random();
                                        int randomIDbook = random.nextInt(bookDataList.size());
                                        randomBookID = bookDataList.get(randomIDbook).getBookId();
                                        Log.v("RANDOM_BOOK_ID", "Book ID: " + randomBookID);

                                        getBookRandom(randomBookID, bookDataList);
                                    }

//                                    HomeFragment homeFragment = new HomeFragment();
//                                    homeFragment.setBookDataList(bookDataList);
//
//                                    getSupportFragmentManager().beginTransaction()
//                                            .replace(R.id.fragment_container, homeFragment)
//                                            .commit();

                                }
                            } else {
                                Log.v("ERROR BOOK", "ok");
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

    public void getBookRandom(String id_book, List<BookDataClass> bookDataList) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = "https://apex.oracle.com/pls/apex/tranthai312/admin/getBookDetail?BOOK_DETAIL_ID=" + id_book;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            List<BannerDataClass> bookBannerDataList = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("items");

                            if (jsonArray.length() > 0) {
                                JSONObject bookDetailObject = jsonArray.getJSONObject(0);
                                String b_ID = bookDetailObject.getString("book_id");
                                String b_NAME = bookDetailObject.getString("book_name");
                                String b_CONTENT = bookDetailObject.getString("content");
                                String b_BOOK_IMAGE = bookDetailObject.getString("book_image");

                                BannerDataClass bookBannerData = new BannerDataClass(b_ID, userID, userAccount, b_NAME, b_CONTENT, b_BOOK_IMAGE);
                                bookBannerDataList.add(bookBannerData);

                                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                                if (currentFragment instanceof HomeFragment) {
                                    HomeFragment homeFragment = (HomeFragment) currentFragment;
                                    homeFragment.setBookDataList(bookDataList);
                                    homeFragment.setBannerDataList(bookBannerDataList);
                                } else {
                                    Log.e("MenuActivity", "Không phải HomeFragment hoặc fragment không tồn tại");
                                }

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


    public void UpdateUsername(String user_name, String user_email) {
        String url = "https://apex.oracle.com/pls/apex/tranthai312/books/UpdateInforUser" +
                "?email=" + toUppercase(user_email) + "&username=" + user_name;
        Log.v("url_changename = ", url);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", user_name);
            jsonBody.put("email", user_email);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest putRequest = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("API_RESPONSE", "Response: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API_ERROR", "Error: " + error.toString());
                    }
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(putRequest);
    }

    private void showSearchDialog() {
        // Tạo dialog
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.fragment_search);
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        EditText edtSearch = dialog.findViewById(R.id.edt_search);
        RecyclerView recyclerView = dialog.findViewById(R.id.RecViewBook);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<SearchDataClass> searchDataList = new ArrayList<>();
        SearchAdapter searchAdapter = new SearchAdapter(this, searchDataList, dialog);
        recyclerView.setAdapter(searchAdapter);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = edtSearch.getText().toString().trim();
                if (!keyword.isEmpty()) {
                    getBookSearch(keyword, searchDataList, searchAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        dialog.show();
    }

    public void getBookSearch(String search_keyword, List<SearchDataClass> searchDataList, SearchAdapter searchAdapter) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = "https://apex.oracle.com/pls/apex/tranthai312/books/SearchBook?keyword=" + search_keyword;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            searchDataList.clear();
                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray jsonArray = jsonObject.getJSONArray("items");

                            if (jsonArray.length() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject bookObject = jsonArray.getJSONObject(i);
                                    String book_id = bookObject.getString("book_id");
                                    String book_name = bookObject.getString("book_name");
                                    String book_chapter = bookObject.getString("chapter_name");
                                    String book_image = bookObject.getString("book_image");

                                    // Thêm dữ liệu mới
                                    SearchDataClass searchData = new SearchDataClass(book_id, book_name, book_chapter, book_image);
                                    searchDataList.add(searchData);
                                }
                                // Cập nhật RecyclerView
                                searchAdapter.notifyDataSetChanged();
                            } else {
                                Log.v("ERROR BOOK", "Không tìm thấy sách");
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



    public String toUppercase(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        return input.toUpperCase();
    }
}
