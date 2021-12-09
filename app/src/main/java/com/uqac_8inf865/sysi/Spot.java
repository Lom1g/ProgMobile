package com.uqac_8inf865.sysi;

import android.graphics.Bitmap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Spot {

    private String author, category, description, title;
    private LatLng coordinate;
    private double rating;
    private ArrayList<Bitmap> bitmapArrayList;

    public Spot(String author, String category, String description,
                String title, LatLng coordinate, double rating,
                ArrayList<Bitmap> bitmapArrayList) {
        this.author = author;
        this.category = category;
        this.description = description;
        this.title = title;
        this.coordinate = coordinate;
        this.rating = rating;
        this.bitmapArrayList = bitmapArrayList;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LatLng getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(LatLng coordinate) {
        this.coordinate = coordinate;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public ArrayList<Bitmap> getBitmapArrayList() {
        return bitmapArrayList;
    }

    public void setBitmapArrayList(ArrayList<Bitmap> bitmapArrayList) {
        this.bitmapArrayList = bitmapArrayList;
    }
}
