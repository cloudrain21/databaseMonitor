package com.cloudrain21.dbmonitor;

import java.util.ArrayList;
import java.util.List;

/*
 * ConfigManager 는 XML, JSON, Property 등의 다양한
 * 설정 파일로 확장할 수 있도록 추상 클래스로.
 */
public abstract class ConfigManager {
	protected String configFile;
	protected List<DBConfig> targetDBs = new ArrayList<>();
	
	/*
	 * 하위 클래스에서 override 필요 
	 */
	public abstract void readConfig() throws Exception;
	
	public int getDBCount() {
		return targetDBs.size();
	}
		
	public DBConfig getDBConfig(String dbName) {
		DBConfig retConfig = null;
		for(DBConfig config : targetDBs) {
			if(dbName.equals(config.getDBName())) {
				retConfig = config;
				break;
			}
		}
		return retConfig;
	}

	public List<String> getTargetDBNameList() {
		List<String> dbList = new ArrayList<>();
		for(DBConfig config : targetDBs) {
			dbList.add(config.getDBName());
		}
		return dbList;
	}
	
	public void showAllDBConfig() {
		for(DBConfig config : targetDBs) {
			config.showDBConfig();
		}
	}
}
