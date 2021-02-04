package com.puppyland.mongnang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.puppyland.mongnang.contract.MemberContract;
import com.puppyland.mongnang.presenter.MemberPresenter;
import com.puppyland.mongnang.widget.FButton;

public class MainActivity3 extends AppCompatActivity implements  MemberContract.View{

    MemberContract.Presenter memberpresenter;
    FButton fButton , fButton2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);
        memberpresenter =new MemberPresenter(this);
        fButton = findViewById(R.id.memberLogin);
        fButton.setButtonColor(getResources().getColor(R.color.fbutton_color_a));
        fButton2 = findViewById(R.id.memberJoin);
        fButton2.setButtonColor(getResources().getColor(R.color.fbutton_color_a));
        goJoin();
        goLogin();

    }

    private void goJoin(){
        // 회원가입 버튼 클릭시 가입 요청
        findViewById(R.id.memberJoin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext() , JoinActivity.class);
                startActivity(intent);
            }
        });
    }

    private void goLogin(){
        findViewById(R.id.memberLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext() , LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    // view 함수들 구현
    @Override
    public void goMainView(){//join이 되고나면 메인으로 보내줘야하니까

    }
    @Override
    public  void showResult(String result){

    }


    @Override
    public void onBackPressed() {
        dialog_native_ad dialog = new dialog_native_ad(this);
        dialog.show();
    }
}