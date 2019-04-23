package com.android.appdevelopment.ModelReadStatus;

import java.io.Serializable;
import java.util.List;

public class Lessons_ID implements Serializable {
    List<Posts_ID> posts_id;

    public Lessons_ID() {
    }

    public Lessons_ID(List<Posts_ID> posts_id) {
        this.posts_id = posts_id;
    }

    public List<Posts_ID> getPosts_id() {
        return posts_id;
    }
}
