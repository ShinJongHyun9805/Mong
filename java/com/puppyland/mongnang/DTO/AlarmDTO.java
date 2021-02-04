package com.puppyland.mongnang.DTO;

public class AlarmDTO {
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getWhatAlarm() {
        return whatAlarm;
    }

    public void setWhatAlarm(int whatAlarm) {
        this.whatAlarm = whatAlarm;
    }

    public int getOnoff() {
        return onoff;
    }

    public void setOnoff(int onoff) {
        this.onoff = onoff;
    }

    private String userid;
    private  int whatAlarm;
    private int onoff;
}
