package com.puppyland.mongnang.DTO;

public class ChatUserDTO {
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String userId;

    public String getChatUserId() {
        return chatUserId;
    }

    public void setChatUserId(String chatUserId) {
        this.chatUserId = chatUserId;
    }

    private String chatUserId;

    private String acceptNumber;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    private String nickname;
    public String getMemberimage() {
        return memberimage;
    }

    public void setMemberimage(String memberimage) {
        this.memberimage = memberimage;
    }

    private String memberimage;
    public String getAcceptNumber() {
        return acceptNumber;
    }

    public void setAcceptNumber(String acceptNumber) {
        this.acceptNumber = acceptNumber;
    }

}
