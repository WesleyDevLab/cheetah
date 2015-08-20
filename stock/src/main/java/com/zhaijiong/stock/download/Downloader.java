package com.zhaijiong.stock.download;

import java.io.InputStream;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-15.
 */
public class Downloader {

    private static BasicDataDownloader downloader = new BasicDataDownloader();

    public static String downloadStr(String url){
        return downloader.downloadStr(url);
    }

    public static InputStream downloadStream(String url){
        return downloader.downloadStream(url);
    }
}
