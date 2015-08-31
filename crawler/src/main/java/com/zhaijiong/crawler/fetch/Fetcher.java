package com.zhaijiong.crawler.fetch;

import com.zhaijiong.crawler.Page;

public interface Fetcher {

    Page fetch(String url);

}
