package com.puppyland.mongnang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.puppyland.mongnang.fragment.DogInfoFragment;
import com.puppyland.mongnang.fragment.MyInfoFragment;

public class InformationActivity extends AppCompatActivity {

    private MyInfoFragment myInfoFragment;
    private DogInfoFragment dogInfoFragment;

    private Button btn_myinfo, btn_doginfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        myInfoFragment = new MyInfoFragment();
        dogInfoFragment = new DogInfoFragment();

        Intent getIntent = getIntent();
        String id = getIntent.getStringExtra("id");

        //번들객체 생성 및 데이터 전달
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        myInfoFragment.setArguments(bundle);
        dogInfoFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, myInfoFragment).commit();

        btn_myinfo = findViewById(R.id.btn_myinfo);
        btn_myinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, myInfoFragment).commit();
            }
        });

        btn_doginfo = findViewById(R.id.btn_doginfo);
        btn_doginfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, dogInfoFragment).commit();
            }
        });
    }
}