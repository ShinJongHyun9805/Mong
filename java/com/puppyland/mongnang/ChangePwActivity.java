package com.puppyland.mongnang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.puppyland.mongnang.widget.FButton;

public class ChangePwActivity extends AppCompatActivity {
    EditText ID;
    TextView textMessage;
    FButton btn_password;

    //변경 이메일
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pw);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ID = findViewById(R.id.input_email);
        textMessage = findViewById(R.id.textMessage);

        //변경 버튼
        btn_password = findViewById(R.id.password_btn2);
        btn_password.setButtonColor(getResources().getColor(R.color.fbutton_color_a));
        btn_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ID.getText().toString().equals("")) {
                    Toast.makeText(ChangePwActivity.this, "빈 칸은 곤란.", Toast.LENGTH_LONG).show();
                } else {
                    //DB 변경
                    //changePW(id, pw);

                    //비밀번호 재설정 이메일 보내기
                    auth.sendPasswordResetEmail(ID.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        textMessage.setVisibility(View.VISIBLE);
                                        textMessage.setText("이메일이 발송되었습니다\n메일 확인 후 비밀번호를 변경해 주세요");
                                        //Toast.makeText(ChangePwActivity.this, "이메일이 발송되었습니다\n 메일 확인 후 비밀번호를 변경해 주세요", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(ChangePwActivity.this, "다시 시도해주세요.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }

}