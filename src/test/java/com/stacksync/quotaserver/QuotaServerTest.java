package com.stacksync.quotaserver;

import com.stacksync.quotaserver.db.ConnectionPool;
import com.stacksync.quotaserver.db.ConnectionPoolFactory;
import com.stacksync.quotaserver.rpc.XmlRpcQuotaHandler;
import com.stacksync.quotaserver.util.Config;
import com.stacksync.quotaserver.util.Constants;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonController;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class QuotaServerTest {

    private static Logger logger = Logger.getLogger(QuotaServerTest.class.getName());
    private static XmlRpcQuotaHandler handler;

    @BeforeClass
    public static void SetUpClass() throws Exception {

        /*daemon = new QuotaServerDaemon();
         try {
         DaemonContext dc = new DaemonContext() {
         @Override
         public DaemonController getController() {
         return null;
         }

         @Override
         public String[] getArguments() {
         return new String[]{"/home/cotes/NetBeansProjects/stacksync-quota-server/config.properties"};
         }
         };

         daemon.init(dc);
         daemon.start();
         } catch (Exception e) {
         throw e;
         }

         XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
         config.setServerURL(new URL("http://127.0.0.1:"+ Constants.XMLRPC_PORT+ "/xmlrpc")); //);// 
         config.setEnabledForExtensions(true);
         config.setConnectionTimeout(60 * 1000);
         config.setReplyTimeout(60 * 1000);

         server = new XmlRpcClient();

         // use Commons HttpClient as transport
         server.setTransportFactory(new XmlRpcCommonsTransportFactory(server));
         // set configuration
         server.setConfig(config);*/

        String configPath = "/home/cotes/NetBeansProjects/stacksync-quota-server/config.properties";

        File file = new File(configPath);
        if (!file.exists()) {
            logger.error("'" + configPath + "' file not found");
            System.exit(2);
        }

        Config.loadProperties(configPath);
        
        String datasource = Config.getDatasource();
        ConnectionPool pool = ConnectionPoolFactory.getConnectionPool(datasource);
        handler = new XmlRpcQuotaHandler(pool);
    }

    @AfterClass
    public static void TearDownClass() {
    }

    @Test
    public void getQuota() {
        handler.getAvailableQuota("4b943500-d49b-4ad4-b489-11cb0e2716e3");
    }
}
