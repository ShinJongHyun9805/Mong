package com.puppyland.mongnang.model;

import android.util.Log;

import com.puppyland.mongnang.DTO.TimeCheckDTO;
import com.puppyland.mongnang.contract.TimeCheckContract;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TimeCheckModel {

    List<String> timelist;
    String memo;
    TimeCheckContract.presenter presenter;

    public TimeCheckModel(TimeCheckContract.presenter presenter) {
        this.presenter = presenter;
    }

    public List<String> getTimeCheck(String userId){
        TimeCheckDTO dto = new TimeCheckDTO();
        dto.setUserId(userId);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환`

        final Call<List<String>> getTime = NetRetrofit.getInstance().getService().getTimeCheck(objJson);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    timelist= getTime.execute().body();

                } catch (IOException e) {
                    Log.d("err", e.getMessage());
                }
            }
        }).start();

        return timelist;
    }

    public void TimeCheckInsert(String userId , String meetDate , String memo) {

        TimeCheckDTO dto = new TimeCheckDTO();
        dto.setUserId(userId);
        dto.setMeetDate(meetDate);
        dto.setMemo(memo);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환`

        Call<ResponseBody> TimeCheckInsert = NetRetrofit.getInstance().getService().TimeCheckInsert(objJson);
        TimeCheckInsert.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.v("insertt" , "insert 통신시도");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    public void TimeUpdate(String userId, String meetDate, String memo){

        TimeCheckDTO dto = new TimeCheckDTO();
        dto.setUserId(userId);
        dto.setMeetDate(meetDate);
        dto.setMemo(memo);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환`

        Call<ResponseBody> TimeCheckUpdate = NetRetrofit.getInstance().getService().TimeCheckUpdate(objJson);
        TimeCheckUpdate.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.v("updatett" , "update 통신시도");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }



    public void TimeDelete(String userId){

        TimeCheckDTO dto = new TimeCheckDTO();
        dto.setUserId(userId);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환`

        Call<ResponseBody> TimeCheckDelete = NetRetrofit.getInstance().getService().TimeCheckDelete(objJson);
        TimeCheckDelete.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.v("deletett" , "delte 통신시도");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public String getOnetimeCheck (String userId ,String meetDate){
        TimeCheckDTO dto = new TimeCheckDTO();
        dto.setUserId(userId);
        dto.setMeetDate(meetDate);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환`

        final Call<ResponseBody> getOneTimeCheck = NetRetrofit.getInstance().getService().getOneTimeCheck(objJson);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    memo= getOneTimeCheck.execute().body().string();
                } catch (IOException e) {
                    memo = "";
                }
            }
        }).start();

        return memo;

    }

}
