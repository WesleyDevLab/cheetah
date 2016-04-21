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


def MACD(stock_data, fast_period=12, slow_period=26, signal_period=9):
    closes = stock_data.sort_index().close.values
    macd = talib.MACD(closes, fast_period, slow_period, signal_period)
    for i in range(len(macd[0])):
        macd[2][i] = (utils.f(macd[0][i]) - utils.f(macd[1][i])) * 2
    stock_data['dif'] = macd[0][::-1]
    stock_data['dea'] = macd[1][::-1]
    stock_data['macd'] = macd[2][::-1]
    return stock_data


def MA(stock_data, timeperiod=30):
    closes = stock_data.sort_index().close.values
    ma = talib.EMA(closes, timeperiod)
    stock_data['ma' + str(timeperiod)] = ma[::-1]
    return stock_data


def golden_cross(metric_first, metric_second):
    arr = []
    for i in range(len(metric_first)):
        if metric_first[i] is not None and metric_second[i] is not None:
            if metric_first[i] < metric_second[i]:
                arr[i] = 0
            else:
                arr[i] = 1


def list_stock():
    stocks = ts.get_stock_basics()
    stocks = stocks[(stocks.pe < 100) & (stocks.pe > 0) & (stocks.totalAssets < 300000)]
    return stocks


def filter(index, day=60):
    start_date = utils.get_start_date(day).strftime("%Y-%m-%d")
    stop_date = datetime.date.today().strftime("%Y-%m-%d")
    stock_data = ts.get_hist_data(index, start=start_date, end=stop_date)

    is_high = False
    # if stock_data.head(20).max().p_change > 9.5:
    #     is_high = True
    # else:
    #     return

    for stock in stock_data.head(3).itertuples():
        if stock.open <= min(stock.ma5, stock.ma10, stock.ma20) \
                and stock.close >= max(stock.ma5, stock.ma10, stock.ma20):
            print index
            print stock_data.head(1)

    # mean = stock_data.mean()

    # if 5 > mean.p_change > 1 and mean.turnover < 10 and mean.close * 1.2 >= stock_data.ix[0].close:
    #     print "%s,p_change=%f,turnover=%f,close=%f,1.2close=%0.2f %0.2f,isHigh=%s" % \
    #           (index, mean.p_change, mean.turnover, mean.close, mean.close * 1.2, stock_data.ix[0].close, is_high)
        # print stock_data.describe()


def realtime(symbol):
    return ts.get_realtime_quotes(symbol)


def tick_today(symbol):
    return ts.get_today_ticks(symbol)


def tick_history(symbol, date):
    df = ts.get_tick_data(symbol, date)
    return df


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

    # stock = ts.get_hist_data('000521', start='2010-04-01', end='2016-04-19')
    # stock = MA(stock, 30)
    # print stock.head(5)
    pass