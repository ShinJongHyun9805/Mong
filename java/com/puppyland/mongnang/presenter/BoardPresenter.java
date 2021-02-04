package com.puppyland.mongnang.presenter;

import android.widget.ListView;
import android.widget.TextView;

import com.puppyland.mongnang.contract.BoardContract;
import com.puppyland.mongnang.model.BoardModel;

public class BoardPresenter implements BoardContract.presenter {

    BoardContract.view Bview;
    BoardModel boardModel;

    public BoardPresenter(BoardContract.view view){
        Bview = view;
        boardModel = new BoardModel(this);
    }

    @Override
    public void DeleteBoard(String bno) {
        boardModel.DeleteBoard(bno);
    }

    @Override
    public void UpdateBoard(String bno, String title, String content, String imgName) {
        boardModel.UpdateBoard(bno, title, content, imgName);
    }

    @Override
    public void InsertContent(String bno, String id, String content , String nickname) {
        boardModel.InsertContent(bno, id, content , nickname);
    }

    //댓글쓰기
    @Override
    public void updateComment(String bno, TextView reviews, ListView listview) {
        boardModel.updateComment(bno, reviews, listview);
    }

    @Override
    public void updateHit(String bno) {
        boardModel.updateHit(bno);
    }

}