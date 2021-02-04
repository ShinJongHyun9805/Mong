package com.puppyland.mongnang.model;

import android.util.Log;

import com.google.gson.Gson;
import com.puppyland.mongnang.DTO.FollowDTO;
import com.puppyland.mongnang.DTO.VideoDTO;
import com.puppyland.mongnang.MainActivity;
import com.puppyland.mongnang.contract.VideoContract;
import com.puppyland.mongnang.retrofitService.NetRetrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoModel implements VideoContract.presenter {

    VideoContract.presenter videoPresenter;

    public VideoModel(VideoContract.presenter presenter){
        this.videoPresenter = presenter;
    }

    @Override
    public void InsertVideo(String userID, String name , String nickname) {
        VideoDTO dto = new VideoDTO();
        dto.setUserid(userID);
        dto.setVideoname(name);
        dto.setNickname(nickname);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> InsertVideo = NetRetrofit.getInstance().getService().InsertVideo(objJson);
        InsertVideo.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d("###videosu", "성공");
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("###Videofail", t.getMessage());
            }
        });
    }

    @Override
    public void UpdateLike(String VideoName) {
        VideoDTO dto = new VideoDTO();
        dto.setVideoname(VideoName);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> UpdateLike = NetRetrofit.getInstance().getService().UpdateLike(objJson);
        UpdateLike.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("###updateLike", t.getMessage());
            }
        });
    }

    @Override
    public void FollowVideo(String userID, String getnickname, String nickname, String videouserID ) {
        FollowDTO dto = new FollowDTO();
        dto.setUserid(userID);
        dto.setNickname(getnickname);
        dto.setMynickname(nickname);
        dto.setVideouserid(videouserID);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> FollowVideo  = NetRetrofit.getInstance().getService().FollowVideo(objJson);
        FollowVideo.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("###Followfail", t.getMessage());
            }
        });
    }

    @Override
    public void InsertLike(String userID, int var) {
        VideoDTO dto = new VideoDTO();
        dto.setUserid(userID);
        dto.setVno(var);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> InsertLike = NetRetrofit.getInstance().getService().InsertLike(objJson);
        InsertLike.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("###InsertLike", t.getMessage());
            }
        });
    }

    @Override
    public void UnLike(int var, String id) {
        VideoDTO dto = new VideoDTO();
        dto.setVno(var);
        dto.setUserid(id);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> UnLike = NetRetrofit.getInstance().getService().UnLike(objJson);
        UnLike.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("###UnLike", t.getMessage());
            }
        });

    }

    @Override
    public void UnFollow(String nickname) {
        VideoDTO dto = new VideoDTO();
        dto.setNickname(nickname);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> UnFollow = NetRetrofit.getInstance().getService().UnFollow(objJson);
        UnFollow.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("###UnFollow", t.getMessage());
            }
        });
    }

    @Override
    public void subtractLike(String VideoName) {
        VideoDTO dto = new VideoDTO();
        dto.setVideoname(VideoName);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> subtractLike = NetRetrofit.getInstance().getService().subtractLike(objJson);
        subtractLike.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("좋아요 차감 실패", t.getMessage());
            }
        });
    }

}