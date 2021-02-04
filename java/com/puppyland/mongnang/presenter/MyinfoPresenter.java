package com.puppyland.mongnang.presenter;

import com.puppyland.mongnang.DTO.MemberDTO;
import com.puppyland.mongnang.contract.MyinfoContract;
import com.puppyland.mongnang.model.MyinfoModel;

public class MyinfoPresenter  implements MyinfoContract.Presenter {

    MyinfoContract.View MyInfoView;
    MyinfoModel myInfoModel;

    public MyinfoPresenter(MyinfoContract.View view) {
        //뷰 연결
        MyInfoView = view;

        //model연결
        myInfoModel = new MyinfoModel(this);
    }

    @Override
    public void userInfoUpdate(MemberDTO member) {
        myInfoModel.userInfoUpdate(member);
    }

    @Override
    public void getInfoUser(String userId){
        MyInfoView.getInfoUserView(myInfoModel.getInfoUser(userId));
    }
}
