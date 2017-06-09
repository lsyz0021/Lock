package com.lsyz0021.lock;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.lsyz0021.lock.tools.Constant;
import com.lsyz0021.lock.tools.SPUtils;

public class SafeSettingActivity extends AppCompatActivity {

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
                // 打开手势锁
                if (mSwitch.isChecked()) {
                    checkLoginPwd(new CheckCallBack() {
                        @Override
                        public void success() {
                            Intent intent = new Intent(SafeSettingActivity.this, GestureLockActivity.class);
                            startActivityForResult(intent, 1);
                        }

                        @Override
                        public void failure() {
                            showSwitch(mSwitch);
                        }

                        @Override
                        public void cancel() {
                            showSwitch(mSwitch);
                        }
                    });

                } else { // 关闭手势锁
                    checkLoginPwd(new CheckCallBack() {
                        @Override
                        public void success() {
                            SPUtils.remove(Constant.GESTURE_OPEN);
                            showSwitch(mSwitch);
                        }

                        @Override
                        public void failure() {
                            showSwitch(mSwitch);
                        }

                        @Override
                        public void cancel() {
                            showSwitch(mSwitch);
                        }
                    });
                }
            }
        });

        // 重置手势密码
        mResetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkLoginPwd(new CheckCallBack() {
                    @Override
                    public void success() {
                        Intent intent = new Intent(SafeSettingActivity.this, GestureLockActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void failure() {

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
     * 验证登陆密码
     */
    private void checkLoginPwd(final CheckCallBack callBack) {
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
                    Toast.makeText(SafeSettingActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                hideKeyboard(pwd);
                MyAsyncTask asyncTask = new MyAsyncTask(callBack);
                asyncTask.execute(s, null, null);

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

        void failure();

        void cancel();
    }

    /**
     * 隐藏输入法
     */
    public void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, String> {

        private CheckCallBack mCallBack;
        private ProgressDialog mDialog;

        MyAsyncTask(CheckCallBack callBack) {
            mCallBack = callBack;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(SafeSettingActivity.this);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: 2017/6/9
            for (int i = 1; i < 3; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            return params[0];
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mDialog.dismiss();
            if ("12345".equals(s)) {
                Toast.makeText(SafeSettingActivity.this, "密码验证通过", Toast.LENGTH_SHORT).show();
                if (mCallBack != null)
                    mCallBack.success();
            } else {
                Toast.makeText(SafeSettingActivity.this, "密码验证失败", Toast.LENGTH_SHORT).show();
                if (mCallBack != null)
                    mCallBack.failure();
            }
        }


    }
}
