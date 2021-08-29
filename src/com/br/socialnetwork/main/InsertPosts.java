package com.br.socialnetwork.main;

import java.sql.Connection;
import java.sql.SQLException;

import com.br.socialnetwork.DAO.PostsDAO;
import com.br.socialnetwork.classes.Posts;
import com.br.socialnetwork.connectionfactory.ConnectionFactory;

public class InsertPosts {

	public static void main(String[] args) throws SQLException {
		Posts posts = new Posts("Aliens", "Evidência de vida fora da terra");
		
		try (Connection connection = new ConnectionFactory().retriveConnection()){
			PostsDAO postDAO = new PostsDAO(connection);
			postDAO.save(posts);
		}
		
	}

}
