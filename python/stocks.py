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
    macd, macdsignal, macdhist = talib.MACD(closes, fastperiod=12, slowperiod=55, signalperiod=9)
    for i in range(len(macd)):
        print "close:%0.2f\tdif:%0.2f\tdea:%0.2f\tmacd:%0.2f" \
              % (utils.f(closes[i]), utils.f(macd[i]), utils.f(macdsignal[i]), utils.f((macd[i] - macdsignal[i]) * 2))


def list_stock(index):
    stocks = ts.get_stock_basics()[index]
    return stocks


if __name__ == "__main__":
    # stocks = list_stock((stocks.pe < 100) & (stocks.pe > 0) & (stocks.totalAssets < 300000))
    print utils.is_working_day(datetime.datetime.now())
    # count = 0
    # for stock in stocks.index:
    # val = ts.get_realtime_quotes(stock)
    #     if float(val.price) < 15 and float(val.price) > 0:
    #         print stock, float(val.price)
    #         count += 1;
    # print len(stocks),count

