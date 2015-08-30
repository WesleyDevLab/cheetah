package com.zhaijiong.stock.download;

import java.io.InputStream;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-15.
 */
public interface Download {

    public String downloadStr(String url);

    public InputStream downloadStream(String url);

}
