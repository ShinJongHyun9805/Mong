package com.puppyland.mongnang.model;

import android.util.Log;

import com.puppyland.mongnang.DTO.MemberDTO;
import com.puppyland.mongnang.contract.EmailCertificationContract;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.puppyland.mongnang.retrofitService.RetrofitService;
import com.google.gson.Gson;

import okhttp3.ResponseBody;
import retrofit2.Call;
import static android.content.ContentValues.TAG;

public class EmailModel {

    int result;
    int result2; // 로그인할때 인증됐는지 알아보고 아니다 맞다 여부
    EmailCertificationContract.Presenter presenter;
    RetrofitService retrofitService;
    public EmailModel(EmailCertificationContract.Presenter presenter){
        this.presenter = presenter;
    }

    public int EmailCertificateCheck(String userId){

        MemberDTO dto = new MemberDTO();
        dto.setUserId(userId); // 업데이트 시켜줄 기준이 될 유저 아이디와

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환
        Log.d(TAG, objJson);

        final Call<ResponseBody> certificationCheck =  NetRetrofit.getInstance().getService().certificateEmailCheck(objJson);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    if(certificationCheck.execute().body().string().equals("2")){//인증이 된 상태일때
                        result2 =1;
                    }
                    else{
                        result2=0;
                    }
                }
                catch (Exception e){
                    Log.d(TAG, e.getMessage());
                }
            }
        }).start();
        try {
            Thread.sleep(2000);
        }catch (Exception e){
            e.printStackTrace();
        }

        return result2;
    }

    public int EmailCertificate(String userId){
    //여기다가 레트로핏 작성할것
        //이메일 검증 하는 레트로핏

        MemberDTO dto = new MemberDTO();
        dto.setUserId(userId); // 업데이트 시켜줄 기준이 될 유저 아이디와
        dto.setCertification("2"); // 인증을 시도할때 바꿔줄 1

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환
        Log.d(TAG, objJson);

        final Call<ResponseBody> certification =  NetRetrofit.getInstance().getService().certificateEmail(objJson);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    if(certification.execute().body().string().equals("1")){//로그인이 제대로 됐을때
                        result =1;
                    }
                    else{
                        result=0;
                    }
                }
                catch (Exception e){
                    Log.d(TAG, e.getMessage());
                }
            }
        }).start();
        try {
            Thread.sleep(1000);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result; // 여기가 이제 retrofit 해서 인증됐을때 update에 성공하면 1로 리턴 실패하면 0으로 리턴
    }
}
