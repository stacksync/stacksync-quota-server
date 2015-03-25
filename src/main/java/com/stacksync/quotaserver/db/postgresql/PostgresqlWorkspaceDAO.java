package com.stacksync.quotaserver.db.postgresql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.stacksync.commons.models.User;
import com.stacksync.commons.models.Workspace;
import com.stacksync.quotaserver.db.WorkspaceDAO;
import com.stacksync.quotaserver.exceptions.dao.DAOException;
import com.stacksync.quotaserver.db.DAOError;

public class PostgresqlWorkspaceDAO extends PostgresqlDAO implements WorkspaceDAO {

	private static final Logger logger = Logger.getLogger(PostgresqlWorkspaceDAO.class.getName());

	public PostgresqlWorkspaceDAO(Connection connection) {
		super(connection);
	}
	
	private User mapUser(ResultSet resultSet) throws SQLException {
		User user = new User();
		user.setId(UUID.fromString(resultSet.getString("id")));
		user.setEmail(resultSet.getString("email"));
		user.setName(resultSet.getString("name"));
		user.setSwiftUser(resultSet.getString("swift_user"));
		user.setSwiftAccount(resultSet.getString("swift_account"));
		user.setQuotaLimit(resultSet.getLong("quota_limit"));
		user.setQuotaUsedLogical(resultSet.getLong("quota_used_logical"));
		user.setQuotaUsedReal(resultSet.getLong("quota_used_real"));
		return user;
	}
	
	@Override
	public User getOwnerBySwiftContainer(String swiftContainer) throws DAOException {
		ResultSet resultSet = null;
		User user = null;
		
		String query = "SELECT * FROM user1 u where id = (SELECT owner_id FROM workspace where swift_container=?)";
		try{
			resultSet = executeQuery(query, new Object[] { swiftContainer });
			if (resultSet.next()) {
				user = mapUser(resultSet);
			}
		}catch(SQLException e){
			logger.error(e);
			throw new DAOException(DAOError.INTERNAL_SERVER_ERROR);

		}
		return user;
	}
}
