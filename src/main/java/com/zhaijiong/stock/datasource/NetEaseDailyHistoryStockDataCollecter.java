package com.zhaijiong.stock.datasource;

import com.google.common.collect.Lists;
import com.zhaijiong.stock.Stock;
import com.zhaijiong.stock.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Scanner;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-4.
 */
public class NetEaseDailyHistoryStockDataCollecter implements StockDataCollecter{
    private static final Logger LOG = LoggerFactory.getLogger(NetEaseDailyHistoryStockDataCollecter.class);



    private String startDate = "";
    private String stopDate = "";
    private String symbol = "";

    public final String historyDataUrl = "http://quotes.money.163.com/service/chddata.html?code=%s&start=%s&end=%s&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP";

//    public NetEaseDailyHistoryStockDataCollecter(String symbol,String startDate,String stopDate){
//        this.symbol = symbol;
//        this.startDate =startDate;
//        this.stopDate =stopDate;
//    }

    @Override
    public List<Stock> collect(String symbol,String start,String stop) {
        String url = String.format(historyDataUrl, Utils.netEaseSymbol(symbol),start,stop);
        System.out.println(url);
        List<Stock> stocks = Lists.newLinkedList();
        try {
            URL netEaseFin = new URL(url);
            URLConnection data = netEaseFin.openConnection();
//            data.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.125 Safari/537.36");
//            data.setRequestProperty("Referer","http://quotes.money.163.com/trade/lsjysj_601886.html");
//            data.setRequestProperty("Upgrade-Insecure-Requests","1");
//            data.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
//            data.setRequestProperty("Accept-Encoding","gzip, deflate, sdch");
//            data.setRequestProperty("Accept-Language","zh,zh-CN;q=0.8,en-US;q=0.6,en;q=0.4");
//            data.setRequestProperty("Connection","keep-alive");
//            data.setRequestProperty("Cookie","_ntes_nuid=d3aed30bc0133ddff908c540943a23b2; vjuids=-1fc49eab0.1458c1773e3.0.0d65e6db; NETEASE_AUTH_SOURCE=space; NETEASE_AUTH_USERNAME=eryk86; NTES_REPLY_NICKNAME=majun2012bj%40126.com%7Cmajun2012bj%7C5354388320516968221%7C4450108723%7Chttp%3A%2F%2Fmimg.126.net%2Fp%2Fbutter%2F1008031648%2Fimg%2Fface_big.gif%7C%7C1%7C2; ANTICSRF=e4a5105bbc415761e200ff7dec4c5218; __utma=187553192.1372071149.1406110450.1413951598.1431490218.6; __utmc=187553192; __utmz=187553192.1431490218.6.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); __oc_uuid=558e1fd0-0195-11e4-a009-bd2e695e8460; usertrack=c+5+hlVUUgeTsGn7CZgwAg==; NTES_SESS=ilt0HxmJT69CpP7ttmtY3yFRUwf2jAT_pVnv2AIxezbLwwsHQ4qTt6iGO26kyPklS9DeoE13Phmguv3bSLzcS66ER3KL9G3p1OkflDxTKo5z0NqzjYsHNiqTny9C0LB1L_bYfQScagtLU7eYPBZBsrRnQ2hwJ.XzgD9Q64Iu93pXq; P_INFO=majun2012bj@126.com|1433667363|0|mail126|00&99|bej&1433663373&mail126#bej&null#10#0#0|&0|mail126|majun2012bj@126.com; S_INFO=1433667363|0|#3&20#|majun2012bj@126.com; _ntes_nnid=51e9786b27f7a04d51f8b3471bf6df79,1435566590785; Province=0571; City=0571; __gads=ID=c97ddd0253a694d7:T=1437558224:S=ALNI_MZg_A474CuSrRed6--NLP00m9HJYg; ne_analysis_trace_id=1437993650324; BBSJSESSIONID=753d5e0f-0530-454f-a15d-cbdac4d1f222; BBS_STATUS=001%7C0; n_ht_s=1; _ntes_stock_recent_=0601886; _ntes_stock_recent_=0601886; _ntes_stock_recent_=0601886; vinfo_n_f_l_n3=038e3e438f11d260.1.5.1402398576968.1422267065658.1438687796758; s_n_f_l_n3=038e3e438f11d2601422323275351; vjlast=1398214718.1438687090.11");
            data.setConnectTimeout(60000);
            Scanner input = new Scanner(data.getInputStream());
            if (input.hasNext()) { // skip line (header)
                input.nextLine();
            }

            //start reading data
            while (input.hasNextLine()) {
                String record = input.nextLine();
                String[] line = record.split(",");
                if(line.length == 15 && !record.contains("None")){
                    try{
                        Stock stock = new Stock();

                        stock.date=Utils.parseDate(line[0]);
                        stock.symbol = line[1].replace("'","");
                        stock.name = line[2];
                        stock.close = Utils.parseDouble(line[3]);
                        stock.high = Utils.parseDouble(line[4]);
                        stock.low = Utils.parseDouble(line[5]);
                        stock.open = Utils.parseDouble(line[6]);
                        stock.lastClose = Utils.parseDouble(line[7]);
                        stock.changeAmount = Utils.parseDouble(line[8]);
                        stock.change = Utils.parseDouble(line[9]);
                        stock.turnoverRate = Utils.parseDouble(line[10]);
                        stock.volume = Utils.parseDouble(line[11]);
                        stock.amount = Utils.parseDouble(line[12]);
                        stock.totalValue = Utils.parseDouble(line[13]);
                        stock.marketValue = Utils.parseDouble(line[14]);
                        stock.marketValue = Utils.formatDouble(Utils.parseDouble(line[14]));
                        stock.amplitude = Utils.formatDouble((stock.high-stock.low)/stock.lastClose);
                        stocks.add(stock);
                    }catch(Exception e){
                        LOG.warn(String.format("stock %s convert error",symbol) + record);
                    }
                }
            }

        } catch (Exception e) {
            LOG.error(String.format("stock %s collect error",symbol),e);
        }
        return stocks;
    }
}
