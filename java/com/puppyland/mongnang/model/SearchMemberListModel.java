package com.puppyland.mongnang.model;

import android.util.Log;

import com.puppyland.mongnang.DTO.DogDTO;
import com.puppyland.mongnang.DTO.MemberDTO;
import com.puppyland.mongnang.DTO.SearchCheckDataDTO;
import com.puppyland.mongnang.DTO.SearchMemberDTO;
import com.puppyland.mongnang.contract.SearchContract;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchMemberListModel {

    List<DogDTO> dlist;
    MemberDTO memberdto;
    SearchContract.Presenter presenter;

    public SearchMemberListModel(SearchContract.Presenter presenter) {
        this.presenter = presenter;
    }

    public MemberDTO getClickMemberinfo(String userId) {
        MemberDTO member = new MemberDTO();
        member.setUserId(userId);
        Log.v("aseeee", member.getUserId());
        Gson gson = new Gson();
        String objJson = gson.toJson(member); // DTO 객체를 json 으로 변환
        Log.v("search", objJson);

        final Call<MemberDTO> searchRequest = NetRetrofit.getInstance().getService().getClickMemberinfo(objJson);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    memberdto = searchRequest.execute().body();

                } catch (IOException e) {
                    Log.d("fail", e.getMessage());
                }
            }
        }).start();
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return memberdto;
    }

    public List<DogDTO> getClickMemberDoginfo(String userId) { //되긴하는데 비동기라서 가져오는데 시간이 좀 걸림 이렇게 하지말고
        //비동기로 바꿀것
        DogDTO dog = new DogDTO();
        dog.setUserId(userId);

        Gson gson = new Gson();
        String objJson = gson.toJson(dog); // DTO 객체를 json 으로 변환
        Log.v("search", objJson);

        final Call<List<DogDTO>> searchRequest = NetRetrofit.getInstance().getService().getClickMemberDoginfo(objJson);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dlist = searchRequest.execute().body();
                    for (DogDTO dog : dlist) {
                        Log.d("IM", dog.getUserId());
                    }
                } catch (IOException e) {
                    Log.d("fail", e.getMessage());
                }
            }
        }).start();
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dlist;
    }


}
