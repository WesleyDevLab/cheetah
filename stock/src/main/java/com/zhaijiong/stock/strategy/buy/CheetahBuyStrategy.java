package com.zhaijiong.stock.strategy.buy;

import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.strategy.StrategyBase;
import com.zhaijiong.stock.strategy.StrategyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.zhaijiong.stock.common.StockConstants.*;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-12-7.
 */
public class CheetahBuyStrategy extends StrategyBase implements BuyStrategy{
    private static final Logger LOG = LoggerFactory.getLogger(CheetahBuyStrategy.class);

    private int crossCount = 3;
    private static final String NAME = "cheetahBuy";

    public CheetahBuyStrategy(){
        this.name = NAME;
    }

    public CheetahBuyStrategy(int crossCount){
        this.crossCount = crossCount;
        this.name = NAME;
    }

    @Override
    public double buy(String symbol) {
        List<StockData> stockDataList = getDailyData(symbol);
        return buy(stockDataList);
    }

    /**
     * 买入策略暂时使用金蜘蛛策略
     * @param stockDataList
     * @return
     */
    @Override
    public double buy(List<StockData> stockDataList) {
        stockDataList = StrategyUtils.goldenSpider(stockDataList);
        if(stockDataList.size()>1){
            double status = stockDataList.get(stockDataList.size() - 1).get(GOLDEN_SPIDER);
            if(status>=crossCount){
                System.out.println("buy:"+stockDataList.get(stockDataList.size() - 1));
                return stockDataList.get(stockDataList.size() - 1).get(CLOSE);
            }
        }
        return -1;
    }

    @Override
    public boolean isBuy(String symbol) {
        if(blackList.contains(symbol)){
            return false;
        }
        List<StockData> stockDataList = getDailyData(symbol);
        if(stockDataList.size()<60){
            LOG.warn(String.format("symbol [%s] stockdata size < 60",symbol));
            blackList.add(symbol);
            return false;
        }
        return isBuy(stockDataList);
    }

    /*
    均线粘合
    RSV:=(C - LLV(L,9)) / (HHV(H, 9) - LLV(L, 9)) * 100;
    FASTK:=SMA(RSV, 3, 1);
    K:=SMA(FASTK, 3, 1);
    D:=SMA(K, 2, 1);
    J:=3*K - 2*D;
    HM:=MAX(MA(C, M1), MAX(MA(C, M2), MAX(MA(C, M3), MA(C, M4)) ) );
    LM:=MIN(MA(C, M1), MIN(MA(C, M2), MIN(MA(C, M3), MA(C, M4)) ) );
    (MA(REF(HM, 3) / REF(LM, 3), 5) < 1.02) AND (V > REF(V, 1)*1.2) AND (J > REF(J,1));
     */
    @Override
    public boolean isBuy(List<StockData> stockDataList) {

        stockDataList = Provider.computeMA(stockDataList,CLOSE);
        double[] high = Utils.getArrayFrom(stockDataList, HIGH);
        double[] low = Utils.getArrayFrom(stockDataList, LOW);
        double[] closes = Utils.getArrayFrom(stockDataList, CLOSE);
        double[][] kdj = indicators.kdj(high,low,closes);

        int size = stockDataList.size();
        for(int i=0;i<size;i++){
            StockData stockData = stockDataList.get(i);
            double hm1 = function.max(stockData.get(CLOSE_MA5),stockData.get(CLOSE_MA10),stockData.get(CLOSE_MA20),stockData.get(CLOSE_MA30));
            double lm1 = function.min(stockData.get(CLOSE_MA5),stockData.get(CLOSE_MA10),stockData.get(CLOSE_MA20),stockData.get(CLOSE_MA30));
            double hm2 = function.max(stockData.get(CLOSE_MA5),stockData.get(CLOSE_MA13),stockData.get(CLOSE_MA34),stockData.get(CLOSE_MA55));
            double lm2 = function.min(stockData.get(CLOSE_MA5),stockData.get(CLOSE_MA13),stockData.get(CLOSE_MA34),stockData.get(CLOSE_MA55));
            stockDataList.get(i).put("hm1",hm1);
            stockDataList.get(i).put("lm1",lm1);
            stockDataList.get(i).put("hm2",hm2);
            stockDataList.get(i).put("lm2",lm2);
            if(i>=3){
                stockDataList.get(i).put("hm1/lm1",stockDataList.get(i-3).get("hm1")/stockDataList.get(i-3).get("lm1"));
                stockDataList.get(i).put("hm2/lm2",stockDataList.get(i-3).get("hm2")/stockDataList.get(i-3).get("lm2"));
            }else{
                stockDataList.get(i).put("hm1/lm1",0d);
                stockDataList.get(i).put("hm2/lm2",0d);
            }
        }
        stockDataList = Provider.computeMA(stockDataList,"hm1/lm1");
        stockDataList = Provider.computeMA(stockDataList,"hm2/lm2");
        if((stockDataList.get(size-1).get("hm1/lm1_ma3") < 1.02 || stockDataList.get(size-1).get("hm2/lm2_ma3") < 1.02) &&
                stockDataList.get(size-1).get(VOLUME) > stockDataList.get(size-2).get(VOLUME) * 1.2 &&
                kdj[2][stockDataList.size()-1] > kdj[2][stockDataList.size()-2]){
            return true;
        }
        return false;
    }
}
