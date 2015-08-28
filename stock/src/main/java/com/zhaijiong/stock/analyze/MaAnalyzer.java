package com.zhaijiong.stock.analyze;

import com.zhaijiong.stock.indicators.Indicators;

import java.util.Map;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-25.
 */
public class MaAnalyzer implements Analyzer{

    private int period;
    private Indicators indicators;

    public MaAnalyzer(int period){
        this.period = period;
        indicators = new Indicators();
    }

    @Override
    public boolean analyze(double[] data) {
        double[] sma = indicators.sma(data, period);
        return false;
    }
}
