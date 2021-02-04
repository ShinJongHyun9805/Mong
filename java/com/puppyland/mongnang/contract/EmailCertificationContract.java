package com.puppyland.mongnang.contract;

public interface EmailCertificationContract {

    interface View{

    }

    interface Presenter{
        int EmailCertificate(String userId);
        int EmailCertificateCheck(String userId);
    }

}
