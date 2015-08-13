package zhaijiong.crawler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import zhaijiong.tool.Utils;

public class WebCrawlerTest {
    WebCrawler crawler;

    @Before
    public void before() throws Exception {
        Utils.setup("/home/eryk/workspace/camera/crawler/src/main/resources/config.properties");
        crawler = new WebCrawler("http://qing.blog.sina.com.cn/tag/%E7%BE%8E%E5%A5%B3");
        crawler.prepare();
    }

    @After
    public void after() throws Exception {
        crawler.close();
    }

    /**
     * Method: crawler()
     */
    @Test
    public void testCrawler() throws Exception {
        crawler.crawler();
    }

} 
