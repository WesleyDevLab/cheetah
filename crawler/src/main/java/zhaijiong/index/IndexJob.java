package zhaijiong.index;

import zhaijiong.Constants;
import zhaijiong.fetcher.Fetcher;
import zhaijiong.fetcher.XMLTemplateFetcher;
import zhaijiong.tool.ESOperator;
import zhaijiong.tool.Utils;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: eryk
 * Date: 14-1-4
 * Time: 下午11:26
 * To change this template use File | Settings | File Templates.
 */
public class IndexJob implements Callable<String>{
    private final String url;
    private final ESOperator es;
    private Map<String,String> template;

    public IndexJob(String url){
        this.url = url;
        this.es = ESOperator.instance(Utils.get(Constants.ES_ADDRESS), Integer.parseInt(Utils.get(Constants.ES_PORT)));
    }

    public void setTemplate(Map<String,String> template){
        this.template = template;
    }

    @Override
    public String call() throws Exception {
        Fetcher sinaFetcher = new XMLTemplateFetcher(template);
        String jsonObject = sinaFetcher.fetch(url);
        es.index(template.get(Constants.XML_PROPERTY_INDEX), Constants.XML_PROPERTY_TYPE, jsonObject);
        return url;
    }
}
