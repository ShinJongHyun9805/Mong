package com.puppyland.mongnang.contract;

public interface DiaryContract {

    interface view {

    }

    interface presenter{
        void InsertBoard(String title, String userid, String content, String ImageName ,int shareConfirmation, String nickname , String tempFont);

        void DeleteBoard(String bno);
        void UpdateBoard(String bno, String title, String content, String imgName);

        void ShareBoard(String bno , String userid, String title , String content, String imgName);
        void ShareBoardoff(String bno ,String userid, String title , String content, String imgName);

        void insert_font(String userId , String fontname);
        void getfontlist(String userId);

        void desUpdateLike(String dno);
        void UpdateLike(String dno);
        //내가 누른 조아여
        void InsertLike(String userID, String dno);

        //안 조아여
        void UnLike(String dno, String id);

    }


}
