package com.dlut.mnist.scriptrecognizer.DAO;

public class Student {
    private int _id;
    private String name;
    private int stunumber;

    public Student(int _id, String name, int stunumber) {
        this._id = _id;
        this.name = name;
        this.stunumber = stunumber;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStunumber() {
        return stunumber;
    }

    public void setStunumber(int stunumber) {
        this.stunumber = stunumber;
    }
}
