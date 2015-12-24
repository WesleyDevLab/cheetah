package com.zhaijiong.stock.download;

import org.junit.Test;

import static org.junit.Assert.*;

public class DownloaderTest {

    @Test
    public void testDownload() throws Exception {
        String url = "http://yunvs.com/mtword/%E7%94%B5%E5%AD%90%E6%94%AF%E4%BB%98";
        String data = Downloader.download(url);
        System.out.println(data);
    }
}