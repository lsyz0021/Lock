package com.lsyz0021.lock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.lsyz0021.lock.tools.Constant;
import com.lsyz0021.lock.tools.MD5Utils;
import com.lsyz0021.lock.tools.SPUtils;

import java.util.List;

public class GestureLockActivity extends AppCompatActivity {

    private TextView mTvSkip;
    private TextView mTvTips;
    private PatternLockView mPatternLockView;
    private int count = 0;
    private String tempPwd1 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guidelock);

        mTvSkip = (TextView) findViewById(R.id.tv_lock_skip);
        mTvTips = (TextView) findViewById(R.id.tv_lock_tips);
        mPatternLockView = (PatternLockView) findViewById(R.id.pattern_lock_view);
        mPatternLockView.setInStealthMode(false);
        mPatternLockView.setTactileFeedbackEnabled(true);
        mPatternLockView.setInputEnabled(true);

        mTvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mPatternLockView.addPatternLockListener(new MyPatternLockViewListener());
    }

    private class MyPatternLockViewListener implements PatternLockViewListener {
        @Override
        public void onStarted() {
            mTvTips.setText("请绘制解锁图案");
        }

        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {

        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {

            String pwd = PatternLockUtils.patternToString(mPatternLockView, pattern);
            if (pwd.isEmpty() || pwd.length() < 4) {
                count = 0;
                Toast.makeText(GestureLockActivity.this, "请连接至少四个点", Toast.LENGTH_SHORT).show();
//                mPatternLockView.clearPattern();
                return;
            }
            count++;
            if (count == 1) {
                tempPwd1 = pwd;
                mTvTips.setText("请再次绘制解锁图案");
            }
            if (count == 2) {
                count = 0;
                if (!tempPwd1.equals(pwd)) {
                    mTvTips.setText("两次绘制的图案不一致，请重新设置");
                    Toast.makeText(GestureLockActivity.this, "两次绘制的图案不一致，请重新设置", Toast.LENGTH_SHORT).show();
                } else {
                    tempPwd1 = "";
                    String md5Sting = MD5Utils.getPwd(pwd);
                    Toast.makeText(GestureLockActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                    SPUtils.putString(Constant.GESTURE_OPEN, md5Sting);
                    mPatternLockView.clearPattern();
                    finish();
                }
            }
            mPatternLockView.clearPattern();
        }

        @Override
        public void onCleared() {
        }
    }
}



