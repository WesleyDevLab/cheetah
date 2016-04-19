#!/usr/bin/python
# coding:utf-8

import time
import stocks
import tushare as ts
import utils


while True:
    if utils.is_working_hour():
        columns = ['name', 'close', 'change']
        df = ts.get_index()
        print df[columns][df.code == '000001']
        print df[columns][df.code == '399005']

        columns = ['time', 'name', 'price', 'b1_v', 'b1_p','a1_v','a1_p']
        print stocks.realtime('000521')[columns]
        print stocks.realtime('002067')[columns]
        print stocks.realtime('000430')[columns]
        print '-------'
    time.sleep(30)
