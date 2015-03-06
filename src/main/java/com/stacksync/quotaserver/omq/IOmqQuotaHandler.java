package com.stacksync.quotaserver.omq;

import omq.Remote;


public interface IOmqQuotaHandler extends Remote{
	 public String getAvailableQuota(String strUser);

}
