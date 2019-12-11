package com.cloudrain21.dbmonitor;

import java.util.Map;

/*
 * Test 등 위해 사용할 Fake Database Manager
 */
public class FakeDatabaseManager extends DatabaseManager {
    public FakeDatabaseManager() {}

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
        url.append("jdbc:fakedb://");
        url.append(ip);
        url.append(":");
        url.append(port);
        url.append("/test");
        url.append(conn_opt);

        return url.toString();
    }
}
