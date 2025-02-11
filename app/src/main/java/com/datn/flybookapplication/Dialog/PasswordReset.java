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
import com.datn.flybookapplication.Class.JavaMailResetPassword;
import com.datn.flybookapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

public class PasswordReset extends AppCompatActivity {
    EditText edtAccount, edtCaptcha;
    ImageButton btnRefresh;
    Button btnReset;
    TextView txtError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        edtAccount = findViewById(R.id.edt_account);
        edtCaptcha = findViewById(R.id.edt_captcha);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnReset   = findViewById(R.id.btnReset);
        txtError   = findViewById(R.id.txtError);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailSend = edtAccount.getText().toString().trim();

                CheckEmailExist(toUppercase(emailSend), new EmailCheckCallback() {
                    @Override
                    public void onResult(boolean exists) {
                        if (exists) {
                            Log.v("email check", "Email tồn tại: " + emailSend);
                            JavaMailResetPassword javaMailResetPassword = new JavaMailResetPassword(
                                    emailSend,
                                    PasswordReset.this,
                                    "tranthai312002@gmail.com",
                                    "evcf mkrt apya ihap",
                                    emailSend);

                            new Thread(javaMailResetPassword::sendMailWithCaptcha).start();

                        } else {
                            Log.v("email check", "Email không tồn tại: " + emailSend);
                            txtError.setText("Email không chính xác!");
                            txtError.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("email check", "Lỗi khi kiểm tra email: " + error);
                        Toast.makeText(PasswordReset.this, "Đã xảy ra lỗi: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtAccount.getText().toString().trim();
                String captcha = edtCaptcha.getText().toString().trim();

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
                CheckCaptchaCorrect(email, captcha);
                txtError.setText("Mật khẩu mới của bạn là: " + captcha);
                txtError.setTextColor(ContextCompat.getColor(PasswordReset.this, R.color.menu_color));
                txtError.setVisibility(View.VISIBLE);
            }
        });
    }


    public interface EmailCheckCallback {
        void onResult(boolean exists);
        void onError(String error);
    }
    public void CheckEmailExist(String emailSend, PasswordReset.EmailCheckCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = "https://apex.oracle.com/pls/apex/tranthai312/books/CheckEmailExist?gmail=" + emailSend;
        Log.v("u1", url);
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

    public void CheckCaptchaCorrect(String email_send, String captcha_send) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = "https://apex.oracle.com/pls/apex/tranthai312/books/ApplyPassword?email="
                + toUppercase(email_send) + "&captcha=" + captcha_send;
        Log.v("url1 = ", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            int count = jsonObject.getInt("count");

                            if (count == 1) {
                                Log.v("reset", "ok");
                                UpdatePassword(email_send, captcha_send);
                                UpdateUserPassword(email_send, captcha_send);
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

    public void UpdatePassword(String user_email, String user_captcha) {
        String url = "https://apex.oracle.com/pls/apex/tranthai312/books/ResetPassword?email="
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

    public void UpdateUserPassword(String user_email, String user_password) {
        String url = "https://apex.oracle.com/pls/apex/tranthai312/books/ApplyPassword" +
                "?email=" + toUppercase(user_email) + "&pass=" + user_password;
        Log.v("url3 = ", url);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", user_email);
            jsonBody.put("pass", user_password);
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
    public String toUppercase(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        return input.toUpperCase();
    }
}