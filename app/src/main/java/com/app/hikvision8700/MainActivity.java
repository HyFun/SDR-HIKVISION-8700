package com.app.hikvision8700;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sdr.hikvision8700.HIKVISION8700;

public class MainActivity extends AppCompatActivity {

    private EditText edtIp, edtUserName, edtPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = edtIp.getText().toString().trim();
                String userName = edtUserName.getText().toString().trim();
                String passWord = edtPassword.getText().toString().trim();

                HIKVISION8700.getInstance().start(MainActivity.this, ip, userName, passWord);
            }
        });
    }

    private void initView() {
        edtIp = findViewById(R.id.main_edt_ip);
        edtUserName = findViewById(R.id.main_edt_username);
        edtPassword = findViewById(R.id.main_edt_pass);
        btnLogin = findViewById(R.id.main_btn_login);
    }
}
