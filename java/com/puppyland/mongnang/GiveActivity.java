package com.puppyland.mongnang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.gson.Gson;
import com.puppyland.mongnang.DTO.FunctionCountDTO;
import com.puppyland.mongnang.DTO.MemberDTO;
import com.puppyland.mongnang.retrofitService.NetRetrofit;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GiveActivity extends AppCompatActivity {

    LottieAnimationView animationToy;
    private boolean check = false;
    private String getTime;
    //동영상 보상 광고
    private RewardedAd rewardedAd;
    private TextView tx_countgive;
    private String count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give);

        tx_countgive = findViewById(R.id.tx_countgive);
        //장난감 후기
        final LottieAnimationView animationToy = (LottieAnimationView) findViewById(R.id.heart);
        animationToy.setAnimation("lf30_editor_yjixlnlv.json");

        rewardedAd = new RewardedAd(this, "ca-app-pub-1267905668913700/6891808884");

        SharedPreferences sf = getSharedPreferences("getDate", MODE_PRIVATE);
        String getDate = sf.getString("getDate", "");
        Log.d("%%%getDate", getDate);

        getCountGive();

        //현재 날짜 구하기
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMdd");
        getTime = simpleDate.format(mDate);
        Log.d("%%%getTime", getTime);

        if (!getDate.equals(getTime)) { // 날짜가 같지 않으면 어짜피 새로 들어온거라서 bool 값을 초기화 해야함
            SharedPreferences sharedPreferences2 = getSharedPreferences("boolean", MODE_PRIVATE);
            SharedPreferences.Editor editor2 = sharedPreferences2.edit();
            editor2.putBoolean("boolean", false);
            editor2.commit();
        }

        //날짜가 다르면 false로 바뀌어서 check 에 들어갈꺼고 같으면 기존 값 그대로 들어감
        SharedPreferences boolsf = getSharedPreferences("boolean", MODE_PRIVATE);
        Boolean bool = boolsf.getBoolean("boolean", false);
        check = bool;
        if (check == true) {
            animationToy.setProgress(1);
        }

        animationToy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check == false) {
                    check = true;
                    animationToy.setProgress(1);
                    SharedPreferences sharedPreferences = getSharedPreferences("getDate", MODE_PRIVATE);
                    SharedPreferences sharedPreferences2 = getSharedPreferences("boolean", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                    editor.putString("getDate", getTime); // key, value를 이용하여 저장하는 형태
                    editor2.putBoolean("boolean", check);
                    editor.commit();
                    editor2.commit();
                    Toast.makeText(getApplicationContext(), "광고시청 후 기부가 완료됩니다.", Toast.LENGTH_SHORT).show();

                    updateGiveCount();
                    RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
                        @Override
                        public void onRewardedAdLoaded() { // 광고 로드가 완료되면 실행
                            // Ad successfully loaded.
                            if (rewardedAd.isLoaded()) {
                                Activity activityContext = GiveActivity.this;
                                RewardedAdCallback adCallback = new RewardedAdCallback() {
                                    @Override
                                    public void onRewardedAdOpened() {
                                        // Ad opened.
                                    }

                                    @Override
                                    public void onRewardedAdClosed() {
                                        rewardedAd = createAndLoadRewardedAd();
                                    }

                                    @Override
                                    public void onUserEarnedReward(@NonNull RewardItem reward) {
                                        // User earned reward.

                                        Toast.makeText(getApplicationContext(), "기부가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onRewardedAdFailedToShow(AdError adError) {
                                        // Ad failed to display.
                                    }
                                };
                                rewardedAd.show(activityContext, adCallback);
                            } else {
                                Log.d("TAG", "The rewarded ad wasn't loaded yet.");
                            }
                        }

                        @Override
                        public void onRewardedAdFailedToLoad(LoadAdError adError) {
                            // Ad failed to load.
                        }
                    };
                    rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
                } else {
                    Toast.makeText(getApplicationContext(), "오늘은 이미 기부를 하셨습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //광고 만들고 끝나면 재생성하는 코드
    public RewardedAd createAndLoadRewardedAd() {
        RewardedAd rewardedAd = new RewardedAd(this,
                "ca-app-pub-3940256099942544/5224354917");
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
        return rewardedAd;
    }


    public void updateGiveCount() {
        MemberDTO dto = new MemberDTO();
        dto.setUserId("1");

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

        Call<ResponseBody> giveHeart = NetRetrofit.getInstance().getService().giveHeart(objJson);
        giveHeart.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "감사합니다.", Toast.LENGTH_SHORT).show();
                    int plus = Integer.parseInt(count);
                    int p = plus + 1;
                    String realcount = String.valueOf(p);
                    tx_countgive.setText(realcount);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("@@@", t.getMessage());
            }
        });
    }

    public void getCountGive() {
        MemberDTO dto = new MemberDTO();
        dto.setUserId("1");

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

        Call<ResponseBody> getCountGive = NetRetrofit.getInstance().getService().getCountGive(objJson);
        getCountGive.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        count = response.body().string();
                        tx_countgive.setText(count);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("@@@", t.getMessage());
            }
        });
    }
}