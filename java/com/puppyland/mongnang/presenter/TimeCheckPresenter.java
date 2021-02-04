package com.puppyland.mongnang.presenter;

import com.puppyland.mongnang.contract.TimeCheckContract;
import com.puppyland.mongnang.model.TimeCheckModel;

import java.util.List;


public class TimeCheckPresenter implements TimeCheckContract.presenter {

    TimeCheckContract.view view;
    TimeCheckModel timeCheckModel;

    public TimeCheckPresenter(TimeCheckContract.view view) {
        this.view = view;
        timeCheckModel = new TimeCheckModel(this);
    }

    @Override
    public List<String> getTimeCheck(String userId) {
         List<String> list = timeCheckModel.getTimeCheck(userId); // 결과로 넘어온 일정 list 를 view 로 넘겨줌

        return list;
    }

    @Override
    public void Timeinsert(String userId, String meetDate, String memo) {
        timeCheckModel.TimeCheckInsert(userId , meetDate ,memo);
    }

    @Override
    public void TimeUpdate(String userId, String meetDate, String memo) {
        timeCheckModel.TimeUpdate(userId,meetDate,memo);
    }

    @Override
    public void TimeDelete(String userId) {
        timeCheckModel.TimeDelete(userId);
    }

    @Override
    public String getOnetimeCheck(String userId, String meetDate) {
        return timeCheckModel.getOnetimeCheck(userId, meetDate);
    }
}
