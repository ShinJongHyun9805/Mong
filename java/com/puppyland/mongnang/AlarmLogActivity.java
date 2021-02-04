package com.puppyland.mongnang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.gson.Gson;
import com.puppyland.mongnang.Alarm.AlarmLogListAdapter;
import com.puppyland.mongnang.DTO.NotificationDTO;
import com.puppyland.mongnang.retrofitService.NetRetrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlarmLogActivity extends AppCompatActivity {

    //광고
    AdLoader adLoader;
    TemplateView template;

    //내용 리스트
    private ListView listview;
    private SwipeRefreshLayout swipe_list;
    private List<NotificationDTO> notificationDTOList;
    AlarmLogListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_log);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        listview = findViewById(R.id.list);
        swipe_list = findViewById(R.id.swipe_list);
        swipe_list.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                notificationlist();
            }
        });

        //광고
        MobileAds.initialize(this, getString(R.string.admob_app_id)); //네이티브 광고
        adLoader = new AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        NativeTemplateStyle styles = new
                                NativeTemplateStyle.Builder().build();

                        template = findViewById(R.id.my_template);
                        template.setStyles(styles);
                        template.setNativeAd(unifiedNativeAd);
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // Handle the failure by logging, altering the UI, and so on.
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();

    }

    @Override
    protected void onResume() {
        super.onResume();
        adLoader.loadAd(new AdRequest.Builder().build()); // 나올때마다 새로운 광고 노출
        notificationlist();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //로그인한 유저 ID값
                SharedPreferences sharedPreferences = getSharedPreferences("Loginfile", MODE_PRIVATE);
                String loginId = sharedPreferences.getString("id", null);

                //게시판 글 상세보기로 데이터 전달

                NotificationDTO dto = notificationDTOList.get(position);
                if(dto.getItem().equals("게시판")){
                    Intent intent = new Intent(getApplicationContext(), BoardDetail.class);
                    intent.putExtra("bno", dto.getNo());
                    intent.putExtra("user", dto.getBoardwriteuser());
                    startActivity(intent);
                }else if(dto.getItem().equals("스토리")){
                    Intent intent = new Intent(getApplicationContext(), SelectedUserDiaryVIewActivity.class);
                    intent.putExtra("user", dto.getDiarywriteuserid());
                    intent.putExtra("nickname",dto.getDiarywritenickname());
                    intent.putExtra("dno", dto.getNo());
                    intent.putExtra("date",dto.getDiarywritedate());
                    intent.putExtra("title",dto.getDiarywritetitle());
                    intent.putExtra("imageName",dto.getDiarywrtieimagename());
                    intent.putExtra("content",dto.getDiarywritecontent());
                    intent.putExtra("font",dto.getDiarywritefont());
                    intent.putExtra("likey",Integer.parseInt(dto.getDiarywritelikey()));
                    intent.putExtra("share",dto.getDiarywriteshare());
                    startActivity(intent);
                }else if(dto.getItem().equals("비디오")){
                    Intent intent = new Intent(getApplicationContext(), SelectedUserVideoVIewActivity.class);
                    intent.putExtra("nickname", dto.getVideonickname());
                    intent.putExtra("vno",dto.getNo());
                    intent.putExtra("videoname",dto.getVideovideoname());
                    intent.putExtra("videoid", dto.getUserid());
                }

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("id", loginId);
                editor.commit();


            }
        });
    }

    private void notificationlist() {
        SharedPreferences sharedPreferences = getSharedPreferences("Loginfile", MODE_PRIVATE);
        String id = sharedPreferences.getString("id", "");

        NotificationDTO dto = new NotificationDTO();
        dto.setUserid(id);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

        Call<List<NotificationDTO>> getListNotification = NetRetrofit.getInstance().getService().getListNotification(objJson);
        getListNotification.enqueue(new Callback<List<NotificationDTO>>() {
            @Override
            public void onResponse(Call<List<NotificationDTO>> call, Response<List<NotificationDTO>> response) {
                notificationDTOList = response.body();

                if(notificationDTOList.size()==0){
                    Toast.makeText(getApplicationContext(), "아직 아무런 알람이 오지 않았어요.", Toast.LENGTH_SHORT).show();
                }

                adapter = new AlarmLogListAdapter();
                listview.setAdapter(adapter);
                try {
                    if (notificationDTOList != null) {
                        for (NotificationDTO notificationDTO : notificationDTOList) { // 리스트로 담기는// 것들 하나씩 출력
                            adapter.addItem(notificationDTO);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "검색결과가 없습니다.", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                //새로고침 완료 !!필수!!
                swipe_list.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<NotificationDTO>> call, Throwable t) {
                Log.d("게시글 가져오기 실패", t.getMessage());
            }
        });
    }


}