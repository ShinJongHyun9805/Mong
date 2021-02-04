package com.puppyland.mongnang.model;

import android.util.Log;

import com.google.gson.JsonObject;
import com.puppyland.mongnang.DTO.DiaryDTO;
import com.puppyland.mongnang.contract.DiaryContract;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.google.gson.Gson;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiaryModel implements DiaryContract.presenter {

    DiaryContract.presenter diaryPresenter;

    public DiaryModel(DiaryContract.presenter presenter){
        this.diaryPresenter = presenter;
    }
    @Override
    public void InsertBoard(String title, String userid, String content, String ImageName ,int shareConfirmation ,String nickname , String tempFont) {
        DiaryDTO dto = new DiaryDTO();
        dto.setTitle(title);
        dto.setUserid(userid);
        dto.setDcontent(content);
        dto.setImg(ImageName);
        dto.setNickname(nickname);
        dto.setFont(tempFont);
        dto.setShareConfirmation(String.valueOf(shareConfirmation));

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> boardWrite = NetRetrofit.getInstance().getService().Writediary(objJson);
        boardWrite.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d("@@@su", "성공");
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });
    }

    @Override
    public void DeleteBoard(String dno) {
        DiaryDTO dto = new DiaryDTO();
        dto.setDno(dno);
        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> Deletediary = NetRetrofit.getInstance().getService().Deletediary(objJson);
        Deletediary.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d("@@@su", "성공");
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });
    }

    @Override
    public void UpdateBoard(String dno, String title, String content, String imgName) {
        DiaryDTO dto = new DiaryDTO();
        dto.setTitle(title);
        dto.setDcontent(content);
        dto.setImg(imgName);
        dto.setDno(dno);
        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> Updatediary = NetRetrofit.getInstance().getService().Updatediary(objJson);
        Updatediary.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d("@@@su", "성공");
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });
    }

    //다이어리 공유변호 변경해서 스토리에서 가져오는 공유변수를 2로 바꾸는 함수
    @Override
    public void ShareBoard(String dno , String userid, String title , String content, String imgName){

        DiaryDTO dto = new DiaryDTO();
        dto.setDno(dno);
        dto.setUserid(userid);
        dto.setTitle(title);
        dto.setDcontent(content);
        dto.setImg(imgName);
        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> shareUpdateStory = NetRetrofit.getInstance().getService().shareUpdateStory(objJson);
        shareUpdateStory.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d("@@@su", "성공");

                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });
    }

    @Override
    public  void ShareBoardoff(String dno ,String userid, String title , String content, String imgName){

        DiaryDTO dto = new DiaryDTO();
        dto.setDno(dno);
        dto.setUserid(userid);
        dto.setTitle(title);
        dto.setDcontent(content);
        dto.setImg(imgName);
        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> shareUpdateStoryoff = NetRetrofit.getInstance().getService().shareUpdateStoryoff(objJson);
        shareUpdateStoryoff.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d("@@@su", "성공");
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });
    }

    @Override
    public void insert_font(String userId, String fontname)
    {
        Gson gson = new Gson();

        JsonObject object = new JsonObject();
        object.addProperty("fontname", fontname);
        object.addProperty("userid", userId);

        String objJson = gson.toJson(object);
        Call<ResponseBody> insert_font = NetRetrofit.getInstance().getService().insert_font(objJson);
        insert_font.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String a = response.body().toString();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("###fail", t.getMessage());
            }
        });
    }

    @Override
    public void getfontlist(String userId){
        Gson gson = new Gson();
        JsonObject object = new JsonObject();
        object.addProperty("userid", userId);
        String objJson = gson.toJson(object);
        Call<List<String>> getfontList = NetRetrofit.getInstance().getService().getfontList(objJson);
        getfontList.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    List<String> list = response.body();
                    for(int i =0; i<list.size();i++){
                        Log.v("llkist", list.get(i));
                    }

                }
            }
            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.d("###fail", t.getMessage());
            }
        });
    }

    @Override
    public void desUpdateLike(String dno){
        DiaryDTO dto = new DiaryDTO();
        dto.setDno(dno);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> diarydesUpdateLike = NetRetrofit.getInstance().getService().diarydesUpdateLike(objJson);
        diarydesUpdateLike.enqueue(new Callback<ResponseBody>() {
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
    public void UpdateLike(String dno){
        DiaryDTO dto = new DiaryDTO();
        dto.setDno(dno);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> diaryUpdateLike = NetRetrofit.getInstance().getService().diaryUpdateLike(objJson);
        diaryUpdateLike.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("###updateLike", t.getMessage());
            }
        });
    }
    //내가 누른 조아여
    @Override
    public void InsertLike(String userID, String var){
        DiaryDTO dto = new DiaryDTO();
        dto.setUserid(userID);
        dto.setDno(var);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> diaryInsertLike = NetRetrofit.getInstance().getService().diaryInsertLike(objJson);
        diaryInsertLike.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("###updateLike", t.getMessage());
            }
        });
    }
    //안 조아여
    @Override
    public  void UnLike(String var, String id){
        DiaryDTO dto = new DiaryDTO();
        dto.setDno(var);
        dto.setUserid(id);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> diaryUnLike = NetRetrofit.getInstance().getService().diaryUnLike(objJson);
        diaryUnLike.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("###updateLike", t.getMessage());
            }
        });
    }

    /*
    @Override
    public int CountLike(String dno){

        DiaryDTO dto = new DiaryDTO();
        dto.setDno(dno);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> diaryCountLike = NetRetrofit.getInstance().getService().diaryCountLike(objJson);
        diaryCountLike.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("###updateLike", t.getMessage());
            }
        });
        return 0;
    }*/


}

