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

public class XmlRpcQuotaHandler {

    private static final Logger logger = Logger.getLogger(XmlRpcQuotaHandler.class.getName());
    
    private UserDAO userDAO;

    public XmlRpcQuotaHandler(ConnectionPool pool) {
        try {
            DAOFactory factory = new DAOFactory(Config.getDatasource());
            userDAO = factory.getUserDao(pool.getConnection());
            logger.info("XMLRPC server set up done.");
        } catch (Exception e) {
            logger.error("XMLRPC server could not initiliaze.");
        }
    }

    public String getAvailableQuota(String strUser) {

        logger.debug(String.format("XMLRPC Request. getAvailableQuota [userId: %s]", strUser));
        JsonObject jResponse = new JsonObject();
        jResponse.addProperty("user", strUser);
        
        try {
            User user = userDAO.findBySwiftName(strUser);
            jResponse.addProperty("quota_used", user.getQuotaUsed());
            jResponse.addProperty("quota_limit", user.getQuotaLimit());
        } catch (DAOException ex) {
            logger.error("Can't get user from ID: " + strUser);
            jResponse.addProperty("quota_used", -1);
            jResponse.addProperty("quota_limit", -1);
        }

        logger.debug(String.format("XMLRPC Response. %s", jResponse.toString()));

        return jResponse.toString();
    }
    
    public String updateAvailableQuota(String strUser, String strNewQuota){
        logger.debug(String.format("XMLRPC Request. getAvailableQuota [user: %s, newQuota: %s]", strUser, strNewQuota));

		Integer newQuota = null;
		User user = null;
        JsonObject jResponse = new JsonObject();

		try {
			newQuota = Integer.parseInt(strNewQuota);
		} catch (NumberFormatException ex) {
			logger.error("Can't parse the new quota value: " + strNewQuota);
		}
		
    	 try {
             user = userDAO.findBySwiftName(strUser);
             user.setQuotaUsed(newQuota);
             userDAO.updateQuota(user);
         } catch (DAOException ex) {
             logger.error("Can't get user from ID: " + strUser);
         }
    	 jResponse.addProperty("ok", 1);
         logger.debug(String.format("XMLRPC Response. %s", jResponse.toString()));

    	 
    	 return jResponse.toString();
    }

}
