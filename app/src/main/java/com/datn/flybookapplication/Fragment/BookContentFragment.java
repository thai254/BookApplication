package com.datn.flybookapplication.Fragment;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.net.ParseException;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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
import com.datn.flybookapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class BookContentFragment extends Fragment {
    private TextToSpeech textToSpeech;
    TextView txtChaptername, txtUpdated, txtContentChapter;
    Button btnPrevious, btnNextChap, btnConfirm, btnCancel;
    ImageButton ibVoice;
    String bookId, userId, ChapterID;
    private int StopPosition = 0;
    float speechRate;
    BottomNavigationView bottomNavigationView_void;
    Dialog dialogPos;
    private int currentCharIndex = 0;
    private boolean isPaused = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_book_content, container, false);
        txtChaptername = view.findViewById(R.id.text_chaptername);
        txtUpdated = view.findViewById(R.id.txtUpdated);
        txtContentChapter = view.findViewById(R.id.text_contentChapter);
        btnPrevious = view.findViewById(R.id.buttonPreviousChapter);
        btnNextChap = view.findViewById(R.id.buttonNextChapter);
        ibVoice = view.findViewById(R.id.imageButtonVoice);
        bottomNavigationView_void = view.findViewById(R.id.bottomNavigationView_Voice);

        dialogPos = new Dialog(getContext());
        dialogPos.setContentView(R.layout.dialog_resume_voice);
        dialogPos.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogPos.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogPos.setCancelable(false);

        btnConfirm = dialogPos.findViewById(R.id.btnConfirm);
        btnCancel = dialogPos.findViewById(R.id.btnCancel);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ibVoice.performClick();
                dialogPos.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPos.dismiss();
            }
        });

        bottomNavigationView_void.setSelectedItemId(R.id.menu_pause);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppSettings", 0);
        String selectedFont = sharedPreferences.getString("SelectedFont", "default1.ttf");
        String selectedVoice = sharedPreferences.getString("SelectedVoice", "voicedefault");
        String selectedSpeed = sharedPreferences.getString("SelectedSpeed", "speeddefault");

        try {
            speechRate = Float.parseFloat(selectedSpeed);
        } catch (NumberFormatException e) {
            speechRate = 1.0f;
        }

        int fontSize = sharedPreferences.getInt("FontSize", 18);

        Typeface typeface;
        try {
            Log.v("select font + voice: ", selectedFont + " - " + selectedVoice);
            typeface = Typeface.createFromAsset(requireActivity().getAssets(), "font/" + selectedFont);
        } catch (Exception e) {
            typeface = Typeface.createFromAsset(requireActivity().getAssets(), "font/ccspaghettiwesternsans.ttf");
        }
        txtContentChapter.setTypeface(typeface);
        txtContentChapter.setTextSize(fontSize);


        Bundle bundle = getArguments();
        if (bundle != null) {
            bookId = bundle.getString("bookId");
            userId = bundle.getString("userId");
            Log.d("BookContentFragment", "bookId: " + bookId + ", userId: " + userId);
            ArrayList<String> chapterList = bundle.getStringArrayList("chapterList");
            int currentPosition = bundle.getInt("currentPosition", 0);

            if (chapterList != null && !chapterList.isEmpty()) {

                String currentChapterID = chapterList.get(currentPosition);
                getBookContent(currentChapterID);

                setupButtonActions(chapterList, currentPosition);
            }
        }

        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Set<Voice> voices = textToSpeech.getVoices();
                    boolean voiceSet = false;

                    for (Voice voice : voices) {
                        // vi-vn-x-gft-network giọng nữ ngang cũ
                        // vi-vn-x-vic-network giọng nữ dịu mới
                        // vi-vn-x-vid-network
                        // vi-vn-x-vie-network
                        // vi-vn-x-gft-network
                        // vi-vn-x-vif-network

                        // vi-vn-x-vid-local giọng nam
                        // vi-vn-x-vif-local
                        // vi-vn-x-vie-local
                        // vi-vn-x-gft-local
                        // vi-vn-x-vic-local
                        // vi-vn-x-vid-local

                        // vi-VN-language
                        if (voice.getName().equals(selectedVoice)) {
                            textToSpeech.setVoice(voice); // Đặt giọng đọc
                            voiceSet = true;
                            Log.d("TTS", "Đã đặt giọng: " + voice.getName());
                            break;
                        }
                    }

                    if (!voiceSet) {
                        Log.e("TTS", "Không tìm thấy giọng"+ textToSpeech +", sử dụng giọng mặc định.");
                        textToSpeech.setLanguage(Locale.forLanguageTag("vi")); // Dự phòng
                    }
                } else {
                    Log.e("TTS", "Khởi tạo TTS thất bại.");
                }
            }
        });

        ibVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = txtContentChapter.getText().toString(); // Lấy nội dung văn bản từ TextView

                if (!content.isEmpty()) {
                    bottomNavigationView_void.setVisibility(View.VISIBLE);
                    speakLongText(content, StopPosition);
                } else {
                    Log.e("TTS", "Không có nội dung để đọc.");
                }
            }
        });

        bottomNavigationView_void.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            MenuItem PauseVoiceMenu = bottomNavigationView_void.getMenu().findItem(R.id.menu_pause);
            if (itemId == R.id.menu_previous) {
                if (textToSpeech != null) {
                    textToSpeech.stop();

                    int skipDurationInSeconds = 30;
                    int charPerSecond = 5;
                    int rewindCharCount  = skipDurationInSeconds * charPerSecond;

                    currentCharIndex = Math.max(currentCharIndex - rewindCharCount, 0);
                    Log.v("TTS", "Pre to character index: " + currentCharIndex);

                    String content = txtContentChapter.getText().toString();
                    if (!content.isEmpty() && currentCharIndex < content.length()) {
                        speakLongText(content, currentCharIndex);
                    } else {
                        Log.v("TTS", "No more content to skip forward to.");
                    }
                }
                return true;
            }else if (itemId == R.id.menu_pause) {
                if (textToSpeech != null) {
                    if (!isPaused) {
                        if (textToSpeech.isSpeaking()) {
                            textToSpeech.stop();
                            UpdateBookHistoryWithStopPos(userId, bookId, ChapterID, String.valueOf(currentCharIndex));
                            PauseVoiceMenu.setIcon(R.drawable.menu_continue);
                            PauseVoiceMenu.setTitle("Phát tiếp");
                            isPaused = true;
                            Log.v("TTS", "Paused at character index: " + currentCharIndex);
                        }
                    } else {
                        String content = txtContentChapter.getText().toString();
                        if (!content.isEmpty()) {
                            isPaused = false;
                            speakLongText(content, currentCharIndex);
                            PauseVoiceMenu.setIcon(R.drawable.ic_pause);
                            PauseVoiceMenu.setTitle("Tạm dừng");
                            Log.v("TTS", "Resumed reading from character index: " + currentCharIndex);
                        }
                    }
                }
                return true;
            } else if (itemId == R.id.menu_skip) {
                if (textToSpeech != null) {
                    textToSpeech.stop();

                    int skipDurationInSeconds = 30;
                    int charPerSecond = 5;
                    int skipCharCount = skipDurationInSeconds * charPerSecond;

                    currentCharIndex = Math.min(currentCharIndex + skipCharCount, txtContentChapter.getText().toString().length());
                    Log.v("TTS", "Skipped to character index: " + currentCharIndex);

                    String content = txtContentChapter.getText().toString();
                    if (!content.isEmpty() && currentCharIndex < content.length()) {
                        speakLongText(content, currentCharIndex);
                    } else {
                        Log.v("TTS", "No more content to skip forward to.");
                    }
                }
                return true;
            }else if (itemId == R.id.menu_replay) {
                if (textToSpeech != null) {
                    textToSpeech.stop();
                    String content = txtContentChapter.getText().toString();
                    if (!content.isEmpty()) {
                        isPaused = false;
                        currentCharIndex = 0;
                        speakLongText(content, currentCharIndex);
                    }
                }
                return true;
            } else if (itemId == R.id.menu_exit) {
                if (textToSpeech != null) {
                    textToSpeech.stop();
                }
                bottomNavigationView_void.setVisibility(View.GONE);
                return true;
            }
            return false;
        });

        return view;
    }

    private void speakLongText(String text, int position) {
        textToSpeech.setSpeechRate(speechRate);
        int maxLength = TextToSpeech.getMaxSpeechInputLength(); // Giới hạn tối đa của TTS
        Log.d("TTS", "Max TTS Length: " + maxLength);

        if (position >= text.length() || position < 0) {
            Log.e("TTS", "Invalid position: " + position);
            return;
        }

        final int[] start = {position};
        final SpannableString spannableText = new SpannableString(text); // Chuẩn bị để đổi màu văn bản

        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                Log.v("TTS", "Started reading chunk at index: " + start[0]);
            }

            @Override
            public void onDone(String utteranceId) {
                // Chuyển đến chunk tiếp theo
                start[0] += maxLength;
                if (start[0] < text.length()) {
                    int end = Math.min(start[0] + maxLength, text.length());
                    String nextChunk = text.substring(start[0], end);
                    textToSpeech.speak(nextChunk, TextToSpeech.QUEUE_ADD, null, utteranceId);
                } else {
                    Log.v("TTS", "Finished reading all chunks.");
                }
            }

            @Override
            public void onError(String utteranceId) {
                Log.e("TTS", "Error occurred while reading.");
            }

            @Override
            public void onRangeStart(String utteranceId, int startChar, int endChar, int frame) {
                // Cập nhật vị trí ký tự hiện tại
                currentCharIndex = start[0] + startChar;
                Log.v("TTS", "Currently reading character at index: " + currentCharIndex);

                spannableText.setSpan(
                        new ForegroundColorSpan(Color.WHITE),
                        currentCharIndex,
                        currentCharIndex + (endChar - startChar),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                spannableText.setSpan(
                        new BackgroundColorSpan(Color.BLACK),
                        currentCharIndex,
                        currentCharIndex + (endChar - startChar),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );

                // Reset lại định dạng của các đoạn trước
                spannableText.setSpan(
                        new ForegroundColorSpan(Color.BLACK),
                        0,
                        currentCharIndex,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                spannableText.setSpan(
                        new BackgroundColorSpan(Color.TRANSPARENT), // Nền PNG
                        0,
                        currentCharIndex,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );

                // Cập nhật TextView
                requireActivity().runOnUiThread(() -> txtContentChapter.setText(spannableText));
            }
        });

        // Đọc đoạn đầu tiên bắt đầu từ position
        int end = Math.min(start[0] + maxLength, text.length());
        String firstChunk = text.substring(start[0], end);
        textToSpeech.speak(firstChunk, TextToSpeech.QUEUE_FLUSH, null, "chunk_0");
    }

    public void getBookContent(String id_chapter) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        String url = "https://apex.oracle.com/pls/apex/tranthai312/books/GetChapterContentbyID?chapter_id=" + id_chapter;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            List<ChapterDataClass> chapterDataList = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("items");
                            if (jsonArray.length() > 0) {
                                JSONObject bookDetailObject = jsonArray.getJSONObject(0);
                                String book_ID = bookDetailObject.getString("book_id");
                                String chapter_ID = bookDetailObject.getString("chapter_id");
                                String chapter_NAME = bookDetailObject.getString("chapter_name");
                                String created_AT = bookDetailObject.getString("created_at");
                                String chapter_CONTENT = bookDetailObject.getString("chapter_content");

                                txtChaptername.setText(chapter_NAME);
                                txtUpdated.setText(changeTime(created_AT));
                                txtContentChapter.setText(chapter_CONTENT);
                                ChapterID = chapter_ID;
                                getStartAtPosition(ChapterID, userId);
                                UpdateBookHistory(userId, bookId, ChapterID);
                                //txtContentChapter.setTypeface(ResourcesCompat.getFont(getContext(), R.font.quicksand_book));

                            } else {
                                Log.v("ERROR CONTENT", "No chapter content found");
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

    public void getStartAtPosition(String chapterID, String userID) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        String url = "https://apex.oracle.com/pls/apex/tranthai312/books/StopPosition?chapter_id=" + chapterID + "&user_id=" + userID;
        Log.v("urlpos*: ", url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            List<ChapterDataClass> chapterDataList = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("items");
                            if (jsonArray.length() > 0) {
                                JSONObject bookDetailObject = jsonArray.getJSONObject(0);
                                String stop_pos = bookDetailObject.getString("stop_position");

                                StopPosition = Integer.parseInt(stop_pos);
                                Log.v("stop pos: ", String.valueOf(StopPosition));

                                if(StopPosition >= 1){
                                    dialogPos.show();
                                }else if(StopPosition == 0){
                                    dialogPos.dismiss();
                                }

                            } else {
                                Log.v("ERROR CONTENT", "No chapter content found");
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

    public void UpdateBookHistory(String user_id, String book_id, String chapter_id) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        String url = "https://apex.oracle.com/pls/apex/tranthai312/books/Histories?" +
                "book_id=" + book_id + "&user_id=" + user_id + "&chapter_id=" + chapter_id;
        Log.v("url bhis = ", url);

        JSONObject postData = new JSONObject();
        try {
            postData.put("book_id", book_id);
            postData.put("user_id", user_id);
            postData.put("chapter_id", chapter_id);
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

    public void UpdateBookHistoryWithStopPos(String user_id, String book_id, String chapter_id, String stop_pos) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        String url = "https://apex.oracle.com/pls/apex/tranthai312/books/StopPosition?" + "book_id=" + book_id +
                "&user_id=" + user_id + "&chapter_id=" + chapter_id + "&stop_position=" + stop_pos;
        Log.v("url bhis2 = ", url);

        JSONObject postData = new JSONObject();
        try {
            postData.put("book_id", book_id);
            postData.put("user_id", user_id);
            postData.put("stop_pos", stop_pos);
            postData.put("chapter_id", chapter_id);
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

    private void setupButtonActions(List<String> chapterList, int currentPosition) {
        btnPrevious.setOnClickListener(v -> {
            updateButtonState(currentPosition, chapterList.size());
            // Chương trước sẽ là vị trí id + 1 (tiến lên trong danh sách giảm dần)
            if (currentPosition + 1 < chapterList.size()) {
                textToSpeech.stop();
                bottomNavigationView_void.setVisibility(View.GONE);
                navigateToChapter(chapterList, currentPosition + 1);
            } else {
                Log.v("Navigation", "Đang ở chương cuối cùng");
            }
        });

        btnNextChap.setOnClickListener(v -> {
            // Chương sau sẽ là vị trí id - 1 (lùi xuống trong danh sách giảm dần)
            if (currentPosition - 1 >= 0) {
                textToSpeech.stop();
                bottomNavigationView_void.setVisibility(View.GONE);
                navigateToChapter(chapterList, currentPosition - 1);
            } else {
                Log.v("Navigation", "Đang ở chương đầu tiên");
            }
        });
    }

    private void navigateToChapter(List<String> chapterList, int newPosition) {
        String newChapterID = chapterList.get(newPosition);

        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        bundle.putString("bookId", bookId);
        bundle.putStringArrayList("chapterList", new ArrayList<>(chapterList));
        bundle.putInt("currentPosition", newPosition);

        BookContentFragment newFragment = new BookContentFragment();
        newFragment.setArguments(bundle);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, newFragment)
                .addToBackStack(null)
                .commit();
    }

    private void updateButtonState(int currentPosition, int listSize) {
        if (currentPosition == 0) {
            btnPrevious.setEnabled(true);
            btnNextChap.setEnabled(false);
        }
        else if (currentPosition == listSize - 1) {
            btnPrevious.setEnabled(false);
            btnNextChap.setEnabled(true);
        }
        else {
            btnPrevious.setEnabled(true);
            btnNextChap.setEnabled(true);
        }
    }
}