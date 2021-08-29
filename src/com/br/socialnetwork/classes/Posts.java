package com.br.socialnetwork.classes;

public class Posts {
	private Integer id;
	private String name;
	private String description;
	
	public Posts(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	public Posts(Integer id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String toString() {
		return String.format("Posts it's: %d, %s, %s", 
				this.id, this.name, this.description);
	}
}
