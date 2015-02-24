package com.stacksync.quotaserver.rpc;

import com.google.gson.JsonObject;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.stacksync.commons.models.User;
import com.stacksync.quotaserver.db.ConnectionPool;
import com.stacksync.quotaserver.db.DAOFactory;
import com.stacksync.quotaserver.db.UserDAO;
import com.stacksync.quotaserver.exceptions.dao.DAOException;
import com.stacksync.quotaserver.util.Config;

public class XmlRpcSyncHandler {

    private static final Logger logger = Logger.getLogger(XmlRpcSyncHandler.class.getName());
    
    private UserDAO userDAO;

    public XmlRpcSyncHandler(ConnectionPool pool) {
        try {
            DAOFactory factory = new DAOFactory(Config.getDatasource());
            userDAO = factory.getUserDao(pool.getConnection());
            logger.info("XMLRPC server set up done.");
        } catch (Exception e) {
            logger.error("XMLRPC server could not initiliaze.");
        }
    }

    public String getAvailableQuota(String strUserId) {

        logger.debug(String.format("XMLRPC Request. getAvailableQuota [userId: %s]", strUserId));
        JsonObject jResponse = new JsonObject();
            jResponse.addProperty("id", strUserId);
        try {
            User user = userDAO.findById(UUID.fromString(strUserId));
            jResponse.addProperty("quota_used", user.getQuotaUsed());
            jResponse.addProperty("quota_limit", user.getQuotaUsed());
        } catch (DAOException ex) {
            logger.error("Can't get user from ID: " + strUserId);
            jResponse.addProperty("quota_used", -1);
            jResponse.addProperty("quota_limit", -1);
        }

        logger.debug(String.format("XMLRPC Response. %s", jResponse.toString()));

        return jResponse.toString();
    }

}
