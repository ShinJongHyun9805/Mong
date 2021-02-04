package com.puppyland.mongnang.presenter;

import com.puppyland.mongnang.contract.VideoContract;
import com.puppyland.mongnang.model.VideoModel;

public class VideoPresenter implements VideoContract.presenter {

    VideoContract.view Vview;
    VideoModel videoModel;

    public VideoPresenter(VideoContract.view view){
        Vview = view;
        videoModel = new VideoModel(this);
    }

    @Override
    public void InsertVideo(String userID, String name ,String nickname) {
        videoModel.InsertVideo(userID, name , nickname);
    }

    @Override
    public void UpdateLike(String VideoName) {
        videoModel.UpdateLike(VideoName);
    }

    @Override
    public void FollowVideo(String userID, String getnickname, String nickname , String videouserID) {
        videoModel.FollowVideo(userID, getnickname, nickname , videouserID);
    }

    @Override
    public void InsertLike(String userID, int var) {
        videoModel.InsertLike(userID, var);
    }

    @Override
    public void UnLike(int var, String id) {
        videoModel.UnLike(var, id);
    }

    @Override
    public void UnFollow(String nickname) {
        videoModel.UnFollow(nickname);
    }

    @Override
    public void subtractLike(String VideoName) {
        videoModel.subtractLike(VideoName);
    }
}