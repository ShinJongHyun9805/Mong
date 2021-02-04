package com.puppyland.mongnang.fragment;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.puppyland.mongnang.DTO.ChatUserDTO;
import com.puppyland.mongnang.MainActivity;
import com.puppyland.mongnang.R;
import com.puppyland.mongnang.chat.ListViewAdapterNoAccept;
import com.puppyland.mongnang.retrofitService.NetRetrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatAcceptUserLIstFragment extends Fragment {

    private ListView NoAcceptList;
    private ListViewAdapterNoAccept adapter;
    private List<ChatUserDTO> listCheckNoAccept;
    private String userID;
    public ChatAcceptUserLIstFragment() {

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
        View view = inflater.inflate(R.layout.fragment_chat_accept_user_l_ist, container, false);
        //다크모드 해제
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        NoAcceptList = view.findViewById(R.id.NoAcceptList);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Loginfile", Activity.MODE_PRIVATE);
        userID = sharedPreferences.getString("id", "");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        /**************************************************************************************************************
         * 나한테 요청 온 유저 목록 가져오기
         ***************************************************************************************************************/
        ChatUserDTO chatUserDTO2 = new ChatUserDTO();
        chatUserDTO2.setUserId(userID);
        chatUserDTO2.setAcceptNumber("1");

        Gson gson2 = new Gson();
        String objJson2 = gson2.toJson(chatUserDTO2);

        Call<List<ChatUserDTO>> chatUserListChecknoAccept = NetRetrofit.getInstance().getService().chatUserListChecknoAccept(objJson2);
        chatUserListChecknoAccept.enqueue(new Callback<List<ChatUserDTO>>() {
            @Override
            public void onResponse(Call<List<ChatUserDTO>> call, Response<List<ChatUserDTO>> response) {
                listCheckNoAccept = response.body();
                    adapter = new ListViewAdapterNoAccept();
                    NoAcceptList.setAdapter(adapter);
                    try {
                        if (NoAcceptList != null) {
                            for (ChatUserDTO chatUserDTO : listCheckNoAccept) { // 리스트로 담기는// 것들 하나씩 출력
                                adapter.addItem(chatUserDTO.getChatUserId(), chatUserDTO.getUserId() , chatUserDTO.getMemberimage() , chatUserDTO.getNickname());
                            }
                        } else {
                            Toast.makeText(getContext(), "검색결과가 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            @Override
            public void onFailure(Call<List<ChatUserDTO>> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });
    }
}