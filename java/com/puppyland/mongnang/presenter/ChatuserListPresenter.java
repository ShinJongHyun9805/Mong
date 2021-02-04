package com.puppyland.mongnang.presenter;

import android.util.Log;

import com.puppyland.mongnang.DTO.ChatUserDTO;
import com.puppyland.mongnang.contract.ChatuserListContract;
import com.puppyland.mongnang.model.ChatuserListModel;

import java.util.List;


public class ChatuserListPresenter implements ChatuserListContract.Presenter {

    ChatuserListModel chatuserListModel;
    ChatuserListContract.View chatuserlistView;
    public ChatuserListPresenter(ChatuserListContract.View view){ // View로 받아온다. 물론 이 뷰의 자료형은 컨트렉트에서 먼저 만들어둔것

        chatuserlistView = view;
        //model 연결
        chatuserListModel = new ChatuserListModel(this); // 여기는 프레젠터에서 모델로 직접연결을 하는 부분 //this는 implements MemberContract.Presenter\
        //를 해줬기 때문에 프레젠터를 저기로 넘길 수 있는 것.

    }

    @Override
    public void ChatuserListInsert(String userId , String chatUserId , String acceptNumber) {
        Log.v("sdfaf2" ,userId );
        Log.v("sdfaf2" ,chatUserId );
        chatuserListModel.ChatuserListInsert(userId ,chatUserId, acceptNumber);
    }

    @Override
    public List<ChatUserDTO> ChatuserListCheck(String userId , String chatUserId) {

        List<ChatUserDTO> chatmemberlist =chatuserListModel.ChatuserListCheck(userId , chatUserId);
        return chatmemberlist;
    }

    @Override
    public int ChatuserListDelet(String userId , String chatUserId) {
        chatuserListModel.ChatuserListDelete(userId , chatUserId);
        return 0;
    }

    @Override
    public void chatUserListCheckAcceptUpdate(String userId , String chatUserId , String acceptNumber){
        chatuserListModel.chatUserListCheckAcceptUpdate(userId, chatUserId, acceptNumber);
    }

    @Override
    public void chatUserListDelete(String userId , String chatUerId , String acceptNumber){
        chatuserListModel.chatUserListDelete(userId , chatUerId , acceptNumber);
    }
}
