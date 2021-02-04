package com.puppyland.mongnang;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;
import com.puppyland.mongnang.DTO.ChatUserDTO;
import com.puppyland.mongnang.DTO.DeviceIdDTO;
import com.puppyland.mongnang.DTO.DiaryDTO;
import com.puppyland.mongnang.DTO.FollowDTO;
import com.puppyland.mongnang.DTO.MemberDTO;
import com.puppyland.mongnang.DTO.VideoDTO;
import com.puppyland.mongnang.Diary.DiaryAdapter;
import com.puppyland.mongnang.contract.ChatuserListContract;
import com.puppyland.mongnang.contract.FunctionCountContract;
import com.puppyland.mongnang.contract.ImageContract;
import com.puppyland.mongnang.contract.VideoContract;
import com.puppyland.mongnang.fragment.DogProfileFragment;
import com.puppyland.mongnang.fragment.SelectedStoryVIewFragment;
import com.puppyland.mongnang.fragment.SelectedVideoVIewFragment;
import com.puppyland.mongnang.fragment.SwipeViewPager;
import com.puppyland.mongnang.fragment.UserProfileFragment;
import com.puppyland.mongnang.fragment.VPSelctedAdapter;
import com.puppyland.mongnang.presenter.ChatuserListPresenter;
import com.puppyland.mongnang.presenter.FunctionCountPresenter;
import com.puppyland.mongnang.presenter.ImagePresenter;
import com.puppyland.mongnang.presenter.VideoPresenter;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectedUserDiaryActivity extends AppCompatActivity implements ImageContract.View , FunctionCountContract.View , ChatuserListContract.View , VideoContract.view {
    GridView mRecyclerView;
    private ArrayList<DiaryDTO> mArrayList;
    private DiaryAdapter mAdapter;
    EditText editText;
    TextView title;
    String id ,Count_jump , nickname;
    CircleImageView circleImageView , dogProfileImage;
    TextView profileUserid , profilemymsg , followCnt , followerCnt , user_age ,user_sex;
    TextView dogname , dogage , dogsex , dogsize;
    ImageContract.Presenter presenter;
    String fileName ,mymsg;
    int result; // 같은 체크 변수

    ChatuserListContract.Presenter chatuserListPresenter;
    List<ChatUserDTO> prelistCheck;

    VideoContract.presenter vpresenter;
    //팔로우 체크
    private boolean follow;
    List<VideoDTO> list; // 팔로워 리스트
    List<FollowDTO> list2; //팔로우 리스트

    FunctionCountContract.Presenter functionCountContractPresenter;
    private TextView btn_jumpchat , followBnt;
    String loginId;

    ViewPager profilepager; // 뷰페이저
    ViewPager pager; // 뷰페이저
    private FragmentManager fragmentManager;
    private Fragment stroyView;

    //채팅하기 눌렀는지 안눌렀는지
    private Boolean check = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_selected_user_diary);
        presenter = new ImagePresenter(this);

        SharedPreferences sharedPreferences = getSharedPreferences("Loginfile",MODE_PRIVATE); // receiver 에선 context로 접근
        loginId = sharedPreferences.getString("id",null); // 지금 로그인한 사람 id

        SharedPreferences sharedPreferences2 = getSharedPreferences("nickname", MODE_PRIVATE);
        final String getnickname = sharedPreferences2.getString("nickname", loginId);

        Intent intent = getIntent();
        id = intent.getExtras().getString("selectedId");
        nickname = intent.getExtras().getString("selectedNickname");
        Log.v("iddd" , id);
        Log.v("nickname" , nickname);
        functionCountContractPresenter = new FunctionCountPresenter(this);
        chatuserListPresenter = new ChatuserListPresenter(this);
        vpresenter = new VideoPresenter(this);
        FollowingListCount(nickname); //내가 따르는 사람
        FollwerListCount(nickname); // 나를 따르는 사람
        followerCnt = findViewById(R.id.followerCnt);
        followerCnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FollowDTO dto = new FollowDTO();
                dto.setNickname(nickname);
                Gson gson = new Gson();
                String objJson = gson.toJson(dto);
                list2 = new ArrayList<FollowDTO>();

                Call<List<FollowDTO>> FollwerList = NetRetrofit.getInstance().getService().FollwerList(objJson);
                FollwerList.enqueue(new Callback<List<FollowDTO>>() {
                    @Override
                    public void onResponse(Call<List<FollowDTO>> call, Response<List<FollowDTO>> response) {
                        list2 = response.body();
                        if (list2.size() != 0) {
                            Intent intent = new Intent(SelectedUserDiaryActivity.this, FollowerActivity.class);
                            intent.putExtra("list", (Serializable) list2);
                            startActivity(intent);
                        } else {
                            Toast.makeText(SelectedUserDiaryActivity.this.getApplicationContext(), "아직 " + getnickname + " 의 팔로워가 없어요.. ", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<FollowDTO>> call, Throwable t) {

                    }
                });
            }
        });
        followCnt = findViewById(R.id.followCnt);
        followCnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoDTO dto = new VideoDTO();
                dto.setMynickname(nickname);
                Gson gson = new Gson();
                String objJson = gson.toJson(dto);
                list = new ArrayList<VideoDTO>();
                Call<List<VideoDTO>> followingList = NetRetrofit.getInstance().getService().FollowingList(objJson);
                followingList.enqueue(new Callback<List<VideoDTO>>() {
                    @Override
                    public void onResponse(Call<List<VideoDTO>> call, Response<List<VideoDTO>> response) {
                        list = response.body();
                        if (list.size() != 0) {
                            Intent intent = new Intent(SelectedUserDiaryActivity.this, FollowingActivity.class);
                            intent.putExtra("Followinglist", (Serializable) list);
                            startActivity(intent);
                        } else {
                            Toast.makeText(SelectedUserDiaryActivity.this, "아직 팔로우가 없어요..", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<VideoDTO>> call, Throwable t) {
                        Log.e("###FollowingFail", t.getMessage());
                    }
                });
            }
        });
        //여기까지 상단 프로필
        //여기부턴 아래 뷰페이저
        pager= findViewById(R.id.selectViewpager);
        pager.setOffscreenPageLimit(2);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        SelectedStoryVIewFragment selectedStoryVIewFragment = new SelectedStoryVIewFragment();
        adapter.addItem(selectedStoryVIewFragment);
        SelectedVideoVIewFragment selectedVideoVIewFragment = new SelectedVideoVIewFragment();
        adapter.addItem(selectedVideoVIewFragment); // 아래 뷰 페이저에 나올 두화면을 어뎁터에 연결.
        pager.setAdapter(adapter);

        profilepager = findViewById(R.id.profilepager);
        profilepager.setOffscreenPageLimit(2);

        PagerAdapter adapter2 = new PagerAdapter(getSupportFragmentManager());
        UserProfileFragment userProfileFragment = new UserProfileFragment(id , nickname);
        adapter2.addItem(userProfileFragment);
        DogProfileFragment dogProfileFragment = new DogProfileFragment(id);
        adapter2.addItem(dogProfileFragment); // 아래 뷰 페이저에 나올 두화면을 어뎁터에 연결.
        profilepager.setAdapter(adapter2);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.selectViewpager, SelectedStoryVIewFragment.newInstance()).commit();

        FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
        fragmentTransaction2.add(R.id.profilepager, UserProfileFragment.newInstance(id, nickname)).commit();

        //인디케이터
        SwipeViewPager vp = findViewById(R.id.selectViewpager);
        VPSelctedAdapter adapter1 = new VPSelctedAdapter(getSupportFragmentManager());
        vp.setAdapter(adapter1);

        //인디케이터
        SwipeViewPager vp2 = findViewById(R.id.profilepager);
        VPSelctedAdapter adapter3 = new VPSelctedAdapter(getSupportFragmentManager());
        vp2.setAdapter(adapter2);

        // 연동

        TabLayout tab = findViewById(R.id.selectedtab);
        tab.setupWithViewPager(vp);

        tab.getTabAt(0).setText("다이어리");
        tab.getTabAt(1).setText("토독");

        ArrayList<Integer> images = new ArrayList<>();
        images.add(R.drawable.booktab);
        images.add(R.drawable.videotab);
        for(int i=0; i<2; i++) tab.getTabAt(i).setIcon(images.get(i));

        TabLayout tab2 = findViewById(R.id.selectedprofiletab);
        tab2.setupWithViewPager(vp2);

        //ArrayList<Integer> images2 = new ArrayList<>();
       // images2.add(R.drawable.sex);
       // images2.add(R.drawable.dog);
      //  for(int i=0; i<2; i++) tab2.getTabAt(i).setIcon(images2.get(i));
        tab2.getTabAt(0).setText("사람");
        tab2.getTabAt(1).setText("반려동물");

        tab.getTabAt(0).getIcon().setColorFilter(Color.parseColor("#FFFFD600"), PorterDuff.Mode.SRC_IN);
        tab.setTabTextColors(Color.parseColor("#FFAEAEAE"), Color.parseColor("#FFFFD600"));
        tab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor("#FFFFD600"), PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor("#FFAEAEAE"), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        CheckFollow(nickname , getnickname);
        //채팅 요청 확인
        CheckConnectChat();

        followBnt = findViewById(R.id.followBnt); // 팔로우 버튼

        if(!nickname.equals(getnickname)){
            followBnt.setTextColor(Color.parseColor("#000000"));
            followBnt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (follow == false) {
                        vpresenter.UnFollow(nickname);
                        //  FollwerListCount(nickname); // 나를 따르는 사람
                        followerCnt.setText(String.valueOf(Integer.parseInt(followerCnt.getText().toString())-1));
                        followBnt.setText("팔로잉");
                        follow = true;
                    } else {
                        vpresenter.FollowVideo(loginId, nickname, getnickname, id);
                        //   FollwerListCount(nickname); // 나를 따르는 사람
                        followerCnt.setText(String.valueOf(Integer.parseInt(followerCnt.getText().toString())+1));
                        followBnt.setText("언팔");
                        follow = false;
                    }
                }
            });
        }


        btn_jumpchat = findViewById(R.id.btn_jumpchat);
        if(!nickname.equals(getnickname)){
            btn_jumpchat.setTextColor(Color.parseColor("#000000"));
            btn_jumpchat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (check == false) {
                        //상대와 나의 지역이 같은 곳인지 확인
                      //  CheckSamePlace(); // 지역이 같은지 판단하는거 일단 이거 제외함

                        //다른 지역도 잘되는거 확인함 2020 10 28

                        chatuserListPresenter.ChatuserListInsert(loginId, id, "2");
                        btn_jumpchat.setText("수락 대기중");
                        // 채팅요청을 보내면 요청을 받는 사람에게 푸시알람을 보내는 코드
                        getDeviceId(id, 3,"");
                        //채팅 요청 확인
                        CheckConnectChat();
                        btn_jumpchat.setClickable(false);
                    } else {
                        btn_jumpchat.setText("수락 대기중");
                        btn_jumpchat.setClickable(false);
                    }
                }
            });
        }

    }

    //어댑터 안에서 각각의 아이템을 데이터로서 관리한다
    class PagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> items = new ArrayList<Fragment>();
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }
        public void addItem(Fragment item) {
            items.add(item);
        }
        @Override
        public Fragment getItem(int position) {
            return items.get(position);
        }
        @Override
        public int getCount() {
            return items.size();
        }
    }

    //글 작성 후 새로고침
    @Override
    public void onResume() {
        super.onResume();

        /*
        MemberDTO dto = new MemberDTO();
        dto.setUserId(id);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

        final Call<MemberDTO> getMyInfo = NetRetrofit.getInstance().getService().getMyInfo(objJson);
        getMyInfo.enqueue(new Callback<MemberDTO>() {
            @Override
            public void onResponse(Call<MemberDTO> call, Response<MemberDTO> response) {
                if (response.isSuccessful()) {
                    MemberDTO dto = response.body();

                    fileName = dto.getMemberimage();
                    Picasso.get().load("https://mongnyang.shop/upload/" + fileName).error(R.drawable.monglogo2).fit().centerCrop().into(circleImageView);

                    user_age.setText(dto.getAge()+"세");
                    if(dto.getGender().equals("m")){
                        user_sex.setText("/남");
                    }else if(dto.getGender().equals("f")){
                        user_sex.setText("/여");
                    }

                    profileUserid.setText(dto.getNickname());
                    try {
                        profilemymsg.setText('"'+dto.getUsermsg()+'"');
                        if(profilemymsg.getText().toString().equals('"'+'"')){
                            profilemymsg.setText("상태메세지가 등록되지 않았습니다.");
                        }
                    } catch (Exception e) {
                        profilemymsg.setText("");
                    }
                }
            }

            @Override
            public void onFailure(Call<MemberDTO> call, Throwable t) {
                Log.d("내 사진 가져오기 실패", t.getMessage());
            }
        });*/

       // dogInfo(id);
        // 여기에 도그 프라그먼트에서 도그 인포 가져오는 코드 작성
    }

    /*
    private void dogInfo(String id) {
        DogDTO dto = new DogDTO();
        dto.setUserId(id);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

        Adto = new ArrayList<DogDTO>();
        Call<List<DogDTO>> dogfileName = NetRetrofit.getInstance().getService().dogfileName(objJson);
        dogfileName.enqueue(new Callback<List<DogDTO>>() {
            @Overrideg
            public void onResponse(Call<List<DogDTO>> call, Response<List<DogDTO>> response) {
                Adto = response.body();
                if(Adto == null){

                }
                for (DogDTO dog : Adto) {
                    Picasso.get().load("https://mongnyang.shop/upload/" + dog.getDogImage()).error(R.drawable.monglogo2).fit().centerCrop().into(dogProfileImage);
                    dogname.setText( dog.getDogName());
                    dogsex.setText(dog.getDogGender());
                    dogage.setText(dog.getDogAge());
                    dogsize.setText(dog.getDogKind());
                }
            }

            @Override
            public void onFailure(Call<List<DogDTO>> call, Throwable t) {
                Log.e("###DogInfofail", t.getMessage());
            }
        });
    }*/


    public void FollowingListCount(String nickname){
        VideoDTO dto = new VideoDTO();
        dto.setMynickname(nickname);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);


        Call<ResponseBody> FollowingListCount = NetRetrofit.getInstance().getService().FollowingListCount(objJson);
        FollowingListCount.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    followCnt.setText(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("###FollowingFail", t.getMessage());
            }
        });
    }
    public void FollwerListCount(String nickname){
        FollowDTO dto = new FollowDTO();
        dto.setNickname(nickname);
        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> FollwerListCount = NetRetrofit.getInstance().getService().FollwerListCount(objJson);
        FollwerListCount.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    followerCnt.setText(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("###FollowingFail", t.getMessage());
            }
        });

    }



    private void CheckFollow(String getnickname, String nickname) {
        VideoDTO dto = new VideoDTO();
        dto.setNickname(getnickname);
        dto.setMynickname(nickname);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> CheckFollow = NetRetrofit.getInstance().getService().CheckFollow(objJson);
        CheckFollow.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    int result = Integer.parseInt(response.body().string());
                    if (result == 1) {
                        follow = false;
                        followBnt.setText("언팔");

                    } else {
                        follow = true;
                        followBnt.setText("팔로잉");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("###checkfollowfail", t.getMessage());
            }
        });
    }


    private void getDeviceId(String userid ,  final int flag , final String dno) { // 이 함수를 가져다 쓰기만하면 3개를 다 돌려막기 할 수 있음.
        DeviceIdDTO deviceIdDTO = new DeviceIdDTO();
        deviceIdDTO.setId(id);

        Gson gson = new Gson();
        String objJson = gson.toJson(deviceIdDTO);

        Call<DeviceIdDTO> getDeviceId = NetRetrofit.getInstance().getService().getDeviceId(objJson);
        getDeviceId.enqueue(new retrofit2.Callback<DeviceIdDTO>() {
            @Override
            public void onResponse(Call<DeviceIdDTO> call, Response<DeviceIdDTO> response) {
                if (response.isSuccessful()) {
                    Log.d("@@@su", "성공");


                    String tempDeviceid = response.body().getDeviceId();
                    DeviceIdDTO deviceIdDTO = new DeviceIdDTO();
                    deviceIdDTO.setDeviceId(tempDeviceid);
                    Log.d("@@@char", id);
                    deviceIdDTO.setId(id);
                    Gson gson = new Gson();
                    String objJson = gson.toJson(deviceIdDTO);
                    Call<ResponseBody> boardPushAlarm = NetRetrofit.getInstance().getService().PushAlarm(flag,objJson ,"0");
                    boardPushAlarm.enqueue(new retrofit2.Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Log.d("@@@su", "성공222");
                            }else{
                                Log.d("@@@su2", "반응없");
                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.d("fail", t.getMessage());
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<DeviceIdDTO> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });
    }

    /***********************************************************************************************************
     * 채팅 요청을 했는지 안했는지
     ***********************************************************************************************************/
    private void CheckConnectChat() {
        ChatUserDTO dto = new ChatUserDTO();
        dto.setUserId(loginId);
        dto.setChatUserId(id);
        dto.setAcceptNumber("2");
        Log.v("weijfmvowev2", String.valueOf(loginId));
        Log.v("weijfmvowev3", String.valueOf(id));
        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

        Call<ResponseBody> CheckConnectChat = NetRetrofit.getInstance().getService().CheckConnectChat(objJson);
        CheckConnectChat.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        int result = Integer.parseInt(response.body().string());
                        Log.v("weijfmvowev", String.valueOf(result));
                        if (result == 1) {
                            check = true;
                            btn_jumpchat.setText("수락 대기중");
                            btn_jumpchat.setClickable(false);
                        } else if(result == 3) {

                            check = false;
                        }
                        else if(result ==4){ // 이미 채팅중 이게4인 이유는 컨트롤러쪽 보면 바로 알 수 있음 주석 적어놨음
                            btn_jumpchat.setText("채팅중");
                            btn_jumpchat.setClickable(false);
                        }
                        else if(result ==5){//차단당함
                            btn_jumpchat.setText("차단당함");
                            btn_jumpchat.setClickable(false);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("채팅 요청 체크 실패", t.getMessage());
            }
        });
    }


    @Override
    public void getMyImage(MemberDTO member) {
      //  mymsg = member.getUsermsg();
       // fileName = member.getMemberimage();
    }

}