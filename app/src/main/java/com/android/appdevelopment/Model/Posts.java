package com.android.appdevelopment.Model;

import java.io.Serializable;
import java.util.List;

public class Posts implements Serializable {

    private String posts_title;
    private List<Steps> steps;

    public Posts() {
    }

    public Posts(String sub_title, List<Steps> steps) {
        this.posts_title = sub_title;
        this.steps = steps;
    }

    public String getPosts_title() {
        return posts_title;
    }

    public void setPosts_title(String posts_title) {
        this.posts_title = posts_title;
    }

    public List<Steps> getSteps() {
        return steps;
    }

    public void setSteps(List<Steps> steps) {
        this.steps = steps;
    }
}
