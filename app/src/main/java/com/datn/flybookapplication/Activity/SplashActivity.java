package com.datn.flybookapplication.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.datn.flybookapplication.R;

import java.util.Random;

public class SplashActivity extends AppCompatActivity {

    TextView txtShowtext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        txtShowtext = findViewById(R.id.txtShowtext);

        displayRandomText();

        Intent intent = getIntent();
        final String nextActivity = intent.getStringExtra("nextActivity");

        String userID = getIntent().getStringExtra("user_id");
        String userEmail = getIntent().getStringExtra("user_email");
        String userAccount = getIntent().getStringExtra("user_account");
        String userImage = getIntent().getStringExtra("user_image");
        String userPass = getIntent().getStringExtra("user_password");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isNetworkAvailable()) {
                    Intent nextIntent = null;
                    if ("MainActivity".equals(nextActivity)) {
                        nextIntent = new Intent(SplashActivity.this, MainActivity.class);
                        nextIntent.putExtra("user_id", userID);
                        nextIntent.putExtra("user_email", userEmail);
                        nextIntent.putExtra("user_account", userAccount);
                        nextIntent.putExtra("user_image", userImage);
                        nextIntent.putExtra("user_password", userPass);
                    } else if ("MenuActivity".equals(nextActivity)) {
                        nextIntent = new Intent(SplashActivity.this, MenuActivty.class);
                        nextIntent.putExtra("user_id", userID);
                        nextIntent.putExtra("user_email", userEmail);
                        nextIntent.putExtra("user_account", userAccount);
                        nextIntent.putExtra("user_image", userImage);
                        nextIntent.putExtra("user_password", userPass);
                    } else { // nextActivity = "acti3"
                        //nextIntent = new Intent(LoadingActivity.this, Acti3.class);
                    }
                    startActivity(nextIntent);
                    finish();
                } else {
                    Toast.makeText(SplashActivity.this, "Không có kết nối internet. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
                    Log.v("Internet Status", "No internet connection");
                    Intent nextIntent = new Intent(SplashActivity.this, MainActivity.class);
                    nextIntent.putExtra("user_id", userID);
                    nextIntent.putExtra("user_email", userEmail);
                    nextIntent.putExtra("user_account", userAccount);
                    nextIntent.putExtra("user_image", userImage);
                    nextIntent.putExtra("user_password", userPass);
                    startActivity(nextIntent);
                    finish();
                }
            }
        }, 3000);
    }

    private void displayRandomText() {
        String[] texts = {
                "Đây là sản phẩm đồ án tốt nghiệp của Trần Văn Thái",
                "Các đầu sách đăng tải không mang tính thương mại",
                "Sản phẩm thực hiện trên Android Studio và lưu trữ dữ liệu tại Oracle Apex",
                "Mọi thắc mắc xin liên hệ tranthai30102002@gmail.com"};

        Random random = new Random();
        String randomText = texts[random.nextInt(texts.length)];

        txtShowtext.setText(randomText);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}