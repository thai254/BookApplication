package com.datn.flybookapplication.Class;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMailResetPassword {
    private String senderEmail;
    private String senderPassword;
    private String recipientEmail;
    private Context context;
    private Intent intent;
    private String user_email;

    public JavaMailResetPassword(String user_email, Context context, String senderEmail, String senderPassword, String recipientEmail) {
        this.senderEmail = senderEmail;
        this.senderPassword = senderPassword;
        this.recipientEmail = recipientEmail;
        this.context = context;
        this.user_email = user_email;
    }
    public String generateCaptcha() {
        Random random = new Random();
        int captcha = 100000 + random.nextInt(900000);
        return String.valueOf(captcha);
    }
    public void sendMailWithCaptcha() {
        // Tạo mã CAPTCHA
        String captchaCode = generateCaptcha();
        Log.v("emailreset1", "ok");
        // Thiết lập cấu hình SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // SMTP server của Gmail
        props.put("mail.smtp.port", "587");           // Cổng SMTP
        props.put("mail.smtp.auth", "true");          // Yêu cầu xác thực
        props.put("mail.smtp.starttls.enable", "true"); // Bảo mật STARTTLS
        Log.v("emailreset2", "ok");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            // Tạo nội dung email
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Xác thực thay đổi mật khẩu của tài khoản - Mã CAPTCHA");
            message.setText("Xin chào,\n\nMã xác thực (CAPTCHA) thay đổi mật khẩu của bạn là: " + captchaCode +
                    "\n\nVui lòng nhập mã này trong ứng dụng để hoàn tất quá trình xác thực." +
                    "\n\nCảm ơn bạn đã sử dụng dịch vụ của chúng tôi!");

            // Gửi email
            Transport.send(message);
            Log.v("emailreset3", "ok");
            System.out.println("Email đã được gửi thành công với mã CAPTCHA: " + captchaCode);
            SendInforAccountReset(user_email, captchaCode);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    public void SendInforAccountReset(String user_email, String user_captcha_reset) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        // URL API để gửi thông tin
        String url = "https://apex.oracle.com/pls/apex/tranthai312/books/ResetPassword?email"
                + toUppercase(user_email) + "&reset_code=" + user_captcha_reset;

        JSONObject postData = new JSONObject();
        try {
            postData.put("email", toUppercase(user_email));
            postData.put("reset_code", user_captcha_reset);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("API Response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API Error", error.toString());
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
