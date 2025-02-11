package com.datn.flybookapplication.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.datn.flybookapplication.Class.UserClass;
import com.datn.flybookapplication.Dialog.Dialog_register;
import com.datn.flybookapplication.Dialog.PasswordReset;
import com.datn.flybookapplication.Fragment.BookDetailFragment;
import com.datn.flybookapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    EditText edtAccount, edtPassword;
    ImageButton imgLogin;
    Button btnRegister, btnForgot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtAccount = findViewById(R.id.edtAccount);
        edtPassword = findViewById(R.id.edtPassword);
        imgLogin = findViewById(R.id.btnLogin);
        btnForgot = findViewById(R.id.txtForgotPassword);
        btnRegister = findViewById(R.id.txtRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Dialog_register.class);
                startActivity(intent);
            }
        });

        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PasswordReset.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Dialog_register.class);
                startActivity(intent);
            }
        });


        imgLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String u_email = edtAccount.getText().toString().trim();
                String u_password = edtPassword.getText().toString().trim();
                //Log.v("id", u_email + " - " + u_password);
                if
                (!u_password.isEmpty() && !u_email.isEmpty())
                {
                    getInforUserFromEmail(toUppercase(u_email), u_password);
                }
                else if(u_password.isEmpty() || u_password.isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập Email và mật khẩu", Toast.LENGTH_LONG).show();
                }
            }
        });
        
    }

    public void getInforUserFromEmail(String u_email, String u_password) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = "https://apex.oracle.com/pls/apex/tranthai312/books/GetUserByEmail?EMAIL=" + u_email + "&PASS=" + u_password;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            // Lấy giá trị của "count" từ phản hồi
                            int count = jsonObject.getInt("count");

                            // Kiểm tra nếu count == 1
                            if (count == 1) {
                                JSONArray jsonArray = jsonObject.getJSONArray("items");

                                if (jsonArray.length() > 0) {
                                    JSONObject bookObject = jsonArray.getJSONObject(0);
                                    String u_ID = bookObject.getString("user_id");
                                    String u_Account = bookObject.getString("book_account");
                                    String u_Password = bookObject.getString("book_password");
                                    String u_gmail = bookObject.getString("email");
                                    String u_role = bookObject.getString("book_role");
                                    String u_status = bookObject.getString("status");
                                    String u_created = bookObject.getString("created_at");
                                    String u_updated = bookObject.getString("updated_at");
                                    String u_imgBase64 = bookObject.getString("user_img");

                                    Log.v("User infor: ", u_ID + " - " + u_Account + " - "+ u_Password);


                                    if("Y".equals(u_status)) {
                                        UserClass user = new UserClass(u_ID, u_Account, u_Password, u_gmail,
                                                u_role, u_status, u_created, u_updated,
                                                u_imgBase64);

                                        Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                                        intent.putExtra("nextActivity", "MenuActivity");
                                        intent.putExtra("user_id", u_ID);
                                        intent.putExtra("user_email", u_gmail);
                                        intent.putExtra("user_account", u_Account);
                                        intent.putExtra("user_image", u_imgBase64);
                                        intent.putExtra("user_password", u_Password);
                                        startActivity(intent);
                                        finish();
                                    }

                                } else {
                                    Log.v("Không tìm thấy thông tin user", "ok");
                                }
                            } else {
                                Log.v("Thông tin tài khoản hoặc mật khẩu không chính xác", "ok");
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
                });

        requestQueue.add(stringRequest);
    }

    public String toUppercase(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        return input.toUpperCase();
    }
}