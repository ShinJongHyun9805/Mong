package com.puppyland.mongnang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.puppyland.mongnang.DTO.NotiboardDTO;
import com.puppyland.mongnang.Notiboard.NotiboardListAdapter;
import com.puppyland.mongnang.retrofitService.NetRetrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotiboardActivity extends AppCompatActivity {


    //내용 리스트
    private ListView listview;
    private List<NotiboardDTO> notiboardDTOList;
    NotiboardListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notiboard);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        listview = findViewById(R.id.list);
        notiboardlist();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotiboardDTO notiboardDTO = notiboardDTOList.get(position);
                Intent intent = new Intent(getApplicationContext(), NotiboardDetailActivity.class);
                intent.putExtra("title" , notiboardDTO.getTitle() );
                intent.putExtra("date", notiboardDTO.getDate());
                intent.putExtra("content",notiboardDTO.getContent());
                startActivity(intent);
            }
        });
    }



    private void notiboardlist() {
        SharedPreferences sharedPreferences = getSharedPreferences("Loginfile", MODE_PRIVATE);
        String id = sharedPreferences.getString("id", "");

        NotiboardDTO dto = new NotiboardDTO();
        dto.setTitle("");

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

        Call<List<NotiboardDTO>> getnotiboardlist = NetRetrofit.getInstance().getService().getnotiboardlist(objJson);
        getnotiboardlist.enqueue(new Callback<List<NotiboardDTO>>() {
            @Override
            public void onResponse(Call<List<NotiboardDTO>> call, Response<List<NotiboardDTO>> response) {
                notiboardDTOList = response.body();

                adapter = new NotiboardListAdapter();
                listview.setAdapter(adapter);
                try {
                    if (notiboardDTOList != null) {
                        for (NotiboardDTO notiboardDTO : notiboardDTOList) { // 리스트로 담기는// 것들 하나씩 출력
                            adapter.addItem(notiboardDTO);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "검색결과가 없습니다.", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<NotiboardDTO>> call, Throwable t) {
                Log.d("게시글 가져오기 실패", t.getMessage());
            }
        });
    }
}