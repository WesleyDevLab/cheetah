package com.zhaijiong.bumblebee.store;

public interface Storage {

    void init();

    void flush();

    void close();
}
