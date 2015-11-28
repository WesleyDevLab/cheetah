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
