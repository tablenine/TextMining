package edu.yonsei.test.data;

public class NaverNewsArticle {

	private String id, link, title, content, timeFirst, timeLast;
	
	public NaverNewsArticle(String id, String link, String title, String content, String timeFirst, String timeLast) {
		this.id = id; this.link = link; this.title = title; this.content = content; this.timeFirst = timeFirst; this.timeLast = timeLast;
	}

	public void clear() {
		id = link = title = content = timeFirst = timeLast = null;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getLink() {
		return link;
	}
	
	public void setLink(String link) {
		this.link = link;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getTimeFirst() {
		return timeFirst;
	}
	
	public void setTimeFirst(String timeFirst) {
		this.timeFirst = timeFirst;
	}
	
	public String getTimeLast() {
		return timeLast;
	}
	
	public void setTimeLast(String timeLast) {
		this.timeLast = timeLast;
	}
}
