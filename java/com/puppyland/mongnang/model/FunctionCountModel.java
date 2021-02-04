package com.puppyland.mongnang.model;

import android.content.Context;
import android.util.Log;

import com.puppyland.mongnang.DTO.FunctionCountDTO;
import com.puppyland.mongnang.contract.FunctionCountContract;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.puppyland.mongnang.retrofitService.RetrofitService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FunctionCountModel {

    FunctionCountContract.Presenter presenter;
    RetrofitService retrofitService;
    private Context mContext;
    public FunctionCountModel(FunctionCountContract.Presenter presenter) {
        this.presenter = presenter;
    }

    public void functionCount_update_changepdf(String userId, int count) {

        Gson gson = new Gson();
        JsonObject object = new JsonObject();
        object.addProperty("count", count);
        object.addProperty("userId", userId);
        String objJson = gson.toJson(object);

        Call<ResponseBody> functionCount_update_changepdf = NetRetrofit.getInstance().getService().functionCount_update_changepdf(objJson);
        functionCount_update_changepdf.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("res", "성공");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });
    }

    public void functionCount_tbl_functionCount_insert_userId(String userId) {
        FunctionCountDTO dto = new FunctionCountDTO();
        dto.setUserid(userId);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> functionCount_tbl_functionCount_insert_userId = NetRetrofit.getInstance().getService().functionCount_tbl_functionCount_insert_userId(objJson);
        functionCount_tbl_functionCount_insert_userId.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("###", "성공");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("###fail", t.getMessage());
            }
        });
    }

    public void functionCount_tbl_membership_insert_userId(String userId) {
        FunctionCountDTO dto = new FunctionCountDTO();
        dto.setUserid(userId);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> functionCount_tbl_membership_insert_userId = NetRetrofit.getInstance().getService().functionCount_tbl_membership_insert_userId(objJson);
        functionCount_tbl_membership_insert_userId.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("###", "성공:)");

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("###fail", t.getMessage());
            }
        });
    }


    public void functionCount_update_rating(String userId, String rating) {
        Gson gson = new Gson();
        JsonObject object = new JsonObject();
        object.addProperty("rating", rating);
        object.addProperty("userId", userId);
        String objJson = gson.toJson(object);

        Call<ResponseBody> functionCount_update_rating = NetRetrofit.getInstance().getService().functionCount_update_rating(objJson);
        functionCount_update_rating.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    //jump권
    public void functionCount_update_jump(String userId, int count) {

        Gson gson = new Gson();

        JsonObject object = new JsonObject();
        object.addProperty("count", count);
        object.addProperty("userId", userId);

        String objJson = gson.toJson(object);

        Call<ResponseBody> functionCount_update_jump = NetRetrofit.getInstance().getService().functionCount_update_jump(objJson);
        functionCount_update_jump.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("###", "성공");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("###fail", t.getMessage());
            }
        });
    }

    public void functionCount_update_point(FunctionCountDTO dto, int flag) {

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> functionCount_update_point = NetRetrofit.getInstance().getService().functionCount_update_point(flag, objJson);
        functionCount_update_point.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("###", "구매 완료");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("###fail", t.getMessage());
            }
        });
    }


    /************************************************************************************************************************************************
     * PDF 구매
     * **********************************************************************************************************************************************/
    public void functionCount_purchase_pdf(FunctionCountDTO dto, int flag){
        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> functionCount_purchase_pdf = NetRetrofit.getInstance().getService().functionCount_purchase_pdf(flag, objJson);
        functionCount_purchase_pdf.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("PDF 구매 실패", t.getMessage());
            }
        });
    }

    /************************************************************************************************************************************************
     * jump chat 구매
     * **********************************************************************************************************************************************/
    public void functionCount_purchase_chat(FunctionCountDTO dto, int flag){
        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> functionCount_purchase_chat = NetRetrofit.getInstance().getService().functionCount_purchase_chat(flag, objJson);
        functionCount_purchase_chat.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("jump chat 구매 실패", t.getMessage());
            }
        });
    }
}