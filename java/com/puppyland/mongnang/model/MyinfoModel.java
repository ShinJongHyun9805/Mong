package com.puppyland.mongnang.model;

import android.util.Log;

import com.puppyland.mongnang.DTO.MemberDTO;
import com.puppyland.mongnang.contract.MyinfoContract;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.google.gson.Gson;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyinfoModel {

    String msg;
    MyinfoContract.Presenter presenter;
    MemberDTO memberDTO = new MemberDTO();
    public MyinfoModel(MyinfoContract.Presenter presenter) {
        this.presenter = presenter;
    }

    public String getInfoUser(String userId){

        MemberDTO memberDTO =new MemberDTO();
        memberDTO.setUserId(userId);
        //Log.v("useridid" , userId);
        Gson gson = new Gson();
        String objJson = gson.toJson(memberDTO); // DTO 객체를 json 으로 변환

        final Call<MemberDTO> getInfoUser = NetRetrofit.getInstance().getService().getuserInfo(objJson);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.v("passpass" , getInfoUser.execute().body().getUsermsg());
                    msg = getInfoUser.execute().body().getUsermsg();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        try {
            Thread.sleep(1000);
        }catch (Exception e){
            e.printStackTrace();
        }

        return msg;
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
