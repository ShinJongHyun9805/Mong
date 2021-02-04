package com.puppyland.mongnang.DTO;

public class DiaryDTO {


    public String getDno() {
        return dno;
    }

    public void setDno(String dno) {
        this.dno = dno;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    private String dno;
    private String userid;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    public String getDcontent() {
        return dcontent;
    }

    public void setDcontent(String dcontent) {
        this.dcontent = dcontent;
    }

    private String dcontent;
    private String create_date;
    private String img;
    private String nickname;

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    private String font;

    public int getLikey() {
        return likey;
    }

    public void setLikey(int likey) {
        this.likey = likey;
    }

    private int likey;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getShareConfirmation() {
        return shareConfirmation;
    }

    public void setShareConfirmation(String shareConfirmation) {
        this.shareConfirmation = shareConfirmation;
    }

    private String shareConfirmation;


}
