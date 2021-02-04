package com.puppyland.mongnang;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.gson.Gson;
import com.puppyland.mongnang.Chatcommon.Util9;
import com.puppyland.mongnang.DTO.FunctionCountDTO;
import com.puppyland.mongnang.DTO.MemberDTO;
import com.puppyland.mongnang.DTO.UserModel;
import com.puppyland.mongnang.contract.ChatuserListContract;
import com.puppyland.mongnang.contract.EmailCertificationContract;
import com.puppyland.mongnang.contract.FunctionCountContract;
import com.puppyland.mongnang.contract.MemberContract;
import com.puppyland.mongnang.presenter.ChatuserListPresenter;
import com.puppyland.mongnang.presenter.EmailPresenter;
import com.puppyland.mongnang.presenter.FunctionCountPresenter;
import com.puppyland.mongnang.presenter.MemberPresenter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.puppyland.mongnang.widget.FButton;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class JoinActivity extends AppCompatActivity implements MemberContract.View, ChatuserListContract.View, EmailCertificationContract.View, FunctionCountContract.View {

    SharedPreferences sharedPreferences;
    MemberContract.Presenter memberpresenter;
    ChatuserListContract.Presenter chatuserListPresenter;
    EmailCertificationContract.Presenter emailCertificationPresenter;
    FunctionCountContract.Presenter functionCountContractPresenter;

    String inputB, inputC, inputD, inputE, inputF, Age, authresult, ID, nickedname ,PassCheck;

    //이메일 인증 여부
    int result, result2 ,checkoverlapid;
    EditText editD, editE, editF, editAge, email, input_auth, nickname,editPassCheck;
    LinearLayout addressSection;

    //인증버튼
    FButton btn_auth, btn_completeauth, btn_check ,btnAddressResult , btnResult;
    LinearLayout auth_layout;

    private RadioGroup radioGroup;
    private RadioButton r_btn1, r_btn2;

    private WebView daum_webView;
    private Handler handler;
    private String certificationNumber ="안녕하세요.\n몽냥몽냥입니다.\n본인인증에 필요한 인증코드입니다.\n";

    FirebaseAuth firebaseAuth; // 파이어베이스
    String deviceId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        memberpresenter = new MemberPresenter(this); // 뷰와 프레젠터를 연결하는 부분
        chatuserListPresenter = new ChatuserListPresenter(this);
        emailCertificationPresenter = new EmailPresenter(this); // 뷰와 프레젠터를 연결하는 부분
        functionCountContractPresenter = new FunctionCountPresenter(this);

        //디바이스 아이디
        SharedPreferences sharedPreferences = getSharedPreferences("DeviceIdFile", MODE_PRIVATE);
        deviceId = sharedPreferences.getString("deviceId", null);

        editD = findViewById(R.id.editInputD);
        editE = findViewById(R.id.editInputE);
        editF = findViewById(R.id.editInputF);
        editPassCheck = findViewById(R.id.editPassCheck);
        editAge = findViewById(R.id.editAge);

        radioGroup = findViewById(R.id.radioGroup);
        r_btn1 = findViewById(R.id.radiobutton1);
        r_btn2 = findViewById(R.id.radiobutton2);

        //아이디
        email = findViewById(R.id.editID);


        //인증버튼
        btn_auth = findViewById(R.id.btn_auth);
        btn_auth.setButtonColor(getResources().getColor(R.color.fbutton_color_a));
        input_auth = findViewById(R.id.input_auth);
        btn_completeauth = findViewById(R.id.btn_completeauth);
        btn_completeauth.setButtonColor(getResources().getColor(R.color.fbutton_color_a));
        auth_layout = findViewById(R.id.auth_layout);

        //닉네임
        nickname = findViewById(R.id.nickname);

        radioGroup.setOnCheckedChangeListener(radioGroupButtonChangeListener);

        //주소 영역
        addressSection = findViewById(R.id.addressSection);
        addressSection.setVisibility(View.GONE);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        //이메일 인증여부
        result = 0;
        if (result == 0) {
            //회원가입 -> 다음버튼
            initListener();
        }

        //중복확인
        result2 = 0;
        if (result2 == 0) {
            //회원가입 -> 다음버튼
            initListener();
        }

        // 핸들러를 통한 JavaScript 이벤트 반응
        handler = new Handler();

        //제일 처음 editText 포커스
        email.requestFocus();

        //메일 전송버튼
        btn_auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //이메일 패턴
                btn_auth.setText("메일 전송 중...");
                btn_auth.setEnabled(false);

                ID = email.getText().toString();
                CheckOverlapID();
            }
        });

        //인증확인버튼
        btn_completeauth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result = CompleteAuthEmail();
                initListener();
            }
        });

        //닉네임 중복확인
        btn_check = findViewById(R.id.btn_check);
        btn_check.setButtonColor(getResources().getColor(R.color.fbutton_color_a));
        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nikname = nickname.getText().toString();

                if (nikname.equals("") || nikname.getBytes().length == 0) {
                    Toast.makeText(getApplicationContext(), "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Checkingoverlap(nikname);
                }
            }
        });

        //파이어베이스
        firebaseAuth = FirebaseAuth.getInstance(); // 파이어베이스 초기 인스턴스 생성
        sharedPreferences = getSharedPreferences("Loginfile", Activity.MODE_PRIVATE);
    }

    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if (i == R.id.radiobutton1) {
                inputC = "f";
            } else if (i == R.id.radiobutton2) {
                inputC = "m";
            }
        }
    };


    private void CheckOverlapID() {
        MemberDTO dto = new MemberDTO();
        dto.setUserId(ID);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);
        Log.d("@@@", "오나?1111");
        Call<ResponseBody> CheckOverlapID = NetRetrofit.getInstance().getService().CheckOverlapID(objJson);
        CheckOverlapID.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        checkoverlapid = Integer.parseInt(response.body().string());
                        Log.d("@@@", "오나?222");
                        //이메일 패턴
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(ID).matches()) {
                            Toast.makeText(JoinActivity.this, "이메일 형식이 아닙니다", Toast.LENGTH_LONG).show();
                            btn_auth.setText("메일 전송");
                            btn_auth.setEnabled(true);
                        } else if (checkoverlapid == 1) {
                            Toast.makeText(JoinActivity.this, "이미 가입된 계정입니다.", Toast.LENGTH_LONG).show();
                            btn_auth.setText("메일 전송");
                            btn_auth.setEnabled(true);
                        } else if (checkoverlapid == 0 && android.util.Patterns.EMAIL_ADDRESS.matcher(ID).matches()) {
                            Log.d("@@@", "오나?3333");
                            sendAuthEmail(ID);

                            auth_layout.setVisibility(View.VISIBLE);
                            input_auth.requestFocus();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("가입된 계정 체크 실패", t.getMessage());
            }
        });


    }
    //주소 검색 웹뷰
    public void init_webView() {

        // WebView 설정
        daum_webView = (WebView) findViewById(R.id.daum_webview);

        // JavaScript 허용
        daum_webView.getSettings().setJavaScriptEnabled(true);

        // JavaScript의 window.open 허용
        daum_webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
        daum_webView.addJavascriptInterface(new JoinActivity.AndroidBridge(), "TestApp");

        // web client 를 chrome 으로 설정
        daum_webView.setWebChromeClient(new WebChromeClient());

        daum_webView.setHorizontalScrollBarEnabled(false);

        daum_webView.setVerticalScrollBarEnabled(false);
        // webview url load. php 파일 주소
        daum_webView.loadUrl("http://192.168.219.100:8092/address");

        daum_webView.getSettings().setUseWideViewPort(true);
    }

    //웹뷰 연결
    private class AndroidBridge {
        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    addressSection.setVisibility(View.VISIBLE);

                    editD.setText(String.format("%s", arg1));
                    editE.setText(String.format("%s", arg2));
                    editF.setText(String.format("%s", arg3));

                    // WebView를 초기화 하지않으면 재사용할 수 없음
                    init_webView();
                }
            });
        }
    }

    //이메일 전송
    public void sendAuthEmail(String id) {
        try {

            GMailSender gMailSender = new GMailSender("officialmongnyang@gmail.com", "iitkpwxohfkyffos");
            gMailSender.sendMail("몽냥몽냥 인증코드입니다", certificationNumber+" \n"+gMailSender.getEmailCode(), ID);

            btn_auth.setText("메일 전송");
            btn_auth.setEnabled(true);

            authresult = gMailSender.getEmailCode();


            Toast.makeText(getApplicationContext(), "이메일을 성공적으로 보냈습니다.", Toast.LENGTH_SHORT).show();
        } catch (SendFailedException e) {
            Toast.makeText(getApplicationContext(), "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (MessagingException e) {
            Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //이메일 키 인증 완료
    public int CompleteAuthEmail() {
        if (authresult.equals(input_auth.getText().toString())) {
            input_auth.setText("인증 성공:)");

            input_auth.setClickable(false);
            input_auth.setFocusable(false);

            btn_completeauth.setClickable(false);
            btn_completeauth.setFocusable(false);


            return 1;
        } else {
            Toast.makeText(getApplicationContext(), "인증 코드가 잘못되었습니다.", Toast.LENGTH_SHORT).show();
            return 0;
        }
    }

    //닉네임 중복확인
    public void Checkingoverlap(String nikname) {
        MemberDTO dto = new MemberDTO();
        dto.setNickname(nikname);
        Log.d("###", dto.getNickname());


        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

        Call<ResponseBody> Checkingoverlap = NetRetrofit.getInstance().getService().Checkingoverlap(objJson);
        Checkingoverlap.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        result2 = Integer.parseInt(response.body().string());
                        if (result2 == 1) {
                            Toast.makeText(getApplicationContext(), "사용가능한 별명입니다:)", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "사용 중인 별명입니다:(", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("###fail", t.getMessage());
            }
        });
    }

    private void initListener() {
        // 회원가입 버튼 클릭시 가입 요청
        btnResult = findViewById(R.id.btnResult);
        btnResult.setButtonColor(getResources().getColor(R.color.fbutton_color_a));
        btnResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //이메일 인증 하지 않고 다음을 눌렀을때
                if (result == 0 || result2 == 0) {
                    Toast.makeText(getApplicationContext(), "이메일 인증 및 닉네임 확인 후 진행해 주세요", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        ID = email.getText().toString();
                        inputB = ((EditText) findViewById(R.id.editPass)).getText().toString();
                        PassCheck = ((EditText) findViewById(R.id.editPassCheck)).getText().toString();
                        Age = ((EditText) findViewById(R.id.editAge)).getText().toString();
                        inputD = editD.getText().toString();//우편주소
                        inputE = editE.getText().toString();//시 구 동 다 들어가고 있음 번지까지
                        inputF = editF.getText().toString();//상세주소
                        nickedname = nickname.getText().toString();

                        ID = plzNoHacking(ID);
                        inputB = plzNoHacking(inputB);
                        PassCheck = plzNoHacking(PassCheck);
                        Age = plzNoHacking(Age);
                        inputD = plzNoHacking(inputD);
                        inputE = plzNoHacking(inputE);
                        inputF = plzNoHacking(inputF);
                        nickedname = plzNoHacking(nickedname);

                    } catch (Exception e) {
                        ID = null;
                        inputB = null;
                        Age = null;
                        inputD = null;
                        inputE = null;
                        inputF = null;
                        nickedname = null;
                    }
                    String str = inputE;
                    String[] array = str.split(" "); // 파싱
                    if ((ID == null || ID.getBytes().length == 0) || (inputB == null || inputB.getBytes().length == 0) || (nickedname == null || nickedname.getBytes().length == 0) ||
                            (inputC == null || inputC.getBytes().length == 0) ||(PassCheck == null || PassCheck.getBytes().length == 0) || (Age == null || Age.getBytes().length == 0) || array.length == 1) {
                        Toast.makeText(JoinActivity.this, "입력하지 않은 정보가 있습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        //모든 값 입력하고 패스워드가 6자리 미만일때
                        if (inputB.getBytes().length <= 5) {
                            Toast.makeText(JoinActivity.this, "패스워드는 6자리 이상입니다", Toast.LENGTH_SHORT).show();
                        }
                        if (!inputB.equals(PassCheck)) {
                            Toast.makeText(JoinActivity.this, "패스워드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //회원가입
                            memberpresenter.memberJoin(ID, inputC, Age, array[0], array[1], array[2], "1", "0", nickedname, deviceId);

                            firebaseUser(nickedname);

                            //별명 저장
                            SharedPreferences sharedPreferences2 = getSharedPreferences("nickname", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences2.edit();
                            editor.putString("nickname", nickedname);
                            editor.commit();
                        }
                    }
                }
            }
        });
        btnAddressResult = findViewById(R.id.btnAddressResult);
        btnAddressResult.setButtonColor(getResources().getColor(R.color.fbutton_color_a));
        btnAddressResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init_webView();
            }
        });
    }


    private void firebaseUser(String nickname) {
        String email = ((EditText) findViewById(R.id.editID)).getText().toString();
        String password = ((EditText) findViewById(R.id.editPass)).getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password를 입력해 주세요.", Toast.LENGTH_SHORT).show();
        }


        FirebaseAuth.getInstance().createUserWithEmailAndPassword(ID, inputB).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    final String uid = FirebaseAuth.getInstance().getUid();

                    UserModel userModel = new UserModel();
                    userModel.setUid(uid);
                    userModel.setUserid(ID);
                    userModel.setUsernm(nickname);
                    userModel.setUsermsg("...");

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users").document(uid)
                            .set(userModel)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(String.valueOf(R.string.app_name), "DocumentSnapshot added with ID: " + uid);
                                }
                            });
                } else {
                    Util9.showMessage(getApplicationContext(), task.getException().getMessage());
                }
            }
        });

    }

    String extractIDFromEmail(String email) {
        String[] parts = email.split("@");
        return parts[0];
    }

    //public void memberJoin(String ID, String inputC, String Age, array[0], array[1], array[2], "1", "0"){

    //}


    @Override
    public void goMainView() {//join이 되고나면 인으로 보내줘야하니까
        Intent intent = new Intent(getApplicationContext(), Join_dogActivity.class);//Join 대신 main2activtiy 로 가야함
        intent.putExtra("ID", ID);
        startActivity(intent);
    }

    @Override
    public void showResult(String result) {

    }


    public String plzNoHacking(String string){
        string = string.replace("!", "\u0021");
        string = string.replace("?", "\u003F");
        string = string.replace("%", "\u0025");
        string = string.replace("#", "\u0023");
        string = string.replace("@", "\u0040");
        string = string.replace("^", "\u005E");
        string = string.replace("&", "\u0026");
        string = string.replace("*", "\u002A");
        string = string.replace("(", "\u0028");
        string = string.replace(")", "\u0029");
        string = string.replace("-", "\u002D");
        string = string.replace("_", "\u005F");
        string = string.replace("+", "\u002B");
        string = string.replace("=", "\u003D");
        string = string.replace(";", "\u003B");
        string = string.replace("(", "\u003A");
        string = string.replace("/", "\u002F");
        string = string.replace(".", "\u002E");
        string = string.replace("<", "\u003C");
        string = string.replace("~", "\u007E");

        return string;
    }


}