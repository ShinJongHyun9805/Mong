package com.puppyland.mongnang.contract;

public interface MemberContract {

    /**
     *  mvp
     */
    interface View{
        // View 회원가입이 성공했는지 안했는지 알려줌
        void showResult(String result);
        void goMainView();

    }

    interface Presenter {
        // View에서 입력한 정보로 회원가입 요청
        void memberJoin(String id, String gender, String age, String address1, String address2, String address3, String memberimage, String certification, String nickedname, String deviceId);
        void DeviceIdInsert(String id, String deviceId);

        //Dog DB에 userID값
        void dogJoin(String id);
        //강아지 정보 업데이트
        void dogUpdate(String userID, String id, String kind, String age, String gender, String imageName);
        //디바이스 아이디 삭제 -> 강아지 테이블 정보 삭제 -> 멤버쉽 삭제 -> 채팅리스트 유저 정보 삭제 -> 회원 탈퇴
        boolean DeviceIdDelete(String userID);

        void alarmUpdate(String userid, int whatAlarm, int onoff);
    }

}
