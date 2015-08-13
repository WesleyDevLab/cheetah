package zhaijiong.tool;

import org.apache.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;

import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: eryk
 * Date: 13-10-29
 * Time: 下午11:47
 * To change this template use File | Settings | File Templates.
 */
public class ESOperator {
    public static Logger LOG = Logger.getLogger(ESOperator.class);
    public Client client;

    private ESOperator(){}

    public static ESOperator instance(String address,int port){
        ESOperator es = new ESOperator();
        if(es.client ==null){
            es.client = new TransportClient().addTransportAddress(new InetSocketTransportAddress(address,port));
        }
        return es;
    }

    public void close(){
        client.close();
    }

    public IndexResponse index(String index,String type,String json){
        IndexResponse _index = client.prepareIndex(index, type)
                .setSource(json)
                .execute()
                .actionGet();
        return _index;
    }

    public GetResponse get(String index,String type,String id){
        GetResponse get = client.prepareGet("twitter", "tweet", "1")
                .execute()
                .actionGet();
        return get;
    }

    public DeleteResponse delete(String index,String type,String id){
        DeleteResponse delete = client.prepareDelete("twitter", "tweet", "1")
                .execute()
                .actionGet();
        return delete;
    }

    public BulkResponse bulkIndex(List<String[]> records){
        BulkRequestBuilder builder = client.prepareBulk();
        Iterator<String[]> iter = records.iterator();
        while(iter.hasNext()){
            String[] record = iter.next();
            if(record.length==3){
                builder.add(client.prepareIndex(record[0],record[1]).setSource(record[2]));
            }
        }
        BulkResponse bulkResponses = builder.execute().actionGet();
        if(bulkResponses.hasFailures()){
            LOG.error(bulkResponses.buildFailureMessage());
        }
        return bulkResponses;
    }

    public SearchResponse search(String[] index,String[] type,TermQueryBuilder query,TermFilterBuilder filter,int offset,int limit){
        SearchResponse search = client.prepareSearch(index)
                .setTypes(type)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(query)
                .setFilter(filter)
                .setFrom(offset).setSize(limit).setExplain(true)
                .execute()
                .actionGet();
        return search;
    }

}
