package com.sdr.hikvision8700.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.SurfaceView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.sdr.hikvision8700.R;
import com.sdr.hikvision8700.base.HK8700BaseActivity;
import com.sdr.hikvision8700.constant.HK8700Constant;
import com.sdr.hikvision8700.contract.HK8700PlayContract;
import com.sdr.hikvision8700.data.HK8700ItemControl;
import com.sdr.lib.rx.RxUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class HK8700ControlActivity extends HK8700BaseActivity implements HK8700PlayContract.View {


    public static final int RQ_OPEN_HKVIDEO_CONTROL_CODE = 213;
    private static final String CAMERAID = "CAMERAID";


    SurfaceView mSurfaceView;


    private View viewOperationView, viewControlView;
    private View viewTakePhoto, viewRecord, viewAudio, viewRemote;
    private CheckBox rbControlTakePhoto, rbControlRecord, rbControlAudio, rbControlRemote;
    private View viewBack;

    /**
     * 十二个 控制按钮
     */
    ImageView ivLeftTop;
    ImageView ivTop;
    ImageView ivRightTop;
    ImageView ivLeft;
    ImageView ivRight;
    ImageView ivLeftBottom;
    ImageView ivBottom;
    ImageView ivRightBottom;
    ImageView ivFar;
    ImageView ivNear;
    ImageView ivZoomIn;
    ImageView ivZoomOut;
    ImageView ivMiddle;


    private SubResourceNodeBean camera;
    private HK8700ItemControl itemControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hk8700_control);
        initIntent();
        initToolbar();
        initView();
        initListener();
    }

    private void initIntent() {
        Intent intent = getIntent();
        camera = (SubResourceNodeBean) intent.getSerializableExtra(CAMERAID);
        if (camera == null) finish();
    }

    private void initToolbar() {
        setTitle("预览视频");
        setDisplayHomeAsUpEnabled();

        mSurfaceView = findViewById(R.id.hk_video_control_surfaceview);
        viewOperationView = findViewById(R.id.hk_video_control_view_control_operation);
        viewControlView = findViewById(R.id.hk_video_control_view_control_view);


        viewTakePhoto = findViewById(R.id.hk_video_control_view_takephoto);
        viewRecord = findViewById(R.id.hk_video_control_view_record);
        viewAudio = findViewById(R.id.hk_video_control_view_audio);
        viewRemote = findViewById(R.id.hk_video_control_view_remote);
        rbControlTakePhoto = findViewById(R.id.hk_video_control_rb_takephoto);
        rbControlRecord = findViewById(R.id.hk_video_control_rb_record);
        rbControlAudio = findViewById(R.id.hk_video_control_rb_audio);
        rbControlRemote = findViewById(R.id.hk_video_control_rb_remote);


        ivLeftTop = findViewById(R.id.hk_video_control_iv_left_top);
        ivTop = findViewById(R.id.hk_video_control_iv_top);
        ivRightTop = findViewById(R.id.hk_video_control_iv_right_top);
        ivLeft = findViewById(R.id.hk_video_control_iv_left);
        ivRight = findViewById(R.id.hk_video_control_iv_right);
        ivLeftBottom = findViewById(R.id.hk_video_control_iv_left_bottom);
        ivBottom = findViewById(R.id.hk_video_control_iv_bottom);
        ivRightBottom = findViewById(R.id.hk_video_control_iv_right_bottom);
        ivFar = findViewById(R.id.hk_video_control_iv_far);
        ivNear = findViewById(R.id.hk_video_control_iv_near);
        ivZoomIn = findViewById(R.id.hk_video_control_iv_zoom_in);
        ivZoomOut = findViewById(R.id.hk_video_control_iv_zoom_out);
        ivMiddle = findViewById(R.id.hk_video_control_iv_middle);


        viewBack = findViewById(R.id.hk_video_control_view_back);
    }


    private void initView() {
        itemControl = new HK8700ItemControl(0, this);
        //mHKItemControl.startPlay(cameraId, mSurfaceView);
    }

    private void initListener() {
        viewTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RxPermissions(getActivity())
                        .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    itemControl.capture(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath(), UUID.randomUUID() + ".jpg");
                                }
                            }
                        });
            }
        });

        viewRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = rbControlRecord.isChecked();
                if (check) {
                    itemControl.stopRecord();
                } else {
                    new RxPermissions(getActivity())
                            .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                            .subscribe(new Consumer<Boolean>() {
                                @Override
                                public void accept(Boolean aBoolean) throws Exception {
                                    if (aBoolean) {
                                        itemControl.startRecord(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath(), UUID.randomUUID() + ".mp4");
                                    }
                                }
                            });
                }
            }
        });

        viewAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = rbControlAudio.isChecked();
                if (check) {
                    itemControl.stopAudio();
                } else {
                    itemControl.startAudio();
                }
            }
        });

        viewRemote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewOperationView.setVisibility(View.GONE);
                viewControlView.setVisibility(View.VISIBLE);
            }
        });


        ivLeftTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCtrlCmd(HK8700Constant.Control.LEFT_TOP);
            }
        });

        ivTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCtrlCmd(HK8700Constant.Control.TOP);
            }
        });

        ivRightTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCtrlCmd(HK8700Constant.Control.RIGHT_TOP);
            }
        });

        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCtrlCmd(HK8700Constant.Control.LEFT);
            }
        });

        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCtrlCmd(HK8700Constant.Control.RIGHT);
            }
        });

        ivLeftBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCtrlCmd(HK8700Constant.Control.LEFT_BOTTOM);
            }
        });

        ivBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCtrlCmd(HK8700Constant.Control.BOTTOM);
            }
        });

        ivRightBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCtrlCmd(HK8700Constant.Control.RIGHT_BOTTOM);
            }
        });

        ivFar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCtrlCmd(HK8700Constant.Control.FAR);
            }
        });

        ivNear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCtrlCmd(HK8700Constant.Control.NEAR);
            }
        });

        ivZoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCtrlCmd(HK8700Constant.Control.ZOOM_IN);
            }
        });

        ivZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCtrlCmd(HK8700Constant.Control.ZOOM_OUT);
            }
        });

        ivMiddle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemControl.stopControl();
            }
        });

        viewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewOperationView.setVisibility(View.VISIBLE);
                viewControlView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Observable.just(0)
                .flatMap(new Function<Integer, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(Integer integer) throws Exception {
                        itemControl.stopLiveSyn();
                        return observer -> {
                            observer.onNext(true);
                            observer.onComplete();
                        };
                    }
                })
                .compose(RxUtils.io_main())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        itemControl.startLive(mSurfaceView,camera);
                    }
                });
    }

    @Override
    protected void setNavigationOnClickListener() {
        new MaterialDialog.Builder(getContext())
                .title("提示")
                .content("退出预览视频？")
                .positiveText("退出")
                .negativeText("取消")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Observable.just(0)
                                .flatMap(new Function<Integer, ObservableSource<Boolean>>() {
                                    @Override
                                    public ObservableSource<Boolean> apply(Integer integer) throws Exception {
                                        itemControl.stopLiveSyn();
                                        return observer -> {
                                            observer.onNext(true);
                                            observer.onComplete();
                                        };
                                    }
                                }).compose(RxUtils.io_main())
                                .subscribe(ret -> {
                                    finish();
                                });
                    }
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        setNavigationOnClickListener();
    }

    // ———————————————————————回调—————————————————————————

    @Override
    public void onPlayMsg(int position, int code, String msg) {
        if (code == HK8700Constant.PlayLive.PLAY_LIVE_RTSP_SUCCESS) {
            // 取流成功
        } else if (code == HK8700Constant.PlayLive.PLAY_LIVE_STOP_SUCCESS) {
            // 停止成功
        } else if (code == HK8700Constant.PlayLive.PLAY_LIVE_FAILED || code == HK8700Constant.PlayLive.PLAY_LIVE_RTSP_FAIL) {
            // 播放失败
            showErrorMsg(msg);
        } else if (code == HK8700Constant.PlayLive.SEND_CTRL_CMD_SUCCESS) {
            // 指令执行成功
            showSuccessToast(msg);
        } else if (code == HK8700Constant.PlayLive.SEND_CTRL_CMD_FAILED) {
            // 指令执行失败
            showErrorToast(msg);
        } else if (code == HK8700Constant.PlayLive.CAPTURE_SUCCESS) {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(msg))));
            showSuccessToast(msg);
        } else if (code == HK8700Constant.PlayLive.CAPTURE_FAILED) {
            showErrorToast(msg);
        } else if (code == HK8700Constant.PlayLive.RECORD_START) {
            showNormalToast("开始录像");
            rbControlRecord.setChecked(true);
        } else if (code == HK8700Constant.PlayLive.RECORD_SUCCESS) {
            showSuccessToast(msg);
            rbControlRecord.setChecked(false);
        } else if (code == HK8700Constant.PlayLive.RECORD_FAILED) {
            showErrorToast(msg);
            rbControlRecord.setChecked(false);
        } else if (code == HK8700Constant.PlayLive.AUDIO_FAILED) {
            showErrorToast(msg);
            rbControlAudio.setChecked(false);
        } else if (code == HK8700Constant.PlayLive.AUDIO_SUCCESS) {
            showSuccessToast(msg);
            rbControlAudio.setChecked(true);
        } else if (code == HK8700Constant.PlayLive.AUDIO_CLOSE_SUCCESS) {
            showSuccessToast(msg);
            rbControlAudio.setChecked(false);
        }
    }



    // —————————————————————————私有—————————————————————————————

    /**
     * 发送指令
     *
     * @param cmd
     */
    private void sendCtrlCmd(int cmd) {
        if (itemControl.getCurrentStatus() == HK8700Constant.PlayStatus.LIVE_INIT) {
            showErrorToast("视频没有正在播放，无法控制");
            return;
        }
        itemControl.startControl(cmd);
    }


    /**
     * 开启此activity
     *
     * @param activity
     */
    public static void startHK8700ControlActivity(Activity activity, SubResourceNodeBean camera) {
        Intent intent = new Intent(activity, HK8700ControlActivity.class);
        intent.putExtra(CAMERAID, camera);
        activity.startActivityForResult(intent, RQ_OPEN_HKVIDEO_CONTROL_CODE);
    }
}
