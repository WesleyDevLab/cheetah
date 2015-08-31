package com.zhaijiong.crawler.storage;

public interface Storage {

    void init();

    void flush();

    void close();
}
