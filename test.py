#!/usr/bin/python
# coding:utf-8

import matplotlib.pyplot as plt
import tushare as ts

stock = ts.get_hist_data('000521', start='2015-04-01', end='2016-04-12')
stock.sort_index(ascending=True).close.plot()
stock.sort_index(ascending=True).ma5.plot()
plt.show()