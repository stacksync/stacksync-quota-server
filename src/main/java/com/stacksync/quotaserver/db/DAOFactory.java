package com.stacksync.quotaserver.db;

import java.sql.Connection;

import com.stacksync.quotaserver.db.postgresql.PostgresqlUserDAO;

public class DAOFactory {

	private String type;

	public DAOFactory(String type) {
		this.type = type;
	}

	public UserDAO getUserDao(Connection connection) {
		return new PostgresqlUserDAO(connection);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
