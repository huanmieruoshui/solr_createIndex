package com.hbl.solr.process.main;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

/**
 * 程序入口，创建solr索引
 * 2016年6月2日18:18:45
 * @author Administrator
 *
 */
public class StartUp {

	public static final Logger log = Logger.getLogger(StartUp.class);
	
	public static void main(String[] args) {
		startScheduler();
	}
	
	public static void startScheduler() {
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
		Scheduler scheduler = null;
		try {
			scheduler = schedulerFactory.getScheduler();
			JobDetail job = JobBuilder.newJob(CommitJob.class).withIdentity("createJob", "jobGroup").build();
			Trigger trigger = TriggerBuilder.newTrigger()
					.withIdentity("createTrigger", "triggerGroup")
					.startNow()
					.withSchedule(CronScheduleBuilder.cronSchedule("10 0 0 * * ?"))
					.build();
			scheduler.scheduleJob(job, trigger);
			scheduler.start();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
