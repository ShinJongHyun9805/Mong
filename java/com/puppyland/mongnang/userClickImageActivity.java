package com.puppyland.mongnang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.puppyland.mongnang.DTO.MemberDTO;
import com.puppyland.mongnang.contract.ImageContract;
import com.puppyland.mongnang.presenter.ImagePresenter;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class userClickImageActivity extends AppCompatActivity implements ImageContract.View{ // 클릭했을때 사진 원본 띄워주는 액티비티

    ImageView userImage;
    String fileName;
    ImageContract.Presenter presenter;
    String dogimage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_click_image);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        presenter = new ImagePresenter(this);
        String userID = getIntent().getStringExtra("id");
        userImage= findViewById(R.id.userImage);
        try {
            dogimage = getIntent().getStringExtra("dogimage");
        }catch (Exception e){
            dogimage = null;
        }
        getmyImgName(userID);


    }

    //내 사진 가져오기
    public void getmyImgName(String userID) {

        MemberDTO dto = new MemberDTO();
        dto.setUserId(userID);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

        Call<MemberDTO> fileName1 = NetRetrofit.getInstance().getService().fileName1(objJson);
        fileName1.enqueue(new Callback<MemberDTO>() {
            @Override
            public void onResponse(Call<MemberDTO> call, Response<MemberDTO> response) {
                if (response.isSuccessful()) {
                    MemberDTO dto = response.body();


                    fileName = dto.getMemberimage();
                    if(dogimage == null){
                        Picasso.get().load("http://192.168.219.100:8092/upload/" + fileName).error(R.drawable.monglogo2).fit().centerCrop().into(userImage);
                    }
                    else{
                        try {
                            Picasso.get().load("http://192.168.219.100:8092/upload/" + dogimage).error(R.drawable.monglogo2).fit().centerCrop().into(userImage);
                        }catch (Exception e){
                            Picasso.get().load("http://192.168.219.100:8092/upload/" + dogimage).error(R.drawable.monglogo2).fit().centerCrop().into(userImage);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MemberDTO> call, Throwable t) {
                Log.d("###fail", t.getMessage());
            }

        });
    }
    @Override
    public void getMyImage(MemberDTO member) {
        fileName = member.getMemberimage();
    }

}