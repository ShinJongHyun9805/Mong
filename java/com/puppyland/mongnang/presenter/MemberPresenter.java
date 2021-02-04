package com.puppyland.mongnang.presenter;


import com.puppyland.mongnang.contract.Apicallback;
import com.puppyland.mongnang.contract.MemberContract;
import com.puppyland.mongnang.model.MemberModel;

public class MemberPresenter implements MemberContract.Presenter {//여기를 기준으로 직접 연결을 시도하는게 여기 implements가 된다.
    //View와의 통신을 위해서
    // 프레젠터는 모델과 뷰를 둘다 관리하는 중간 단계다. 뷰에서 데이터를 받아와서 처리하고 다시 뷰를 업데이트 하기도하고
    // 모델로 데이터를 보내서 처리하게 하기도 한다.
    MemberContract.View memberView;
    MemberModel memberModel;

    public MemberPresenter(MemberContract.View view) { // View로 받아온다. 물론 이 뷰의 자료형은 컨트렉트에서 먼저 만들어둔것
        //view 연결
        memberView = view; // view 액티비티에서 연결된 View 가 여기에 담긴다. 즉 프레젠터에서도 view와연결이 된 상황

        //model 연결
        memberModel = new MemberModel(this); // 여기는 프레젠터에서 모델로 직접연결을 하는 부분 //this는 implements MemberContract.Presenter\
        //를 해줬기 때문에 프레젠터를 저기로 넘길 수 있는 것.

    }

    @Override
    public void memberJoin(String id, String gender, String age, String address1, String address2, String address3, String memberimage, String certification, String nickname, String deviceId) {

        memberModel.saveData(id, gender, age, address1, address2, address3, memberimage, certification, nickname, deviceId, new Apicallback() {

            @Override
            public void onSuccess() {
                memberView.goMainView();
            }

            @Override
            public void onFail() {

            }
        }); // //2020-07-04 여기에 로그인된 메인화면으로 넘어가는 코드가 있어야함.
    } // 뷰에서 넘어준 데이터를 위임받아서 계산하고 다시 뷰로 업데이트 시켜주고 있음


    @Override
    public void DeviceIdInsert(String id, String deviceId) {
        memberModel.DeviceIdInsert(id, deviceId);
    }

    //Dog DB, userID값
    @Override
    public void dogJoin(String id) {
        memberModel.dogJoin(id);
    }

    //강아지 정보 업데이트
    @Override
    public void dogUpdate(String userID, String id, String kind, String age, String gender, String imagName) {
        memberModel.dogUpdate(userID, id, kind, age, gender, imagName);
    }

    //디바이스 아이디 삭제 -> 강아지 테이블 정보 삭제 -> 멤버쉽 삭제 -> 채팅리스트 유저 정보 삭제 -> 회원 탈퇴
    @Override
    public boolean DeviceIdDelete(String userID) {
        return memberModel.DeviceIdDelete(userID);
    }

    @Override
    public void alarmUpdate(String userid, int whatAlarm, int onoff) {
        memberModel.alarmUpdate(userid, whatAlarm, onoff);
    }

}
