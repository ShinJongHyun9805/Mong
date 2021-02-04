package com.puppyland.mongnang.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.puppyland.mongnang.DTO.VideoDTO;
import com.puppyland.mongnang.R;
import com.puppyland.mongnang.SelectedUserVideoVIewActivity;
import com.puppyland.mongnang.Video.VideoAdapter;
import com.puppyland.mongnang.retrofitService.NetRetrofit;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class SelectedVideoVIewFragment extends Fragment {
    private ArrayList<VideoDTO> mArrayList;
    GridView VideoRecycler;
    private VideoAdapter vAdapter;
    String loginId , videoid , nickname;
    SharedPreferences videolistname;
    public SelectedVideoVIewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.fragment_selected_video_v_iew, container, false);
        VideoRecycler = view.findViewById(R.id.VideoRecycler);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("Loginfile",MODE_PRIVATE); // receiver 에선 context로 접근
        loginId = sharedPreferences.getString("id",null); // 지금 로그인한 사람 id

        Intent intent = getActivity().getIntent();
        videoid = intent.getExtras().getString("selectedId");
        nickname = intent.getExtras().getString("selectedNickname");

        videolistname =view.getContext().getSharedPreferences("VideoListfile", MODE_PRIVATE);


        VideoRecycler.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                VideoDTO item = vAdapter.getItem(position);
                Intent intent = new Intent(view.getContext(), SelectedUserVideoVIewActivity.class);
                intent.putExtra("videoid", videoid);
                intent.putExtra("vno", item.getVno());
                intent.putExtra("nickname" , item.getNickname());
                intent.putExtra("videoname" , item.getVideoname());
                intent.putExtra("user" , item.getUserid());
                // 여기 나중에 좋아요도 넣어야함
                startActivity(intent);
            }
        });

        getvideoList();
        return view;
    }


    public void getvideoList(){
        VideoDTO dto = new VideoDTO();
        dto.setUserid(videoid);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환
        mArrayList = new ArrayList<VideoDTO>();

        Call<List<VideoDTO>> getVideolist = NetRetrofit.getInstance().getService().getVideolist(objJson);
        getVideolist.enqueue(new Callback<List<VideoDTO>>() {
            @Override
            public void onResponse(Call<List<VideoDTO>> call, Response<List<VideoDTO>> response) {
                List<VideoDTO> templist = new ArrayList<VideoDTO>();
                templist = response.body();
                if(templist !=null){
                    for(int i=0;i<templist.size();i++){
                        Log.v("testt", String.valueOf(templist.get(i).getVideoname()));
                        Log.v("testt2", String.valueOf(templist.get(i).getNickname()));
                        mArrayList.add(templist.get(i));

                    }
                }
                vAdapter = new VideoAdapter();
                VideoRecycler.setAdapter(vAdapter);
                try {
                    if (mArrayList != null) {
                        for (VideoDTO videoDTO : mArrayList) { // 리스트로 담기는// 것들 하나씩 출력
                            vAdapter.addItem(videoDTO.getVno(),videoDTO.getUserid() , videoDTO.getNickname(),videoDTO.getVideoname());
                        }
                    } else {
                        Toast.makeText(getActivity(), "검색결과가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<List<VideoDTO>> call, Throwable t) {
                Log.d("###", t.getMessage());
            }
        });
    }



    @Override
    public void onResume() {
        super.onResume();
    }
}