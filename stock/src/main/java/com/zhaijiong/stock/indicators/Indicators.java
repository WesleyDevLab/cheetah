package com.zhaijiong.stock.indicators;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

public class Indicators {
    private Core core;

    public Indicators() {
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
        double[][] output = {new double[prices.length], new double[prices.length], new double[prices.length]};

        double[] result1 = new double[prices.length];
        double[] result2 = new double[prices.length];
        double[] result3 = new double[prices.length];

        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;
        MAType optInFastMAType = MAType.Ema;
        MAType optInSlowMAType = MAType.Ema;
        MAType optInSignalMAType = MAType.Ema;

        retCode = core.macdExt(0, prices.length - 1, prices, optInFastPeriod, optInFastMAType, optInSlowPeriod,
                optInSlowMAType, optInSignalPeriod, optInSignalMAType, begin, length, tempoutput1, tempoutput2,
                tempoutput3);
        for (int i = 0; i < begin.value; i++) {
            result1[i] = 0;
            result2[i] = 0;
            result3[i] = 0;
        }
        for (int i = begin.value; 0 < i && i < (prices.length); i++) {
            result1[i] = tempoutput1[i - begin.value];
            result2[i] = tempoutput2[i - begin.value];
            result3[i] = tempoutput3[i - begin.value];
        }

        for (int i = 0; i < prices.length; i++) {
            output[0][i] = result1[i];
            output[1][i] = result2[i];
            output[2][i] = result3[i];
        }
        return output;
    }

    public double[][] macd(double[] prices) {
        return macd(prices, 12, 26, 9);
    }

    public double[][] bbands(double[] prices, int optInTimePeriod, double optInNbDevUp, double optInNbDevDn) {
        MAType optInMAType = MAType.Sma;

        double[] tempoutput1 = new double[prices.length];
        double[] tempoutput2 = new double[prices.length];
        double[] tempoutput3 = new double[prices.length];
        double[][] output = {new double[prices.length], new double[prices.length], new double[prices.length]};

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

    public double[][] bbands(double[] prices) {
        return bbands(prices, 20, 2.0, 2.0);
    }

    // 6,12,24
    public double[] rsi(double[] prices, int period) {

        double[] output = new double[prices.length];
        double[] tempOutPut = new double[prices.length];

        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;

        retCode = core.rsi(0, prices.length - 1, prices, period, begin, length, tempOutPut);

        for (int i = 0; i < period; i++) {
            output[i] = 0;
        }
        for (int i = period; 0 < i && i < (prices.length); i++) {
            output[i] = tempOutPut[i - period];
        }
        return output;
    }

    public double[] sar(double[] highPrices, double[] lowPrices, double optInAcceleration, double optInMaximum) {

        double[] output = new double[lowPrices.length];
        double[] tempoutput = new double[lowPrices.length];

        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;

        retCode = core.sar(0, lowPrices.length - 1, highPrices, lowPrices, optInAcceleration, optInMaximum, begin,
                length, tempoutput);

        for (int i = 1; i < lowPrices.length; i++) {
            output[i] = tempoutput[i - 1];
        }
        return output;

    }

    public double[] adx(double[] lowPrices, double[] highPrices, double[] closePrices, int optInTimePeriod) {

        double[] output = new double[lowPrices.length];
        double[] tempOutPut = new double[lowPrices.length];

        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;

        retCode = core.adx(0, lowPrices.length - 1, highPrices, lowPrices, closePrices, optInTimePeriod, begin, length,
                tempOutPut);
        // Adx(int startIdx, int endIdx, double[] inHigh, double[] inLow,
        // double[] inClose, int optInTimePeriod, out int outBegIdx, out int
        // outNBElement, double[] outReal);

        for (int i = 0; i < lowPrices.length - length.value; i++) {
            output[i] = 0;
        }
        for (int i = lowPrices.length - length.value; 0 < i && i < (lowPrices.length); i++) {
            output[i] = tempOutPut[i - (lowPrices.length - length.value)];
        }
        return output;
    }

    public double[] adxr(double[] lowPrices, double[] highPrices, double[] closePrices, int optInTimePeriod) {

        double[] output = new double[lowPrices.length];
        double[] tempOutPut = new double[lowPrices.length];

        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;

        retCode = core.adxr(0, lowPrices.length - 1, highPrices, lowPrices, closePrices, optInTimePeriod, begin,
                length, tempOutPut);
        // Adx(int startIdx, int endIdx, double[] inHigh, double[] inLow,
        // double[] inClose, int optInTimePeriod, out int outBegIdx, out int
        // outNBElement, double[] outReal);

        for (int i = 0; i < lowPrices.length - length.value; i++) {
            output[i] = 0;
        }
        for (int i = lowPrices.length - length.value; 0 < i && i < (lowPrices.length); i++) {
            output[i] = tempOutPut[i - (lowPrices.length - length.value)];
        }

        return output;
    }

    public double[] cci(double[] highPrices, double[] lowPrices, double[] closePrices, int inTimePeriod) {

        double[] output = new double[lowPrices.length];
        double[] tempOutPut = new double[lowPrices.length];

        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;

        retCode = core.cci(0, lowPrices.length - 1, highPrices, lowPrices, closePrices, inTimePeriod, begin, length,
                tempOutPut);
        // Cci(int startIdx, int endIdx, double[] inHigh, double[] inLow,
        // double[] inClose, int optInTimePeriod, out int outBegIdx, out int
        // outNBElement, double[] outReal);

        for (int i = 0; i < inTimePeriod - 1; i++) {
            output[i] = 0;
        }
        for (int i = inTimePeriod - 1; 0 < i && i < (lowPrices.length); i++) {
            output[i] = tempOutPut[i - inTimePeriod + 1];
        }

        return output;
    }

    public double[] mfi(double[] highPrices, double[] lowPrices, double[] closePrices, double[] inVolume,
                        int optInTimePeriod) {

        double[] output = new double[lowPrices.length];
        double[] tempOutPut = new double[lowPrices.length];

        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;

        retCode = core.mfi(0, lowPrices.length - 1, highPrices, lowPrices, closePrices, inVolume, optInTimePeriod,
                begin, length, tempOutPut);

        // Mfi(int startIdx, int endIdx, double[] inHigh, double[] inLow,
        // double[] inClose, double[] inVolume, int optInTimePeriod, out int
        // outBegIdx, out int outNBElement, double[] outReal);
        for (int i = 0; i < optInTimePeriod; i++) {
            output[i] = 0;
        }
        for (int i = optInTimePeriod; 0 < i && i < (lowPrices.length); i++) {
            output[i] = tempOutPut[i - optInTimePeriod];
        }

        return output;
    }

    public double[] obv(double[] prices, double[] volume) {
        double[] output = new double[prices.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;

        retCode = core.obv(0, prices.length - 1, prices, volume, begin, length, output);
        // public static RetCode Obv(int startIdx, int endIdx, double[] inReal,
        // double[] inVolume, out int outBegIdx, out int outNBElement, double[]
        // outReal);
        return output;

    }

    public double[] roc(double[] prices, int optInTimePeriod) {
        /*
         * ((price/prevPrice)-1)*100
         */
        double[] tempOutPut = new double[prices.length];
        double[] output = new double[prices.length];

        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;

        retCode = core.roc(0, prices.length - 1, prices, optInTimePeriod, begin, length, tempOutPut);
        // Roc(int startIdx, int endIdx, float[] inReal, int optInTimePeriod,
        // out int outBegIdx, out int outNBElement, double[] outReal);
        for (int i = 0; i < optInTimePeriod - 1; i++) {
            output[i] = 0;
        }
        for (int i = optInTimePeriod - 1; 0 < i && i < (prices.length); i++) {
            output[i] = tempOutPut[i - optInTimePeriod + 1];
        }
        return output;
    }

    public double[] rocP(double[] prices, int optInTimePeriod) {
        /*
         * (price-prevPrice)/prevPrice
         */
        double[] tempOutPut = new double[prices.length];
        double[] output = new double[prices.length];

        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;

        retCode = core.rocP(0, prices.length - 1, prices, optInTimePeriod, begin, length, tempOutPut);
        // Roc(int startIdx, int endIdx, float[] inReal, int optInTimePeriod,
        // out int outBegIdx, out int outNBElement, double[] outReal);
        for (int i = 0; i < optInTimePeriod - 1; i++) {
            output[i] = 0;
        }
        for (int i = optInTimePeriod - 1; 0 < i && i < (prices.length); i++) {
            output[i] = tempOutPut[i - optInTimePeriod + 1];
        }
        return output;
    }

    public double[] trix(double[] prices, int period) {

        double[] output = new double[prices.length];
        double[] tempOutPut = new double[prices.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;

        retCode = core.trix(0, prices.length - 1, prices, period, begin, length, tempOutPut);

        for (int i = 0; i < begin.value; i++) {
            output[i] = 0;
        }
        for (int i = begin.value; 0 < i && i < (prices.length); i++) {
            output[i] = tempOutPut[i - begin.value];
        }
        return output;
    }

    public double[] willR(double[] highPrices, double[] lowPrices, double[] closePrices, int inTimePeriod) {

        double[] output = new double[lowPrices.length];
        double[] tempOutPut = new double[lowPrices.length];

        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;

        retCode = core.willR(0, lowPrices.length - 1, highPrices, lowPrices, closePrices, inTimePeriod, begin, length,
                tempOutPut);
        // WillR(int startIdx, int endIdx, double[] inHigh, double[] inLow,
        // double[] inClose, int optInTimePeriod, out int outBegIdx, out int
        // outNBElement, double[] outReal);

        for (int i = 0; i < inTimePeriod - 1; i++) {
            output[i] = 0;
        }
        for (int i = inTimePeriod - 1; 0 < i && i < (lowPrices.length); i++) {
            output[i] = tempOutPut[i - inTimePeriod + 1];
        }

        return output;
    }

    // AD=Chaikin A/D Line
    public double[] ad(double[] highPrices, double[] lowPrices, double[] closePrices, double[] inVolume,
                       int optInTimePeriod) {

        double[] output = new double[lowPrices.length];
        // double[] tempOutPut = new double[lowPrices.length];

        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;

        retCode = core.ad(0, lowPrices.length - 1, highPrices, lowPrices, closePrices, inVolume, begin, length, output);

        // Ad(int startIdx, int endIdx, double[] inHigh, double[] inLow,
        // double[] inClose, double[] inVolume, out int outBegIdx, out int
        // outNBElement, double[] outReal);

        return output;
    }

    public double[][] aroon(double[] inHigh, double[] inLow, int optInTimePeriod) {
        double[][] output = {new double[inHigh.length], new double[inHigh.length]};
        double[] tempOutPut1 = new double[inHigh.length];
        double[] tempOutPut2 = new double[inHigh.length];

        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;

        // MAType optInMAType = new MAType();
        // MAType optInSlowD_MAType = new MAType();

        retCode = core.aroon(0, inHigh.length - 1, inHigh, inLow, optInTimePeriod, begin, length, tempOutPut1,
                tempOutPut2);
        // Aroon(int startIdx, int endIdx, double[] inHigh, double[] inLow, int
        // optInTimePeriod, out int outBegIdx, out int outNBElement, double[]
        // outAroonDown, double[] outAroonUp);

        for (int i = 0; i < inHigh.length - length.value; i++) {
            output[0][i] = 0;
            output[1][i] = 0;
        }
        for (int i = inHigh.length - length.value; 0 < i && i < (inHigh.length); i++) {
            output[0][i] = tempOutPut1[i - (inHigh.length - length.value)];
            output[1][i] = tempOutPut1[i - (inHigh.length - length.value)];
        }

        return output;
    }

    public double[] aroonOsc(double[] inHigh, double[] inLow, int optInTimePeriod) {
        double[] output = new double[inHigh.length];
        double[] tempOutPut = new double[inHigh.length];

        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;

        retCode = core.aroonOsc(0, inHigh.length - 1, inHigh, inLow, optInTimePeriod, begin, length, tempOutPut);
        // AroonOsc(int startIdx, int endIdx, double[] inHigh, double[] inLow,
        // int optInTimePeriod, out int outBegIdx, out int outNBElement,
        // double[] outReal);

        for (int i = 0; i < inHigh.length - length.value; i++) {
            output[i] = 0;
        }
        for (int i = inHigh.length - length.value; 0 < i && i < (inHigh.length); i++) {
            output[i] = tempOutPut[i - (inHigh.length - length.value)];
        }

        return output;

    }

    public double[] bop(double[] openPrices, double[] highPrices, double[] lowPrices, double[] closePrices) {
        double[] output = new double[highPrices.length];

        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;

        retCode = core.bop(0, lowPrices.length - 1, openPrices, highPrices, lowPrices, closePrices, begin, length,
                output);
        // Bop(int startIdx, int endIdx, double[] inOpen, double[] inHigh,
        // double[] inLow, double[] inClose, out int outBegIdx, out int
        // outNBElement, double[] outReal);

        return output;
    }

    public double[] cmo(double[] closePrices, int period) {

        double[] output = new double[closePrices.length];
        double[] tempOutPut = new double[closePrices.length];

        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;

        retCode = core.cmo(0, closePrices.length - 1, closePrices, period, begin, length, tempOutPut);
        // Cmo(int startIdx, int endIdx, double[] inReal, int optInTimePeriod,
        // out int outBegIdx, out int outNBElement, double[] outReal);

        for (int i = 0; i < closePrices.length - length.value; i++) {
            output[i] = 0;
        }
        for (int i = closePrices.length - length.value; 0 < i && i < (closePrices.length); i++) {
            output[i] = tempOutPut[i - (closePrices.length - length.value)];
        }
        return output;
    }

    public double[] kama(double[] prices, int optInTimePeriod) {

        double[] tempOutPut = new double[prices.length];
        double[] output = new double[prices.length];

        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;

        retCode = core.kama(0, prices.length - 1, prices, optInTimePeriod, begin, length, tempOutPut);
        // Kama(int startIdx, int endIdx, double[] inReal, int optInTimePeriod,
        // out int outBegIdx, out int outNBElement, double[] outReal);
        for (int i = 0; i < optInTimePeriod; i++) {
            output[i] = 0;
        }
        for (int i = optInTimePeriod; 0 < i && i < (prices.length); i++) {
            output[i] = tempOutPut[i - optInTimePeriod];
        }
        return output;
    }

    public double[] trima(double[] prices, int optInTimePeriod) {

        double[] tempOutPut = new double[prices.length];
        double[] output = new double[prices.length];

        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;

        retCode = core.trima(0, prices.length - 1, prices, optInTimePeriod, begin, length, tempOutPut);
        // Kama(int startIdx, int endIdx, double[] inReal, int optInTimePeriod,
        // out int outBegIdx, out int outNBElement, double[] outReal);
        for (int i = 0; i < optInTimePeriod - 1; i++) {
            output[i] = 0;
        }
        for (int i = optInTimePeriod - 1; 0 < i && i < (prices.length); i++) {
            output[i] = tempOutPut[i - optInTimePeriod + 1];
        }
        return output;
    }
}