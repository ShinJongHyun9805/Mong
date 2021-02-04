package com.puppyland.mongnang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.puppyland.mongnang.DTO.DiaryDTO;
import com.puppyland.mongnang.Story.StoryAdapter;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoryActivity extends AppCompatActivity {


    public static RecyclerView mRecyclerView;
    private ArrayList<DiaryDTO> mArrayList;
    private StoryAdapter mAdapter;
    public static Context context;
    TextView  storymore;
    int aaac=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_story);
        storymore = findViewById(R.id.storymore);
        storymore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreStory();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView3);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    ((StoryActivity)StoryActivity.context).mRecyclerView.requestDisallowInterceptTouchEvent(false);

                }
                else{
                    ((StoryActivity)StoryActivity.context).mRecyclerView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });

        context = this;

    }

    public void moreStory(){
        aaac= aaac+5;
     //   storymore.setVisibility(View.GONE); // 더보기 클릭하면 이건 없어지고 밑에 5개 또 나와야하니까 이렇게
        SharedPreferences sharedPreferences = getSharedPreferences("Loginfile", MODE_PRIVATE);
        String id = sharedPreferences.getString("id", "");

        Gson gson = new Gson();
        JsonObject object = new JsonObject();
        object.addProperty("count", aaac);
        String objJson = gson.toJson(object);
        mArrayList = new ArrayList<DiaryDTO>();

        Call<List<DiaryDTO>> shareInsertdiary = NetRetrofit.getInstance().getService().shareInsertdiary(objJson);//여기 레트로핏 하나 더 만들어야함
        //인증번호 2인 애들만 다이어리 테이블에서 셀렉트 리스트 하는거
        shareInsertdiary.enqueue(new retrofit2.Callback<List<DiaryDTO>>() {
            @Override
            public void onResponse(Call<List<DiaryDTO>> call, Response<List<DiaryDTO>> response) {
                List<DiaryDTO> templist = new ArrayList<DiaryDTO>();
                templist = response.body();
                if(templist !=null){
                    for(int i=0;i<templist.size();i++){
                        mArrayList.add(templist.get(i));
                    }
                }
                //mAdapter = new StoryAdapter();
                //mRecyclerView.setAdapter(mAdapter);
                try {
                    if (mArrayList != null) {
                        for (DiaryDTO diaryDTO : mArrayList) { // 리스트로 담기는// 것들 하나씩 출력
                            mAdapter.addItem(diaryDTO.getDno(), diaryDTO.getTitle(), diaryDTO.getUserid(),diaryDTO.getDcontent(),diaryDTO.getImg(), diaryDTO.getCreate_date() , diaryDTO.getNickname(), diaryDTO.getFont() , diaryDTO.getLikey(),diaryDTO.getShareConfirmation());
                            Log.v("wowiwlww",String.valueOf(diaryDTO.getLikey()));
                        }
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), "검색결과가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<List<DiaryDTO>> call, Throwable t) {
                Log.d("###", t.getMessage());
            }
        });
    }


    //글 작성 후 새로고침
    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences("Loginfile", MODE_PRIVATE);
        String id = sharedPreferences.getString("id", "");

        Gson gson = new Gson();
        JsonObject object = new JsonObject();
        object.addProperty("count", 0);
        String objJson = gson.toJson(object);
        mArrayList = new ArrayList<DiaryDTO>();

        Call<List<DiaryDTO>> shareInsertdiary = NetRetrofit.getInstance().getService().shareInsertdiary(objJson);//여기 레트로핏 하나 더 만들어야함
        //인증번호 2인 애들만 다이어리 테이블에서 셀렉트 리스트 하는거
        shareInsertdiary.enqueue(new Callback<List<DiaryDTO>>() {
            @Override
            public void onResponse(Call<List<DiaryDTO>> call, Response<List<DiaryDTO>> response) {
                List<DiaryDTO> templist = new ArrayList<DiaryDTO>();
                templist = response.body();
                if(templist !=null){
                    for(int i=0;i<templist.size();i++){
                        mArrayList.add(templist.get(i));
                    }
                }
                mAdapter = new StoryAdapter(StoryActivity.this);
                mRecyclerView.setAdapter(mAdapter);
                try {
                    if (mArrayList != null) {
                        for (DiaryDTO diaryDTO : mArrayList) { // 리스트로 담기는// 것들 하나씩 출력
                            mAdapter.addItem(diaryDTO.getDno(), diaryDTO.getTitle(), diaryDTO.getUserid(),diaryDTO.getDcontent(),diaryDTO.getImg(), diaryDTO.getCreate_date() , diaryDTO.getNickname(), diaryDTO.getFont() , diaryDTO.getLikey() , diaryDTO.getShareConfirmation());
                            Log.v("wowiwlww",String.valueOf(diaryDTO.getLikey()));
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "검색결과가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<List<DiaryDTO>> call, Throwable t) {
                Log.d("###", t.getMessage());
            }
        });
    }
}