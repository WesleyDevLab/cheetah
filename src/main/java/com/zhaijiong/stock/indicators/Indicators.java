package com.zhaijiong.stock.indicators;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

public class Indicators {
    private Core core;

    public Indicators(){
        core = new Core();
    }

    public double[] sma(double[] prices, int ma) {

        double[] tempOutPut = new double[prices.length];
        double[] output = new double[prices.length];

        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;

        retCode = core.sma(0, prices.length - 1, prices, ma, begin, length, tempOutPut);

        for (int i = 0; i < ma - 1; i++) {
            output[i] = 0;
        }
        for (int i = ma - 1; 0 < i && i < (prices.length); i++) {
            output[i] = tempOutPut[i - ma + 1];
        }

        return output;
    }

    public double[][] macd(double[] prices, int optInFastPeriod, int optInSlowPeriod, int optInSignalPeriod) {
        double[] tempoutput1 = new double[prices.length];
        double[] tempoutput2 = new double[prices.length];
        double[] tempoutput3 = new double[prices.length];
        double[][] output = { new double[prices.length], new double[prices.length], new double[prices.length] };

        double[] result1 = new double[prices.length];
        double[] result2 = new double[prices.length];
        double[] result3 = new double[prices.length];

        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;

        retCode = core.macd(0, prices.length - 1, prices, optInFastPeriod, optInSlowPeriod, optInSignalPeriod, begin,
                length, tempoutput1, tempoutput2, tempoutput3);

        for (int i = 0; i < prices.length - optInSlowPeriod; i++) {
            result1[i] = 0;
            result2[i] = 0;
            result3[i] = 0;
        }
        for (int i = prices.length - optInSlowPeriod; 0 < i && i < (prices.length); i++) {
            result1[i] = tempoutput1[i - (prices.length - optInSlowPeriod)];
            result2[i] = tempoutput2[i - (prices.length - optInSlowPeriod)];
            result3[i] = tempoutput3[i - (prices.length - optInSlowPeriod)];
        }

        for (int i = 0; i < prices.length; i++) {
            output[0][i] = result1[i];
            output[1][i] = result2[i];
            output[2][i] = result3[i];
        }
        return output;
    }

    public double[][] macd(double[] prices){
        return macd(prices,12, 26, 9);
    }

    public double[][] bbands(double[] prices, int optInTimePeriod, double optInNbDevUp, double optInNbDevDn) {
        MAType optInMAType = MAType.Sma;

        double[] tempoutput1 = new double[prices.length];
        double[] tempoutput2 = new double[prices.length];
        double[] tempoutput3 = new double[prices.length];
        double[][] output = { new double[prices.length], new double[prices.length], new double[prices.length] };

        double[] result1 = new double[prices.length];
        double[] result2 = new double[prices.length];
        double[] result3 = new double[prices.length];

        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;

        retCode = core.bbands(0, prices.length - 1, prices, optInTimePeriod, optInNbDevUp, optInNbDevDn, optInMAType,
                begin, length, tempoutput1, tempoutput2, tempoutput3);

        for (int i = 0; i < optInTimePeriod - 1; i++) {
            result1[i] = 0;
            result2[i] = 0;
            result3[i] = 0;
        }
        for (int i = optInTimePeriod - 1; 0 < i && i < (prices.length); i++) {
            result1[i] = tempoutput1[i - optInTimePeriod + 1];
            result2[i] = tempoutput2[i - optInTimePeriod + 1];
            result3[i] = tempoutput3[i - optInTimePeriod + 1];
        }

        for (int i = 0; i < prices.length; i++) {
            output[0][i] = result1[i];
            output[1][i] = result2[i];
            output[2][i] = result3[i];
        }
        return output;
    }

    public double[][] bbands(double[] prices){
        return bbands(prices,20, 2.0, 2.0);
    }

    public double[] obv(double[] prices, double[] volume) {
        double[] output = new double[prices.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;

        retCode = core.obv(0, prices.length - 1, prices, volume, begin, length, output);

        return output;
    }

    public double[] avgPrice(double[] opens,double[] highs,double[] lows,double[] closes){
        double[] output = new double[opens.length];
        MInteger outBegIdx = new MInteger();
        MInteger outNBElement = new MInteger();
        core.avgPrice(0,opens.length-1,opens,highs,lows,closes,outBegIdx,outNBElement,output);
        return output;
    }
}
