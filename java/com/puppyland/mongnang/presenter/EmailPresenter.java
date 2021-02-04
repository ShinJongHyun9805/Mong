package com.puppyland.mongnang.presenter;

import com.puppyland.mongnang.contract.EmailCertificationContract;
import com.puppyland.mongnang.model.EmailModel;

public class EmailPresenter implements EmailCertificationContract.Presenter  {
    EmailCertificationContract.View emailView;
    EmailModel emailModel;

    public EmailPresenter(EmailCertificationContract.View view){ // View로 받아온다. 물론 이 뷰의 자료형은 컨트렉트에서 먼저 만들어둔것
        //view 연결
        emailView = view; // view 액티비티에서 연결된 View 가 여기에 담긴다. 즉 프레젠터에서도 view와연결이 된 상황

        //model 연결
        emailModel = new EmailModel(this); // 여기는 프레젠터에서 모델로 직접연결을 하는 부분 //this는 implements MemberContract.Presenter\
        //를 해줬기 때문에 프레젠터를 저기로 넘길 수 있는 것.

    }

    @Override
    public int EmailCertificate(String userId){
        return emailModel.EmailCertificate(userId);
    }

    @Override
    public int EmailCertificateCheck(String userId){
        return emailModel.EmailCertificateCheck(userId);
    }
}
