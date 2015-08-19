package com.zhaijiong.stock.collect;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.download.Downloader;

import java.util.List;
import java.util.Map;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-17.
 */
public class FinanceDataCollecter implements Collecter<String,Map<String,String>>{

    //主要财务指标
    private String mainFinanceReport = "http://quotes.money.163.com/service/zycwzb_%s.html?type=report";
    //盈利能力
    private String profitReport = "http://quotes.money.163.com/service/zycwzb_%s.html?type=report&part=ylnl";
    //偿还能力
    private String debtReport = "http://quotes.money.163.com/service/zycwzb_%s.html?type=report&part=chnl";
    //成长能力
    private String growReport = "http://quotes.money.163.com/service/zycwzb_%s.html?type=report&part=cznl";
    //营运能力
    private String operateReport = "http://quotes.money.163.com/service/zycwzb_%s.html?type=report&part=yynl";
    //财务报表摘要
    private String abstractFinanceReport = "http://quotes.money.163.com/service/cwbbzy_%s.html";

    //TODO http://soft-f9.eastmoney.com/soft/gp5.php?code=30024602

    @Override
    public Map<String,Map<String,String>> collect(String symbol) {
        Map<String,Map<String,String>> report = Maps.newTreeMap();

        String[] lines = Downloader.download(String.format(mainFinanceReport,symbol)).split("\n");
        toReport(lines, report);
        lines = Downloader.download(String.format(profitReport,symbol)).split("\n");
        toReport(lines, report);
        lines = Downloader.download(String.format(debtReport,symbol)).split("\n");
        toReport(lines, report);
        lines = Downloader.download(String.format(growReport,symbol)).split("\n");
        toReport(lines, report);
        lines = Downloader.download(String.format(operateReport,symbol)).split("\n");
        toReport(lines, report);
        lines = Downloader.download(String.format(abstractFinanceReport,symbol)).split("\n");
        toReport(lines, report);

        return report;
    }

    private void toReport(String[] lines, Map<String, Map<String, String>> report) {
        if(lines.length<=1){
            return;
        }
        List<String[]> columns = Lists.newArrayList();
        for(int i=0;i<lines.length;i++){
            if(!Strings.isNullOrEmpty(lines[i].trim())){
                columns.add(lines[i].split(","));
            }
        }

        //列
        for(int i=1;i<columns.get(0).length;i++){
            Map<String,String> maps = Maps.newTreeMap();
            //行
            for(int j=1;j<columns.size();j++){
                if(i<columns.get(j).length){
                    maps.put(columns.get(j)[0],columns.get(j)[i]);
                }
            }
            if(report.get(columns.get(0)[i])!=null&&!Strings.isNullOrEmpty(columns.get(0)[i])){
                report.get(columns.get(0)[i]).putAll(maps);
            }else if(!Strings.isNullOrEmpty(columns.get(0)[i].trim())){
                report.put(columns.get(0)[i],maps);
            }
        }
    }

    @Override
    public String getPath(String symbol) {
        return null;
    }

    public static void main(String[] args) {
        FinanceDataCollecter collecter = new FinanceDataCollecter();
        Map<String,Map<String,String>> collect = collecter.collect("601886");
        for(Map.Entry<String,Map<String,String>> records:collect.entrySet()){
            System.out.println("---------"+  records.getKey() +"---------"+records.getValue().size());
            for(Map.Entry<String,String> kv:records.getValue().entrySet()){
                System.out.println(kv.getKey()+":"+kv.getValue());
            }
        }
    }
}
