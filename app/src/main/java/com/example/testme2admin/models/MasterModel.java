package com.example.testme2admin.models;

import java.util.List;

public class MasterModel {

    private String name ;
    private String place ;
    private List<String> categories;
    private List<String> sets;
    private String url;
    private String key;

    public MasterModel(){
        //empty constructor
    }
    public MasterModel(String name, String place, List<String> categories , List<String> sets, String url, String key) {
        this.name = name;
        this.place = place;
        this.categories = categories;
        this.sets = sets;
        this.url = url;
        this.key = key;
    }

    //getter

    public String getName() {
        return name;
    }

    public String getPlace() {
        return place;
    }

    public List<String> getCategories() {
        return categories;
    }

    public List<String> getSets() {
        return sets;
    }

    public String getUrl() {
        return url;
    }

    public String getKey() {
        return key;
    }

    //setter

    public void setName(String name) {
        this.name = name;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public void setSets(List<String> sets) {
        this.sets = sets;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
