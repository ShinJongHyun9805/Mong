package com.puppyland.mongnang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.puppyland.mongnang.DTO.AlarmDTO;
import com.puppyland.mongnang.DTO.MemberDTO;
import com.puppyland.mongnang.contract.MemberContract;
import com.puppyland.mongnang.presenter.MemberPresenter;
import com.puppyland.mongnang.retrofitService.NetRetrofit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlarmSetActivity extends AppCompatActivity implements MemberContract.View {

    TextView story , board , chat , timecheck;
    Switch storySwitch,boardSwitch,chatSwitch;
    private MemberContract.Presenter mpresenter;
    int storyAlarm = 1;
    int boardAlarm = 1;
    int chatAlarm = 1;

    String loginId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_set);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        SharedPreferences sharedPreferences3 = getSharedPreferences("Loginfile", MODE_PRIVATE);
        loginId = sharedPreferences3.getString("id", null);

        mpresenter = new MemberPresenter(this);

        storySwitch = findViewById(R.id.storySwitch);
        boardSwitch = findViewById(R.id.boardSwitch);
        chatSwitch = findViewById(R.id.chatSwitch);

        //처음에는 알람이 다 켜져있게 해야한다.
        storySwitch.setChecked(true);
        boardSwitch.setChecked(true);
        chatSwitch.setChecked(true);

        getAlarmInfo(loginId);

        //처음에 위에 setchecked 가 다 true 되있는건데 먼저 레트로핏해서 이게 알람이 풀려있는지 되어있는지
        //체크해서 setcheck 를 바꿀지 안바꿀지 알아야함.


        storySwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(storyAlarm==1){ //알람 되어있을때
                    //여기서 알람 푸는 동작 해야함
                    mpresenter.alarmUpdate(loginId , 0 , 0);
                    // whatAlarm 0은 스토리 1은 보드 , 2는 채팅 , 3은 일정관리
                    //onoff 0은 끈다는 소리 , 1은 켠다는 소리
                    storyAlarm =0;
                }
                else{ //알람 안되어있을때
                    //여기서 알람 넣는 동작 해줘야함
                    mpresenter.alarmUpdate(loginId , 0 , 1);
                    storyAlarm =1;
                }
            }
        });
        boardSwitch.setOnCheckedChangeListener(new colorSwitchListener());

        boardSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(boardAlarm==1){ //알람 되어있을때
                    //여기서 알람 푸는 동작 해야함
                    mpresenter.alarmUpdate(loginId , 1 , 0);
                    // whatAlarm 0은 스토리 1은 보드 , 2는 채팅 , 3은 일정관리
                    //onoff 0은 끈다는 소리 , 1은 켠다는 소리
                    boardAlarm =0;
                }
                else{ //알람 안되어있을때
                    //여기서 알람 넣는 동작 해줘야함
                    mpresenter.alarmUpdate(loginId , 1 , 1);
                    boardAlarm =1;
                }
            }
        });
        boardSwitch.setOnCheckedChangeListener(new colorSwitchListener());

        chatSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chatAlarm==1){ //알람 되어있을때
                    //여기서 알람 푸는 동작 해야함
                    mpresenter.alarmUpdate(loginId , 2 , 0);
                    // whatAlarm 0은 스토리 1은 보드 , 2는 채팅 , 3은 일정관리
                    //onoff 0은 끈다는 소리 , 1은 켠다는 소리
                    chatAlarm =0;
                }
                else{ //알람 안되어있을때
                    //여기서 알람 넣는 동작 해줘야함
                    mpresenter.alarmUpdate(loginId , 2 , 1);
                    chatAlarm =1;
                }
            }
        });
        chatSwitch.setOnCheckedChangeListener(new colorSwitchListener());



    }

    public void getAlarmInfo(String userid){
        AlarmDTO alarmDTO = new AlarmDTO();
        alarmDTO.setUserid(userid);

        Gson gson = new Gson();
        String objJson = gson.toJson(alarmDTO); // DTO 객체를 json 으로 변환

        //member 테이블에 알람관련 컬럼 있어서 member 객체로 받아옴
        Call<MemberDTO> getAlarmInfo = NetRetrofit.getInstance().getService().getAlarmInfo(objJson);
        getAlarmInfo.enqueue(new Callback<MemberDTO>() {
            @Override
            public void onResponse(Call<MemberDTO> call, Response<MemberDTO> response) {
                if (response.isSuccessful()) {
                    Log.d("res", "성공");
                    if(response.body().getStoryalram() ==0){ // db에서 봤을때 알람을 끄라고 했을때
                        //push 알람 flag 랑 같이 있는 거기서 보내기전에 if 문으로 플래그 체크하는데 그때 getAlarmInfo 로 알아오고
                        //거기서 알아온 알람플래그도 1이면 보내도록 조건 적어야함 스프링에서
                        storySwitch.setChecked(false);
                        storyAlarm =0;
                    }
                    if(response.body().getBoardalram() ==0){
                        boardSwitch.setChecked(false);
                        boardAlarm =0;
                    }
                    if(response.body().getChatalram() ==0){
                        chatSwitch.setChecked(false);
                        chatAlarm =0;
                    }
                }
            }

            @Override
            public void onFailure(Call<MemberDTO> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });
    }

    @Override
    public void showResult(String result) {

    }

    @Override
    public void goMainView() {

    }

    class colorSwitchListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        }
    }

}