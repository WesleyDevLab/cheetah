#!/usr/bin/python
# coding:utf-8
import tushare as ts
import db
from common.Constants import *
from common import downloader
from bs4 import BeautifulSoup

try:
    import cPickle as pickle
except ImportError:
    import pickle

import sys
reload(sys)
sys.setdefaultencoding('utf-8')

concept_url = "http://quote.eastmoney.com/center/BKList.html#notion_0_0?sortRule=0"


class StockBlock(object):
    industry = "industry"  # 行业
    concept = "concept"  # 概念
    area = "area"  # 地域
    sme = "sme"  # 中小板
    gem = "gem"  # 创业板
    st = "st"  # 风险警示板
    hs300s = "hs300s"  # 沪深300成份及权重
    sz50s = "sz50s"  # 上证50成份股
    zz500s = "zz500s"  # 中证500成份股

    def __init__(self):
        pass

    def parse(self, url):
        soup = BeautifulSoup(downloader.download(url), 'lxml').find_all(id='bklist')
        table = soup[0]
        tr = table.find_all('tr')
        for line in tr:
            td = line.find_all('td')
            if td:
                print "id=%s,板块名称=%s,涨跌幅=%s,总市值(亿)=%s，换手率=%s，上涨家数=%s,下跌家数=%s" % \
                    (td[0].get_text(), td[1].get_text(), td[3].get_text(), td[4].get_text(), td[5].get_text(), td[6].get_text(), td[7].get_text())

    def list(self, stock_block_type):
        stock_block = None
        if stock_block_type == self.industry:
            stock_block = db.get(STOCK_BLOCK_INDUSTRY)
            if stock_block is None:
                stock_block = ts.get_industry_classified()
                db.save(STOCK_BLOCK_INDUSTRY, stock_block)
        elif stock_block_type == self.concept:
            stock_block = db.get(STOCK_BLOCK_CONCEPT)
            if stock_block is None:
                stock_block = ts.get_concept_classified()
                db.save(STOCK_BLOCK_CONCEPT, stock_block)
        elif stock_block_type == self.area:
            stock_block = db.get(STOCK_BLOCK_AREA)
            if stock_block is None:
                stock_block = ts.get_area_classified()
                db.save(STOCK_BLOCK_AREA, stock_block)
        elif stock_block_type == self.sme:
            stock_block = db.get(STOCK_BLOCK_SME)
            if stock_block is None:
                stock_block = ts.get_sme_classified()
                db.save(STOCK_BLOCK_SME, stock_block)
        elif stock_block_type == self.gem:
            stock_block = db.get(STOCK_BLOCK_GEM)
            if stock_block is None:
                stock_block = ts.get_gem_classified()
                db.save(STOCK_BLOCK_GEM, stock_block)
        elif stock_block_type == self.st:
            stock_block = db.get(STOCK_BLOCK_ST)
            if stock_block is None:
                stock_block = ts.get_st_classified()
                db.save(STOCK_BLOCK_ST, stock_block)
        elif stock_block_type == self.hs300s:
            stock_block = db.get(STOCK_BLOCK_HS300S)
            if stock_block is None:
                stock_block = ts.get_hs300s()
                db.save(STOCK_BLOCK_HS300S, stock_block)
        elif stock_block_type == self.sz50s:
            stock_block = db.get(STOCK_BLOCK_SZ50S)
            if stock_block is None:
                stock_block = ts.get_sz50s()
                db.save(STOCK_BLOCK_SZ50S, stock_block)
        elif stock_block_type == self.zz500s:
            stock_block = db.get(STOCK_BLOCK_ZZ500S)
            if stock_block is None:
                stock_block = ts.get_zz500s()
                db.save(STOCK_BLOCK_ZZ500S, stock_block)
        else:
            return None
        return stock_block

    @staticmethod
    def preload():
        stock_block = ts.get_industry_classified()
        db.save(STOCK_BLOCK_INDUSTRY, stock_block)
        stock_block = ts.get_concept_classified()
        db.save(STOCK_BLOCK_CONCEPT, stock_block)
        stock_block = ts.get_area_classified()
        db.save(STOCK_BLOCK_AREA, stock_block)
        stock_block = ts.get_sme_classified()
        db.save(STOCK_BLOCK_SME, stock_block)
        stock_block = ts.get_gem_classified()
        db.save(STOCK_BLOCK_GEM, stock_block)
        stock_block = ts.get_st_classified()
        db.save(STOCK_BLOCK_ST, stock_block)
        stock_block = ts.get_hs300s()
        db.save(STOCK_BLOCK_HS300S, stock_block)
        stock_block = ts.get_sz50s()
        db.save(STOCK_BLOCK_SZ50S, stock_block)
        stock_block = ts.get_zz500s()
        db.save(STOCK_BLOCK_ZZ500S, stock_block)


if __name__ == '__main__':
    # StockBlock.preload()
    sb = StockBlock()
    sblist = sb.list(sb.industry).split('\n')
    for line in sblist:
        print line
