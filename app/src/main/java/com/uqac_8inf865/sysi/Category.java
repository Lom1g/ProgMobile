package com.uqac_8inf865.sysi;

public class Category {

    private long id;
    private String name;
    private int color;

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() { return color; }

    public void setColor(int color) {
        this.color = color;
    }

    public Category(long id, String name, int color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }
}
