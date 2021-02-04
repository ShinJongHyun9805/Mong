package com.puppyland.mongnang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.puppyland.mongnang.Chatcommon.Util9;
import com.puppyland.mongnang.DTO.DeviceIdDTO;
import com.puppyland.mongnang.DTO.MemberDTO;
import com.puppyland.mongnang.contract.EmailCertificationContract;
import com.puppyland.mongnang.contract.MemberContract;
import com.puppyland.mongnang.presenter.EmailPresenter;
import com.puppyland.mongnang.presenter.MemberPresenter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.puppyland.mongnang.widget.FButton;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements MemberContract.View, EmailCertificationContract.View {

    MemberContract.Presenter memberpresenter;
    EmailCertificationContract.Presenter emailCertificationContract;
    String id, password;
    FButton btnResult;
    TextView tx_changepw;
    SharedPreferences sharedPreferences;
    String deviceId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        memberpresenter = new MemberPresenter(this);
        emailCertificationContract = new EmailPresenter(this);

        SharedPreferences sharedPreferences = getSharedPreferences("DeviceIdFile",MODE_PRIVATE);
        deviceId= sharedPreferences.getString("deviceId" , null);

        //로그인 함수
        initListener();

        tx_changepw = findViewById(R.id.tx_changepw);
        tx_changepw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangePwActivity.class);
                startActivity(intent);
            }
        });

        LottieAnimationView animationView = (LottieAnimationView) findViewById(R.id.Lottie_dog);
        animationView.setAnimation("lf30_editor_gdrput47.json");
        animationView.playAnimation();
        animationView.setRotation(180);
    }

    private void initListener() {
        btnResult = findViewById(R.id.btnResult);
        btnResult.setButtonColor(getResources().getColor(R.color.fbutton_color_a));
        btnResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = ((EditText) findViewById(R.id.editInputA)).getText().toString();
                password = ((EditText) findViewById(R.id.editInputB)).getText().toString();

                if (id.equals("") || password.equals("")) {
                    Toast.makeText(LoginActivity.this, "공백을 채워주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences sharedPreferences = getSharedPreferences("Loginfile", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("id", id);


                    //SharedPreferences sharedPreferences12 = getSharedPreferences("nickname", MODE_PRIVATE);
                   // String nickname = sharedPreferences12.getString("nickname", "");

                    editor.commit();


                    //여기다가 로그인할때 닉네임 가져오는 레트로핏 만들고 그걸 프리팹에 넣는 작업함
                    MemberDTO memberDTO = new MemberDTO();
                    memberDTO.setUserId(id);

                    Gson gson = new Gson();
                    String objJson = gson.toJson(memberDTO);

                    Call<MemberDTO> getMynickname = NetRetrofit.getInstance().getService().getMynickname(objJson);
                    getMynickname.enqueue(new Callback<MemberDTO>() {
                        @Override
                        public void onResponse(Call<MemberDTO> call, Response<MemberDTO> response) {
                            //별명 저장
                            String nickedname = response.body().getNickname();
                            SharedPreferences sharedPreferences2 = getSharedPreferences("nickname", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences2.edit();
                            editor.putString("nickname", nickedname);
                            editor.commit();
                        }

                        @Override
                        public void onFailure(Call<MemberDTO> call, Throwable t) {
                            Log.d("###", t.getMessage());
                        }
                    });

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(id, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                //로그인을 성공했을때 디바이스 아이디 확인하고 달라졌으면 변경시켜야함

                                getDeviceId(id);
                                Intent intent = new Intent(LoginActivity.this, MainActivity2.class);
                                intent.putExtra("id", id);
                                //intent.putExtra("nickname" , nickname);
                                startActivity(intent);
                                finish();
                            } else {
                                Util9.showMessage(getApplicationContext(), task.getException().getMessage());
                                Toast.makeText(LoginActivity.this, "로그인에 실패했습니다. \n아이디 및 패스워드를 확인해 주세요", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        sharedPreferences = getSharedPreferences("Loginfile", MODE_PRIVATE);
    }

    private void getDeviceId(String userid) { // 이 함수를 가져다 쓰기만하면 3개를 다 돌려막기 할 수 있음.
        DeviceIdDTO deviceIdDTO = new DeviceIdDTO();
        deviceIdDTO.setId(userid);

        Gson gson = new Gson();
        String objJson = gson.toJson(deviceIdDTO);

        Call<DeviceIdDTO> getDeviceId = NetRetrofit.getInstance().getService().getDeviceId(objJson);
        getDeviceId.enqueue(new retrofit2.Callback<DeviceIdDTO>() {
            @Override
            public void onResponse(Call<DeviceIdDTO> call, Response<DeviceIdDTO> response) {
                if (response.isSuccessful()) {
                    Log.d("@@@su", "성공");

                    String tempDeviceid = response.body().getDeviceId(); // 로그인 하려는 사람의 디바이스 아이디를 알아옴
                    //이게 프리팹에 있는 디바이스 아이디와 비교하고 다르면 업데이트 시켜야함

                    Log.v("tttttempdivce",tempDeviceid);
                    Log.v("tttttempdivce2",deviceId);
                    if(!(tempDeviceid.equals(deviceId))){ //디바이스 아이디가 다르면
                        DeviceIdDTO deviceIdDTO = new DeviceIdDTO();
                        deviceIdDTO.setId(userid);
                        deviceIdDTO.setDeviceId(deviceId);
                        Gson gson = new Gson();
                        String objJson = gson.toJson(deviceIdDTO);

                        Call<ResponseBody> UpdateDeviceId = NetRetrofit.getInstance().getService().UpdateDeviceId(objJson);
                        UpdateDeviceId.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<DeviceIdDTO> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });
    }


    // view 함수들 구현
    @Override
    public void goMainView() {

    }

    @Override
    public void showResult(String result) {

    }


    String extractIDFromEmail(String email) {
        String[] parts = email.split("@");
        return parts[0];
    }
}