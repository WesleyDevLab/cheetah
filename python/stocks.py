#!/usr/bin/python
# coding:utf-8

import tushare as ts
import pandas as pd
import numpy as np
import talib
import matplotlib.pyplot as plt
import datetime
import numpy, array
import scipy
import utils
import db

stock_list = db.load_file()


def MACD(stock_data):
    closes = stock_data.sort_index().close.values
    macd = talib.MACD(closes, fastperiod=12, slowperiod=55, signalperiod=9)
    for i in range(len(macd[0])):
        macd[2][i] = (utils.f(macd[0][i]) - utils.f(macd[1][i])) * 2
    stock['dif'] = macd[0][::-1]
    stock['dea'] = macd[1][::-1]
    stock['macd'] = macd[2][::-1]
    return macd


def golden_cross(metricA, metricB):
    arr = []
    for i in range(len(metricA)):
        if metricA[i] != None and metricB[i] != None:
            if metricA[i] < metricB[i]:
                arr[i] = 0
            else:
                arr[i] = 1


def list_stock():
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
    # print stock, float(val.price)
    # count += 1;
    # print len(stocks),count

    # macd = MACD('300145')
    # print macd
    # get_basic('300415')
    # condition()

    stock = ts.get_hist_data('000521', start='2015-04-01', end='2016-04-13')
    macd = MACD(stock)
    print stock
