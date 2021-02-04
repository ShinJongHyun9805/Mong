package com.puppyland.mongnang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.puppyland.mongnang.DTO.SearchCheckDataDTO;
import com.puppyland.mongnang.DTO.SearchMemberDTO;
import com.puppyland.mongnang.retrofitService.NetRetrofit;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultActivity extends AppCompatActivity {

    private RecyclerView.LayoutManager mLayoutManger;
    private ListViewAdapter adapter = new ListViewAdapter();
    private TextView titleText;
    RecyclerView recyclerView;
    //당겨서 새로고침
    private SwipeRefreshLayout swipe_refresh_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        mLayoutManger = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManger);
        recyclerView.setAdapter(adapter);


        titleText = findViewById(R.id.titleText);

        Intent intent = getIntent();
        String id = intent.getExtras().getString("loginId");
        String age = intent.getExtras().getString("result_age");
        String address = intent.getExtras().getString("result_address");
        String gender = intent.getExtras().getString("result_gender");
        String doggender = intent.getExtras().getString("result_doggender");
        String dogkind = intent.getExtras().getString("result_dogkind");

        getlist(id, age, address, gender, doggender, dogkind);

        //당겨서 새로고침
        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);
        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getlist(id, age, address, gender, doggender, dogkind);
            }
        });

        recyclerView.setAdapter(adapter);
        //새로고침 완료 !!필수!!

    }

    public void getlist(String userId, String age, String address, String gender, String doggender, String dogkind) {
        SearchCheckDataDTO searchCheckDataDTO = new SearchCheckDataDTO();
        searchCheckDataDTO.setUserId(userId);
        searchCheckDataDTO.setAddress(address);
        searchCheckDataDTO.setAge(age);
        searchCheckDataDTO.setGender(gender);
        searchCheckDataDTO.setDoggender(doggender);
        searchCheckDataDTO.setDogkind(dogkind);

        Gson gson = new Gson();
        String objJson = gson.toJson(searchCheckDataDTO); // DTO 객체를 json 으로 변환

        Call<ArrayList<SearchMemberDTO>> searchRequest = NetRetrofit.getInstance().getService().searchRequest(objJson);
        searchRequest.enqueue(new Callback<ArrayList<SearchMemberDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchMemberDTO>> call, Response<ArrayList<SearchMemberDTO>> response) {
                ArrayList<SearchMemberDTO> list = new ArrayList<SearchMemberDTO>();

                try {
                    list.addAll(response.body());

                    SharedPreferences sharedPreferences = getSharedPreferences("Loginfile", MODE_PRIVATE);
                    String loginId = sharedPreferences.getString("id", null);

                    titleText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);

                    adapter = new ListViewAdapter();
                    ArrayList<ListViewItem> list2 = new ArrayList<ListViewItem>();

                    if (list != null) {
                        for (SearchMemberDTO searchmemberDTO : list) { // 리스트로 담기는// 것들 하나씩 출력
                            ListViewItem item = new ListViewItem();
                            item.setTitle(searchmemberDTO.getUserid());
                            item.setDesc(searchmemberDTO.getUsermsg());
                            item.setIcon(searchmemberDTO.getMemberimage());
                            item.setDogAge(searchmemberDTO.getDogAge());
                            item.setDogGender(searchmemberDTO.getDoggender());
                            item.setDogName(searchmemberDTO.getDogname());
                            item.setDogImage(searchmemberDTO.getDogimage());
                            item.setNickname(searchmemberDTO.getNickname());

                            if (!loginId.equals(searchmemberDTO.getUserid())) { // 로그인한 유저 제외하고 검색
                                list2.add(item);
                            }
                        }
                        adapter.setItems(list2);
                    }
                    else{ // list가 null 이면
                        titleText.setText("검색 결과가\n없습니다.");
                    }
                } catch (Exception e) {
                    list = null;
                }
                recyclerView.setAdapter(adapter);
                swipe_refresh_layout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ArrayList<SearchMemberDTO>> call, Throwable t) {
                Log.d("###fail", t.getMessage());
            }
        });
    }
}