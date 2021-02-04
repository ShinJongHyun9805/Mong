package com.puppyland.mongnang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.puppyland.mongnang.DTO.MemberDTO;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.puppyland.mongnang.widget.FButton;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InsertAddressActivity extends AppCompatActivity {

    //주소 웹뷰 관련
    private WebView daum_webview2;
    private Handler handler;

    private FButton fbtn_insertaddress;
    private LinearLayout addressSection;
    private EditText editD, editE, editF;
    private TextView Tx_InsertAddressSave;
    private String address1, address2, address3, myID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_address);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        SharedPreferences sharedPreferences = getSharedPreferences("Loginfile", MODE_PRIVATE);
        myID = sharedPreferences.getString("id", null);

        //주소 영역
        addressSection = findViewById(R.id.addressSection);
        addressSection.setVisibility(View.GONE);

        editD = findViewById(R.id.editInputD);
        editE = findViewById(R.id.editInputE);
        editF = findViewById(R.id.editInputF);

        // 핸들러를 통한 JavaScript 이벤트 반응
        handler = new Handler();

        //주소 검색 버튼
        fbtn_insertaddress = findViewById(R.id.fbtn_insertaddress);
        fbtn_insertaddress.setButtonColor(getResources().getColor(R.color.fbutton_color_a));
        fbtn_insertaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init_webView();
            }
        });

        //완료
        Tx_InsertAddressSave = findViewById(R.id.Tx_InsertAddressSave);
        Tx_InsertAddressSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    address1 = editD.getText().toString();//우편주소
                    address2 = editE.getText().toString();//시 구 동 다 들어가고 있음 번지까지
                    address3 = editF.getText().toString();//상세주소
                } catch (Exception e) {
                    address1 = null;
                    address2 = null;
                    address3 = null;
                }

                String str = address2;
                String[] array = str.split(" "); // 파싱
                if ((address1 == null || address1.getBytes().length == 0) || (address2 == null || address2.getBytes().length == 0)) {
                    Toast.makeText(getApplicationContext(), "공백을 채워주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    MemberDTO dto = new MemberDTO();
                    dto.setUserId(myID);
                    dto.setAddress1(array[0]);
                    dto.setAddress2(array[1]);
                    dto.setAddress3(array[2]);

                    Gson gson = new Gson();
                    String objJson = gson.toJson(dto);

                    Call<ResponseBody> UpdateAddress = NetRetrofit.getInstance().getService().UpdateAddress(objJson);
                    UpdateAddress.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Toast.makeText(getApplicationContext(), "등록 완료", Toast.LENGTH_SHORT).show();

                            finish();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.d("주소 업데이트 실패", t.getMessage());
                        }
                    });

                }
            }
        });
    }

    public void init_webView() {

        // WebView 설정
        daum_webview2 = (WebView) findViewById(R.id.daum_webview2);

        // JavaScript 허용
        daum_webview2.getSettings().setJavaScriptEnabled(true);

        // JavaScript의 window.open 허용
        daum_webview2.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
        daum_webview2.addJavascriptInterface(new InsertAddressActivity.AndroidBridge() , "TestApp");

        // web client 를 chrome 으로 설정
        daum_webview2.setWebChromeClient(new WebChromeClient());

        daum_webview2.setHorizontalScrollBarEnabled(false);

        daum_webview2.setVerticalScrollBarEnabled(false);
        // webview url load. php 파일 주소
        daum_webview2.loadUrl("http://192.168.219.100:8092/address");

        daum_webview2.getSettings().setUseWideViewPort(true);
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


}