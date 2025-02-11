package com.datn.flybookapplication.Fragment;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.datn.flybookapplication.Activity.MenuActivty;
import com.datn.flybookapplication.Class.SearchAdapter;
import com.datn.flybookapplication.Class.SearchCateAdapter;
import com.datn.flybookapplication.Class.SearchDataClass;
import com.datn.flybookapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class CateFragment extends Fragment {

    private Animation rotateOpen, rotateClose, rotateFromBottom, rotateToBottom;
    FloatingActionButton btn_filter, btn_edit, btn_refresh;
    Button CateSelect_btn;
    EditText edtSearch;
    private boolean clicked = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cate, container, false);

        rotateOpen = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_open_animation);
        rotateClose = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_close_animation);
        rotateFromBottom = AnimationUtils.loadAnimation(getContext(), R.anim.from_bottom_animation);
        rotateToBottom = AnimationUtils.loadAnimation(getContext(), R.anim.to_bottom_animation);

        edtSearch = view.findViewById(R.id.edtSearch);
        RecyclerView recyclerView = view.findViewById(R.id.RecViewBook);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<SearchDataClass> searchDataList = new ArrayList<>();
        SearchCateAdapter searchCateAdapter = new SearchCateAdapter(getContext(), searchDataList);
        recyclerView.setAdapter(searchCateAdapter);

        btn_filter = view.findViewById(R.id.floatingActionButton);
        btn_edit = view.findViewById(R.id.floatingActionButton2);
        btn_refresh = view.findViewById(R.id.floatingActionButton3);

        CateSelect_btn = view.findViewById(R.id.imageButton);

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.activity_category_dialog);
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddButtonClick();
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "edit btn",Toast.LENGTH_LONG).show();
            }
        });

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "ref btn",Toast.LENGTH_LONG).show();
            }
        });

        final String[] categories = new String[17];
        final boolean[] checkboxStates = new boolean[17];
        final int[] selectedCount = {0};

        CateSelect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy keyword từ EditText
                String keyword = edtSearch.getText().toString().trim();
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.activity_category_dialog);
                dialog.getWindow().setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                CheckBox rb_Hanhdong = dialog.findViewById(R.id.rb_Hanhdong);
                CheckBox rb_Phieuluu = dialog.findViewById(R.id.rb_Phieuluu);
                CheckBox rb_Chuyensinh = dialog.findViewById(R.id.rb_Chuyensinh);
                CheckBox rb_Trinhtham = dialog.findViewById(R.id.rb_Trinhtham);
                CheckBox rb_Drama = dialog.findViewById(R.id.rb_Drama);
                CheckBox rb_Haihuoc = dialog.findViewById(R.id.rb_Haihuoc);
                CheckBox rb_Kinhdi = dialog.findViewById(R.id.rb_Kinhdi);
                CheckBox rb_Lichsu = dialog.findViewById(R.id.rb_Lichsu);
                CheckBox rb_Phepthuat = dialog.findViewById(R.id.rb_Phepthuat);
                CheckBox rb_Bian = dialog.findViewById(R.id.rb_Bian);
                CheckBox rb_Khoahoc = dialog.findViewById(R.id.rb_Khoahoc);
                CheckBox rb_Bikich = dialog.findViewById(R.id.rb_Bikich);
                CheckBox rb_Thethao = dialog.findViewById(R.id.rb_Thethao);
                CheckBox rb_Tamly = dialog.findViewById(R.id.rb_Tamly);
                CheckBox rb_Sieunhien = dialog.findViewById(R.id.rb_Sieunhien);
                CheckBox rb_Langman = dialog.findViewById(R.id.rb_Langman);
                CheckBox rb_Cuocsong = dialog.findViewById(R.id.rb_Cuocsong);
                Button btnApply = dialog.findViewById(R.id.btnApply);
                ImageButton cate_reset = dialog.findViewById(R.id.imgResetCate);

                ArrayList<CheckBox> checkBoxes = new ArrayList<>();
                checkBoxes.add(rb_Hanhdong);
                checkBoxes.add(rb_Phieuluu);
                checkBoxes.add(rb_Chuyensinh);
                checkBoxes.add(rb_Trinhtham);
                checkBoxes.add(rb_Drama);
                checkBoxes.add(rb_Haihuoc);
                checkBoxes.add(rb_Kinhdi);
                checkBoxes.add(rb_Lichsu);
                checkBoxes.add(rb_Phepthuat);
                checkBoxes.add(rb_Bian);
                checkBoxes.add(rb_Khoahoc);
                checkBoxes.add(rb_Bikich);
                checkBoxes.add(rb_Thethao);
                checkBoxes.add(rb_Tamly);
                checkBoxes.add(rb_Sieunhien);
                checkBoxes.add(rb_Langman);
                checkBoxes.add(rb_Cuocsong);

                selectedCount[0] = 0;
                for (int i = 0; i < checkBoxes.size(); i++) {
                    checkBoxes.get(i).setChecked(checkboxStates[i]);
                    if (checkboxStates[i]) {
                        selectedCount[0]++;
                    }
                }

                for (int i = 0; i < checkBoxes.size(); i++) {
                    int finalI = i;
                    checkBoxes.get(i).setOnCheckedChangeListener((buttonView, isChecked) -> {
                        checkboxStates[finalI] = isChecked;
                        if (isChecked) {
                            selectedCount[0]++;
                        } else {
                            selectedCount[0]--;
                        }
                    });
                }

                btnApply.setOnClickListener(v1 -> {
                    categories[0]  = rb_Hanhdong.isChecked() ? "CATE_01" : null;
                    categories[1]  = rb_Phieuluu.isChecked() ? "CATE_02" : null;
                    //cate3 anime
                    categories[2]  = rb_Chuyensinh.isChecked() ? "CATE_04" : null;
                    //cate4 isekai
                    categories[3]  = rb_Trinhtham.isChecked() ? "CATE_05" : null;
                    categories[4]  = rb_Drama.isChecked() ? "CATE_06" : null;
                    categories[5]  = rb_Haihuoc.isChecked() ? "CATE_07" : null;
                    categories[6]  = rb_Kinhdi.isChecked() ? "CATE_09" : null;
                    categories[7]  = rb_Lichsu.isChecked() ? "CATE_10" : null;
                    categories[8]  = rb_Phepthuat.isChecked() ? "CATE_11" : null;
                    categories[9]  = rb_Bian.isChecked() ? "CATE_12" : null;
                    categories[10] = rb_Khoahoc.isChecked() ? "CATE_13" : null;
                    categories[11] = rb_Bikich.isChecked() ? "CATE_14" : null;
                    categories[12] = rb_Thethao.isChecked() ? "CATE_15" : null;
                    categories[13] = rb_Tamly.isChecked() ? "CATE_18" : null;
                    categories[14] = rb_Sieunhien.isChecked() ? "CATE_19" : null;
                    categories[15] = rb_Langman.isChecked() ? "CATE_20" : null;
                    categories[16] = rb_Cuocsong.isChecked() ? "CATE_16" : null;

                    StringBuilder selectedCategories = new StringBuilder("Selected categories: ");
                    for (CheckBox checkBox : checkBoxes) {
                        if (checkBox.isChecked()) {
                            selectedCategories.append(checkBox.getText().toString()).append(", ");
                        }
                    }
                    if (selectedCategories.length() > 2) {
                        selectedCategories.setLength(selectedCategories.length() - 2);
                    }
                    Log.v("CategoryDialog", selectedCategories.toString());
                    Log.v("CategoryDialog", "Number of selected categories: " + selectedCount[0]);

                    dialog.dismiss();
                    // Gọi hàm tìm kiếm theo cate với các giá trị từ mảng categories
                    getBookSearchbyCate(keyword, searchDataList, searchCateAdapter,
                            categories[0], categories[1], categories[2], categories[3], categories[4],
                            categories[5], categories[6], categories[7], categories[8], categories[9],
                            categories[10], categories[11], categories[12], categories[13],
                            categories[14], categories[15], categories[16], selectedCount[0]);
                });

                cate_reset.setOnClickListener(v1 -> {
                    for (int i = 0; i < checkBoxes.size(); i++) {
                        checkBoxes.get(i).setChecked(false);
                        checkboxStates[i] = false;
                    }
                    selectedCount[0] = 0;
                });

                dialog.show();
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = edtSearch.getText().toString().trim();

                if (!keyword.isEmpty()) {
                    boolean allCategoriesNull = true;
                    for (String cat : categories) {
                        if (cat != null) {
                            allCategoriesNull = false;
                            break;
                        }
                    }
                    if (allCategoriesNull && selectedCount[0] == 0) {
                        getBookSearch(keyword, searchDataList, searchCateAdapter);
                    } else {
                        getBookSearchbyCate(keyword, searchDataList, searchCateAdapter,
                                categories[0], categories[1], categories[2], categories[3], categories[4],
                                categories[5], categories[6], categories[7], categories[8], categories[9],
                                categories[10], categories[11], categories[12], categories[13],
                                categories[14], categories[15], categories[16], selectedCount[0]);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });


        return view;
    }

    public void getBookSearch(String search_keyword, List<SearchDataClass> searchDataList, SearchCateAdapter searchCateAdapter) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

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


                                    SearchDataClass searchData = new SearchDataClass(book_id, book_name, book_chapter, book_image);
                                    searchDataList.add(searchData);
                                }

                                searchCateAdapter.notifyDataSetChanged();
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

    public void getBookSearchbyCate(String search_keyword, List<SearchDataClass> searchDataList, SearchCateAdapter searchCateAdapter,
                                    String cate1, String cate2, String cate3, String cate4, String cate5, String cate6, String cate7,
                                    String cate8, String cate9, String cate10, String cate11, String cate12, String cate13, String cate14,
                                    String cate15, String cate16, String cate17, int sumcate) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        String url = "https://apex.oracle.com/pls/apex/tranthai312/books/GetBookByCate?" +
                "CATE1=" +cate1+ "&CATE2=" +cate2+ "&CATE3=" +cate3+ "&CATE4=" +cate4+ "" +
                "&CATE5=" +cate5+ "&CATE6=" +cate6+ "&CATE7=" +cate7+ "&CATE8=" +cate8+ "" +
                "&CATE9=" +cate9+ "&CATE10=" +cate10+ "&CATE11=" +cate11+ "&CATE12=" +cate12+ "" +
                "&CATE13=" +cate13+ "&CATE14=" +cate14+ "&CATE15=" +cate15+ "&CATE16=" +cate16+ "" +
                "&CATE17=" +cate17+ "&SUMCATE=" +sumcate+ "&keyword=" + search_keyword;

        Log.v("urlcate: ", url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("items");

                            searchDataList.clear();

                            if (jsonArray.length() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject bookObject = jsonArray.getJSONObject(i);
                                    String book_id = bookObject.getString("book_id");
                                    String book_name = bookObject.getString("book_name");
                                    String book_chapter = bookObject.getString("chapter_name");
                                    String book_image = bookObject.getString("book_image");

                                    SearchDataClass searchData = new SearchDataClass(book_id, book_name, book_chapter, book_image);
                                    searchDataList.add(searchData);
                                }
                            } else {
                                Log.v("ERROR BOOK", "Không tìm thấy sách");
                            }
                            searchCateAdapter.notifyDataSetChanged();

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


    private void onAddButtonClick() {
        setVisibility(clicked);
        setAnimation(clicked);
        if(!clicked){
            clicked = true;
        } else clicked = false;
    }

    private void setVisibility(Boolean clicked){
        if(!clicked){
            btn_edit.setVisibility(View.VISIBLE);
            btn_refresh.setVisibility(View.VISIBLE);
        }else {
            btn_edit.setVisibility(View.INVISIBLE);
            btn_refresh.setVisibility(View.INVISIBLE);
        }
    }

    private void setAnimation(Boolean clicked){
        if(!clicked){
            btn_edit.startAnimation(rotateFromBottom);
            btn_refresh.startAnimation(rotateFromBottom);
            btn_filter.startAnimation(rotateOpen);

        }else {
            btn_edit.startAnimation(rotateToBottom);
            btn_refresh.startAnimation(rotateToBottom);
            btn_filter.startAnimation(rotateClose);
        }
    }
}