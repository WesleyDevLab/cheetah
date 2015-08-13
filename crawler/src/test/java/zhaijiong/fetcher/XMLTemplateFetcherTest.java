package zhaijiong.fetcher;

import org.jsoup.nodes.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import zhaijiong.tool.XMLConfigFileReader;

import java.util.Map;

/**
 * XMLTemplateFetcher Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>十二月 30, 2013</pre>
 */
public class XMLTemplateFetcherTest {
    private Document doc;
    private Map<String,Map<String,String>> properties;
    private XMLTemplateFetcher fetcher;

    @Before
    public void before() throws Exception {
        properties = XMLConfigFileReader.getTemplate("/home/eryk/workspace/camera/crawler/src/main/resources/page.xml");
        fetcher = new XMLTemplateFetcher(properties.get("qing.blog.sina.com.cn"));
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: fetch(String url)
     */
    @Test
    public void testFetch() throws Exception {
        System.out.println(fetcher.fetch("http://qing.blog.sina.com.cn/tj/ab1b261c33004rxc.html"));
    }

} 
