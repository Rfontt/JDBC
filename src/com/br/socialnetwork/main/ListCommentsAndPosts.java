package com.br.socialnetwork.main;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.br.socialnetwork.DAO.CommentsDAO;
import com.br.socialnetwork.classes.Comments;
import com.br.socialnetwork.classes.Posts;
import com.br.socialnetwork.connectionfactory.ConnectionFactory;

public class ListCommentsAndPosts {

	public static void main(String[] args) throws SQLException {
		try (Connection connection = new ConnectionFactory().retriveConnection()) {
			CommentsDAO commentsDAO = new CommentsDAO(connection);
			List<Comments> list = commentsDAO.list();
			
			list.stream().forEach(comment -> {
				 System.out.println(comment.getComment() + " " + comment.getPost_id());
				 
				 for(Posts posts : comment.getPosts()) {
					System.out.println(posts.getName() + " " + posts.getDescription());
				 }
			});
		}

	}

}
