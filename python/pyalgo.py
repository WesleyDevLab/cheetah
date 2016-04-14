#!/usr/bin/python
# coding:utf-8

from pyalgotrade import strategy
from pyalgotrade.barfeed import yahoofeed
from pyalgotrade.technical import cross, ma, macd, rsi


class MyStrategy(strategy.BacktestingStrategy):
    def __init__(self, feed, instrument):
        strategy.BacktestingStrategy.__init__(self, feed)
        # We want a 15 period SMA over the closing prices.
        self.__sma = ma.SMA(feed[instrument].getCloseDataSeries(), 15)
        self.__rsi = rsi.RSI(feed[instrument].getCloseDataSeries(), 14)
        self.__macd = macd.MACD(feed[instrument].getCloseDataSeries(), 12, 55, 9).getSignal()
        self.__instrument = instrument

    def onBars(self, bars):
        bar = bars[self.__instrument]
        # self.info("%s\t%s\t%s\t%s" % (bar.getClose(), self.__sma[-1], self.__rsi[-1], self.__macd[-1]))

# Load the yahoo feed from the CSV file
feed = yahoofeed.Feed()
feed.addBarsFromCSV("orcl", "orcl-2015.csv")
print cross.cross_above(feed['orcl'].getCloseDataSeries(),feed['orcl'].getHighDataSeries())

# Evaluate the strategy with the feed's bars.
myStrategy = MyStrategy(feed, "orcl")
myStrategy.run()