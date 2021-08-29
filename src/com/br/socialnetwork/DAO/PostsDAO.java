package com.br.socialnetwork.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.br.socialnetwork.classes.Posts;

public class PostsDAO {
	private Connection connection;
	
	public PostsDAO(Connection connection) {
		this.connection = connection;
	}
	
	public void save(Posts posts) throws SQLException {
		String sql = "INSERT INTO Posts (name, description) VALUES (?, ?)";
		
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			preparedStatement.setString(1, posts.getName());
			preparedStatement.setString(2, posts.getDescription());
			
			preparedStatement.execute();
			
			try (ResultSet resultSet = preparedStatement.getGeneratedKeys()){
				while(resultSet.next()) {
					posts.setId(resultSet.getInt(1));
				}
			}
		}
	}
	
	public List<Posts> list() throws SQLException {
		List<Posts> posts = new ArrayList<Posts>();
		String sql = "SELECT * FROM Posts";
		
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.execute();
			
			try (ResultSet resultSet = preparedStatement.getResultSet()){
				while(resultSet.next()) {
					Posts post = new Posts(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3));
					
					posts.add(post);
				}
			}
		}
		return posts;
	}
}
