package com.stacksync.quotaserver.exceptions.dao;

import com.stacksync.quotaserver.db.DAOError;

public class NoResultReturnedDAOException extends DAOException {

	private static final long serialVersionUID = -1412276333572134887L;

	public NoResultReturnedDAOException(DAOError error) {
        super(error);
    }
    
    public NoResultReturnedDAOException(Exception e, DAOError error) {
        super(e, error);
    }

    public NoResultReturnedDAOException(String message) {
        super(message);
    }

    public NoResultReturnedDAOException(Throwable cause) {
        super(cause);
    }

    public NoResultReturnedDAOException(String message, Throwable cause) {
        super(message, cause);
    }

}