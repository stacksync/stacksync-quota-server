package com.stacksync.quotaserver.rpc;

import com.google.gson.JsonObject;

import java.util.UUID;

import org.apache.log4j.Logger;

import com.stacksync.commons.models.User;
import com.stacksync.commons.models.Workspace;
import com.stacksync.quotaserver.db.ConnectionPool;
import com.stacksync.quotaserver.db.DAOFactory;
import com.stacksync.quotaserver.db.UserDAO;
import com.stacksync.quotaserver.db.WorkspaceDAO;
import com.stacksync.quotaserver.exceptions.dao.DAOException;
import com.stacksync.quotaserver.util.Config;

public class XmlRpcQuotaHandler {

    private static final Logger logger = Logger.getLogger(XmlRpcQuotaHandler.class.getName());
    
    private UserDAO userDAO;
    private WorkspaceDAO workspaceDAO;

    public XmlRpcQuotaHandler(ConnectionPool pool) {
        try {
            DAOFactory factory = new DAOFactory(Config.getDatasource());
            userDAO = factory.getUserDao(pool.getConnection());
            workspaceDAO = factory.getWorkspaceDao(pool.getConnection());
            logger.info("XMLRPC server set up done.");
        } catch (Exception e) {
            logger.error("XMLRPC server could not initiliaze.");
        }
    }

    public String getAvailableQuota(String strSwiftContainer) {

        logger.debug(String.format("XMLRPC Request. getAvailableQuota [containerdId: %s]", strSwiftContainer));
        JsonObject jResponse = new JsonObject();
        
        
        try {
        	User user = workspaceDAO.getOwnerBySwiftContainer(strSwiftContainer);
            jResponse.addProperty("quota_used", user.getQuotaUsedReal());
            jResponse.addProperty("quota_limit", user.getQuotaLimit());
            jResponse.addProperty("user", user.getSwiftUser());
        } catch (DAOException ex) {
            logger.error("Can't get user from swiftContainer: " + strSwiftContainer);
            jResponse.addProperty("quota_used", -1);
            jResponse.addProperty("quota_limit", -1);
        }

        logger.debug(String.format("XMLRPC Response. %s", jResponse.toString()));

        return jResponse.toString();
    }
    
    public String updateAvailableQuota(String strUser, String strNewQuota){
        logger.debug(String.format("XMLRPC Request. getAvailableQuota [user: %s, newQuota: %s]", strUser, strNewQuota));

		Long newQuota = null;
		User user = null;
        JsonObject jResponse = new JsonObject();

		try {
			newQuota = Long.parseLong(strNewQuota);
		} catch (NumberFormatException ex) {
			logger.error("Can't parse the new quota value: " + strNewQuota);
		}
		
    	 try {
             user = userDAO.findBySwiftName(strUser);
             user.setQuotaUsedReal(newQuota);
             userDAO.updateQuota(user);
         } catch (DAOException ex) {
             logger.error("Can't get user from ID: " + strUser);
         }
    	 jResponse.addProperty("ok", 1);
         logger.debug(String.format("XMLRPC Response. %s", jResponse.toString()));

    	 
    	 return jResponse.toString();
    }

}
