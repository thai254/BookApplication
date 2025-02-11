package com.datn.flybookapplication.Dialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.datn.flybookapplication.Class.JavaMailAPI;
import com.datn.flybookapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

public class Dialog_register extends AppCompatActivity {
    EditText edtAccount, edtPass, edtPassAgain, edtCaptcha;
    Button btnRegister;
    ImageButton btnRefresh;
    TextView txtError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_register);

        Log.v("re1", "ok");
        edtAccount = findViewById(R.id.edt_account);
        edtPass = findViewById(R.id.edt_password);
        edtPassAgain = findViewById(R.id.edt_pass_again);
        edtCaptcha = findViewById(R.id.edt_captcha);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnRegister = findViewById(R.id.btnRegister);
        txtError = findViewById(R.id.txtError);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailSend = edtAccount.getText().toString().trim();
                Log.v("email send", emailSend);

                String password = edtPass.getText().toString().trim();
                String passwordAgain = edtPassAgain.getText().toString().trim();

                if (!password.equals(passwordAgain)) {
                    txtError.setText("Mật khẩu không trùng khớp, vui lòng nhập lại");
                    txtError.setVisibility(View.VISIBLE);
                    return;
                }

                CheckEmailExist(toUppercase(emailSend), new EmailCheckCallback() {
                    @Override
                    public void onResult(boolean exists) {
                        if (exists) {
                            Log.v("email check", "Email tồn tại: " + emailSend);
                            txtError.setText("Email này đã được sử dụng");
                            txtError.setVisibility(View.VISIBLE);
                        } else {
                            Log.v("email check", "Email không tồn tại: " + emailSend);
                            JavaMailAPI javaMailAPI = new JavaMailAPI(
                                    emailSend,
                                    Dialog_register.this,
                                    "tranthai312002@gmail.com",
                                    "evcf mkrt apya ihap",
                                    emailSend);

                            new Thread(javaMailAPI::sendMailWithCaptcha).start();
                            Log.v("email send", "CAPTCHA đã gửi tới: " + emailSend);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("email check", "Lỗi khi kiểm tra email: " + error);
                        Toast.makeText(Dialog_register.this, "Đã xảy ra lỗi: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String captcha = edtCaptcha.getText().toString().trim();
                String emailSend = edtAccount.getText().toString().trim();
                String password = edtPass.getText().toString().trim();
                String passwordAgain = edtPassAgain.getText().toString().trim();

                if(captcha.isEmpty()){
                    txtError.setText("Mã captcha không được để trống");
                    txtError.setVisibility(View.VISIBLE);
                    return;
                }

                if(captcha.length() != 6){
                    txtError.setText("Mã captcha phải đủ 6 kí tự");
                    txtError.setVisibility(View.VISIBLE);
                    return;
                }

                if (password.equals(passwordAgain)) {
                    CheckCaptchaCorrect(emailSend, captcha, passwordAgain);
                }
            }
        });
    }
    public interface EmailCheckCallback {
        void onResult(boolean exists);
        void onError(String error);
    }
    public void CheckEmailExist(String emailSend, EmailCheckCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = "https://apex.oracle.com/pls/apex/tranthai312/books/CheckEmailExist?gmail=" + emailSend;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            int count = jsonObject.getInt("count");

                            if (count == 1) {
                                callback.onResult(true);
                            } else {
                                callback.onResult(false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError("Lỗi parse JSON: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        callback.onError("Lỗi mạng: " + error.getMessage());
                    }
                });

        requestQueue.add(stringRequest);
    }

    public void CheckCaptchaCorrect(String email_send, String captcha_send, String u_password) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = "https://apex.oracle.com/pls/apex/tranthai312/books/CreateUser?email=" + toUppercase(email_send) + "&captcha=" + captcha_send;
        Log.v("url1 = ", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            int count = jsonObject.getInt("count");

                            if (count == 1) {
                                UpdateVerification(email_send, captcha_send);
                                UpdateUserFlybook(email_send, u_password);
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

    public void UpdateVerification(String user_email, String user_captcha) {
        String url = "https://apex.oracle.com/pls/apex/tranthai312/books/CreateUser?email="
                + toUppercase(user_email) + "&captcha=" + user_captcha;
        Log.v("url2 = ", url);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", user_email);
            jsonBody.put("captcha", user_captcha);
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

    public void UpdateUserFlybook(String user_email, String user_password) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = "https://apex.oracle.com/pls/apex/tranthai312/books/UpdateInforUser" +
                "?email=" + toUppercase(user_email) + "&book_account=" + user_email +
                "&book_password=" + user_password;
        Log.v("url3 = ", url);

        JSONObject postData = new JSONObject();
        try {
            postData.put("email", toUppercase(user_email));
            postData.put("book_account", user_email);
            postData.put("book_password", user_password);
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
                            txtError.setText("Đăng ký tài khoản thành công!");
                            txtError.setTextColor(ContextCompat.getColor(Dialog_register.this, R.color.menu_color));
                            txtError.setVisibility(View.VISIBLE);
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


    public String toUppercase(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        return input.toUpperCase();
    }


}