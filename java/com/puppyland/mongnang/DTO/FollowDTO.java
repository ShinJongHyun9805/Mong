package com.puppyland.mongnang.DTO;

import java.io.Serializable;

public class FollowDTO implements Serializable {

    private String userid;
    private String nickname;
    private String mynickname;
    private String videouserid;


    public String getVideouserid() {
        return videouserid;
    }

    public void setVideouserid(String videouserid) {
        this.videouserid = videouserid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMynickname() {
        return mynickname;
    }

    public void setMynickname(String mynickname) {
        this.mynickname = mynickname;
    }
}