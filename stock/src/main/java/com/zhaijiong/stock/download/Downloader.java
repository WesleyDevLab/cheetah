package com.zhaijiong.stock.download;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-15.
 */
public class Downloader {

    private static BasicDataDownloader downloader = new BasicDataDownloader();

    public static String download(String url){
        return downloader.download(url);
    }
}
