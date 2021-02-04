package com.puppyland.mongnang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.puppyland.mongnang.widget.FButton;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TermsActivity extends AppCompatActivity {

    TextView policy1;
    TextView policy2;
    TextView policy3;
    TextView policy4;

    ScrollView rootScroll;
    ScrollView scrollView1;
    ScrollView scrollView2;
    ScrollView scrollView3;
    ScrollView scrollView4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        CheckBox checkBox1 = findViewById(R.id.check1);
        CheckBox checkBox2 = findViewById(R.id.check2);
        CheckBox checkBox3 = findViewById(R.id.check3);
        CheckBox checkBox4 = findViewById(R.id.check4);
        CheckBox checkBox5 = findViewById(R.id.check5);
        CheckBox checkBox6 = findViewById(R.id.check6);


        policy1 = (TextView) findViewById(R.id.policy1);
        policy2 = findViewById(R.id.policy2);
        policy3 = findViewById(R.id.policy3);
        policy4 = findViewById(R.id.policy4);

        rootScroll= findViewById(R.id.rootScroll);
        scrollView1 =findViewById(R.id.scroll1);
        scrollView2 =findViewById(R.id.scroll2);
        scrollView3 =findViewById(R.id.scroll3);
        scrollView4 =findViewById(R.id.scroll4);


        scrollView1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP)
                    rootScroll.requestDisallowInterceptTouchEvent(false);
                else rootScroll.requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });
        scrollView2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP)
                    rootScroll.requestDisallowInterceptTouchEvent(false);
                else rootScroll.requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });
        scrollView3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP)
                    rootScroll.requestDisallowInterceptTouchEvent(false);
                else rootScroll.requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });
        scrollView4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP)
                    rootScroll.requestDisallowInterceptTouchEvent(false);
                else rootScroll.requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });

        loadPolicy(); // 약관 로드하기

        checkBox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkBox1.isChecked()){
                    checkBox6.setChecked(false);
                }
            }
        });
        checkBox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkBox2.isChecked()) {
                    checkBox6.setChecked(false);
                }
            }
        });
        checkBox3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkBox3.isChecked()) {
                    checkBox6.setChecked(false);
                }

            }
        });
        checkBox4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkBox4.isChecked()) {
                    checkBox6.setChecked(false);
                }

            }
        });
        checkBox5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkBox5.isChecked()) {
                    checkBox6.setChecked(false);
                }
            }
        });


        checkBox6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox6.isChecked()){ // 모두 동의 누르면 다 체크되게 만들어버림
                    checkBox1.setChecked(true);
                    checkBox2.setChecked(true);
                    checkBox3.setChecked(true);
                    checkBox4.setChecked(true);
                    checkBox5.setChecked(true);
                }else if(!checkBox6.isChecked()){
                    checkBox1.setChecked(false);
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(false);
                    checkBox4.setChecked(false);
                    checkBox5.setChecked(false);
                }
            }
        });

        FButton fButton = findViewById(R.id.nextbutton);
        fButton.setButtonColor(getResources().getColor(R.color.fbutton_color_a));
        fButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((checkBox1.isChecked() && checkBox2.isChecked()) && checkBox3.isChecked() && checkBox4.isChecked() && checkBox5.isChecked()) { // 다 동의 했을때
                    // TODO : 체크 됐을때
                    Intent intent = new Intent(TermsActivity.this , JoinActivity.class);
                    startActivity(intent);
                }
                else {
                    // TODO : 체크 안됐을때
                    Toast.makeText(TermsActivity.this ,"약관에 동의 해주세요." , Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void loadPolicy() {
        try {
            String temppolicy1 = readFromAssets("policy1.txt");
            policy1.setText(Html.fromHtml(temppolicy1));

            String temppolicy2 = readFromAssets("policy2.txt");
            policy2.setText(Html.fromHtml(temppolicy2));

            String temppolicy3 = readFromAssets("policy3.txt");
            policy3.setText(Html.fromHtml(temppolicy3));

            String temppolicy4 = readFromAssets("policy4.txt");
            policy4.setText(Html.fromHtml(temppolicy4));

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private String readFromAssets(String filename) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open(filename)));

        StringBuilder sb = new StringBuilder();
        String line = reader.readLine();
        while(line != null) {
            sb.append(line);
            line = reader.readLine();
        }
        reader.close();
        return sb.toString();
    }


}