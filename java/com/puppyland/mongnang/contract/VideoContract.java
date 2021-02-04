package com.puppyland.mongnang.contract;

public interface VideoContract {

    interface view {

    }

    interface presenter{
        void InsertVideo(String userID, String name , String nickname);

        void UpdateLike(String VideoName);
        //내가 누른 조아여
        void InsertLike(String userID, int var);

        //안 조아여
        void UnLike(int var, String id);

        //동영상 팔로우
        void FollowVideo(String userID, String getnickname, String nickname , String videouserID);



        //언팔로우
        void UnFollow(String nickname);
        //좋아요 차감
        void subtractLike(String VideoName);
    }
}