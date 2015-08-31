package com.zhaijiong.bumblebee.crawler;

public class Page {

    private String url;

    private String source;

    public Page(String url, String source) {
        this.url = url;
        this.source = source;
    }

    public String getUrl() {
        return url;
    }

    public String getSource() {
        return source;
    }
}
