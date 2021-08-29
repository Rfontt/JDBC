package com.br.socialnetwork.main;

import java.sql.Connection;
import java.sql.SQLException;

import com.br.socialnetwork.DAO.CommentsDAO;
import com.br.socialnetwork.classes.Comments;
import com.br.socialnetwork.connectionfactory.ConnectionFactory;

public class InsertComments {

	public static void main(String[] args) throws SQLException {
		Comments comments = new Comments("Uau, acredito totalmente nisso também!", 1);
		
		try (Connection connection = new ConnectionFactory().retriveConnection()) {
			CommentsDAO commentsDAO = new CommentsDAO(connection);
			commentsDAO.save(comments);
		}

	}

}
