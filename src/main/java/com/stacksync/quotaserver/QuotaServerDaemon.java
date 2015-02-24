package com.stacksync.quotaserver;

import com.stacksync.quotaserver.db.ConnectionPool;
import com.stacksync.quotaserver.db.ConnectionPoolFactory;
import com.stacksync.quotaserver.exceptions.dao.DAOConfigurationException;
import com.stacksync.quotaserver.rpc.XmlRpcSyncHandler;
import com.stacksync.quotaserver.rpc.XmlRpcSyncServer;
import com.stacksync.quotaserver.util.Config;
import com.stacksync.quotaserver.util.Constants;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.log4j.Logger;

public class QuotaServerDaemon implements Daemon {

    private static final Logger logger = Logger.getLogger(QuotaServerDaemon.class.getName());
    private ConnectionPool pool;
    private XmlRpcSyncServer xmlRpcServer;

    @Override
    public void init(DaemonContext dc) {

        logger.info(String.format("Initializing Quota Server v%s...", QuotaServerDaemon.getVersion()));

        logger.info(String.format("Java VM: %s", System.getProperty("java.vm.name")));
        logger.info(String.format("Java VM version: %s", System.getProperty("java.vm.version")));
        logger.info(String.format("Java Home: %s", System.getProperty("java.home")));
        logger.info(String.format("Java version: %s", System.getProperty("java.version")));

        loadProperties(dc);
        testDatabaseConnection();
        launchXmlRpc();
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void destroy() {
    }

    private static String getVersion() {
        String path = "/version.properties";
        InputStream stream = Config.class.getResourceAsStream(path);
        if (stream == null) {
            return "UNKNOWN";
        }
        Properties props = new Properties();
        try {
            props.load(stream);
            stream.close();
            return (String) props.get("version");
        } catch (IOException e) {
            return "UNKNOWN";
        }
    }

    private void loadProperties(DaemonContext dc) {
        try {
            String[] argv = dc.getArguments();

            if (argv.length == 0) {
                logger.error("No config file passed to StackSync Server.");
                System.exit(1);
            }

            String configPath = argv[0];

            File file = new File(configPath);
            if (!file.exists()) {
                logger.error("'" + configPath + "' file not found");
                System.exit(2);
            }

            Config.loadProperties(configPath);

        } catch (IOException e) {
            logger.error("Could not load properties file.", e);
            System.exit(7);
        }
    }

    private void testDatabaseConnection() {
        try {

            String datasource = Config.getDatasource();
            pool = ConnectionPoolFactory.getConnectionPool(datasource);

            // it will try to connect to the DB, throws exception if not
            // possible.
            Connection conn = pool.getConnection();
            conn.close();

            logger.info("Connection to database succeded");
        } catch (DAOConfigurationException e) {
            logger.error("Connection to database failed.", e);
            System.exit(3);
        } catch (SQLException e) {
            logger.error("Connection to database failed.", e);
            System.exit(4);
        }
    }

    private void launchXmlRpc() {
        logger.info("Initializing XML RPC...");

        try {
            xmlRpcServer = new XmlRpcSyncServer(Constants.XMLRPC_PORT);
            xmlRpcServer.addHandler("XmlRpcSyncHandler", new XmlRpcSyncHandler(pool));
            xmlRpcServer.serve_forever();
            logger.info("XML RPC initialization succeded");
        } catch (Exception e) {
            logger.fatal("Could not initialize XMLRPC.", e);
            System.exit(6);
        }
    }
}
