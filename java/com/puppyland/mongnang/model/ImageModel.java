package com.puppyland.mongnang.model;

import android.util.Log;

import com.puppyland.mongnang.DTO.DogDTO;
import com.puppyland.mongnang.DTO.MemberDTO;
import com.puppyland.mongnang.contract.ImageContract;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.google.gson.Gson;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageModel {

    ImageContract.Presenter presenter;
    MemberDTO memberDTO;
    List<DogDTO> Adto;

    public ImageModel(ImageContract.Presenter presenter) {
        this.presenter = presenter;
    }

    //회원가입 시 사진 등록 및 내 정보 사진 수정
    public void insertImage(String imageName, String UID) {
        MemberDTO dto = new MemberDTO();
        dto.setUserId(UID);
        Log.d("Model", UID);
        dto.setMemberimage(imageName);
        //Log.d("Model", imageName);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환
        //Log.d(TAG, objJson);

        Call<ResponseBody> insertImage = NetRetrofit.getInstance().getService().insertImage(objJson);
        insertImage.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    int res = 1;
                    Log.d("res", String.valueOf(res));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("err", t.getMessage());
            }
        });
    }

    //내 사진 가져오기. 상태메세지도 가져옴
    public MemberDTO fileName(String myID) {

        MemberDTO dto = new MemberDTO();
        dto.setUserId(myID);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

        final Call<MemberDTO> fileName1 = NetRetrofit.getInstance().getService().fileName1(objJson);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    memberDTO = fileName1.execute().body();
                 //   Log.d("kjkjk", memberDTO.getUserId());
                   // Log.d("kjkjk2", memberDTO.getUsermsg()); 제대로 되는거 확인
                } catch (Exception e) {
                    Log.d("err", e.getMessage());
                }
            }
        }).start();
        try {
            Thread.sleep(1000);
        }catch (Exception e){
            e.printStackTrace();
        }
        return memberDTO;
    }

    public void userInfoUpdate(MemberDTO member){

        Gson gson = new Gson();
        String objJson = gson.toJson(member); // DTO 객체를 json 으로 변환

        Call<ResponseBody> userInfoUpdate = NetRetrofit.getInstance().getService().userInfoUpdate(objJson);
        userInfoUpdate.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d("res", "성공");
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });
    }

}
