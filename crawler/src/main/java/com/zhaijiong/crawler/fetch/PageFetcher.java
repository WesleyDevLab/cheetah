package com.zhaijiong.crawler.fetch;

import com.google.common.base.Strings;
import com.zhaijiong.crawler.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class PageFetcher implements Fetcher {
    private static final Logger LOG = LoggerFactory.getLogger(PageFetcher.class);

    HttpURLConnection connection;
    ByteBuffer buffer;

    public PageFetcher() {
        buffer = ByteBuffer.allocate(1024);
    }

    @Override
    public Page fetch(String url) {
        StringBuilder sb = new StringBuilder();
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.connect();
            String encoding = getEncoding(connection.getHeaderField("Content-Type"));
            InputStream inputStream = connection.getInputStream();
            ReadableByteChannel rChannel = Channels.newChannel(inputStream);
            int bytesRead = rChannel.read(buffer);

            while (bytesRead != -1) {
                buffer.flip();
                if(Strings.isNullOrEmpty(encoding)){
                    sb.append(new String(buffer.array()));
                }else{
                    sb.append(new String(buffer.array(), encoding));
                }
                buffer.array();
                buffer.clear();
                bytesRead = rChannel.read(buffer);
            }
            rChannel.close();
            inputStream.close();
            Page page = new Page(url, sb.toString());
            page.setEncode(encoding);
            return page;
        } catch (IOException e) {
            LOG.error(String.format("failed to fetch url %s",url),e);
        }
        return null;
    }

    public String getEncoding(String coding) {
        // 获取content-type charset
        int m = coding.toLowerCase().indexOf("charset=");
        if (m != -1) {
            return coding.substring(m + 8).replace("]", "");
        }
        return "";
    }

    public static void main(String[] args) {
        PageFetcher fetcher = new PageFetcher();
        Page page =fetcher.fetch("http://stock.10jqka.com.cn/tzjh_list/");
        System.out.println(page.getSource());
    }
}
