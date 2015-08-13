package com.zhaijiong.bumblebee.crawler;

public class Page {

    private final String url;

    private final String sourceHTML;

    public Page(String url, String sourceHTML) {
        this.url = url;
        this.sourceHTML = sourceHTML;
    }

    public String getUrl() {
        return url;
    }

    public String getSourceHTML() {
        return sourceHTML;
    }
}
