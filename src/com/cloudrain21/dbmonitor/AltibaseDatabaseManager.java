package com.cloudrain21.dbmonitor;

import java.util.Map;

/*
 * Altibase DB 관련 작업을 수행하는 Manager Class 
 */
public class AltibaseDatabaseManager extends DatabaseManager {
    public AltibaseDatabaseManager() {}

    /*
     * @Override
     */
    public String makeConnectionUrl(Map<String,String> dbConfig) {
        String conn_opt = "";

        String ip   = dbConfig.get("ip");
        String port = dbConfig.get("port");
        String opt  = dbConfig.get("conn_opt");

        if(opt.length() != 0) {
            conn_opt = "?" + opt;
        }

        StringBuffer url = new StringBuffer();
        url.append("jdbc:Altibase://");
        url.append(ip);
        url.append(":");
        url.append(port);
        url.append("/mydb");
        url.append(conn_opt);

        return url.toString();
    }
}
