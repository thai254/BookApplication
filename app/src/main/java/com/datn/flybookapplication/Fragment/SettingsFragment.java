package com.datn.flybookapplication.Fragment;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.datn.flybookapplication.Dialog.PasswordReset;
import com.datn.flybookapplication.R;

import org.json.JSONObject;

public class SettingsFragment extends Fragment {
    private String selectedFont, selectedVoice, selectedSpeed;
    Dialog dialogPass;
    String userId, userPass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Ánh xạ các view
        SeekBar seekBar = view.findViewById(R.id.seekbar_slider);
        Spinner fontSpinner = view.findViewById(R.id.spinner_options);
        Spinner voiceSpinner = view.findViewById(R.id.spinner_options_voice);
        Spinner speedSpinner = view.findViewById(R.id.spinner_options_speed);
        TextView seekBarValue = view.findViewById(R.id.tv_seekbar_value);
        Button btnSave = view.findViewById(R.id.buttonSave);
        Button btnChange = view.findViewById(R.id.btnChangePassword);

        dialogPass = new Dialog(getContext());
        dialogPass.setContentView(R.layout.dialog_change_password);
        dialogPass.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogPass.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogPass.setCancelable(false);

        Button btnConfirm = dialogPass.findViewById(R.id.btnConfirm);
        Button btnCancel = dialogPass.findViewById(R.id.btnCancel);
        EditText edtCurPass = dialogPass.findViewById(R.id.edtOldPass);
        EditText edtNewPass = dialogPass.findViewById(R.id.edtNewPass);
        TextView txtStatus = dialogPass.findViewById(R.id.txtStatus);

        Bundle bundle = getArguments();
        if (bundle != null) {
            userId = bundle.getString("user_id");
            userPass = bundle.getString("user_pass");
            Log.d("SF", "User ID: " + userId + "- userpass - " +userPass);
        }

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPass.show();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPass.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtCpass = edtCurPass.getText().toString().trim();
                String txtNpass = edtNewPass.getText().toString().trim();

                if(txtCpass.isEmpty()){
                    txtStatus.setText("Hãy điền mật khẩu cũ");
                    txtStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                    txtStatus.setVisibility(View.VISIBLE);
                    return;
                }

                if(!txtCpass.equals(userPass)){
                    txtStatus.setText("Mật khẩu cũ không chính xác");
                    txtStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                    txtStatus.setVisibility(View.VISIBLE);
                    return;
                }

                if(txtNpass.isEmpty()){
                    txtStatus.setText("Hãy điền mật khẩu mới");
                    txtStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                    txtStatus.setVisibility(View.VISIBLE);
                    return;
                }

                if(txtNpass.length() < 5){
                    txtStatus.setText("Mật khẩu yêu cầu từ  5-18 kí tự");
                    txtStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                    txtStatus.setVisibility(View.VISIBLE);
                    return;
                }

                if(txtNpass.length() > 18){
                    txtStatus.setText("Mật khẩu không vượt quá 18 kí tự");
                    txtStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                    txtStatus.setVisibility(View.VISIBLE);
                    return;
                }

                if(txtNpass == txtCpass){
                    txtStatus.setText("Mật khẩu mới không trùng với mật khẩu cũ");
                    txtStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                    txtStatus.setVisibility(View.VISIBLE);
                    return;
                }

                UpdatePassword(userId, txtCpass, txtNpass);
                txtStatus.setText("Thay đổi mật khẩu thành công");
                txtStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.lavender));
                txtStatus.setVisibility(View.VISIBLE);
                userPass = txtNpass;
            }
        });

        // Khởi tạo SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppSettings", 0);

        // Lấy giá trị đã lưu từ SharedPreferences
        selectedFont = sharedPreferences.getString("SelectedFont", "default1.ttf"); // Font mặc định
        selectedVoice = sharedPreferences.getString("SelectedVoice", "voicedefault"); // Voice mặc định
        selectedSpeed = sharedPreferences.getString("SelectedSpeed", "speeddefault");
        int fontSize = sharedPreferences.getInt("FontSize", 18); // Kích thước mặc định
        int fontSpinnerPosition = sharedPreferences.getInt("FontSpinnerPosition", 0); // Vị trí Spinner mặc định
        int voiceSpinnerPosition = sharedPreferences.getInt("VoiceSpinnerPosition", 0); // Vị trí Spinner mặc định
        int speedSpinnerPosition = sharedPreferences.getInt("SpeedSpinnerPosition", 0);

        // Khôi phục trạng thái của SeekBar và hiển thị giá trị
        seekBar.setProgress(fontSize);
        seekBarValue.setText("Cỡ chữ văn bản: " + fontSize + "px");

        // Khôi phục trạng thái của Spinner
        fontSpinner.setSelection(fontSpinnerPosition);
        voiceSpinner.setSelection(voiceSpinnerPosition);
        speedSpinner.setSelection(speedSpinnerPosition);

        voiceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selectedVoice = "vi-VN-language";
                        break;
                    case 1:
                        selectedVoice = "vi-vn-x-gft-network";
                        break;
                    case 2:
                        selectedVoice = "vi-vn-x-vic-network";
                        break;
                    case 3:
                        selectedVoice = "vi-vn-x-vid-network";
                        break;
                    case 4:
                        selectedVoice = "vi-vn-x-vie-network";
                        break;
                    case 5:
                        selectedVoice = "vi-vn-x-vif-network";
                        break;
                    case 6:
                        selectedVoice = "vi-vn-x-vid-local";
                        break;
                    case 7:
                        selectedVoice = "vi-vn-x-vif-local";
                        break;
                    case 8:
                        selectedVoice = "vi-vn-x-vie-local";
                        break;
                    case 9:
                        selectedVoice = "vi-vn-x-gft-local";
                        break;
                    case 10:
                        selectedVoice = "vi-vn-x-vic-local";
                        break;
                }

                // Lưu lại vị trí voiceSpinner
                sharedPreferences.edit().putInt("VoiceSpinnerPosition", position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        fontSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selectedFont = "blahblahuciciel_regular.ttf";
                        break;
                    case 1:
                        selectedFont = "fontspagetti.ttf";
                        break;
                    case 2:
                        selectedFont = "quicksand_bold.ttf";
                        break;
                    case 3:
                        selectedFont = "ccspaghettiwesternsans.ttf";
                        break;
                }

                // Lưu lại vị trí fontSpinner
                sharedPreferences.edit().putInt("FontSpinnerPosition", position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        speedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSpeed = "1.0";
                switch (position) {
                    case 0:
                        selectedSpeed = "0.5";
                        break;
                    case 1:
                        selectedSpeed = "0.75";
                        break;
                    case 2:
                        selectedSpeed = "1.0";
                        break;
                    case 3:
                        selectedSpeed = "1.5";
                        break;
                    case 4:
                        selectedSpeed = "2.0";
                        break;
                }

                sharedPreferences.edit()
                        .putInt("SpeedSpinnerPosition", position)
                        .putString("SelectedSpeed", selectedSpeed) // Lưu giá trị selectedSpeed
                        .apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarValue.setText("Cỡ chữ văn bản: " + progress + "px");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnSave.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("SelectedFont", selectedFont);
            editor.putString("SelectedVoice", selectedVoice);
            editor.putInt("FontSize", seekBar.getProgress());
            editor.apply();

            Toast.makeText(getContext(), "Thay đổi thành công!", Toast.LENGTH_LONG).show();
        });


        return view;
    }

    public void UpdatePassword(String user_id, String user_pw, String user_npw) {
        String url = "https://apex.oracle.com/pls/apex/tranthai312/books/ChangePassword?user_id=" +
                user_id +"&user_pw=" + user_pw + "&user_npw=" + user_npw;
        Log.v("url2 = ", url);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", user_id);
            jsonBody.put("user_pw", user_pw);
            jsonBody.put("user_npw", user_npw);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(putRequest);
    }
}
