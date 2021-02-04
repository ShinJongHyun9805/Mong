package com.puppyland.mongnang.model;

import android.util.Log;

import com.google.gson.Gson;
import com.puppyland.mongnang.DTO.NotificationDTO;
import com.puppyland.mongnang.MainActivity;
import com.puppyland.mongnang.contract.NotificationContract;
import com.puppyland.mongnang.retrofitService.NetRetrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationModel {

    NotificationContract.Presenter presenter;
    public NotificationModel(NotificationContract.Presenter presenter) {
        this.presenter = presenter;
    }

    public void UpdateNotification(NotificationDTO dto){


        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> UpdateNotification = NetRetrofit.getInstance().getService().UpdateNotification(objJson);
        UpdateNotification.enqueue(new Callback<ResponseBody>() {
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
