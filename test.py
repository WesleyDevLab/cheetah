#!/usr/bin/python
# coding:utf-8

import matplotlib.pyplot as plt
import tushare as ts
import stocks

# stock = ts.get_hist_data('000521', start='2015-04-01', end='2016-04-12')
# stock.sort_index(ascending=True).close.plot()
# stock.sort_index(ascending=True).ma5.plot()
# plt.show()

# dict={"name":"python","english":33,"math":35}
#
# print "##for in "
# for i in dict:
#         print "dict[%s]=" % i,dict[i]
#
# print "##items"
# for (k,v) in  dict.items():
#         print "dict[%s]=" % k,v
#
# print "##iteritems"
# for k,v in dict.iteritems():
#         print "dict[%s]=" % k,v

df = stocks.tick_today('000521')
desc = df.describe()
mid = desc.volume['std'] * 3
big = desc.volume['std'] * 6
print "---"
print df[(df.volume > mid) & (df.volume < big)]
print "---"
print df[df.volume > big]