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
            
            LoggerManager.initialize(DatabaseMon.class.getSimpleName());

            String configFile = args[0];

            /*
             * 주어진 설정파일의 확장자에 따라 ConfigManagerFactory 를 통해
             * ConfigManager 를 선택하도록 수정 필요.
             * 일단은 이렇게...
             */
            ConfigManager configMgr = XmlConfigManager.getConfigManager(configFile);
            configMgr.readConfig();
            configMgr.showAllDBConfig();

            /*
             * Target DB 별로 Monitoring Thread 를 기동   
             */
            List<String> dbNameList = configMgr.getTargetDBNameList();
            
            for(String dbName : dbNameList) {
                DBConfig config = configMgr.getDBConfig(dbName);
                String driverName = config.getDriverName();

                /*
                 * Driver 이름을 통해 적절한 Database Manager 를 만든다.
                 */
                DatabaseManager dbMgr = DatabaseManagerFactory.getDatabaseManager(driverName);
                dbMgr.setDBConfig(config);

                Thread thread = new MonitorThread(dbMgr);
                thread.start();
            }

            while(true) {
                Thread.sleep(1000);
            }
        } catch(Exception e) {
            logger.error(e.toString());
            LoggerManager.remove();
        }
    }
    
    public static void showUsage() {
        System.out.println("");
        System.out.println("Usage : java GoldMon config_file_name");
        System.out.println("");
    }
}
