package store;

import com.zhaijiong.crawler.storage.RedisStorage;
import com.zhaijiong.crawler.Config;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RedisTest {
    private RedisStorage redis;
    String url = "http://www.zhaijiong.com";

    @Before
    public void setUp() throws Exception {
        Config config = new Config("crawler.yaml");
        redis = new RedisStorage(config);
        redis.init();
    }

    @After
    public void tearDown() throws Exception {
        redis.cleanDB();
        redis.close();
    }

    @Test
    public void testAddGetTask() throws Exception {
        redis.addTask(url);
        Assert.assertEquals(1, redis.taskCount());
        String task = redis.getTask();
        Assert.assertEquals(url, task);
        Assert.assertEquals(0, redis.taskCount());
    }

    @Test
    public void testAddGetTask2() throws Exception {
        redis.addTask(url+1);
        redis.addTask(url+2);
        Assert.assertEquals(2, redis.taskCount());
        String task = redis.getTask();
        System.out.println(task);
        Assert.assertEquals(url + 1, task);
        Assert.assertEquals(1, redis.taskCount());
    }

    @Test
    public void performance() {
        long starttime = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            redis.addTask(url + i);
        }
        long costtime = System.currentTimeMillis() - starttime;
        System.out.println("cost time:" + costtime + "ms");
        System.out.println("task count:" + redis.taskCount());
    }
}