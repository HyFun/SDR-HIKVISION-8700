# 海康威视 8700 平台视频

## 使用

application初始化

```java
SDR_HIKVISION_8700_HTTPS.getInstance().init(this, BuildConfig.DEBUG);
```

打开视频页面
```java
String ip = edtIp.getText().toString().trim();
String userName = edtUserName.getText().toString().trim();
String passWord = edtPassword.getText().toString().trim();

SDR_HIKVISION_8700_HTTPS.getInstance().start(MainActivity.this, ip, userName, passWord);
```