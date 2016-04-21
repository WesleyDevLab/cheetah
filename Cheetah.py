#!/usr/bin/python
# coding:utf-8

import stocks
import tushare as ts

stock_list = ts.get_hist_data('000521', start='2015-04-01', end='2016-04-18')
stocks.MACD(stock_list, slow_period=55)

for row in stock_list.iterrows():
    print "row:",row