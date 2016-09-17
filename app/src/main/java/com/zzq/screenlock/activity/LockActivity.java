package com.zzq.screenlock.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.zzq.screenlock.R;
import com.zzq.screenlock.view.GestureLock;

import java.util.List;


public class LockActivity extends Activity {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        SharedPreferences sp = getSharedPreferences("password", this.MODE_PRIVATE);
        final String password = sp.getString("password", "");

        GestureLock lock = (GestureLock) findViewById(R.id.LockView);
        lock.setOnDrawFinishedListener(new GestureLock.OnDrawFinishedListener() {
            @Override
            public boolean OnDrawFinished(List<Integer> passList) {
                StringBuilder sb = new StringBuilder();
                for (Integer i : passList) {
                    sb.append(i);
                }
                if (sb.toString().equals(password)) {
                    Toast.makeText(LockActivity.this, "正确", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    Toast.makeText(LockActivity.this, "错误", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        });
    }

}
