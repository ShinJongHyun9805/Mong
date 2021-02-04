package com.puppyland.mongnang.presenter;


import com.puppyland.mongnang.DTO.FunctionCountDTO;
import com.puppyland.mongnang.contract.FunctionCountContract;
import com.puppyland.mongnang.model.FunctionCountModel;

public class FunctionCountPresenter implements FunctionCountContract.Presenter {

    FunctionCountContract.View functionCountView;
    FunctionCountModel functionCountModel;


    public FunctionCountPresenter(FunctionCountContract.View view){ // View로 받아온다. 물론 이 뷰의 자료형은 컨트렉트에서 먼저 만들어둔것
        //view 연결
        functionCountView = view; // view 액티비티에서 연결된 View 가 여기에 담긴다. 즉 프레젠터에서도 view와연결이 된 상황
        //model 연결
        functionCountModel = new FunctionCountModel(this); // 여기는 프레젠터에서 모델로 직접연결을 하는 부분 //this는 implements MemberContract.Presenter\
        //를 해줬기 때문에 프레젠터를 저기로 넘길 수 있는 것.

    }

    @Override
    public void functionCount_insert_userId(String userId) {
        functionCountModel.functionCount_tbl_functionCount_insert_userId(userId);
    }

    @Override
    public void functionCount_tbl_membership_insert_userId(String userId) {
        functionCountModel.functionCount_tbl_membership_insert_userId(userId);
    }

    @Override
    public void functionCount_update_Ad(String userId,boolean flag) {

    }

    @Override
    public void functionCount_update_changepdf(String userId,int count) {
        functionCountModel.functionCount_update_changepdf(userId,count);
    }

    @Override
    public void functionCount_update_jump(String userId,int count) {
        functionCountModel.functionCount_update_jump(userId, count);
    }


    @Override
    public void functionCount_update_rating(String userId,String rating) {
        functionCountModel.functionCount_update_rating(userId, rating);
    }


    @Override
    public void functionCount_update_rating_init() {

    }

    @Override
    public void functionCount_update_point(FunctionCountDTO dto , int flag) {
        functionCountModel.functionCount_update_point(dto , flag);
    }

    @Override
    public void functionCount_purchase_pdf(FunctionCountDTO dto, int flag) {
        functionCountModel.functionCount_purchase_pdf(dto , flag);
    }

    @Override
    public void functionCount_purchase_chat(FunctionCountDTO dto, int flag) {
        functionCountModel.functionCount_purchase_chat(dto , flag);
    }



}