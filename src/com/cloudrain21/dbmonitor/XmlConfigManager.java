package com.cloudrain21.dbmonitor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * 설정 파일로 XML 을 사용할 때의 ConfigManager.
 * 일반적으로 DOM 방식보다 SAX 방식이 Parsing 속도가 빠르지만,
 * Config 정보 읽는데 성능까지 생각할 필요는 없으므로 구현이 편한 DOM 방식으로.
 */
public class XmlConfigManager extends ConfigManager {
    private static XmlConfigManager configManager = null;

    private XmlConfigManager(String configFile) {
        this.configFile = configFile;
    }

    /*
     * ConfigManager 는 Singleton
     */
    public synchronized static ConfigManager getConfigManager(String configFile) {
        if(configManager == null) {
            configManager = new XmlConfigManager(configFile);
        }
        return configManager;
    }

    private Document initDocument(String configFile) throws Exception {
        try( FileInputStream fis = new FileInputStream(configFile)) {

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(fis);
            doc.getDocumentElement().normalize();

            return doc;

        } catch(FileNotFoundException e) {
            throw e;
        } catch(Exception e) {
            throw e;
        }
    }

    /*
     * 주어진 root element 에 설정된 item 들을 Map<String,String>으로 구성하여 리턴한다.
     */
    private Map<String,String> readConfigItemsToMap(Element root) {
        Map<String,String> map = new HashMap<>(); 

        NodeList list = root.getChildNodes();

        for(int i=0; i<list.getLength(); i++) {
            if(Node.ELEMENT_NODE == list.item(i).getNodeType()) {

                String key = list.item(i).getNodeName().trim();
                String value = list.item(i).getTextContent().trim();

                map.put(key, value);
            }
        }

        return map;
    }

    private Element getRootElemByTagName(Element root, String tagName ) {
        return (Element)root.getElementsByTagName(tagName).item(0);
    }

    /*
     * @Override
     * 설정 파일에서 설정을 읽어들여 DBConfig 정보 구축하기
     * (ConfigManager.readConfig() 의 구현)
     */
    public void readConfig() throws Exception {
        try {

            Document doc = initDocument(configFile);
            Element root = doc.getDocumentElement();

            Element commonElem = getRootElemByTagName(root, "common_configs");
            Element targetDBsElem = getRootElemByTagName(root, "mon_target_dbs");

            Map<String,String> commonConfig = readConfigItemsToMap(commonElem);

            NodeList dbList = targetDBsElem.getChildNodes();
            for(int i=0; i<dbList.getLength(); i++) {

                if(dbList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                Element oneDB = (Element)dbList.item(i);
                Map<String,String> connectionConfig = readConfigItemsToMap(oneDB);

                String dbName = connectionConfig.get("name");
                String monQueryConfigFile = connectionConfig.get("mon_query_config");

                List<Map<String,String>> monQueryConfig = new ArrayList<>();

                /*
                 * DB 마다 query 설정파일을 다르게 설정할 수 있음.
                 */
                Document docQuery = initDocument(monQueryConfigFile);
                Element rootQuery = docQuery.getDocumentElement();

                NodeList queryList = rootQuery.getChildNodes();

                for(int j=0; j<queryList.getLength(); j++) {
                    if(queryList.item(j).getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }
                    Element oneQuery = (Element)queryList.item(j);
                    Map<String,String> queryMap = readConfigItemsToMap(oneQuery);

                    monQueryConfig.add(queryMap);
                }

                /*
                 * DB 하나마다 DBConfig 객체 하나씩 구축하여 targetDBs 에 추가한다.
                 * DBConfig 객체에는 공통정보, DB접속정보, 모니터링 쿼리 정보가 포함된다.
                 * MonitorThread 가 이 DBConfig 객체 하나씩을 가지고 작업을 수행하게 된다.
                 * 굳이 DB 하나마다 DBConfig 객체를 생성하지 않고, Config 정보가 바뀌는게 아니므로
                 * 전체를 static 으로 관리해도 되지만 구현 편의상 그냥 이렇게...;;
                 */
                DBConfig dbConfig = new DBConfig();

                dbConfig.setDBName(dbName);
                dbConfig.setCommonConfig(commonConfig);
                dbConfig.setConnectionConfig(connectionConfig);
                dbConfig.setMonQueryConfig(monQueryConfig);

                targetDBs.add(dbConfig);
            }
        } catch(Exception e) {
            throw e;
        }
    }
}
