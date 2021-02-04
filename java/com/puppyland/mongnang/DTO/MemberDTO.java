package com.puppyland.mongnang.DTO;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MemberDTO implements Serializable{

    private String userId;
    private String password;
    private String gender;
    private String address1;
    private String address2;
    private String address3;
    private String memberimage;
    private String age;
    private String certification;
    private String usermsg;
    private String nickname;

    public int getStoryalram() {
        return storyalram;
    }

    public void setStoryalram(int storyalram) {
        this.storyalram = storyalram;
    }

    public int getBoardalram() {
        return boardalram;
    }

    public void setBoardalram(int boardalram) {
        this.boardalram = boardalram;
    }

    public int getChatalram() {
        return chatalram;
    }

    public void setChatalram(int chatalram) {
        this.chatalram = chatalram;
    }

    public int getTimecheckalram() {
        return timecheckalram;
    }

    public void setTimecheckalram(int timecheckalram) {
        this.timecheckalram = timecheckalram;
    }

    private int storyalram;
    private int boardalram;
    private int chatalram;
    private int timecheckalram;


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUsermsg() {
        return usermsg;
    }

    public void setUsermsg(String usermsg) {
        this.usermsg = usermsg;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }


    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }




    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getMemberimage() {
        return memberimage;
    }

    public void setMemberimage(String memberimage) {
        this.memberimage = memberimage;
    }

    public MemberDTO() {

    }

}
