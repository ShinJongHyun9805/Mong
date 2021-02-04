package com.puppyland.mongnang.DTO;

public class NotiboardDTO {

    public String getNno() {
        return nno;
    }

    public void setNno(String nno) {
        this.nno = nno;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String nno;
    private  String title;
    private String date;
    private String content;

}
