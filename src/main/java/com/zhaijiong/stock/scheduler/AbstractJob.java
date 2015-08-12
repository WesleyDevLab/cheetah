package com.zhaijiong.stock.scheduler;

import org.quartz.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-12.
 */
public abstract class AbstractJob implements Job {
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractJob.class);
}
