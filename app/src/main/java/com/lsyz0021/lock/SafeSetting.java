package com.lsyz0021.lock;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SafeSetting extends AppCompatActivity {

    private Switch mSwitch;
    private RelativeLayout mResetPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_setting);
        mSwitch = (Switch) findViewById(R.id.sw_change_toggle);
        mResetPwd = (RelativeLayout) findViewById(R.id.rl_change_reset_gesture_pwd);

        showSwitch(mSwitch);

        mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSwitch.isChecked()) {
                    checkPwd(new CheckCallBack() {
                        @Override
                        public void success() {
                            Intent intent = new Intent(SafeSetting.this, GestureLock.class);
                            startActivityForResult(intent, 1);
                        }

                        @Override
                        public void cancel() {
                            showSwitch(mSwitch);
                        }
                    });

                } else {
                    checkPwd(new CheckCallBack() {
                        @Override
                        public void success() {
                            SPUtils.remove(Constant.GESTURE_OPEN);
                        }

                        @Override
                        public void cancel() {
                            showSwitch(mSwitch);
                        }
                    });
                }
            }
        });
        mResetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkPwd(new CheckCallBack() {
                    @Override
                    public void success() {
                        Intent intent = new Intent(SafeSetting.this, GestureLock.class);
                        startActivity(intent);
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        });
    }

    /**
     * 根据设置情况显示switch按钮
     *
     * @param aSwitch
     */
    private void showSwitch(Switch aSwitch) {
        String pwd = SPUtils.getString(Constant.GESTURE_OPEN, "");
        if (TextUtils.isEmpty(pwd)) {
            aSwitch.setChecked(false);
            mResetPwd.setVisibility(View.INVISIBLE);
        } else {
            aSwitch.setChecked(true);
            mResetPwd.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 验证密码
     */
    private void checkPwd(final CheckCallBack callBack) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.alert_checkpwd, null);
        final TextView pwd = (TextView) view.findViewById(R.id.et_check_pwd);
        Button cancel = (Button) view.findViewById(R.id.btn_check_cancel);
        Button confirm = (Button) view.findViewById(R.id.btn_check_confirm);

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setView(view);
        alertDialog.show();

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (callBack != null) {
                    callBack.cancel();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = pwd.getText().toString();
                if (TextUtils.isEmpty(s)) {
                    Toast.makeText(SafeSetting.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean isOk = true;

                if (isOk) {
                    if (callBack != null) {
                        callBack.success();
                    }
                    Toast.makeText(SafeSetting.this, "密码验证通过", Toast.LENGTH_SHORT).show();
                } else {
//                    callBack.failure();
                }

                alertDialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (1 == requestCode) {
            showSwitch(mSwitch);
        }
    }

    interface CheckCallBack {
        void success();

        //        void failure();
        void cancel();
    }

}
