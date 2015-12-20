package com.zhaijiong.analyze;

import com.google.common.collect.Lists;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.tools.Suggest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-12-19.
 */
@RestController
public class StocksController {

    @RequestMapping("/suggest/{symbol}")
    public Result suggest(@PathVariable String symbol) {
        List<Map<String, String>> suggest = Suggest.suggest(symbol);
        return Result.successResult(suggest);
    }

    @RequestMapping("/shareholder/{symbol}")
    public Result shareholder(@PathVariable String symbol){
        List<StockData> shareHolderCountData = Provider.getShareHolderCountData(symbol);
        List<Map<String,String>> result = Lists.newArrayList();
        for(StockData stockData :shareHolderCountData){
            result.add(Utils.toMap(stockData));
        }
        return Result.successResult(result);
    }
}
