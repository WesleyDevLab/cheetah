# Provider 基础数据获取器

提供基础数据获取接口，数据类型包括：

* 股票列表：
    * 融资融券股票列表
    * 交易中股票列表（非退市，停牌的股票）
    * 按条件获取股票列表（eg：市盈率小于200，流通市值小于100亿，股价小于20元的股票列表）
* 版块列表（概念，地区，行业）
* 个股分类（概念，地区，行业）
* 日线数据（个股数据可前复权）
* 实时股票数据
* 股票逐笔数据
* 分钟k线数据（5，10，30，60分钟）
* 基本计算:
    * MACD
    * MA（估价，成交量等）
    * Boll
    * 均线粘合
    * 逐笔计算各分钟级别k线数据
* 个股财报数据
* 分配预案数据
* 季度股东人数
* 盈利预期数据
* 龙虎榜数据
* 融资融券数据（大盘和个股）
* 资金流向数据（大盘和个股）


    

# Strategy 策略类

策略类有三类接口：

* BuyStrategy
* SellStrategy
* RiskStrategy

# StockPool 股票池

# Recommender 推荐器

* SimpleRecommender 通过spring注入strategy策略实现单策略股票推荐
* CombinedRecommender 注入List<Recommender>策略列表实现一组策略的股票推荐

# RcommendSystem 推荐系统
