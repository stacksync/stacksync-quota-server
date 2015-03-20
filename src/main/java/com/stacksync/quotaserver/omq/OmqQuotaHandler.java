package com.stacksync.quotaserver.omq;

import omq.server.RemoteObject;

import com.google.gson.JsonObject;

import java.util.UUID;

import org.apache.log4j.Logger;

import com.stacksync.commons.models.User;
import com.stacksync.quotaserver.db.ConnectionPool;
import com.stacksync.quotaserver.db.DAOFactory;
import com.stacksync.quotaserver.db.UserDAO;
import com.stacksync.quotaserver.exceptions.dao.DAOException;
import com.stacksync.quotaserver.rpc.XmlRpcQuotaHandler;
import com.stacksync.quotaserver.util.Config;


public class OmqQuotaHandler extends RemoteObject implements IOmqQuotaHandler{
    private static final Logger logger = Logger.getLogger(OmqQuotaHandler.class.getName());
    private UserDAO userDAO;
    
    public OmqQuotaHandler(ConnectionPool pool) {
        try {
            DAOFactory factory = new DAOFactory(Config.getDatasource());
            userDAO = factory.getUserDao(pool.getConnection());
            logger.info("Omq server set up done.");
        } catch (Exception e) {
            logger.error("Omq server could not initiliaze.");
        }
    }

	@Override
	public String getAvailableQuota(String strUser) {
		logger.debug(String.format("Omq Request. getAvailableQuota [userId: %s]", strUser));
        JsonObject jResponse = new JsonObject();
        jResponse.addProperty("user", strUser);
        
        try {
            User user = userDAO.findBySwiftName(strUser);
            jResponse.addProperty("quota_used", user.getQuotaUsedReal());
            
            jResponse.addProperty("quota_limit", user.getQuotaLimit());
        } catch (DAOException ex) {
            logger.error("Can't get user from ID: " + strUser);
            jResponse.addProperty("quota_used", -1);
            jResponse.addProperty("quota_limit", -1);
        }

        logger.debug(String.format("Omq Response. %s", jResponse.toString()));

        return jResponse.toString();
	}

}
 