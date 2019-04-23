package com.android.appdevelopment.Model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Lessons implements Serializable {
    private String lessons_title;
    private String lessons_short_descriptions;
    private String lessons_image_url;
    private List<Posts> posts;

    public Lessons() { }

    public Lessons(String title_main, String short_descriptions, String image_url, List<Posts> posts) {
        this.lessons_title = title_main;
        this.lessons_short_descriptions = short_descriptions;
        this.lessons_image_url = image_url;
        this.posts = posts;
    }

    public String getLessons_title() {
        return lessons_title;
    }

    public void setLessons_title(String lessons_title) {
        this.lessons_title = lessons_title;
    }

    public String getLessons_short_descriptions() {
        return lessons_short_descriptions;
    }

    public void setLessons_short_descriptions(String lessons_short_descriptions) {
        this.lessons_short_descriptions = lessons_short_descriptions;
    }

    public String getLessons_image_url() {
        return lessons_image_url;
    }

    public void setLessons_image_url(String lessons_image_url) {
        this.lessons_image_url = lessons_image_url;
    }

    public List<Posts> getPosts() {
        return posts;
    }

    public void setPosts(List<Posts> posts) {
        this.posts = posts;
    }
}
