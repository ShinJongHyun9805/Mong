package com.puppyland.mongnang.contract;

import com.puppyland.mongnang.DTO.ChatUserDTO;

import java.util.List;

public interface ChatuserListContract {

    interface View{

    }

    interface Presenter{
        void ChatuserListInsert(String userId , String chatUserId ,String acceptNumber); // 새롭게 넣을때
        List<ChatUserDTO> ChatuserListCheck(String userId , String chatUserId ); //이미 있는지 체크
        int ChatuserListDelet(String userId, String chatUserId); // 삭제할때
        void chatUserListCheckAcceptUpdate(String userId , String chatUerId , String acceptNumber);
        void chatUserListDelete(String userId , String chatUerId , String acceptNumber);
    }
}
