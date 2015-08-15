package com.zhaijiong.stock.download;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-15.
 */
public class BasicDataDownloader implements DataDownload {
    private static final Logger LOG = LoggerFactory.getLogger(DataDownload.class);

    @Override
    public String download(String url) {
        try {
            HttpResponse<String> response = Unirest.get(url).asString();
            if(response.getStatus()==200){
                return response.getBody();
            }else{
                LOG.warn("status="+response.getStatus()+",url="+url);
            }
        } catch (UnirestException e) {
            LOG.error("fail to download from " + url);
        }
        return "";
    }

    public static void main(String[] args) {
        BasicDataDownloader downloader = new BasicDataDownloader();
        String download = downloader.download("http://nuff.eastmoney.com/EM_Finance2015TradeInterface/JS.ashx?id=3002462&token=beb0a0047196124721f56b0f0ff5a27c&cb=callback031451186537742615&callback=callback031451186537742615&_=1439558792487");
        System.out.println(download);
    }
}
