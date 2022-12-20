package com.chelsy.listplanner;

public class Plans {
    public String title;
    public String date;
    public String time;

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String desc;
    public String uId;

    public Plans(String title, String date, String time) {
        this.title = title;
        this.date = date;
        this.time = time;
    }
    public Plans(String title, String date, String time, String desc, String uId) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.desc = desc;
        this.uId = uId;
    }

}
