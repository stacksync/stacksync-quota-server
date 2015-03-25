package com.stacksync.quotaserver.db;

import com.stacksync.commons.models.User;
import com.stacksync.commons.models.Workspace;
import com.stacksync.quotaserver.exceptions.dao.DAOException;

public interface WorkspaceDAO {

	public User getOwnerBySwiftContainer(String swiftContainer) throws DAOException;


}
