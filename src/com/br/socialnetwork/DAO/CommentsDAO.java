package com.br.socialnetwork.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.br.socialnetwork.classes.Comments;
import com.br.socialnetwork.classes.Posts;

public class CommentsDAO {
	private Connection connection;
	
	public CommentsDAO(Connection connection) {
		this.connection = connection;
	}
	
	public void save(Comments comments) throws SQLException {
		String sql = "INSERT INTO Comments (comment, post_id) VALUES (?, ?)";
		
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			preparedStatement.setString(1, comments.getComment());
			preparedStatement.setInt(2, comments.getPost_id());
			
			preparedStatement.execute();
			
			try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
				while(resultSet.next()) {
					comments.setId(resultSet.getInt(1));
				}
			}
		}
	}
	
	public List<Comments> list() throws SQLException {
		List<Comments> commentsWithPosts = new ArrayList<Comments>();
		Comments last = null;
		String sql = "SELECT c.comment, c.post_id, p.id, p.name, p.description FROM Comments c INNER JOIN"+" Posts p ON c.post_id = p.id"; 
		
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
			preparedStatement.execute();
			
			try (ResultSet resultSet = preparedStatement.getResultSet()){
				while(resultSet.next()) {
					if(last == null || !last.getComment().equals(resultSet.getString(2))) {
						Comments comments = new Comments(resultSet.getString(1), resultSet.getInt(2));
						last = comments;
						commentsWithPosts.add(comments);
					}
					Posts posts = new Posts(resultSet.getInt(3), resultSet.getString(4), resultSet.getString(5));
					last.add(posts);
				}
			}
		}
		return commentsWithPosts;
	}
}
