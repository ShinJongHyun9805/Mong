package com.puppyland.mongnang.DTO;

public class SearchCheckDataDTO {



    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    //사람 정보
    private String userId;
    private String age;
    private  String address;

    private String gender;

    //강아지 정보
    private String dogname;
    private String dogkind;
    private String doggender;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDogname() {
        return dogname;
    }

    public void setDogname(String dogname) {
        this.dogname = dogname;
    }

    public String getDogkind() {
        return dogkind;
    }

    public void setDogkind(String dogkind) {
        this.dogkind = dogkind;
    }

    public String getDoggender() {
        return doggender;
    }

    public void setDoggender(String doggender) {
        this.doggender = doggender;
    }

}
