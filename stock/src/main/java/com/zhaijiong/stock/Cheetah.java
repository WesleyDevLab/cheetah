package com.zhaijiong.stock;

import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.Context;
import com.zhaijiong.stock.common.Utils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-5.
 */
public class Cheetah {
    private static final Logger LOG = LoggerFactory.getLogger(Cheetah.class);

    public static void main(String[] args) throws IOException, SchedulerException {
        Context context = new Context();

        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();

        Map<String, Object> jobs = context.getMap(Constants.SCHEDULER_JOBS);
        LOG.info("jobCount=" + jobs.size());
        for(Map.Entry<String,Object> object: jobs.entrySet()){
            LOG.info("adding job:" + object.getKey());
            Map<String,String> pairs = (Map<String, String>) object.getValue();

            JobDetail jobDetail = JobBuilder.newJob(Utils.getClass(pairs.get(Constants.SCHEDULER_JOB_CLASSNAME)))
                    .withIdentity(object.getKey(),"dailyDataBuild")
                    .build();

            for(Map.Entry<String,String> pair:pairs.entrySet()){
                jobDetail.getJobDataMap().put(pair.getKey(), pair.getValue());
            }
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(object.getKey() + "_trigger","dailyDataBuild")
                    .startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(pairs.get(Constants.SCHEDULER_JOB_CRON)))
                    .forJob(object.getKey(),"dailyDataBuild")
                    .build();
            scheduler.scheduleJob(jobDetail,trigger);
        }
    }
}
