package com.puppyland.mongnang.DTO;

import java.io.Serializable;

public class VideoDTO implements Serializable {

    private int vno;
    private String userid;
    private String videoname;
    private String nickname;
    private int likey;
    private String videouserid;
    private String mynickname;

    public String getVideouserid() {
        return videouserid;
    }

    public void setVideouserid(String videouserid) {
        this.videouserid = videouserid;
    }

    public String getMynickname() {
        return mynickname;
    }

    public void setMynickname(String mynickname) {
        this.mynickname = mynickname;
    }
    public int getVno() {
        return vno;
    }

    public void setVno(int vno) {
        this.vno = vno;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getVideoname() {
        return videoname;
    }

    public void setVideoname(String videoname) {
        this.videoname = videoname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getLikey() {
        return likey;
    }

    public void setLikey(int likey) {
        this.likey = likey;
    }
}