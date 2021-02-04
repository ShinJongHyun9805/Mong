package com.puppyland.mongnang.contract;

import com.puppyland.mongnang.DTO.MemberDTO;

public interface MyinfoContract {

    interface View{
        void getInfoUserView(String usermsg);
    }

    interface Presenter{
        void userInfoUpdate(MemberDTO member);
        void getInfoUser(String userId);
    }


}
