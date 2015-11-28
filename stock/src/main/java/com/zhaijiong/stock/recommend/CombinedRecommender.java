package com.zhaijiong.stock.recommend;

import com.zhaijiong.stock.model.StockData;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-11-28.
 */
public class CombinedRecommender extends Recommender{

    @Autowired
    public List<Recommender> recommenderList;

    @Override
    public boolean isBuy(String symbol) {
        boolean isBuy = true;
        for(Recommender recommender:recommenderList){
            if(!recommender.isBuy(symbol)){
                isBuy = false;
            }
        }
        return isBuy;
    }

    @Override
    public boolean isBuy(List<StockData> stockDataList) {
        boolean isBuy = true;
        for(Recommender recommender:recommenderList){
            if(!recommender.isBuy(stockDataList)){
                isBuy = false;
            }
        }
        return isBuy;
    }

    public List<Recommender> getRecommenderList() {
        return recommenderList;
    }

    public void setRecommenderList(List<Recommender> recommenderList) {
        this.recommenderList = recommenderList;
    }
}
