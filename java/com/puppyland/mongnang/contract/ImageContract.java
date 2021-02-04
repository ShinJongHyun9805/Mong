package com.puppyland.mongnang.contract;

import com.puppyland.mongnang.DTO.MemberDTO;

public interface ImageContract {
    interface View{
        //내 사진 파일이름.
        void getMyImage(MemberDTO member);
    }

    interface Presenter{
        void image(String imageName, String UID);

        //내 사진 파일 이름.
        void fileName(String myID);

        void userInfoUpdate(MemberDTO member);

    }
}