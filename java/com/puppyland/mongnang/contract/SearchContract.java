package com.puppyland.mongnang.contract;

import com.puppyland.mongnang.DTO.DogDTO;
import com.puppyland.mongnang.DTO.MemberDTO;

import java.util.ArrayList;
import java.util.List;

public interface SearchContract {

    interface View{
        // View 회원가입이 성공했는지 안했는지 알려줌
        void showResult(ArrayList<MemberDTO> list); // 리스트 뷰에 뿌려주는 함수
        void showClickMember(MemberDTO member);
        void showClickMemberDog(List<DogDTO> dog);
    }

    interface Presenter {
        // View에서 입력한 정보로 검색 리스트 가져오기

        // 검색된 SearchMemberDTO를 List로 반환

        void getClickMember(String userId);
        void getClickMemberDog(String userId);


    }
}
