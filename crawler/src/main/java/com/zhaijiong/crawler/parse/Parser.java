package com.zhaijiong.crawler.parse;

import com.zhaijiong.crawler.Page;

import java.util.Map;

/**
 * 将获取到的html页面根据配置要求解析出来
 */
public interface Parser {

    Map parse(Page page);

}
