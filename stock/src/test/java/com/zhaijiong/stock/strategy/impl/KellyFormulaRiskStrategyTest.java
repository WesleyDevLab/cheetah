package com.zhaijiong.stock.strategy.impl;

import com.google.common.collect.Lists;
import com.zhaijiong.stock.strategy.RiskStrategy;
import com.zhaijiong.stock.strategy.risk.KellyFormulaRiskStrategy;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class KellyFormulaRiskStrategyTest {

    @Test
    public void testRisk() throws Exception {
        RiskStrategy riskStrategy = new KellyFormulaRiskStrategy(0.5,2.5);
        Map<String, Double> risk = riskStrategy.risk("");
        Double val = risk.get("position");
        Double expected = 0.3d;
        Assert.assertEquals(expected,val);
    }
}