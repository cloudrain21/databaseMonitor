package com.cloudrain21.dbmonitor;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBConfig {
    private static Logger logger = LoggerFactory.getLogger(DatabaseMon.class);
    private String dbName;
    private Map<String,String> commonConfig; 
    private Map<String,String> connectionConfig;
    private List<Map<String,String>> monQueryConfig;

    public void setDBName(String dbName) {
        this.dbName = dbName;
    }

    public String getDBName() {
        return this.dbName;
    }

    public long getQueryIntervalSec() {
        return Integer.parseInt(this.commonConfig.get("query_interval_sec"));
    }

    public long getQueryIntervalMSec() {
        return Integer.parseInt(this.commonConfig.get("query_interval_sec")) * 1000;
    }

    public String getDriverName() {
        return connectionConfig.get("driver_name");
    }

    public void setCommonConfig(Map<String,String> commonConfig) {
        this.commonConfig = commonConfig;
    }

    public Map<String,String> getCommonConfig() {
        return this.commonConfig;
    }

    public void setConnectionConfig(Map<String,String> connectionConfig) {
        this.connectionConfig = connectionConfig;
    }

    public Map<String,String> getDatabaseConfig() {
        return this.connectionConfig;
    }

    public void setMonQueryConfig(List<Map<String,String>> monQueryConfig) {
        this.monQueryConfig = monQueryConfig;
    }

    public List<Map<String,String>> getMonQueryConfig() {
        return this.monQueryConfig;
    }

    public Map<String,String> getMonQueryMapByName(String queryName) {
        for(Map<String,String> queryMap : monQueryConfig) {
            if(queryMap.get("name").equals(queryName)) {
                return queryMap;
            }
        }
        return null;
    }

    private void showCommonConfig() {
        logger.debug("  - common_config -");
        Iterator<String> it = commonConfig.keySet().iterator();
        while(it.hasNext()) {
            String key = it.next();
            logger.debug("    " + key + " : " + commonConfig.get(key));
        }
    }

    private void showConnectionConfig() {
        logger.debug("  - connection_config -");
        Iterator<String> it = connectionConfig.keySet().iterator();
        while(it.hasNext()) {
            String key = it.next();
            logger.debug("    " + key + " : " + connectionConfig.get(key));
        }
    }

    private void showMonQueryConfig() {
        logger.debug("  - mon_query_config -");
        for(Map<String,String> query : monQueryConfig) {
            String queryName = query.get("name");
            logger.debug("    query_name : " + queryName);
            Iterator<String> it = query.keySet().iterator();
            while(it.hasNext()) {
                String key = it.next();
                String value = query.get(key);
                logger.debug("      " + key + " : " + value);
            }
        }
    }

    public void showDBConfig() {
        logger.debug("DB Name : " + dbName);

        showCommonConfig();
        showConnectionConfig();
        showMonQueryConfig();
    }
}
