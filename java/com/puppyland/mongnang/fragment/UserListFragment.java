package com.puppyland.mongnang.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.puppyland.mongnang.DTO.ChatUserDTO;
import com.puppyland.mongnang.R;
import com.puppyland.mongnang.chat.ListViewAdapterAccept;
import com.puppyland.mongnang.retrofitService.NetRetrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserListFragment extends Fragment {


    //당겨서 새로고침
    private SwipeRefreshLayout swipe_refresh_layout;
    private ListView AcceptUserListview;
    private ListViewAdapterAccept adapter;
    private List<ChatUserDTO> AcceptUserlist;
    private String userID;

    public UserListFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        AcceptUserListview = view.findViewById(R.id.AcceptUserListview);

        //로그인한 유저 아이디
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Loginfile", Activity.MODE_PRIVATE);
        userID = sharedPreferences.getString("id", "");

        //당겨서 새로고침
        swipe_refresh_layout = view.findViewById(R.id.swipe_refresh_layout);
        //채팅 유저 목록
        ChatUserList();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //당겨서 새로고침 실 동작
        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //채팅 유저 목록
                ChatUserList();
            }
        });
    }

    private void ChatUserList() { // 여기서는 3만 가져오게 되어있는데 , spring xml 쪽에서 5인 애들도 가져오게 바꿀거임
        ChatUserDTO dto = new ChatUserDTO();
        dto.setUserId(userID);
        dto.setAcceptNumber("3");

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<List<ChatUserDTO>> AcceptUser = NetRetrofit.getInstance().getService().AcceptUser(objJson);
        AcceptUser.enqueue(new Callback<List<ChatUserDTO>>() {
            @Override
            public void onResponse(Call<List<ChatUserDTO>> call, Response<List<ChatUserDTO>> response) {
                AcceptUserlist = response.body();
                    adapter = new ListViewAdapterAccept();
                    AcceptUserListview.setAdapter(adapter);
                    try {
                        if (AcceptUserListview != null) {
                            for (ChatUserDTO chatUserDTO : AcceptUserlist) { // 리스트로 담기는// 것들 하나씩 출력

                                adapter.addItem(chatUserDTO.getChatUserId(), chatUserDTO.getMemberimage(), chatUserDTO.getNickname(), chatUserDTO.getAcceptNumber(), chatUserDTO.getUserId());
                            }
                        } else {
                            Toast.makeText(getContext(), "검색결과가 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //새로고침 완료 !!필수!!
                    swipe_refresh_layout.setRefreshing(false);
                }
            @Override
            public void onFailure(Call<List<ChatUserDTO>> call, Throwable t) {
                Log.d("허용된 유저 목록 가져오기 실패", t.getMessage());
            }
        });
    }

}