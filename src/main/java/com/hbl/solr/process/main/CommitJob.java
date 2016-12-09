package com.hbl.solr.process.main;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.hbl.solr.config.Config;
import com.hbl.solr.process.thread.CommonProcess;
import com.hbl.solr.util.LogUtils;
import com.hbl.solr.util.MysqlJdbcUtil;

/**
* @className:CommitJob.java
* @classDescription:
* @author:hbl
* @createTime:2016年12月8日
*/
public class CommitJob implements Job {

	public static final Logger log = Logger.getLogger(CommitJob.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("begin ............");
		long current = System.currentTimeMillis();
		try {
			String processUrl = Config.getString("solr_blog");
			CommonProcess process = new CommonProcess(new MysqlJdbcUtil(Config.getString("jdbc_url"),Config.getString("jdbc_username"),
					Config.getString("jdbc_password")), processUrl);
			process.processIndex();
		} catch (Exception e) {
			e.printStackTrace();
			LogUtils.write(e.getMessage(), Config.getString("long_err"));
		}
		long end = System.currentTimeMillis();
		log.info("index over.spends times "+(end-current));
	}

}
