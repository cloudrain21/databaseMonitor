package com.cloudrain21.dbmonitor;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorThread extends Thread {
	private static Logger logger = LoggerFactory.getLogger(DatabaseMon.class);
	private DatabaseManager dbMgr;
	
	public MonitorThread(DatabaseManager dbMgr) {
		this.dbMgr = dbMgr;
	}
	
	public synchronized void run() {
		long queryIntervalMSec = dbMgr.getDBConfig().getQueryIntervalMSec();

		while(true) {
			try {
				dbMgr.connect();
				
				while(true) {
					dbMgr.executeAllQueries();
					
					Thread.sleep(queryIntervalMSec);
				}
			} catch(SQLException e) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
				}
				
				if("08S01".equals(e.getSQLState())) {
					continue;
				} else {
					logger.error("(" + dbMgr.getDBName() + ")" + " critical db error : " + e.getSQLState());
				}
			} catch (InterruptedException e) {
			} catch (Exception e) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
				}
				logger.error("(" + dbMgr.getDBName() + ")" + " critical db error" + e.toString());
			}
		}
	}
}
