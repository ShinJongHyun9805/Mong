package com.puppyland.mongnang;

public class ListViewItem {
    private String iconDrawable ; // 유저이미지
    private String titleStr ; // 유저 아이디
    private String descStr ; // 상태메세지

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    private String nickname;

    private String dogImage;
    private String dogName;


    public String getDogGender() {
        return dogGender;
    }

    public void setDogGender(String dogGender) {
        this.dogGender = dogGender;
    }

    public String getDogAge() {
        return dogAge;
    }

    public void setDogAge(String dogAge) {
        this.dogAge = dogAge;
    }

    private String dogGender;
    private String dogAge;


    public void setIcon(String icon) {
        iconDrawable = icon ;
    }
    public void setTitle(String title) {
        titleStr = title ;
    }
    public void setDesc(String desc) {
        descStr = desc ;
    }
    public void setDogImage(String dogImage) { this.dogImage = dogImage; }
    public void setDogName(String dogName) { this.dogName = dogName; }

    public String getIcon() {
        return this.iconDrawable ;
    }
    public String getTitle() {
        return this.titleStr ;
    }
    public String getDesc() {
        return this.descStr ;
    }
    public String getDogImage() { return dogImage; }
    public String getDogName() { return dogName; }
}
