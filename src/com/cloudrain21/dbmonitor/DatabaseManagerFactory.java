package com.cloudrain21.dbmonitor;

/*
 * 설정에 주어진 driver 이름을 이용하여 DatabaseManager 를 선택한다.
 */
public class DatabaseManagerFactory {
    public static DatabaseManager getDatabaseManager(String driverName) throws ClassNotFoundException {
        try {
            if(driverName.equals("com.mysql.cj.jdbc.Driver")) 
            {
                return new MysqlDatabaseManager();
            } 
            else if(driverName.equals("oracle.jdbc.driver.OracleDriver")) 
            {
                return new OracleDatabaseManager();
            } 
            else if(driverName.equals("sunje.goldilocks.jdbc.GoldilocksDriver")) 
            {
                return new GoldilocksDatabaseManager();
            } 
            else if(driverName.equals("Altibase.jdbc.driver.AltibaseDriver")) 
            {
                return new AltibaseDatabaseManager();
            } 
            else if(driverName.equals("org.postgresql.Driver")) 
            {   
                return new PostgresqlDatabaseManager();
            }
            else if(driverName.equals("FakeDatabaseDriverForTest")) 
            {
                return new FakeDatabaseManager();
            }
            else 
            {
                throw new ClassNotFoundException(driverName);
            }
        } catch(ClassNotFoundException e) {
            throw e;
        }
    }
}
