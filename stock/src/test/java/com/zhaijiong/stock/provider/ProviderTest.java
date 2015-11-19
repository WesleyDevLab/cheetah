package com.zhaijiong.stock.provider;

import com.google.common.base.Stopwatch;
import com.zhaijiong.stock.common.Conditions;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.DateRange;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.model.Bar;
import com.zhaijiong.stock.model.PeriodType;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.model.Tick;
import com.zhaijiong.stock.provider.Provider;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ProviderTest {

    @Test
    public void testRealtimeData() throws Exception {
        StockData values = Provider.realtimeData("600199");
        System.out.println(Utils.formatDate(values.date, "yyyy-MM-dd HH:mm:ss"));
        System.out.println(values);
    }

    @Test
    public void testDailyData() throws Exception {
        List<StockData> values = Provider.dailyData("600376");
        for (StockData stockData : values) {
            System.out.println(stockData.date);
            Utils.printMap(stockData);
        }
    }

    @Test
    public void testDailyDataHis() throws Exception {
        DateRange dateRange = DateRange.getRange(10);
        List<StockData> values = Provider.dailyData("600199", dateRange.start(), dateRange.stop());
        for (StockData stockData : values) {
            System.out.println(stockData);
        }
    }

    @Test
    public void testDailyDataZS(){
        List<StockData> stockDataList = Provider.dailyDataZS("000001");
        for(StockData stockData:stockDataList){
            System.out.println(stockData);
        }
    }

    @Test
    public void testMinuteData() throws Exception {
        List<StockData> stockData = Provider.minuteData("600376", "15");
        System.out.println(stockData);
        Utils.printMap(stockData.get(0));
    }

    @Test
    public void testMinuteDataHis() throws Exception {
        DateRange range = DateRange.getRange(10);
        List<StockData> stockDataList = Provider.minuteData("600376", range.start(), range.stop(), "5");
        for (StockData stockData : stockDataList) {
            System.out.println(stockData);
            Utils.printMap(stockData);
        }
    }

    @Test
    public void testMoneyFlow() throws Exception {
        StockData stockData = Provider.moneyFlowData("601886");
        System.out.println(stockData);
        Utils.printMap(stockData);
    }

    @Test
    public void testMoneyFlowHis() throws Exception {
        List<StockData> stockDataList = Provider.moneyFlowData("600376", "20150810", "20150830");
        for (StockData stockData : stockDataList) {
            System.out.println(stockData);
            Utils.printMap(stockData);
        }
    }

    @Test
    public void testMoneyFlowDapan() throws Exception {
        List<StockData> stockDataList = Provider.moneyFlowDapanData();
        for (StockData stockData : stockDataList) {
            System.out.println(stockData);
            Utils.printMap(stockData);
        }
    }

    @Test
    public void testMoneyFlowIndustry() throws Exception {
        List<StockData> stockDataList = Provider.moneyFlowIndustryData("1");
        for (StockData stockData : stockDataList) {
            System.out.println(stockData);
            Utils.printMap(stockData);
        }
    }

    @Test
    public void testMoneyFlowConcept() throws Exception {
        List<StockData> stockDataList = Provider.moneyFlowConceptData("1");
        for (StockData stockData : stockDataList) {
            System.out.println(stockData);
            Utils.printMap(stockData);
        }
    }

    @Test
    public void testMoneyFlowRegion() throws Exception {
        List<StockData> stockDataList = Provider.moneyFlowRegionData("1");
        for (StockData stockData : stockDataList) {
            System.out.println(stockData);
            Utils.printMap(stockData);
        }
    }

    @Test
    public void testFinanceDataHis() throws Exception {
        StockData stockData = Provider.financeData("600376");
        System.out.println(stockData);
        Utils.printMap(stockData);
    }

    @Test
    public void testFinanceData() throws Exception {
        List<StockData> stockDataList = Provider.financeData("600376", "20150101", "20150830");
        for (StockData stockData : stockDataList) {
            System.out.println(stockData);
            Utils.printMap(stockData);
        }
    }

    @Test
    public void testFinanceYearData() throws Exception {
        List<StockData> stockDataList = Provider.financeYearData("600376");
        for (StockData stockData : stockDataList) {
            System.out.println(stockData);
            Utils.printMap(stockData);
        }
    }

    @Test
    public void testTickData() {
        List<Tick> ticks = Provider.tickData("600376");

        double avgVolume = 0;
        for (Tick tick : ticks) {
            avgVolume += tick.volume;
        }
        avgVolume = avgVolume / ticks.size();

        int count = 0;
        for (int i = 1; i < ticks.size(); i++) {
            Tick tick = ticks.get(i);

            if (tick.type == Tick.Type.BUY && tick.volume > avgVolume * 20 && !Utils.formatDate(tick.date,"HH:mm:dd").contains("15:00:") && !Utils.formatDate(tick.date,"HH:mm:dd").contains("09:30:")) {
                System.out.println(ticks.get(i));
                count++;
            }
        }
        if (count > 1) {
            System.out.println(",平均每笔成交量:" + Utils.formatDouble(avgVolume));
        }
    }

    @Test
    public void testTickDataHis() {
        List<Tick> ticks = Provider.tickData("600376", "20150901");

        double avgVolume = 0;
        for (Tick tick : ticks) {
            avgVolume += tick.volume;
        }
        avgVolume = avgVolume / ticks.size();

        int count = 0;
        for (int i = 1; i < ticks.size(); i++) {
            Tick tick = ticks.get(i);

            if (tick.type == Tick.Type.BUY && tick.volume > avgVolume * 20 && !Utils.formatDate(tick.date,"HH:mm:dd").contains("15:00:") && !Utils.formatDate(tick.date,"HH:mm:dd").contains("09:30:")) {
                System.out.println(ticks.get(i));
                count++;
            }
        }
        if (count > 1) {
            System.out.println(",平均每笔成交量:" + Utils.formatDouble(avgVolume));
        }
    }

    @Test
    public void testTradingStockListWith(){
        Conditions conditions = new Conditions();
        conditions.addCondition("PE", Conditions.Operation.LT,10d);
        conditions.addCondition("PE", Conditions.Operation.GT,0d);
        List<String> stocks = Provider.tradingStockList(conditions);
        for(String stock :stocks){
            System.out.println(stock);
        }
    }

    @Test
    public void testTradingStockList(){
        List<String> stocks = Provider.tradingStockList();
        for(String stock :stocks){
            System.out.println(stock);
        }
    }

    @Test
    public void testComputeMACD(){
        List<StockData> stockDataList = Provider.computeDailyMACD("300217",120);
        for(StockData stockData :stockDataList){
//            System.out.println(Utils.formatDate(stockData.date,"MMdd") + " " + stockData.symbol + "\t" + stockData.get(StockConstants.DIF) + "\t"+ stockData.get(StockConstants.DEA) + "\t" + stockData.get(StockConstants.MACD) + "\t" + (stockData.get(StockConstants.MACD_CROSS)==null?"":stockData.get(StockConstants.MACD_CROSS)));
            System.out.println(stockData);
        }
    }

    @Test
    public void testComputeBoll(){
        List<StockData> stockDataList = Provider.computeDailyBoll("300217", 120);
        for(StockData stockData :stockDataList){
//            System.out.println(Utils.formatDate(stockData.date,"MMdd") + " " + stockData.symbol + "\t" + stockData.get(StockConstants.DIF) + "\t"+ stockData.get(StockConstants.DEA) + "\t" + stockData.get(StockConstants.MACD) + "\t" + (stockData.get(StockConstants.MACD_CROSS)==null?"":stockData.get(StockConstants.MACD_CROSS)));
            System.out.println(stockData);
        }
    }

    @Test
    public void testComputeMA(){
        List<StockData> stockDataList1 = Provider.computeDailyMA("300217", 120,"close");
        List<StockData> stockDataList2 = Provider.computeDailyMA("300217", 120, "volume");
        for(int i=0;i<stockDataList1.size();i++){
            System.out.println(stockDataList1.get(i));
            System.out.println(stockDataList2.get(i));
            System.out.println("");
        }
    }

    @Test
    public void testComputeDailyAll(){
        Stopwatch stopwatch = Stopwatch.createStarted();
        List<StockData> stockDataList = Provider.computeDailyAll("300217",120);
        for(StockData stockData :stockDataList){
            System.out.println(stockData);
        }
        System.out.println(stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testComputeBar(){
        List<Bar> bars = Provider.computeBar("600160", "20151119", PeriodType.FIFTEEN_MIN);
        for(Bar bar:bars){
            System.out.println(bar);
        }
    }
}