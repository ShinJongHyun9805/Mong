package com.puppyland.mongnang.model;

import android.util.Log;

import com.puppyland.mongnang.DTO.WeatherDTO;
import com.puppyland.mongnang.contract.WeatherContract;
import com.puppyland.mongnang.fragment.Fragment2;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherModel {

    String res;
    WeatherContract.presenter presenter;
    Fragment2 fragment2;

    public WeatherModel(WeatherContract.presenter presenter) {
        this.presenter = presenter;
    }

    public void Location(String se, String gu, String dong) {
        WeatherDTO dto = new WeatherDTO();
        dto.setSe(se);
        dto.setGu(gu);
        dto.setDong(dong);

        Log.d("!!!@", dto.getSe());

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

        Call<ResponseBody> LocationAddress = NetRetrofit.getInstance().getService().LocationAddress(objJson);
        LocationAddress.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("!!!", "성공");
                    try {
                        //넘어온 값
                        res = response.body().string();
                       ((Fragment2)Fragment2.mContext).weatherValue(res); //여기가 문제일 가능성이크다.
                        Log.d("!!!!!", res);

                    } catch (IOException e) {
                        Log.d("!!!", e.getMessage());
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("err", t.getMessage());
            }
        });
    }

}