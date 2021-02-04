package com.puppyland.mongnang.contract;

import android.widget.ListView;
import android.widget.TextView;

public interface BoardContract {

    interface view {

    }

    interface presenter{

        void DeleteBoard(String bno);

        void UpdateBoard(String bno, String title, String content, String imgName);
        void InsertContent(String bno, String id, String content ,String nickname);
        //댓글 갯수
        void updateComment(String bno, TextView reviews, ListView listview);
        //조회수 증가
        void updateHit(String bno);
    }

}