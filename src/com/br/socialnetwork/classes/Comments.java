package com.br.socialnetwork.classes;

import java.util.ArrayList;
import java.util.List;

public class Comments {
	private Integer id;
	private String comment;
	private Integer post_id;
	private List<Posts> posts = new ArrayList<Posts>();
	
	public Comments(String comment,  Integer post_id) {
		this.comment = comment;
		this.post_id = post_id;
	}
	
	public Comments(Integer id, String comment,  Integer post_id) {
		this.id = id;
		this.comment = comment;
		this.post_id = post_id;
	}
	
	public String getComment() {
		return comment;
	}
	
	public Integer getPost_id() {
		return post_id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public void add(Posts post) {
		posts.add(post);
	}
	
	public List<Posts> getPosts() {
		return posts;
	}
	
	public String toString() {
		return String.format("Comments its: %d, %s, %d", this.id, this.comment, this.post_id);
	}
}
