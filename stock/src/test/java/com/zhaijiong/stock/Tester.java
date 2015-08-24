package com.zhaijiong.stock;

import com.zhaijiong.stock.download.Downloader;
import org.junit.Test;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-24.
 */
public class Tester {
    @Test
    public void test(){
        String url = "http://weixin.sogou.com/gzhjs?cb=sogou.weixin.gzhcb&openid=oIWsFt07zRTrPfdu0-5-xX28XVhE&eqs=JqslowzgGjc0oNBum50PcuNsw3cbK1W3z9gqf3F6BSRfEKOqCnr7BfSVdyVVn62hkF65Z&ekv=7&page=1&t=1440395470571";
        String data = Downloader.downloadStr(url);
        System.out.println(data);
    }
}
