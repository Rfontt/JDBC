package com.br.socialnetwork.main;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.br.socialnetwork.DAO.PostsDAO;
import com.br.socialnetwork.classes.Posts;
import com.br.socialnetwork.connectionfactory.ConnectionFactory;

public class ListPosts {

	public static void main(String[] args) throws SQLException {
		try (Connection connection = new ConnectionFactory().retriveConnection()) {
			PostsDAO postsDAO = new PostsDAO(connection);
			List<Posts> list = postsDAO.list();
			
			list.stream().forEach(post -> {
				System.out.println(post.getName() + " " + post.getDescription());
			});
		}
		
	}

}
