package com.android.appdevelopment.Model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Steps implements Serializable {
    private String header;
    private String body;
    private String body_before_image;
    private String body_image_url;
    private String body_after_image;
    private String body_before_code;
    private String body_code;
    private String body_after_code;

    public Steps() {
    }

    public Steps(String header, String body, String body_before_image, String image_url, String body_after_image, String body_before_code, String code, String body_after_code) {
        this.header = header;
        this.body = body;
        this.body_before_image = body_before_image;
        this.body_image_url = image_url;
        this.body_after_image = body_after_image;
        this.body_before_code = body_before_code;
        this.body_code = code;
        this.body_after_code = body_after_code;
    }

    public String getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }

    public String getBody_before_image() {
        return body_before_image;
    }

    public String getBody_image_url() {
        return body_image_url;
    }

    public String getBody_after_image() {
        return body_after_image;
    }

    public String getBody_before_code() {
        return body_before_code;
    }

    public String getBody_code() {
        return body_code;
    }

    public String getBody_after_code() {
        return body_after_code;
    }
}
