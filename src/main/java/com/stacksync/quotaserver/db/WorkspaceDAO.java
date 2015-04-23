package com.stacksync.quotaserver.db;

import com.stacksync.commons.models.User;
import com.stacksync.quotaserver.exceptions.dao.DAOException;
import com.stacksync.quotaserver.exceptions.dao.NoResultReturnedDAOException;

public interface WorkspaceDAO {

	public User getOwnerBySwiftContainer(String swiftContainer) throws NoResultReturnedDAOException, DAOException;


}
