package com.puppyland.mongnang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.puppyland.mongnang.DTO.ChatUserDTO;
import com.puppyland.mongnang.chat.ChatVPAdapter;
import com.puppyland.mongnang.fragment.ChatAcceptUserLIstFragment;
import com.puppyland.mongnang.fragment.ChatRoomFragment;
import com.puppyland.mongnang.fragment.SwipeViewPager;
import com.puppyland.mongnang.fragment.UserListFragment;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatMainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private List<ChatUserDTO> prelistCheck,  prelistCheckNoAccept;
    private SharedPreferences sharedPreferences1, sharedPreferences2;
    private String userID;
    private FloatingActionButton makeRoomBtn;
    Fragment userListFragment, chatRoomFragment, chatAcceptUserLIstFragment;
    ViewPager pager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        sendRegistrationToServer();

        //프라그먼트 생성
        userListFragment = new UserListFragment();
        chatRoomFragment = new ChatRoomFragment();
        chatAcceptUserLIstFragment = new ChatAcceptUserLIstFragment();

        //ChatMainActivity에 프라그먼트 뷰페이저로 등록
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        //페이저에 프라그먼트생성
        pager = findViewById(R.id.chatpager);
        pager.setOffscreenPageLimit(3);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());

        UserListFragment userListFragment = new UserListFragment();
        adapter.addItem(userListFragment);

        ChatRoomFragment chatRoomFragment = new ChatRoomFragment();
        adapter.addItem(chatRoomFragment);

        ChatAcceptUserLIstFragment chatAcceptUserLIstFragment = new ChatAcceptUserLIstFragment();
        adapter.addItem(chatAcceptUserLIstFragment);

        pager.setAdapter(adapter);


        // 화면 전환 프래그먼트 선언 및 초기 화면 설정
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.chatpager, userListFragment).commit();
        //뷰페이저 선언

        SwipeViewPager vp = findViewById(R.id.chatpager);
        ChatVPAdapter adapter1 = new ChatVPAdapter(getSupportFragmentManager());
        vp.setAdapter(adapter1);

        // 연동
        TabLayout tab = findViewById(R.id.tabs);
        tab.setupWithViewPager(vp);

        //아이콘 입력
        ArrayList<Integer> images = new ArrayList<>();
        images.add(R.drawable.chatuserlist);
        images.add(R.drawable.chatroom);
        images.add(R.drawable.chatusernoaddlist);

        //밑줄색
        tab.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"));

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

        for(int i=0; i<3; i++)
        {
            tab.getTabAt(i).setIcon(images.get(i));
        }
        tab.getTabAt(0).getIcon().setColorFilter(Color.parseColor("#FFFFD600"), PorterDuff.Mode.SRC_IN);


        // Set up the ViewPager with the sections adapter.
      //  mViewPager = (ViewPager) findViewById(R.id.container);
     //   mViewPager.setAdapter(mSectionsPagerAdapter);
      //  mViewPager.setOffscreenPageLimit(3);
      //  CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator2);
      //  indicator.setViewPager(mViewPager);


        prelistCheck = new ArrayList<ChatUserDTO>();
        prelistCheckNoAccept = new ArrayList<ChatUserDTO>();

        SharedPreferences sharedPreferences = getSharedPreferences("Loginfile", Activity.MODE_PRIVATE);
        userID = sharedPreferences.getString("id", "");

        //채팅유저 데이터를 미리 가져옴 이건 acceptNumber 가 1인 애들만 가져옴.
        //NoAcceptUser();

        //채팅유저 데이터를 미리 가져옴 이건 기존에 채팅추가 하던 코드. 이걸 이제 acceptNumber 가 3인 애들만 가져오게 바꿔아함
        ChatUserDTO chatUserDTO = new ChatUserDTO();
        //chatUserDTO.setChatUserId("master");
        chatUserDTO.setUserId(userID);
        chatUserDTO.setAcceptNumber("3");

        Gson gson = new Gson();
        String objJson = gson.toJson(chatUserDTO); // DTO 객체를 json 으로 변환

        Call<List<ChatUserDTO>> chatUserListCheck = NetRetrofit.getInstance().getService().chatUserListCheck(objJson);
        chatUserListCheck.enqueue(new Callback<List<ChatUserDTO>>() {
            @Override
            public void onResponse(Call<List<ChatUserDTO>> call, Response<List<ChatUserDTO>> response) {
                if (response.isSuccessful()) {
                    prelistCheck.addAll(response.body());
                    sharedPreferences1 = getSharedPreferences("ChatuserListfile", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences1.edit();
                    String chat = "";
                    for (int i = 0; i < prelistCheck.size(); i++) {
                        Log.v("wgewgahwhehe", prelistCheck.get(i).getChatUserId());
                        //onListItemClick 바로 아래에 있는 아이디를 넣는다.
                        chat = chat + prelistCheck.get(i).getChatUserId() + " ";
                    }
                    editor.putString("chatid", chat);
                    editor.commit(); // 로컬에 id 저장하는것.
                    prelistCheck = null; // sharedPreferences1에 넣어주고 메모리 해제
                }
            }

            @Override
            public void onFailure(Call<List<ChatUserDTO>> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });

    }
    void sendRegistrationToServer() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();

        Map<String, Object> map = new HashMap<>();
        map.put("token", token);

        FirebaseFirestore.getInstance().collection("users").document(uid).set(map, SetOptions.merge());
    }

    //프라그먼트 연결
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new UserListFragment();  //유저목록(친추)
                case 1:
                    return new ChatRoomFragment();  //채팅화면전
                default:
                    return new ChatAcceptUserLIstFragment(); // 채팅 수락거절
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    //채팅 요청온 유저 리스트
    private void NoAcceptUser(){
        ChatUserDTO chatUserDTO2 = new ChatUserDTO();
        chatUserDTO2.setUserId(userID);
        chatUserDTO2.setAcceptNumber("1");

        Gson gson2 = new Gson();
        String objJson2 = gson2.toJson(chatUserDTO2); // DTO 객체를 json 으로 변환

        Call<List<ChatUserDTO>> chatUserListChecknoAccept = NetRetrofit.getInstance().getService().chatUserListChecknoAccept(objJson2);
        chatUserListChecknoAccept.enqueue(new Callback<List<ChatUserDTO>>() {
            @Override
            public void onResponse(Call<List<ChatUserDTO>> call, Response<List<ChatUserDTO>> response) {
                if (response.isSuccessful()) {
                    prelistCheckNoAccept = response.body();

                    sharedPreferences2 = getSharedPreferences("ChatuserListfileNoAccept", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences2.edit();
                    String chat = "";
                    for (int i = 0; i < prelistCheckNoAccept.size(); i++) {
                        Log.d("###noAct", prelistCheckNoAccept.get(i).getChatUserId());
                        //onListItemClick 바로 아래에 있는 아이디를 넣는다.
                        chat = chat + prelistCheckNoAccept.get(i).getChatUserId() + " ";
                    }
                    editor.putString("NoAcceptChatid", chat);
                    editor.commit(); // 로컬에 id 저장하는것.
                    prelistCheckNoAccept = null;
                }
            }

            @Override
            public void onFailure(Call<List<ChatUserDTO>> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
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


}