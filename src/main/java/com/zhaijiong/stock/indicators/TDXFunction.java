package com.zhaijiong.stock.indicators;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-12.
 */
public class TDXFunction {
    Core core;

    public TDXFunction(){
        core = new Core();
    }

    public double[] hhv(double[] prices,int period){
        double[] output = new double[prices.length];
        double[] tempOutPut = new double[prices.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        begin.value = -1;
        length.value = -1;

        RetCode max = core.max(0, prices.length-1, prices, period, begin, length, tempOutPut);

        for (int i = 0; i < period; i++) {
            output[i] = 0;
        }
        for (int i = period; 0 < i && i < (prices.length); i++) {
            output[i] = tempOutPut[i - period];
        }
        return output;
    }
}
