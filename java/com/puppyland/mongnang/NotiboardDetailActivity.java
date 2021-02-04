package com.puppyland.mongnang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class NotiboardDetailActivity extends AppCompatActivity {

    TextView notiboardTitle , notiboardDate , notiboardContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notiboard_detail);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        notiboardTitle = findViewById(R.id.notiboardTitle);
        notiboardDate = findViewById(R.id.notiboardDate);
        notiboardContent = findViewById(R.id.notiboardContent);

        Intent intent = getIntent();
        notiboardTitle.setText(intent.getExtras().getString("title"));
        notiboardDate.setText(intent.getExtras().getString("date"));
        notiboardContent.setText(intent.getExtras().getString("content"));
    }
}