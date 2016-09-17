package com.zzq.screenlock.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zzq.screenlock.R;
import com.zzq.screenlock.view.GestureLock;

import java.util.List;


public class SettingActivity extends ActionBarActivity {

    List<Integer> passList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        final GestureLock lock = (GestureLock) findViewById(R.id.LockView);
        Button btn_reset = (Button) findViewById(R.id.btn_reset);
        Button btn_save = (Button) findViewById(R.id.btn_save);

        lock.setOnDrawFinishedListener(new GestureLock.OnDrawFinishedListener() {
            @Override
            public boolean OnDrawFinished(List<Integer> passList) {
                if (passList.size() < 3) {
                    Toast.makeText(SettingActivity.this, "密码不能少于3个点", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    SettingActivity.this.passList = passList;
                    return true;
                }
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lock.resetPoints();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passList != null) {
                    StringBuilder sb = new StringBuilder();
                    for (Integer i : passList) {
                        sb.append(i);
                    }
                    SharedPreferences sp = SettingActivity.this.getSharedPreferences("password", SettingActivity.this.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("password", sb.toString());
                    editor.commit();

                    Toast.makeText(SettingActivity.this, "保存完成", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
