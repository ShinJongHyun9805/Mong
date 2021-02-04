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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.puppyland.mongnang.DTO.DiaryDTO;
import com.puppyland.mongnang.Diary.DiaryAdapter;
import com.puppyland.mongnang.DiaryWriteActivity;
import com.puppyland.mongnang.R;
import com.puppyland.mongnang.SelectedUserDiaryVIewActivity;
import com.puppyland.mongnang.retrofitService.NetRetrofit;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static android.content.Context.MODE_PRIVATE;

public class SelectedStoryVIewFragment extends Fragment {
    private ArrayList<DiaryDTO> mArrayList;
    String id , nickname;
    String loginId;
    private DiaryAdapter mAdapter;
    GridView storyGrid;
    LinearLayout buttonInsert;
    private FloatingActionButton fab;

    public static SelectedStoryVIewFragment newInstance() {
        return new SelectedStoryVIewFragment();
    }
    public SelectedStoryVIewFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.fragment_selected_story_v_iew, container, false);
        // Inflate the layout for this fragment
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("Loginfile",MODE_PRIVATE); // receiver 에선 context로 접근
        loginId = sharedPreferences.getString("id",null); // 지금 로그인한 사람 id

        Intent intent = getActivity().getIntent();
        id = intent.getExtras().getString("selectedId");
        nickname = intent.getExtras().getString("selectedNickname");

        storyGrid = (GridView)view.findViewById(R.id.diaryRecycler);
        storyGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DiaryDTO item = mAdapter.getItem(position);


                    Intent intent = new Intent(view.getContext(), SelectedUserDiaryVIewActivity.class);
                    intent.putExtra("dno", item.getDno());
                    intent.putExtra("nickname" , item.getNickname());
                    intent.putExtra("title" , item.getTitle());
                    intent.putExtra("user" , item.getUserid());
                    intent.putExtra("date",item.getCreate_date());
                    intent.putExtra("imageName" , item.getImg());
                    intent.putExtra("content" , item.getDcontent());
                    intent.putExtra("font" , item.getFont());
                    intent.putExtra("share",item.getShareConfirmation());
                    intent.putExtra("likey",item.getLikey());
                    startActivity(intent);

            }
        });
        fab = view.findViewById(R.id.btnDiaryWrite);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), DiaryWriteActivity.class);
                startActivity(intent);
            }
        });
        if(!(id.equals(loginId))) {
            fab.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(id.equals(loginId)){ // 보려고 누른 아이디랑 로그인한 유저의 아이디가 같을때 , 즉 자기 피드를 볼때

            DiaryDTO dto = new DiaryDTO();
            dto.setUserid(loginId);

            Gson gson = new Gson();
            String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환
            mArrayList = new ArrayList<DiaryDTO>();

            Call<List<DiaryDTO>> diaryInfo = NetRetrofit.getInstance().getService().diaryInfo(objJson);
            diaryInfo.enqueue(new Callback<List<DiaryDTO>>() {
                @Override
                public void onResponse(Call<List<DiaryDTO>> call, Response<List<DiaryDTO>> response) {
                    List<DiaryDTO> templist = new ArrayList<DiaryDTO>();
                    templist = response.body();
                    if(templist !=null){
                        for(int i=0;i<templist.size();i++){
                            Log.v("testt", String.valueOf(templist.get(i).getTitle()));
                            Log.v("testt2", String.valueOf(templist.get(i).getCreate_date()));
                            mArrayList.add(templist.get(i));
                        }
                    }
                    mAdapter = new DiaryAdapter();
                    storyGrid.setAdapter(mAdapter);
                    try {
                        if (mArrayList != null) {
                            for (DiaryDTO diaryDTO : mArrayList) { // 리스트로 담기는// 것들 하나씩 출력
                                mAdapter.addItem(diaryDTO.getDno(), diaryDTO.getTitle(), diaryDTO.getUserid(), diaryDTO.getCreate_date() , diaryDTO.getImg() , diaryDTO.getDcontent(), diaryDTO.getNickname() , diaryDTO.getFont() , diaryDTO.getLikey() , diaryDTO.getShareConfirmation());
                                Log.v("likkkk" , String.valueOf(diaryDTO.getLikey()));
                            }
                        } else {

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
        else{ // 다른 사람 피드를 볼때
            DiaryDTO dto = new DiaryDTO();
            dto.setUserid(id);

            Gson gson = new Gson();
            String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환
            mArrayList = new ArrayList<DiaryDTO>();

            Call<List<DiaryDTO>> selectedUserdiaryInfo = NetRetrofit.getInstance().getService().selectedUserdiaryInfo(objJson);
            selectedUserdiaryInfo.enqueue(new Callback<List<DiaryDTO>>() {
                @Override
                public void onResponse(Call<List<DiaryDTO>> call, Response<List<DiaryDTO>> response) {
                    List<DiaryDTO> templist = new ArrayList<DiaryDTO>();
                    templist = response.body();
                    if(templist !=null){
                        for(int i=0;i<templist.size();i++){
                            Log.v("testt", String.valueOf(templist.get(i).getTitle()));
                            Log.v("testt2", String.valueOf(templist.get(i).getCreate_date()));
                            mArrayList.add(templist.get(i));
                        }
                    }
                    mAdapter = new DiaryAdapter();
                    storyGrid.setAdapter(mAdapter);
                    try {
                        if (mArrayList != null) {
                            for (DiaryDTO diaryDTO : mArrayList) { // 리스트로 담기는// 것들 하나씩 출력
                                mAdapter.addItem(diaryDTO.getDno(), diaryDTO.getTitle(), diaryDTO.getUserid(), diaryDTO.getCreate_date() , diaryDTO.getImg() , diaryDTO.getDcontent(),diaryDTO.getNickname(),diaryDTO.getFont() , diaryDTO.getLikey() , diaryDTO.getShareConfirmation());
                            }
                        } else {
                            Toast.makeText(getActivity(), "검색결과가 없습니다.", Toast.LENGTH_SHORT).show();
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
}