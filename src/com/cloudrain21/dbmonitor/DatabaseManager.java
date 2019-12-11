package com.cloudrain21.dbmonitor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DatabaseManager {
    private static Logger logger = LoggerFactory.getLogger(DatabaseMon.class);
    protected DBConfig config = null;
    protected Connection conn = null;

    /*
     * Query 별로 한번 Prepare 한 후 재사용을 위한 PreparedStatement
     */
    Map<String,PreparedStatement> pstmt = new HashMap<>();

    public void setDBConfig(DBConfig config) {
        this.config = config;
    }

    public String getDriverName() {
        return config.getDatabaseConfig().get("driver_name");
    }

    public String getDBName() {
        return config.getDatabaseConfig().get("name");
    }

    public DBConfig getDBConfig() {
        return config;
    }

    private String getDBUserName() {
        return config.getDatabaseConfig().get("user");
    }

    private String getDBPassword() {
        return config.getDatabaseConfig().get("pass");
    }
    
    public void checkDriver() throws ClassNotFoundException {
        try {
            Class.forName(getDriverName());
        } catch(ClassNotFoundException e) {
            throw e;
        }
    }

    public void connect() throws SQLException {
        Map<String,String> dbConfig = config.getDatabaseConfig();

        try {
            Connection conn = DriverManager.getConnection(
                    makeConnectionUrl(dbConfig), 
                    getDBUserName(), 
                    getDBPassword());

            this.conn = conn;
            logger.info("connect success.(" + getDBName() + ")");

        } catch(SQLException e) {
            logger.error(e.toString());
            try {
                if(this.conn != null) {
                    this.conn.close();
                    this.conn = null;
                }
            } catch(SQLException ein) {
            }
            throw e;
        }
    }

    private boolean isDisplayColumn(String queryName, String colName, String colAliasName) {
        Map<String,String> queryMap = config.getMonQueryMapByName(queryName);
        String displayCols = queryMap.get("display_columns");

        String[] arr = displayCols.split(",");
        for(String token : arr) {
            if(colName.equals(token.toLowerCase()) || colAliasName.equals(token.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private synchronized void dbPrint(String queryName, String colAliasName, String value) {
        logger.info("[ " + queryName + " ]" + colAliasName + " : " + value);
    }

    private void executeQuery(String queryName, String query) throws SQLException {
        try {
            String colName = "";
            String colAliasName = "";

            ResultSet rs = pstmt.get(query).executeQuery();

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsCount = rsmd.getColumnCount();

            while(rs.next())
            {
                for(int i=1; i<=columnsCount; i++) {
                    colName = rsmd.getColumnName(i).toLowerCase();
                    colAliasName = rsmd.getColumnLabel(i).toLowerCase();

                    if(! isDisplayColumn(queryName, colName, colAliasName)) {
                        continue;
                    }

                    int type = rsmd.getColumnType(i);
                    switch(type) {
                    case Types.CHAR:
                    case Types.VARCHAR:
                    case Types.LONGVARCHAR:
                    case Types.LONGNVARCHAR:
                        dbPrint(queryName, colAliasName, rs.getString(i));
                        break;
                    case Types.BIGINT:
                    case Types.INTEGER:
                    case Types.TINYINT:
                    case Types.SMALLINT:
                        dbPrint(queryName, colAliasName, String.valueOf(rs.getInt(i)));
                        break;
                    case Types.DECIMAL:
                    case Types.NUMERIC:
                        if(rsmd.getScale(i) > 0) {
                            dbPrint(queryName, colAliasName, String.valueOf(rs.getDouble(i)));
                        } else {
                            dbPrint(queryName, colAliasName, String.valueOf(rs.getInt(i)));
                        }
                        break;
                    case Types.REAL:
                    case Types.DOUBLE:
                        dbPrint(queryName, colAliasName, String.valueOf(rs.getDouble(i)));
                        break;
                    case Types.FLOAT:
                        dbPrint(queryName, colAliasName, String.valueOf(rs.getFloat(i)));
                        break;
                    case Types.DATE:
                        dbPrint(queryName, colAliasName, String.valueOf(rs.getDate(i)));
                        break;
                    case Types.TIME:
                    case Types.TIME_WITH_TIMEZONE:
                        dbPrint(queryName, colAliasName, String.valueOf(rs.getTime(i)));
                        break;
                    case Types.TIMESTAMP:
                    case Types.TIMESTAMP_WITH_TIMEZONE:
                        dbPrint(queryName, colAliasName, String.valueOf(rs.getTimestamp(i)));
                        break;
                    default :
                        throw new SQLException("unsupported column type : " + type);
                        // need to add other types
                    }
                }
            }
        } catch(SQLException e) {
            logger.warn(e.getSQLState() + " : " + e);
            throw e;
        }
    }

    private boolean isMonEnable(Map<String,String> queryMap) {
        String configStr = queryMap.get("mon_enable");
        if( "on".equals(configStr.toLowerCase()) ||
                "yes".equals(configStr.toLowerCase()) ||
                "1".equals(configStr.toLowerCase()) ) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * 설정에 등록된 monitoring query 를 모두 실행한다. (enable 된 query 만)
     */
    public void executeAllQueries() throws SQLException {
        try {
            for(Map<String,String> queryMap : config.getMonQueryConfig()) {
                if(isMonEnable(queryMap)) {
                    String queryStr = queryMap.get("mon_sql");
                    if(pstmt.containsKey(queryStr) == false) {
                        PreparedStatement pst = conn.prepareStatement(queryStr);
                        pstmt.put(queryStr, pst);
                    }
                }
            }

            for(Map<String,String> queryMap : config.getMonQueryConfig() ) {
                if(isMonEnable(queryMap)) {
                    executeQuery(queryMap.get("name"), queryMap.get("mon_sql"));
                }
            }
        } catch(SQLException e) {
            try {
                Iterator<String> it = pstmt.keySet().iterator();
                while(it.hasNext()) {
                    String queryStr = it.next();
                    PreparedStatement pst = pstmt.get(queryStr);
                    pst.close();
                }
                pstmt.clear();
                conn.close();
                conn = null;
            } catch(SQLException e1) {
            }
            throw e;
        }
    }

    public abstract String makeConnectionUrl(Map<String,String> dbConfig);
}
