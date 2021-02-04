package com.puppyland.mongnang.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.puppyland.mongnang.DTO.MemberDTO;
import com.puppyland.mongnang.R;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.puppyland.mongnang.userClickImageActivity;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserProfileFragment extends Fragment {

    String id , nickname;
    CircleImageView circleImageView;
    String fileName ,mymsg;
    TextView profileUserid , profilemymsg , followCnt , followerCnt , user_age ,user_sex , usercity;

    public static UserProfileFragment newInstance(String selectedId , String nickname) {
        return new UserProfileFragment(selectedId , nickname);
    }
    public UserProfileFragment(){

    }
    public UserProfileFragment(String selectedId , String nickname) {
        this.id = selectedId;
        this.nickname = nickname;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_user_profile, container, false);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        circleImageView= view.findViewById(R.id.userProfileImage);
        profileUserid = view.findViewById(R.id.userid);
        profileUserid.setText(nickname);

        user_age = view.findViewById(R.id.userage);
        user_sex = view.findViewById(R.id.usersex);
        usercity = view.findViewById(R.id.usercity);
        MemberDTO dto = new MemberDTO();
        dto.setUserId(id);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

        circleImageView.setOnClickListener(new View.OnClickListener() {//스토리에서 프로필 누르면 확대되서 원본 사진 나오게 해야함
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getContext() , userClickImageActivity.class);
                intent1.putExtra("id" ,id);
                startActivity(intent1);
            }
        });

        final Call<MemberDTO> getMyInfo = NetRetrofit.getInstance().getService().getMyInfo(objJson);
        getMyInfo.enqueue(new Callback<MemberDTO>() {
            @Override
            public void onResponse(Call<MemberDTO> call, Response<MemberDTO> response) {
                if (response.isSuccessful()) {
                    MemberDTO dto = response.body();

                    fileName = dto.getMemberimage();
                    Picasso.get().load("http://192.168.219.100:8092/upload/" + fileName).error(R.drawable.monglogo2).fit().centerCrop().into(circleImageView);

                    if(dto.getAge() == null){
                        user_age.setText("0"+"세");
                    }else{
                        user_age.setText(dto.getAge()+"세");
                    }

                    if(dto.getGender().equals("m")){
                        user_sex.setText("/남/");
                    }else if(dto.getGender().equals("f")){
                        user_sex.setText("/여/");
                    }
                    usercity.setText(dto.getAddress1()); //도시 나오도록 입력

                    profileUserid.setText(dto.getNickname());
                  //  try {
                  //      profilemymsg.setText('"'+dto.getUsermsg()+'"');
                   //     if(profilemymsg.getText().toString().equals('"'+'"')){
                  //          profilemymsg.setText("상태메세지가 등록되지 않았습니다.");
                    //    }
                   // } catch (Exception e) {
                   //     profilemymsg.setText("");
                   // }
                }
            }

            @Override
            public void onFailure(Call<MemberDTO> call, Throwable t) {
                Log.d("내 사진 가져오기 실패", t.getMessage());
            }
        });


        return  view;
    }
}