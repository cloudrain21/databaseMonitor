package com.cloudrain21.dbmonitor;


/*
 * 설정 파일로 JSON 을 사용할 때의 ConfigManager.
 */
public class JsonConfigManager extends ConfigManager {
	private static JsonConfigManager configManager = null;
	
	private JsonConfigManager(String configFile) {
		this.configFile = configFile;
	}
	
	/*
	 * ConfigManager 는 Singleton
	 */
	public synchronized static ConfigManager getConfigManager(String configFile) {
		if(configManager == null) {
			configManager = new JsonConfigManager(configFile);
		}
		return configManager;
	}

	/*
	 * @override
	 * 설정 파일에서 설정을 읽어들여 DBConfig 정보 구축하기
	 * (ConfigManager.readConfig() 의 구현)
	 */
	public void readConfig() throws Exception {
	}
}
