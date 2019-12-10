package com.cloudrain21.dbmonitor;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseMon {
	private static Logger logger = LoggerFactory.getLogger(DatabaseMon.class);

	public static void main(String[] args) {
		try {
	        
			if(args.length != 1) {
				showUsage();
			}

			String configFile = args[0];

			ConfigManager configMgr = XmlConfigManager.getConfigManager(configFile);
			configMgr.readConfig();
			configMgr.showAllDBConfig();

			List<String> dbNameList = configMgr.getTargetDBNameList();
			for(String dbName : dbNameList) {
				DBConfig config = configMgr.getDBConfig(dbName);
				String driverName = config.getDriverName();

				DatabaseManager dbMgr = DatabaseManagerFactory.getDatabaseManager(driverName);
				dbMgr.setDBConfig(config);
				
				Thread thread = new MonitorThread(dbMgr);
				thread.start();
			}
			
			while(true) {
				logger.debug("main thread is running....");
				Thread.sleep(1000);
			}
		} catch(Exception e) {
			logger.error(e.toString());
		}
	}
	
	public static void showUsage() {
		logger.info("");
		logger.info("Usage : java GoldMon config_file_name");
		logger.info("");
	}
}
