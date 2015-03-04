package com.stacksync.quotaserver;

import com.stacksync.quotaserver.db.ConnectionPool;
import com.stacksync.quotaserver.db.ConnectionPoolFactory;
import com.stacksync.quotaserver.rpc.XmlRpcQuotaHandler;
import com.stacksync.quotaserver.util.Config;
import java.io.File;
import org.apache.log4j.Logger;
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
