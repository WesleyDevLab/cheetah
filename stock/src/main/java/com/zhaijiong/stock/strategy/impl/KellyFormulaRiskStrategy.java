package com.zhaijiong.stock.strategy.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.zhaijiong.stock.common.StockConstants;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.strategy.RiskStrategy;

import java.util.Map;

/**
 * 凯利公式
 * f*=（bp-q)/b = p-q/b
 * 其中
 * f* 为现有资金应进行下次投注的比例；
 * b 盈亏比；
 * p 为获胜率；
 * q 为落败率，即 1 - p；
 */
public class KellyFormulaRiskStrategy implements RiskStrategy {

    private double p = 0;
    private double q = 0;
    private double b = 0;
    private double f = 0;

    /**
     * @param p 胜率
     * @param b 赔率
     */
    public KellyFormulaRiskStrategy(double p, double b) {
        Preconditions.checkArgument(p > 0 && p < 1);
        Preconditions.checkArgument(b > 1);
        this.p = p;
        this.q = 1 - p;
        this.b = b;
        f = Utils.formatDouble(compute(p, b));
    }

    public KellyFormulaRiskStrategy(double p, double profit, double loss) {
        Preconditions.checkArgument(p > 0 && p < 1);
        Preconditions.checkArgument(profit > 0 && loss > 0 && profit > loss);
        this.p = p;
        this.q = 1 - p;
        this.b = profit / loss;
        this.f = Utils.formatDouble(p - q / b);
    }

    public static double compute(double p, double b) {
        return p - (1 - p) / b;
    }

    public static double compute(double p, double profit, double loss) {
        return p - (1 - p) / (profit / loss);
    }

    @Override
    public Map<String, Double> risk(String stock) {
        Map<String, Double> risk = Maps.newHashMap();
        risk.put(StockConstants.POSITION, this.f);
        return risk;
    }
}
