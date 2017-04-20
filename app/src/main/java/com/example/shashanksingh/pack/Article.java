package com.example.shashanksingh.pack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shashank Singh on 4/6/2017.
 */

public class Article {
    private String id, url, title, body, image;
    private Map<String , Object> otherProperties = new HashMap<String , Object>();

    public Article() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Map<String, Object> getOtherProperties() {
        return otherProperties;
    }

    public void setOtherProperties(Map<String, Object> otherProperties) {
        this.otherProperties = otherProperties;
    }

    public Object get(String name) {
        return otherProperties.get(name);
    }
}
