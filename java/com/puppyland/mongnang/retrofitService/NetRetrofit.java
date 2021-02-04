package com.puppyland.mongnang.retrofitService;

import android.content.Context;

import com.puppyland.mongnang.Ssl.NullHostNameVerifier;
import com.puppyland.mongnang.Ssl.SSLUtil;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.puppyland.mongnang.MainActivity.mmcontext;
import static com.puppyland.mongnang.StoryActivity.context;

public class NetRetrofit {

    private static NetRetrofit outInstance = new NetRetrofit();

    // 싱글톤으로 만들어줌.
    public static NetRetrofit getInstance() {
        return outInstance;
    }

    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.219.100:8092/") //https://mongnyang.shop/
            .client(new OkHttpClient.Builder()
                    .hostnameVerifier(new NullHostNameVerifier())
                    .sslSocketFactory(SSLUtil.getPinnedCertSslSocketFactory(mmcontext))
                    .build())
            .addConverterFactory(GsonConverterFactory.create())
            .build(); // 스프링 서버에 연결하고 레트로핏 초기화

    public static RetrofitService service = retrofit.create(RetrofitService.class);

    public RetrofitService getService() {
        // 싱글톤으로 만들어줌. 다른데서 이 함수를 호출하면 여기서
        // 만든 retfrofit service 를 사용할 수 있게 됌.
        return service;
    }
}
