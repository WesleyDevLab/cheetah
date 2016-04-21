#!/usr/bin/python
# coding:utf-8

import time
import datetime
import stocks
import tushare as ts
import utils

# target_list = []
target_list = ['000521', '002067', '000430']


while True:
    if utils.is_working_hour():
        columns = ['name', 'close', 'change']
        df = ts.get_index()
        print df[columns][df.code == '000001']
        print df[columns][df.code == '399005']

        columns = ['time', 'name', 'price', 'b1_v', 'b1_p','a1_v','a1_p']
        for stock in target_list:
            print stocks.realtime(stock)[columns]
        print '-------'
    time.sleep(30)

# def print_index(index):
#     print index
#
#
# starttime = datetime.datetime.now()
#
# stock_list = stocks.list_stock()
# for stock in stock_list.index:
#     stocks.filter(stock)
#
# endtime = datetime.datetime.now()
# print "cost:", (endtime - starttime).seconds
