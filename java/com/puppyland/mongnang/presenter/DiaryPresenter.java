package com.puppyland.mongnang.presenter;

import com.puppyland.mongnang.contract.DiaryContract;
import com.puppyland.mongnang.model.DiaryModel;

public class DiaryPresenter  implements DiaryContract.presenter {

    DiaryContract.view Bview;
    DiaryModel diaryModel;

    public DiaryPresenter(DiaryContract.view view){
        Bview = view;
        diaryModel = new DiaryModel(this);
    }
    @Override
    public void InsertBoard(String title, String userid, String content, String ImageName,int shareConfirmation, String nickname , String tempFont) {
        diaryModel.InsertBoard(title, userid, content, ImageName ,shareConfirmation, nickname , tempFont);
    }
    @Override
    public void DeleteBoard(String bno) {
        diaryModel.DeleteBoard(bno);
    }
    @Override
    public void UpdateBoard(String bno, String title, String content, String imgName) {
        diaryModel.UpdateBoard(bno, title, content, imgName);
    }

    //공유설정
    @Override
    public  void ShareBoard(String bno , String userid, String title , String content, String imgName){
        diaryModel.ShareBoard(bno, userid,title, content, imgName);
    }

    //공유해제
    @Override
    public  void ShareBoardoff(String bno ,String userid, String title , String content, String imgName){
        diaryModel.ShareBoardoff(bno, userid,title, content, imgName);
    }

    @Override
    public void insert_font(String userId, String fontname) {
        diaryModel.insert_font(userId, fontname);
    }

    @Override
    public void getfontlist(String userId) {
        diaryModel.getfontlist(userId);
    }

    @Override
    public void UpdateLike(String dno){
        diaryModel.UpdateLike(dno);
    }
    @Override
    public void desUpdateLike(String dno){
        diaryModel.desUpdateLike(dno);
    }
    //내가 누른 조아여
    @Override
    public void InsertLike(String userID, String var){
        diaryModel.InsertLike(userID,var);
    }
    //안 조아여
    @Override
    public  void UnLike(String var, String id){
        diaryModel.UnLike(var, id);
    }

}
