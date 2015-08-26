package com.zhaijiong.crawler;

public interface Storage {

    void init();

    void flush();

    void close();
}
