package com.puppyland.mongnang.presenter;

import com.puppyland.mongnang.contract.SearchContract;
import com.puppyland.mongnang.model.SearchMemberListModel;

public class SearchMemberListPresenter implements SearchContract.Presenter {

    SearchContract.View SearchView;
    SearchMemberListModel searchMemberListModel;
    public SearchMemberListPresenter(SearchContract.View view){ // View로 받아온다. 물론 이 뷰의 자료형은 컨트렉트에서 먼저 만들어둔것
        //view 연결
        SearchView = view; // view 액티비티에서 연결된 View 가 여기에 담긴다. 즉 프레젠터에서도 view와연결이 된 상황
        //model 연결
        searchMemberListModel = new SearchMemberListModel(this); // 여기는 프레젠터에서 모델로 직접연결을 하는 부분 //this는 implements MemberContract.Presenter\
        //를 해줬기 때문에 프레젠터를 저기로 넘길 수 있는 것.
    }

    @Override
    public void getClickMember(String userId) {
        SearchView.showClickMember(searchMemberListModel.getClickMemberinfo(userId));
    }

    @Override
    public void getClickMemberDog(String userId) {
        SearchView.showClickMemberDog(searchMemberListModel.getClickMemberDoginfo(userId));
    }
}
