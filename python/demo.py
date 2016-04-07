#!/usr/bin/python
# coding:utf-8

import tushare as ts
import pandas as pd
import numpy as np
import talib
import matplotlib.pyplot as mp
import datetime
import numpy,array
import scipy

# print ts.get_h_data('000521')

stocks = ts.get_hist_data('000521', start='2016-01-01', end='2016-04-09')

closes = stocks.sort_index(axis=0,ascending=True).close.values

print closes

macd, macdsignal, macdhist = talib.MACD(closes, fastperiod=12, slowperiod=26, signalperiod=9)

print macd[-1]
print macdsignal[-1]
print (macd[-1] - macdsignal[-1])*2
