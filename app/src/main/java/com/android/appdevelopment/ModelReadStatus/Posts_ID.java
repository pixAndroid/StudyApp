package com.android.appdevelopment.ModelReadStatus;

import java.io.Serializable;
import java.util.List;

public class Posts_ID implements Serializable {
    List<Steps_ID> steps_id;

    public Posts_ID() {
    }

    public Posts_ID(List<Steps_ID> steps_id) {
        this.steps_id = steps_id;
    }

    public List<Steps_ID> getSteps_id() {
        return steps_id;
    }
}
