package com.puppyland.mongnang.contract;

import com.puppyland.mongnang.DTO.TimeCheckDTO;

import java.util.List;

public interface TimeCheckContract {

    interface view{
        void UpdateCalendar(List<TimeCheckDTO> list);
    }

    interface presenter{
        //내 위치, 주소
        List<String> getTimeCheck(String userId);
        void Timeinsert(String userId, String meetDate, String memo);
        void TimeUpdate(String userId, String meetDate, String memo);
        void TimeDelete(String userId);
        String getOnetimeCheck(String userId, String meetDate);
    }



}
