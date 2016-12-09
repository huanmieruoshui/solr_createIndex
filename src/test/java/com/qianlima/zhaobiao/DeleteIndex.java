package com.qianlima.zhaobiao;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

/**
* @className:DeleteIndex.java
* @classDescription:
* @author:hbl
* @createTime:2016年7月21日
*/
public class DeleteIndex {

	public static void main(String[] args) throws SolrServerException, IOException {
		HttpSolrClient server = new HttpSolrClient("http://115.29.109.120:8983/solr/blog/");
		server.deleteByQuery("*:*");
		server.commit();
		server.close();
		System.out.println("done!");
	}
}
