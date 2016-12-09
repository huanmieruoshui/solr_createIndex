package com.hbl.solr.bean;

public class Product {

	private String id;
	
	private String articleTitle;
	private String articleTags;
	private String articleContent;
	private String articlePermalink;
	private String articleCreateDate;
	private String articleUpdateDate;	

	@Override
	public String toString() {
		return "Product [id=" + id + ", articleTitle=" + articleTitle + ", articleTags=" + articleTags
				+ ", articleContent=" + articleContent + ", articlePermalink=" + articlePermalink
				+ ", articleCreateDate=" + articleCreateDate + ", articleUpdateDate=" + articleUpdateDate + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getArticleTitle() {
		return articleTitle;
	}

	public void setArticleTitle(String articleTitle) {
		this.articleTitle = articleTitle;
	}

	public String getArticleTags() {
		return articleTags;
	}

	public void setArticleTags(String articleTags) {
		this.articleTags = articleTags;
	}

	public String getArticleContent() {
		return articleContent;
	}

	public void setArticleContent(String articleContent) {
		this.articleContent = articleContent;
	}

	public String getArticlePermalink() {
		return articlePermalink;
	}

	public void setArticlePermalink(String articlePermalink) {
		this.articlePermalink = articlePermalink;
	}

	public String getArticleCreateDate() {
		return articleCreateDate;
	}

	public void setArticleCreateDate(String articleCreateDate) {
		this.articleCreateDate = articleCreateDate;
	}

	public String getArticleUpdateDate() {
		return articleUpdateDate;
	}

	public void setArticleUpdateDate(String articleUpdateDate) {
		this.articleUpdateDate = articleUpdateDate;
	}

}
