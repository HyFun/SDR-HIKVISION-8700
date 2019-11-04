package com.app.hikvision8700;

import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sdr.hikvision8700.SDR_HIKVISION_8700_HTTPS;

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

                SDR_HIKVISION_8700_HTTPS.getInstance().start(MainActivity.this, ip, userName, passWord);
            }
        });
    }

    private void initView() {
        edtIp = findViewById(R.id.main_edt_ip);
        edtUserName = findViewById(R.id.main_edt_username);
        edtPassword = findViewById(R.id.main_edt_pass);
        btnLogin = findViewById(R.id.main_btn_login);

        edtIp.setText(getPlaceHolder("ip") + getPlaceHolder("port"));
        edtUserName.setText(getPlaceHolder("name"));
        edtPassword.setText(getPlaceHolder("password"));
    }


    public String getPlaceHolder(String key) {
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.getString(key);
        } catch (Exception e) {
            return "";
        }
    }

}
