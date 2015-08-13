package zhaijiong.crawler;

import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import zhaijiong.tool.Utils;

import java.util.List;

/**
 * CrawlerQueue Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>一月 1, 2014</pre>
 */
public class CrawlerQueueTest {
    private CrawlerQueue queue;

    @Before
    public void before() throws Exception {
        Utils.setup("/home/eryk/workspace/camera/crawler/src/main/resources/config.properties");
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxActive(100);
        config.setMaxIdle(20);
        config.setMaxWait(1000l);
        JedisPool pool = new JedisPool(config, "a", 6379);

        queue = new CrawlerQueue(pool,"test:rq1",10);
    }

    @After
    public void after() throws Exception {
        queue.close();
    }

    /**
     * Method: list()
     */
    @Test
    public void testList() throws Exception {
        List<String> list = queue.list();
        for(String ip : list){
            System.out.println(ip);
        }
    }

    /**
     * Method: put(String id)
     */
    @Test
    public void testPut() throws Exception {
        List<String> ids = Lists.newArrayList("123", "majun", "java", "Eral Scruggs");
        queue.putAll(ids);
    }


} 
