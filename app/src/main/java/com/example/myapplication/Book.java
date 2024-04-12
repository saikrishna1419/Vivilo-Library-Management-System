package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

public class Book {
    private String title, type;
    private int available = 0, id;
    private List<Integer> unit = new ArrayList<Integer>();


    public Book() {
    }

    public Book(String title, String type, int total, int id) {
        this.title = title;
        this.type = type;
        this.id = id;
        this.available=total;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Integer> getUnit() {
        return unit;
    }

    public void setUnit(List<Integer> unit) {
        this.unit = unit;
    }
}
