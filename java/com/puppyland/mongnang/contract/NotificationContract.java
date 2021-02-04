package com.puppyland.mongnang.contract;

import com.puppyland.mongnang.DTO.NotificationDTO;

public interface NotificationContract {

    interface View{

    }

    interface Presenter{
        void UpdateNotification(NotificationDTO dto);
        //알람을 받았을때 tbl_noticiation 테이블에 업데이트 하는 함수
        // 이름은 update 인데 사실 하는 일은 insert 다

        void selectNotification(String userid); // 해당 유저의 모든 알람을 가져오는 함수
        void goNotificationItem(String userid, String item ,String no); // 알람을 눌렀을때 해당 게시물로 가게하는 함수
        // 해당 게시물이 삭제됐을때 존재 하지않는 게시물입니다 라고 띄워주는 기능도 처리해야함.
        // 게시판 , 동영상 , 다이어리만 다룸//채팅관련은 여기서 처리 하지않음.
    }


}
