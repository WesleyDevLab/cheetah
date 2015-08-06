package com.zhaijiong.stock.indicators;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

//refer to:
//https://code.google.com/p/quantitativeinvestment/source/browse/trunk/+quantitativeinvestment+--username+Huafeng.LOU@gmail.com/QuantitativeInvestment/Tools/TaLib.cs?spec=svn60&r=48
//https://github.com/chartsy/chartsy/blob/624d54224615bda9ec55bbaca6e62653550e4be5/Chartsy/Stochastic%20Fast/src/org/chartsy/stochf/StochF.java
public class TALIBWraper {
    private Core core = new Core();

    // ��ƽ��Ĳ���
    public double[] getSma(double[] prices, int ma) {

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

    // ��Ȩ�ƶ�ƽ��ָ��
    public double[] getWma(double[] prices, int ma) {
        double[] tempOutPut = new double[prices.length];
        double[] output = new double[prices.length];

        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;

        retCode = core.wma(0, prices.length - 1, prices, ma, begin, length, tempOutPut);

        for (int i = 0; i < ma - 1; i++) {
            output[i] = 0;
        }
        for (int i = ma - 1; 0 < i && i < (prices.length); i++) {
            output[i] = tempOutPut[i - ma + 1];
        }
        return output;
    }

    // ������ָ��
    public double[] getSar(double[] highPrices, double[] lowPrices, double optInAcceleration/*
                                                                                             * �
                                                                                             * �
                                                                                             * �
                                                                                             * ٶ
                                                                                             * �
                                                                                             */, double optInMaximum/*
                                                                                                                     * �
                                                                                                                     * �
                                                                                                                     * �
                                                                                                                     * ֵ
                                                                                                                     */) {
        /*
         * SAR��n��=SAR��n��1��+AF[EP��N-1����SAR��N-1��]��
         * ���У�SAR��n��Ϊ��n�յ�SARֵ��SAR��n��1��Ϊ�ڣ�n��1���յ�ֵ��
         * AFΪ�������ӣ���м���ϵ��EPΪ����ۣ���߼ۻ���ͼۣ�
         */
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

    // ���ǿ��ָ��
    public double[] getRsi(double[] prices, int period) {
        /*
         * ����AΪN�������̼۵�����֮�ͣ�BΪN�������̼۵ĸ���֮�ͳ��ԣ���1��
         * ����A��B��Ϊ��A��B����RSI���㹫ʽ����RSI��N��=A�£�A��B����100
         */
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

    // ƽ����ͬƽ��ָ��
    public double[][] getMACD(double[] prices, int optInFastPeriod, int optInSlowPeriod, int optInSignalPeriod) {
        /*
         * optInFastPeriod(����)��optInSlowPeriod(����)��optInSignalPeriod
         * (�ź��� �� �ţͣ�n=��n*2/��n+1��+�ţͣ�n-1*��n-1��/��n+1�� ʽ��
         * ��n:�������̼�;�ƶ�ƽ�������ڣţͣ�n:��n�գţͣ�ֵ ��һ������£����
         * ٣ţͣ�һ��ѡ���գ����٣ţͣ�һ��ѡ12�գ���ʱ����ֵ���ģɣƣ��ļ���Ϊ��
         * �ģɣƣ��ţͣ�6���ţͣ�12 ������ƽ��ֵ����DEA����ʾ������㹫ʽΪ��
         * �ģţ�n���ģţ�n-1*8/10+�ģɣ�n*2/10
         * �ͣ��ã�n�����ģɣ�n���ͣ��ã�n-1���������������ͣ��ã�n-1
         */
        double[] tempoutput1 = new double[prices.length];
        double[] tempoutput2 = new double[prices.length];
        double[] tempoutput3 = new double[prices.length];
        double[][] output = { new double[prices.length], new double[prices.length], new double[prices.length] };

        double[] result1 = new double[prices.length];
        double[] result2 = new double[prices.length];
        double[] result3 = new double[prices.length];

        double[] temp = new double[60];

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

    // ƽ�������˶�ָ��
    public double[] getAdx(double[] lowPrices, double[] highPrices, double[] closePrices, int optInTimePeriod) {
        /*
         * ADX = SUM[(+DI-(-DI))/(+DI+(-DI)), N]/N N ��
         * ���ڼ�������ʹ�õ�ʱ��ε���ֵ��
         */
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

    // ƽ�������˶�ָ��
    public double[] getAdxr(double[] lowPrices, double[] highPrices, double[] closePrices, int optInTimePeriod) {
        /*
         * ADX = SUM[(+DI-(-DI))/(+DI+(-DI)), N]/N N ��
         * ���ڼ�������ʹ�õ�ʱ��ε���ֵ��
         */
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

    // ����ͨ��ָ��
    public double[][] getBbands(double[] prices, int optInTimePeriod, double optInNbDevUp, double optInNbDevDn) {
        /*
         * optInTimePeriod:ʱ�䣬optInNbDevUp���Ϲ죨UP�ߣ�,optInNbDevDn:�¹죨Down
         * �� �� �й
         * ���=N�����ƶ�ƽ���ߣ��Ϲ���=�й��ߣ���D����׼����¹���=�й�
         * �ߣ���D����׼����� ���й���=SMA��close
         * ��N����D=��׼��Ĳ���һ��ΪĬ��ֵ����2����׼��=SUM[(Close-a
         * )2��N]��(N-1)��ֵ��ƽ����.
         */
        MAType optInMAType = MAType.Sma;

        double[] tempoutput1 = new double[prices.length];
        double[] tempoutput2 = new double[prices.length];
        double[] tempoutput3 = new double[prices.length];
        double[][] output = { new double[prices.length], new double[prices.length], new double[prices.length] };

        double[] result1 = new double[prices.length];
        double[] result2 = new double[prices.length];
        double[] result3 = new double[prices.length];

        double[] temp = new double[60];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;

        retCode = core.bbands(0, prices.length - 1, prices, optInTimePeriod, optInNbDevUp, optInNbDevDn, optInMAType,
                begin, length, tempoutput1, tempoutput2, tempoutput3);
        // public static RetCode Bbands(int startIdx, int endIdx, float[]
        // inReal, int optInTimePeriod, double optInNbDevUp, double
        // optInNbDevDn, MAType optInMAType, out int outBegIdx, out int
        // outNBElement, double[] outRealUpperBand, double[] outRealMiddleBand,
        // double[] outRealLowerBand);

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

    // ��Ʒͨ��ָ��
    public double[] getCci(double[] highPrices, double[] lowPrices, double[] closePrices, int inTimePeriod) {
        /*
         * (1) TP=����߼�+��ͼ�+���̼ۣ���3 �� (2) SMA TP=SMA (TP,
         * N)ע�⣺NΪ�������ڡ� ��3��MD��Mean Deviation��Ҳ����ƽ����ֵ
         * MD=�Ʀ�TP-SMA TP���Nע�⣺�ƴ��Ϊ�ܺ� ��4�������CCI��ֵ
         * ��CCI=��TP-SMA TP����(0.015��MD)
         */
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

    // EMA=Exponential Moving Average��ָ��ƽ����ָ��
    public double[] getEma(double[] prices, int optInTimePeriod) {
        /*
         * EXPMA��(���ջ������̼ۣ���һ�ջ�����EXPMA)/��+��һ�ջ�����EXPMA
         */
        double[] tempOutPut = new double[prices.length];
        double[] output = new double[prices.length];

        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;

        retCode = core.ema(0, prices.length - 1, prices, optInTimePeriod, begin, length, tempOutPut);
        // Ema(int startIdx, int endIdx, double[] inReal, int optInTimePeriod,
        // out int outBegIdx, out int outNBElement, double[] outReal);
        for (int i = 0; i < optInTimePeriod - 1; i++) {
            output[i] = 0;
        }
        for (int i = optInTimePeriod - 1; 0 < i && i < (prices.length); i++) {
            output[i] = tempOutPut[i - optInTimePeriod + 1];
        }
        return output;
    }

    // MACDEXT=MACD with controllable MA type���ɿؾ���ƽ����ͬƽ��ָ��
    public double[][] getMacdExt(double[] prices, int optInFastPeriod, int optInSlowPeriod, int optInSignalPeriod) {
        /*
         * DIFF�ߡ���Difference�����̼۶��ڡ�����ָ��ƽ���ƶ�ƽ���߼�Ĳ�
         * DEA�ߡ���Difference Exponential
         * Average��DIFF�ߵ�M��ָ��ƽ���ƶ�ƽ����
         * MACD�ߡ�DIFF����DEA�ߵĲ��ɫ��״��
         * ����SHORT(����)��LONG(����)��M����һ��Ϊ12��26��9��ʽ������ʾ��
         * ��Ȩƽ��ָ��ģɣ�=���������ָ��+��������ָ��+2���ĵ������ָ��
         * ʮ����ƽ��ϵ��̣�����
         * =2/��12+1��=0.1538��ʮ����ƽ��ϵ��̣�����=2/��26+1��=0.0741
         * ʮ����ָ��ƽ��ֵ�������գţͣ���=L12����������ָ�� +
         * 11/��12+1�������յ�12��EMA
         * ��ʮ����ָ��ƽ��ֵ�������գţͣ���=L26����������ָ�� +
         * 25/��26+1�������յ�26��EMA EMA��Exponential Moving
         * Average����ָ��ƽ����ָ�ꡣҲ��EXPMAָ�꣬��Ҳ��һ��������ָ��
         * ��ָ��ƽ����ָ������ָ��ʽ�ݼ���Ȩ���ƶ�ƽ��
         * ����ֵ�ļ�Ȩ����ʱ���ָ��ʽ�ݼ�
         * ��Խ���ڵ���ݼ�ȨԽ�أ����Ͼɵ����Ҳ����һ���ļ�Ȩ��
         * �����ʣ��ģɣƣ�=12��EMA
         * -26��EMA����DIFƽ��ֵ��DEA��=���9�յ�DIF֮��/9
         * �ͣ��ã�=�����յ�DIFF-���յ�DEA����2
         */
        double[] tempoutput1 = new double[prices.length];
        double[] tempoutput2 = new double[prices.length];
        double[] tempoutput3 = new double[prices.length];
        double[][] output = { new double[prices.length], new double[prices.length], new double[prices.length] };

        double[] result1 = new double[prices.length];
        double[] result2 = new double[prices.length];
        double[] result3 = new double[prices.length];

        double[] temp = new double[60];
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
        // MacdExt(int startIdx, int endIdx, double[] inReal, int
        // optInFastPeriod, MAType optInFastMAType, int optInSlowPeriod, MAType
        // optInSlowMAType, int optInSignalPeriod, MAType optInSignalMAType, out
        // int outBegIdx, out int outNBElement, double[] outMACD, double[]
        // outMACDSignal, double[] outMACDHist);

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

    // MFI=Money Flow Index���ʽ�����ָ��
    public double[] getMfi(double[] highPrices, double[] lowPrices, double[] closePrices, double[] inVolume,
            int optInTimePeriod) {
        /*
         * 1.���ͼ۸�TP��=N�������̼�����߼ۡ���ͼ������һ�����̼۵�����ƽ��ֵ
         * 2.����������MF�������ͼ۸�TP����N���ڳɽ����
         * 3.�����MF������MF���򽫵��յ�MFֵ��Ϊ�����������PMF��
         * 4.�����MF������MF���򽫵��յ�MFֵ��Ϊ������������NMF��
         * 5.MFI��100-��100/(1��PMF/NMF)��
         */
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

    // ������ָ��
    public double[] getObv(double[] prices, double[] volume) {
        /*
         * ����OBV=����OBV+sgn������ĳɽ�������sgn�Ƿ�ŵ���˼��sgn������+1��
         * Ҳ������-1� �������ʽ������ Sgn=+1 �����̼ۡ������̼�Sgn=�D1
         * �����̼�<�����̼۳ɽ���ָ���ǳɽ���Ʊ�������ǳɽ���
         */
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

    // �仯��
    public double[] getRoc(double[] prices, int optInTimePeriod) {
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

    // �ٷ���ı����
    public double[] getRocP(double[] prices, int optInTimePeriod) {
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

    // ���ָ��ָ��
    public double[][] getStochF(double[] highPrices, double[] lowPrices, double[] closePrices, int optInFastK_Period,
            int optInFastD_Period) {
        /*
         * optInFastK_Period:��K���ڣ�optInSlowK_Period����K���ڣ�
         * optInSlowD_Period ����D���� STOCH=Stochastic�����ָ��,KDJ��K
         * %K = 100* (LOSE-LOW(%K))/(HIGH(%K)-LOW(%K)) CLOSE ��
         * ��������̼۸�LOW(%K) �� %K�����ֵ��HIGH(%K) �� %K�����ֵ
         * %D���ƶ�ƽ���ߣ�%D = SMA(%K�� N)
         */

        double[][] output = { new double[lowPrices.length], new double[lowPrices.length] };
        double[] tempOutPut1 = new double[lowPrices.length];
        double[] tempOutPut2 = new double[lowPrices.length];

        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;

        MAType optInFastD_MAType = MAType.Sma;

        retCode = core.stochF(0, lowPrices.length - 1, highPrices, lowPrices, closePrices, optInFastK_Period,
                optInFastD_Period, optInFastD_MAType, begin, length, tempOutPut1, tempOutPut2);
        // StochF(int startIdx, int endIdx, double[] inHigh, double[] inLow,
        // double[] inClose, int optInFastK_Period, int optInFastD_Period,
        // MAType optInFastD_MAType, out int outBegIdx, out int outNBElement,
        // double[] outFastK, double[] outFastD);

        for (int i = 0; i < lowPrices.length - length.value; i++) {
            output[0][i] = 0;
            output[1][i] = 0;
        }
        for (int i = lowPrices.length - length.value; 0 < i && i < (lowPrices.length); i++) {
            output[0][i] = tempOutPut1[i - (lowPrices.length - length.value)];
            output[1][i] = tempOutPut2[i - (lowPrices.length - length.value)];
        }

        return output;

    }

    // �����ָ��
    public double[][] getStoch(double[] highPrices, double[] lowPrices, double[] closePrices, int optInFastK_Period,
            int optInSlowK_Period, int optInSlowD_Period) {
        /*
         * optInFastK_Period:��K���ڣ�optInSlowK_Period����K���ڣ�
         * optInSlowD_Period ����D���� STOCH=Stochastic�����ָ��,KDJ��K
         * %K = 100* (LOSE-LOW(%K))/(HIGH(%K)-LOW(%K)) CLOSE ��
         * ��������̼۸�LOW(%K) �� %K�����ֵ��HIGH(%K) �� %K�����ֵ
         * MaxHigh(N) - N ������ǰ����ߵ㣻 MinLow(N) - N ������ǰ����͵㣻 MA
         * - �ƶ�ƽ���ߣ�N - ��/�ͷ�Χ�ļ��㳤�ȣ� P - %D(i)���˲����ڡ�
         */

        double[][] output = { new double[lowPrices.length], new double[lowPrices.length] };
        double[] tempOutPut1 = new double[lowPrices.length];
        double[] tempOutPut2 = new double[lowPrices.length];

        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;

        MAType optInSlowK_MAType = MAType.Sma;
        MAType optInSlowD_MAType = MAType.Sma;

        retCode = core.stoch(0, lowPrices.length - 1, highPrices, lowPrices, closePrices, optInFastK_Period,
                optInSlowK_Period, optInSlowK_MAType, optInSlowD_Period, optInSlowD_MAType, begin, length, tempOutPut1,
                tempOutPut2);
        // Stoch(int startIdx, int endIdx, double[] inHigh, double[] inLow,
        // double[] inClose, int optInFastK_Period, int optInSlowK_Period,
        // MAType optInSlowK_MAType, int optInSlowD_Period, MAType
        // optInSlowD_MAType, out int outBegIdx, out int outNBElement, double[]
        // outSlowK, double[] outSlowD);

        for (int i = 0; i < lowPrices.length - length.value; i++) {
            output[0][i] = 0;
            output[1][i] = 0;
        }
        for (int i = lowPrices.length - length.value; 0 < i && i < (lowPrices.length); i++) {
            output[0][i] = tempOutPut1[i - (lowPrices.length - length.value)];
            output[1][i] = tempOutPut2[i - (lowPrices.length - length.value)];
        }

        return output;

    }

    // ����ָ��ƽ���ƶ�ƽ��ָ��
    public double[] getTrix(double[] prices, int period) {
        /*
         * (1)����N������̼۵�ָ��ƽ��AX AX = (I��) ���̼� * 2 /(N+1) +
         * (I-1)�� AX (N-1) *(N+1) (2)����N���AX��ָ��ƽ��BX BX = (I��)
         * AX * 2 /(N+1) + (I-1)�� BX (N-1) *(N+1)
         * ��3)����N���BX��ָ��ƽ��TRIX TRIX = (I��) BX * 2 /(N+1) +
         * (I-1)�� TRIX (N-1) *(N+1) (4)����TRIX��m���ƶ�ƽ��TMA TMA =
         * ((I-M)��TRIX�ۼ�) /M��
         */
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

    // ��Ʒͨ��ָ��
    public double[] getWillR(double[] highPrices, double[] lowPrices, double[] closePrices, int inTimePeriod) {
        /*
         * n��WMS����Hn��Ct��/(Hn��Ln)��100��CtΪ��������̼ۣ�Hn��Ln�����n
         * ���ڣ������ ��죩���ֵ���߼ۺ���ͼ�
         */
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

    // AD=Chaikin A/D Line���ۻ��ɷ���ָ��
    public double[] getAd(double[] highPrices, double[] lowPrices, double[] closePrices, double[] inVolume,
            int optInTimePeriod) {
        /*
         * AD=Chaikin A/D Line���ۻ��ɷ���ָ�� CLV���㹫ʽ��
         * {[(C-L)-(H-C)]}/(H-L)) = CLV C�����̼ۡ�L�����յ͵�H�������ոߵ㡣
         * CLV�ı䶯��Χ��-1��1֮�䣬���ĵ�Ϊ0��CLV�����
         * �Ӧ��ʱ���ڵĳɽ�����ˣ��ۻ���ܺ;�������ۻ�/�ɷ���
         */
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

    // ����ָ��
    public double[] getAdosc(double[] highPrices, double[] lowPrices, double[] closePrices, double[] inVolume,
            int optInFastPeriod, int optInSlowPeriod) {
        /*
         * (1)ADOSC=Chaikin A/D Oscillator������ָ�� (2)
         * CHAIKIN��A/D�ģ�n��expma - A/D�ģ�m��expma��
         */
        double[] output = new double[lowPrices.length];
        // double[] tempOutPut = new double[lowPrices.length];

        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;

        retCode = core.adOsc(0, lowPrices.length - 1, highPrices, lowPrices, closePrices, inVolume, optInFastPeriod,
                optInSlowPeriod, begin, length, output);
        // AdOsc(int startIdx, int endIdx, double[] inHigh, double[] inLow,
        // double[] inClose, double[] inVolume, int optInFastPeriod, int
        // optInSlowPeriod, out int outBegIdx, out int outNBElement, double[]
        // outReal)

        return output;
    }

    // �𵴾�Լ�
    public double[] getApo(double[] prices, int optInFastPeriod, int optInSlowPeriod) {
        /*
         * APO=Absolute Price Oscillator���𵴾�Լ�
         */

        double[] output = new double[prices.length];
        double[] tempOutPut = new double[prices.length];

        MAType optInMAType = null;
        // MAType optInSlowD_MAType = new MAType();
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        RetCode retCode = RetCode.InternalError;
        begin.value = -1;
        length.value = -1;

        retCode = core.apo(0, prices.length - 1, prices, optInFastPeriod, optInSlowPeriod, optInMAType, begin, length,
                tempOutPut);
        // Apo(int startIdx, int endIdx, double[] inReal, int optInFastPeriod,
        // int optInSlowPeriod, MAType optInMAType, out int outBegIdx, out int
        // outNBElement, double[] outReal);

        for (int i = 0; i < prices.length - length.value; i++) {
            output[i] = 0;

        }
        for (int i = prices.length - length.value; 0 < i && i < (prices.length); i++) {
            output[i] = tempOutPut[i - (prices.length - length.value)];
        }

        return output;

    }

    // ��¡ָ��
    public double[][] getAroon(double[] inHigh, double[] inLow, int optInTimePeriod) {
        /*
         * Aroon����¡ָ��
         * Aroon(����)=[(����������-��߼ۺ������)/����������]*100
         * Aroon(�½�)=[(����������-��ͼۺ������)/����������]*100
         */

        double[][] output = { new double[inHigh.length], new double[inHigh.length] };
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

    // ��¡��
    public double[] getAroonOsc(double[] inHigh, double[] inLow, int optInTimePeriod) {
        /*
         * AROONOSC=Aroon Oscillator����¡��ָ�� ͨ����㰢¡�����(Aroon up
         * and down)֮��ֵ����
         */

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

    // ����ƽ���
    public double[] getBop(double[] openPrices, double[] highPrices, double[] lowPrices, double[] closePrices) {
        /*
         * ����ƽ���
         */
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

    // Ǯ�¶����ڶ�ָ��
    public double[] getCmo(double[] closePrices, int period) {
        /*
         * CMO=Chande Momentum Oscillator��Ǯ�¶����ڶ�ָ�ꡣ CMO =��Su-Sd)
         * * 100 / (Su +Sd)
         * u�ǽ������̼����������̼ۣ������գ���ֵ���ܡ������
         * ��µ�������ֵΪ0��Sd� ǽ������̼����������̼�
         * ���µ��գ���ֵ�ľ��ֵ���ܡ� ���������ǣ�������ֵΪ0
         */
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

    // ����������Ӧ������
    public double[] getKama(double[] prices, int optInTimePeriod) {
        /*
         * KAMA=Kaufman Adaptive Moving Average������������Ӧ������
         */
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

    // �ݹ��ƶ�ƽ��ָ��
    public double[] getTrima(double[] prices, int optInTimePeriod) {
        /*
         * TRIMA=Triangular Moving Average���ݹ��ƶ�ƽ��ָ�� SMA = (P1 +
         * P2 + P3 + P4 + ... + Pn) / n TMA = (SMA1 + SMA2 + SMA3 +
         * SMA4 + ... SMAn) / n PnΪ��n��۸�
         */
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

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
