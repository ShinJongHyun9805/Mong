package com.puppyland.mongnang;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.kakao.auth.ApiErrorCode;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;
import com.puppyland.mongnang.Chatcommon.Util9;
import com.puppyland.mongnang.DTO.DeviceIdDTO;
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
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.puppyland.mongnang.widget.FButton;
import com.puppyland.mongnang.widget.NetworkStatus;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Random;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MemberContract.View, ChatuserListContract.View, EmailCertificationContract.View, FunctionCountContract.View {

    public static Context mmcontext;
    MemberContract.Presenter memberpresenter;
    ChatuserListContract.Presenter chatuserListPresenter;
    EmailCertificationContract.Presenter emailCertificationPresenter;
    FunctionCountContract.Presenter functionCountContractPresenter;
    int checkoverlapid;
    String loginId;
    FButton fButton, fButton2;
    LoginButton btn_kakao_login; // 카카오 로그인버튼
    String kakaoNickname;
    private SessionCallback sessionCallback = new SessionCallback();
    Session session;
    String deviceId;
    String pushflag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mmcontext = getApplicationContext();
        pushflag = "2";
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        session = Session.getCurrentSession();
        session.addCallback(sessionCallback);
        //앱 실행 시 로그인 토큰이 있으면 자동으로 로그인 수행

        memberpresenter = new MemberPresenter(this);
        chatuserListPresenter = new ChatuserListPresenter(this);
        emailCertificationPresenter = new EmailPresenter(this); // 뷰와 프레젠터를 연결하는 부분
        functionCountContractPresenter = new FunctionCountPresenter(this);


        SharedPreferences sharedPreferences = getSharedPreferences("Loginfile", MODE_PRIVATE);
        loginId = sharedPreferences.getString("id", null);
        Intent pushintent = getIntent();
        try {
            pushflag = pushintent.getExtras().getString("pushflag");
        } catch (Exception e) {
            pushflag = "2";
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) { // 자동로그인 // 인터넷 연결이 됐을때만 자동로그인 실행
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                                     //새로운 테스크에 액티비티 올림     및  백그라운드에 있는 액티비티 제거
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("pushflag", pushflag);
            startActivity(intent);
            Session.getCurrentSession().removeCallback(sessionCallback);
            finish();
        }
        fButton = findViewById(R.id.memberJoin);
        fButton.setButtonColor(getResources().getColor(R.color.fbutton_color_a));
        fButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TermsActivity.class);
                startActivity(intent);
            }
        });
        fButton2 = findViewById(R.id.memberLogin);
        fButton2.setButtonColor(getResources().getColor(R.color.fbutton_color_a));
        findViewById(R.id.memberLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Session.getCurrentSession().removeCallback(sessionCallback);
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);

            }
        });

        btn_kakao_login = findViewById(R.id.btn_kakao_login);
        btn_kakao_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.open(AuthType.KAKAO_TALK, MainActivity.this);

            }
        });
    }

    private void goJoin() {
        // 회원가입 버튼 클릭시 가입 요청

        fButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(intent);
            }
        });
    }

    private void goLogin() {

        findViewById(R.id.memberLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 세션 콜백 삭제
        Session.getCurrentSession().removeCallback(sessionCallback);
        clearApplicationCache(null);
    }


    private void clearApplicationCache(java.io.File dir) {
        if (dir == null)
            dir = getCacheDir();
        else ;
        if (dir == null)
            return;
        else ;
        java.io.File[] children = dir.listFiles();
        try {
            for (int i = 0; i < children.length; i++)
                if (children[i].isDirectory())
                    clearApplicationCache(children[i]);
                else children[i].delete();
        } catch (Exception e) {
        }
    }

    // view 함수들 구현
    @Override
    public void goMainView() {//join이 되고나면 메인으로 보내줘야하니까

    }

    @Override
    public void showResult(String result) {

    }


    @Override
    public void onBackPressed() {
        dialog_native_ad dialog = new dialog_native_ad(this);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //카카오 로그인 콜백
    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() { //세션이 성공적으로 열린 경우
            UserManagement.getInstance().me(new MeV2ResponseCallback() { //유저 정보를 가져온다.
                @Override
                public void onFailure(ErrorResult errorResult) { //유저 정보를 가져오는 데 실패한 경우
                    int result = errorResult.getErrorCode(); //오류 코드를 받아온다.

                    if (result == ApiErrorCode.CLIENT_ERROR_CODE) { //클라이언트 에러인 경우: 네트워크 오류
                        Toast.makeText(getApplicationContext(), "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else { //클라이언트 에러가 아닌 경우: 기타 오류
                        Toast.makeText(getApplicationContext(), "로그인 도중 오류가 발생했습니다: " + errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) { //세션이 도중에 닫힌 경우
                    Toast.makeText(getApplicationContext(), "세션이 닫혔습니다. 다시 시도해 주세요: " + errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(MeV2Response result) { //유저 정보를 가져오는데 성공한 경우
                    String needsScopeAutority = ""; //이메일, 성별, 연령대, 생일 정보 가져오는 권한 체크용

                    if (result.getKakaoAccount().needsScopeAccountEmail()) { //이메일 정보를 가져오는 데 사용자가 동의하지 않은 경우
                        needsScopeAutority = needsScopeAutority + "이메일";
                    }
                    if (result.getKakaoAccount().needsScopeGender()) { //성별 정보를 가져오는 데 사용자가 동의하지 않은 경우
                        needsScopeAutority = needsScopeAutority + ", 성별";
                    }
                    if (result.getKakaoAccount().needsScopeAgeRange()) { //연령대 정보를 가져오는 데 사용자가 동의하지 않은 경우
                        needsScopeAutority = needsScopeAutority + ", 연령대";
                    }
                    if (result.getKakaoAccount().needsScopeBirthday()) { //생일 정보를 가져오는 데 사용자가 동의하지 않은 경우
                        needsScopeAutority = needsScopeAutority + ", 생일";
                    }

                    if (needsScopeAutority.length() != 0) { //거절된 권한이 있는 경우
                        //거절된 권한을 허용해달라는 Toast 메세지 출력
                        if (needsScopeAutority.charAt(0) == ',') {
                            needsScopeAutority = needsScopeAutority.substring(2);
                        }
                        Toast.makeText(getApplicationContext(), needsScopeAutority + "에 대한 권한이 허용되지 않았습니다. 개인정보 제공에 동의해주세요.", Toast.LENGTH_SHORT).show();


                        //회원탈퇴 수행
                        //회원탈퇴에 대한 자세한 내용은 MainActivity의 회원탈퇴 버튼 참고
                        UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
                            @Override
                            public void onFailure(ErrorResult errorResult) {
                                int result = errorResult.getErrorCode();

                                if (result == ApiErrorCode.CLIENT_ERROR_CODE) {
                                    Toast.makeText(getApplicationContext(), "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "오류가 발생했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onSessionClosed(ErrorResult errorResult) {
                                Toast.makeText(getApplicationContext(), "로그인 세션이 닫혔습니다. 다시 로그인해 주세요.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNotSignedUp() {
                                Toast.makeText(getApplicationContext(), "가입되지 않은 계정입니다. 다시 로그인해 주세요.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(Long result) {
                            }
                        });
                    } else { //모든 정보를 가져오도록 허락받았다면

                        Random random = new Random();
                        int rand = random.nextInt(9999);

                        SharedPreferences sharedPreferences = getSharedPreferences("DeviceIdFile", MODE_PRIVATE);
                        deviceId = sharedPreferences.getString("deviceId", null);

                        FirebaseAuth.getInstance().signInWithEmailAndPassword(result.getKakaoAccount().getEmail(), "signout").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    //로그인을 성공했을때 디바이스 아이디 확인하고 달라졌으면 변경시켜야함
                                    getDeviceId(result.getKakaoAccount().getEmail());

                                    MemberDTO memberDTO = new MemberDTO();
                                    memberDTO.setUserId(result.getKakaoAccount().getEmail());

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

                                    Intent intent = new Intent(getApplicationContext(), MainActivity2.class);//Join 대신 main2activtiy 로 가야함
                                    SharedPreferences loginsharedPreferences = getSharedPreferences("Loginfile", MODE_PRIVATE);
                                    SharedPreferences.Editor logineditor = loginsharedPreferences.edit();
                                    logineditor.putString("id", result.getKakaoAccount().getEmail());
                                    logineditor.commit();

                                    intent.putExtra("id", result.getKakaoAccount().getEmail());
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    Log.v("kakaoNsdfsickname", "들어옴");
                                    kakaoNickname = result.getKakaoAccount().getProfile().getNickname() + "kakao_" + String.valueOf(rand);
                                    Log.v("kakaoNickname", kakaoNickname);
                                    CheckOverlapID(result.getKakaoAccount().getEmail(), result);

                                }
                            }
                        });
                    }
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException e) { //세션을 여는 도중 오류가 발생한 경우 -> Toast 메세지를 띄움.
            Toast.makeText(getApplicationContext(), "로그인 도중 오류가 발생했습니다. 인터넷 연결을 확인해주세요: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    private void firebaseUser(String usermail, String kakaoNickname) {
        String email = usermail;
        String password = "signout";

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password를 입력해 주세요.", Toast.LENGTH_SHORT).show();
        }


        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    final String uid = FirebaseAuth.getInstance().getUid();

                    UserModel userModel = new UserModel();
                    userModel.setUid(uid);
                    userModel.setUserid(email);
                    userModel.setUsernm(kakaoNickname);
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

                    if (!(tempDeviceid.equals(deviceId))) { //디바이스 아이디가 다르면
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

    private void CheckOverlapID(String ID, MeV2Response result) {
        MemberDTO dto = new MemberDTO();
        dto.setUserId(ID);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> CheckOverlapID = NetRetrofit.getInstance().getService().CheckOverlapID(objJson);
        CheckOverlapID.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        checkoverlapid = Integer.parseInt(response.body().string());
                        if (checkoverlapid == 0) {
                            memberpresenter.memberJoin(result.getKakaoAccount().getEmail(), "", "0", "", "", "", "1", "0", kakaoNickname, deviceId);
                            firebaseUser(result.getKakaoAccount().getEmail(), kakaoNickname);
                            //별명 저장
                            SharedPreferences sharedPreferences2 = getSharedPreferences("nickname", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences2.edit();
                            editor.putString("nickname", kakaoNickname);
                            editor.commit();


                            SharedPreferences loginsharedPreferences = getSharedPreferences("Loginfile", MODE_PRIVATE);
                            SharedPreferences.Editor logineditor = loginsharedPreferences.edit();
                            logineditor.putString("id", result.getKakaoAccount().getEmail());
                            logineditor.commit();


                            Intent intent = new Intent(getApplicationContext(), MainActivity2.class);//Join 대신 main2activtiy 로 가야함
                            intent.putExtra("id", result.getKakaoAccount().getEmail());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {

                            SharedPreferences sharedPreferences2 = getSharedPreferences("nickname", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences2.edit();
                            editor.putString("nickname", kakaoNickname);
                            editor.commit();

                            SharedPreferences loginsharedPreferences = getSharedPreferences("Loginfile", MODE_PRIVATE);
                            SharedPreferences.Editor logineditor = loginsharedPreferences.edit();
                            logineditor.putString("id", result.getKakaoAccount().getEmail());
                            logineditor.commit();

                            Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
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

    private void getHashKey() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }

    public static SSLSocketFactory getPinnedCertSslSocketFactory(Context context) {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = context.getResources().openRawResource(R.raw.mongnyan);
            Certificate ca = null;
            try {
                ca = cf.generateCertificate(caInput);
                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } catch (CertificateException e) {
                e.printStackTrace();
            } finally {
                caInput.close();
            }

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            if (ca == null) {
                return null;
            }
            keyStore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}