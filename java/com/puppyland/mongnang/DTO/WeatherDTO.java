package com.puppyland.mongnang.DTO;

public class WeatherDTO {

    private String latitude;
    private String longitude;

    private String se;
    private String gu;
    private String dong;

    private String POP;
    private String PTY;
    private String REH;
    private String SKY;
    private String T3H;
    private String res;

    /*
     * POP : 강수확률
     * PTY : 강수형태
     * REH : 습도
     * SKY : 하늘은 우릴 향해 열려 있어 그리고 내 곁에는 니가 있어 환한 미소와 함께 서 있는 그래 너는 푸른 바다야
     * T3H : 3시간 기온
     * */

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getSe() {
        return se;
    }

    public void setSe(String se) {
        this.se = se;
    }

    public String getGu() {
        return gu;
    }

    public void setGu(String gu) {
        this.gu = gu;
    }

    public String getDong() {
        return dong;
    }

    public void setDong(String dong) {
        this.dong = dong;
    }

    public String getPOP() {
        return POP;
    }

    public void setPOP(String POP) {
        this.POP = POP;
    }

    public String getPTY() {
        return PTY;
    }

    public void setPTY(String PTY) {
        this.PTY = PTY;
    }

    public String getREH() {
        return REH;
    }

    public void setREH(String REH) {
        this.REH = REH;
    }

    public String getSKY() {
        return SKY;
    }

    public void setSKY(String SKY) {
        this.SKY = SKY;
    }

    public String getT3H() {
        return T3H;
    }

    public void setT3H(String t3H) {
        T3H = t3H;
    }

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }
}