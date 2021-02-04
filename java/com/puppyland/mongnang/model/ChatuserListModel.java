package com.puppyland.mongnang.model;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.puppyland.mongnang.DTO.ChatUserDTO;
import com.puppyland.mongnang.contract.ChatuserListContract;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.google.gson.Gson;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatuserListModel extends AppCompatActivity {
    List<ChatUserDTO> chatUserDTOList;
    ChatuserListContract.Presenter presenter;
    public ChatuserListModel(ChatuserListContract.Presenter presenter){
        this.presenter = presenter;
    }



    public void ChatuserListInsert(String userId, String chatUserId, String acceptNumber){
        ChatUserDTO chatUserDTO = new ChatUserDTO();
        chatUserDTO.setUserId(userId);
        chatUserDTO.setChatUserId(chatUserId);
        chatUserDTO.setAcceptNumber(acceptNumber);
        Gson gson = new Gson();
        String objJson = gson.toJson(chatUserDTO); // DTO 객체를 json 으로 변환

        Call<ResponseBody> chatUserInsert = NetRetrofit.getInstance().getService().chatUserInsert(objJson);
        chatUserInsert.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d("res2q", "성공");
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });
    }

    public List<ChatUserDTO> ChatuserListCheck(String userId, String chatUserId){ // 이건 동기로 처리하는게 좋을지도
        ChatUserDTO chatUserDTO = new ChatUserDTO();
        chatUserDTO.setUserId(userId);
        chatUserDTO.setChatUserId(chatUserId);

        Gson gson = new Gson();
        String objJson = gson.toJson(chatUserDTO); // DTO 객체를 json 으로 변환

        final Call<List<ChatUserDTO>> chatUserListCheck = NetRetrofit.getInstance().getService().chatUserListCheck(objJson);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    chatUserDTOList=chatUserListCheck.execute().body();

                }
                catch (Exception e){
                    Log.d("err", e.getMessage());
                }
            }
        }).start();
        try {
            Thread.sleep(1000);
        }catch (Exception e){
            e.printStackTrace();
        }

        /*chatUserListCheck.enqueue(new Callback<List<ChatUserDTO>>() {
            @Override
            public void onResponse(Call<List<ChatUserDTO>> call, Response<List<ChatUserDTO>> response) {
                if(response.isSuccessful()){
                    Log.d("res", "성공");
                    List<ChatUserDTO> list = response.body();
                    ArrayList<String> arr = new ArrayList<String>();
                    try {
                        if (list != null) {
                            for(ChatUserDTO chatUserDTO : list){ // 리스트로 담기는// 것들 하나씩 출력
                                //chatUserDTO.getUserId(); //
                                arr.add(chatUserDTO.getChatUserId());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace ();
                    }

                    ((UserListFragment) UserListFragment.mContext).showResult(list);// 이래야지 다른 프라그먼트에 있는 함수 호출 가능


                    // 여기서 반환된 리스트를 바로 userListFragment 로 던져줘서 쓰자
                }
            }
            @Override
            public void onFailure(Call<List<ChatUserDTO>> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }/
        });*/
        return chatUserDTOList;
    }

    public void ChatuserListDelete(String userId, String chatUserId){
        ChatUserDTO chatUserDTO = new ChatUserDTO();
        chatUserDTO.setUserId(userId);
        chatUserDTO.setChatUserId(chatUserId);

        Gson gson = new Gson();
        String objJson = gson.toJson(chatUserDTO); // DTO 객체를 json 으로 변환

        Call<ResponseBody> chatUserDelete = NetRetrofit.getInstance().getService().chatUserDelete(objJson);
        chatUserDelete.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.v("woxeodk", "성공");
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });
    }

    public void chatUserListCheckAcceptUpdate(String userId, String chatUserId , String acceptNumber){
        ChatUserDTO chatUserDTO = new ChatUserDTO();
        chatUserDTO.setUserId(userId);
        chatUserDTO.setChatUserId(chatUserId);
        chatUserDTO.setAcceptNumber(acceptNumber);

        Gson gson = new Gson();
        String objJson = gson.toJson(chatUserDTO); // DTO 객체를 json 으로 변환

        Call<ChatUserDTO> chatUserListCheckAcceptUpdate = NetRetrofit.getInstance().getService().chatUserListCheckAcceptUpdate(objJson);
        chatUserListCheckAcceptUpdate.enqueue(new Callback<ChatUserDTO>() {
            @Override
            public void onResponse(Call<ChatUserDTO> call, Response<ChatUserDTO> response) {
                if(response.isSuccessful()){
                    Log.v("woxeodk", "성공");
                }
            }
            @Override
            public void onFailure(Call<ChatUserDTO> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });
    }

    public void chatUserListDelete(String userId, String chatUserId , String acceptNumber){
        ChatUserDTO chatUserDTO = new ChatUserDTO();
        chatUserDTO.setUserId(userId);
        chatUserDTO.setChatUserId(chatUserId);
        chatUserDTO.setAcceptNumber(acceptNumber);

        Gson gson = new Gson();
        String objJson = gson.toJson(chatUserDTO); // DTO 객체를 json 으로 변환

        Call<ResponseBody> chatUserListDelete = NetRetrofit.getInstance().getService().chatUserListDelete(objJson);
        chatUserListDelete.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.v("woxeodk", "성공");
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });
    }




}
