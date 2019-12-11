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

        LoggerManager.initialize(dbMgr.getDBName());
        
        /*
         * driver 설정이 잘못된 경우는 main thread 에게 exception 을
         * 던지고 thread 를 끝낸다.
         */
        try {
            dbMgr.checkDriver();

            while(true) {
                try {
                    dbMgr.connect();
                } 
                catch(SQLException e) {
                    Thread.sleep(10000); 
                    continue;
                }

                while(true) {
                    try {
                        dbMgr.executeAllQueries();
                        Thread.sleep(queryIntervalMSec);
                    } 
                    catch(SQLException e) { break; } 
                }

                /*
                 * 실패 시 reconnect 시도는 10 초마다.
                 */
                Thread.sleep(10000); 
            }
        } catch(InterruptedException e) {
            logger.error("interrupted from main thread. thread exit...");
        } catch(Exception e) {
            logger.error("thread exit..." + e.toString());
        }
    }
}
