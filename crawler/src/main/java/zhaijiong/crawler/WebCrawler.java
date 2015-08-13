package zhaijiong.crawler;

import com.google.common.collect.Sets;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import zhaijiong.Constants;
import zhaijiong.fetcher.Fetcher;
import zhaijiong.index.IndexJob;
import zhaijiong.tool.Utils;
import zhaijiong.tool.XMLConfigFileReader;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.regex.Pattern;

public class WebCrawler implements Runnable{
    private static Logger LOG = LoggerFactory.getLogger(WebCrawler.class);

    private ExecutorService crawlerPool;
    private ExecutorCompletionService finishedTask;
    private Map<String, String> template;
    private CrawlerQueue queue;
    private Set<String> fetchRecords;
    private JedisPool pool;

    private String url;
    private final String HREF = "href";

    public WebCrawler(String seedURL) {
        this.url = seedURL;
    }

    public void prepare() {
        crawlerPool = Executors.newFixedThreadPool(Integer.parseInt(Utils.get(Constants.POOL_SIZE)));
        finishedTask = new ExecutorCompletionService(crawlerPool);

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxActive(100);
        config.setMaxIdle(20);
        config.setMaxWait(1000l);
        pool = new JedisPool(config, Utils.get(Constants.REDIS_ADDRESS), Integer.parseInt(Utils.get(Constants.REDIS_PORT)));
        queue = new CrawlerQueue(pool,Utils.get(Constants.QUEUENAME), Integer.parseInt(Utils.get(Constants.QUEUELENGTH)));
        template = XMLConfigFileReader.getTemplate(Utils.get(Constants.TEMPLATE)).get(Utils.parserDomain(url));
        fetchRecords = Sets.newHashSet(queue.list());
    }

    public void crawler() throws IOException, InterruptedException, TimeoutException, ExecutionException {
        Elements html = getHTMLContent(url);
        Iterator<Element> links = html.iterator();
        while (links.hasNext()) {
            String fetchURL = links.next().attr(HREF);
            if (!fetchRecords.contains(fetchURL) && Pattern.matches(template.get(Constants.URLREGEX),fetchURL)) {
                indexPage(fetchURL);
            }
        }
        recordFetchedURL();
    }

    public void recordFetchedURL() throws InterruptedException, ExecutionException {
        Future<String> result;
        while((result = finishedTask.poll(5, TimeUnit.SECONDS))!=null){
            queue.put(result.get());
        }
    }

    private void indexPage(final String fetchURL){
        IndexJob job = new IndexJob(fetchURL);
        job.setTemplate(template);
        finishedTask.submit(job);
    }

    public void close() {
        crawlerPool.shutdown();
    }

    private Elements getHTMLContent(String url) throws IOException {
        Fetcher fetcher = new Fetcher() {
            @Override
            public String fetch(String url) {
                return null;
            }
        };
        String regex = template.get(Constants.URLFILTER);
        return Jsoup.parse(fetcher.getHTML(url)).select(regex);
    }

    @Override
    public void run() {
        try {
            crawler();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
