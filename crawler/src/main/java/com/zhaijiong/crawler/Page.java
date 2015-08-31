package com.zhaijiong.crawler;

public class Page {

    private String url;

    private String encode;

    private String source;

    public Page(String url, String source) {
        this.url = url;
        this.source = source;
    }

    public String getEncode() {
        return encode;
    }

    public void setEncode(String encode) {
        this.encode = encode;
    }

    public String getUrl() {
        return url;
    }

    public String getSource() {
        return source;
    }
}
