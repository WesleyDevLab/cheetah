package zhaijiong.crawler;

import com.google.common.collect.Lists;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

public class CrawlerQueue {
    private Jedis queue;

    private final String queueName;
    private final int queueCapacity;
    private final JedisPool pool;

    public CrawlerQueue(JedisPool pool,String queueName, int queueCapacity) {
        this.queueName = queueName;
        this.queueCapacity = queueCapacity;
        this.pool = pool;
        queue = pool.getResource();
    }

    public List<String> list() {
        List<String> iterator = queue.lrange(queueName, 0, -1);
        List<String> postIDs = Lists.newArrayList(iterator);
        return postIDs;
    }

    public boolean put(String id) {
        Long length = queue.llen(queueName);
        if (length >= queueCapacity) {
            queue.rpop(queueName);
        }
        queue.lpush(queueName,id);
        return true;
    }

    public boolean putAll(List<String> ids) {
        for (String id : ids) {
            Long length = queue.llen(queueName);
            if (length >= queueCapacity) {
                queue.rpop(queueName);
            }
            queue.lpush(queueName, id);
        }
        return true;
    }

    public void close() {
        pool.returnResource(queue);
    }
}
