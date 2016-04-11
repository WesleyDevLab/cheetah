#!/usr/bin/python
# coding:utf-8

import tushare as ts
import pandas as pd
import numpy as np
import talib
import matplotlib.pyplot as mp
import datetime
import numpy, array
import scipy
import utils

# print ts.get_h_data('000521')

df = pd.DataFrame


def MACD(symbol):
    stocks = ts.get_hist_data(symbol, start='2015-01-01', end='2016-04-09')
    closes = stocks.sort_index().close.values
    # macd, macdsignal, macdhist = talib.MACD(closes, fastperiod=12, slowperiod=55, signalperiod=9)
    macd = talib.MACD(closes, fastperiod=12, slowperiod=55, signalperiod=9)
    # for i in range(len(macd)):
    # print "close:%0.2f\tdif:%0.2f\tdea:%0.2f\tmacd:%0.2f" \
    #           % (utils.f(closes[i]), utils.f(macd[i]), utils.f(macdsignal[i]), utils.f((macd[i] - macdsignal[i]) * 2))
    return macd


def list_stock():
    # stocks = ts.get_stock_basics()[index]
    stocks = ts.get_stock_basics()
    stocks = stocks[(stocks.pe < 100) & (stocks.pe > 0) & (stocks.totalAssets < 300000)]
    return stocks


def get_basic(index, day=30):
    start_date = utils.get_start_date(day).strftime("%Y-%m-%d")
    stop_date = datetime.date.today().strftime("%Y-%m-%d")
    stock_data = ts.get_hist_data(index, start=start_date, end=stop_date)

    is_high = False
    if stock_data.max().p_change > 9.5:
        is_high = True
    else:
        return

    mean = stock_data.mean()

    if 5 > mean.p_change > 1 and mean.turnover < 10 and mean.close * 1.2 >= stock_data.ix[0].close:
        print "%s,p_change=%f,turnover=%f,close=%f,1.2close=%0.2f %0.2f,isHigh=%s" % \
              (index, mean.p_change, mean.turnover, mean.close, mean.close * 1.2, stock_data.ix[0].close, is_high)
        # print stock_data.describe()


def condition():
    stocks = list_stock()
    stock_list = stocks.index
    for stock in stock_list:
        get_basic(stock)


if __name__ == "__main__":
    # stocks = list_stock((stocks.pe < 100) & (stocks.pe > 0) & (stocks.totalAssets < 300000))
    # print utils.is_working_day(datetime.datetime.now())
    # count = 0
    # for stock in stocks.index:
    # val = ts.get_realtime_quotes(stock)
    # if float(val.price) < 15 and float(val.price) > 0:
    #         print stock, float(val.price)
    #         count += 1;
    # print len(stocks),count

    # macd = MACD('300145')
    # print macd
    # get_basic('300415')
    condition()
