package com.dlut.mnist.scriptrecognizer.DAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataBean {
    private int id;

    private String name;

    private String stunum;

    private HashMap<String, String> score;

    public DataBean() {
        this.score = new HashMap<>();
    }

    public int getId() {
        return id;
    }
    public String getIdString() {
        return String.valueOf(id);
    }
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStunum(String stunum) {
        this.stunum = stunum;
    }

    public void addScore(String time, String score) {
        this.score.put(time, score);
    }

    public String getName() {
        return name;
    }

    public String getStunum() {
        return stunum;
    }

    public Object[] getScoreAll(List<String> dateList) {

        List<String> list=new ArrayList<>();
        for (String dateKey : dateList) {
           list.add(score.get(dateKey));
        }
        return list.toArray();
    }

    public String getScoreByDate(String date) {
        return score.get(date);
    }
}
