package com.puppyland.mongnang.contract;

public interface WeatherContract {
    interface view{

    }

    interface presenter{
        //내 위치, 주소
        void Location(String se, String gu, String dong);
    }


}
