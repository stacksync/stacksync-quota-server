package com.stacksync.quotaserver.main;

import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonController;

import com.stacksync.quotaserver.QuotaServerDaemon;

public class ServerTest {

    public static void main(String[] args) throws Exception {

        QuotaServerDaemon daemon = new QuotaServerDaemon();
        try {
            DaemonContext dc = new DaemonContext() {
                @Override
                public DaemonController getController() {
                    return null;
                }

                @Override
                public String[] getArguments() {
                    return new String[]{"/home/edgar/Documents/stackSync/stacksync-quota-server/config.properties"};
                }
            };

            daemon.init(dc);
            daemon.start();
        } catch (Exception e) {
            throw e;
        }
    }
}
