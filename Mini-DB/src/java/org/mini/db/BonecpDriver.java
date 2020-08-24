package org.mini.db;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
  *   <?xml version="1.0" encoding="UTF-8"?>
  *   <bonecp-config>
  *   <config alias="sms">
  *   <property name="driverClass">com.mysql.jdbc.Driver</property>
  *   <property name="jdbcUrl">jdbc:mysql://192.168.1.214:3306/sms_ems?characterEncoding=utf8&amp;allowMultiQueries=true&amp;noAccessToProcedureBodies=true</property>
  *   <property name="username">chedu_platform</property>
  *   <property name="password">chedu_platform@chedu.com.707</property>
  *   <property name="partitionCount">1</property>
  *   <property name="maxConnectionsPerPartition">100</property>
  *   <property name="minConnectionsPerPartition">1</property>
  *   <property name="maxConnectionAgeInSeconds">7200</property>
  *   <property name="acquireIncrement">3</property>
  *   </config>
  *   </bonecp-config>
  *
  * Created by Wuquancheng on 14-9-11.
  */
public class BonecpDriver {

    Map<String,BoneCP> map = new HashMap<String, BoneCP>();

    private static BonecpDriver bonecpDriver = null;

    private static BonecpDriver instance() {
        synchronized (BonecpDriver.class) {
            if (bonecpDriver == null) {
                bonecpDriver = new BonecpDriver();
            }
        }
        return bonecpDriver;
    }

    private boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    private void shutdown() {
        for (BoneCP boneCP:map.values()) {
            boneCP.close();
        }
    }
    private BonecpDriver() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                shutdown();
            }
        }));
    }

    private Connection fetchConnection(String alias) throws SQLException{
        BoneCP boneCP = map.get(alias);
        if (boneCP!=null) {
            return boneCP.getConnection();
        }
        else {
            return null;
        }
    }

    private void doConfigure( InputStream inputStream ) throws ParserConfigurationException, SAXException, IOException ,ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, SQLException {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            Element root = document.getDocumentElement();
            NodeList nodeList = root.getElementsByTagName("config");
            if (nodeList != null) {
                for (int index = 0; index < nodeList.getLength(); index++) {
                    Element config = (Element) nodeList.item(index);
                    String alias = config.getAttribute("alias");
                    BoneCPConfig boneCPConfig = new BoneCPConfig();
                    NodeList propertyList = config.getElementsByTagName("property");
                    for (int i = 0; i < propertyList.getLength(); i++) {
                        Element property = (Element) propertyList.item(i);
                        String propertyName = property.getAttribute("name");
                        String textContent = property.getTextContent();
                        if ("driverClass".equals(propertyName)) {
                            Class.forName(textContent);
                        } else if ("maxConnectionAgeInSeconds".equals(propertyName)) {
                            boneCPConfig.setMaxConnectionAgeInSeconds(Long.parseLong(textContent));
                        } else if ("idleConnectionTestPeriodInSeconds".equals(propertyName)) {
                            boneCPConfig.setIdleConnectionTestPeriodInSeconds(Long.parseLong(textContent));
                        }
                        else {
                            if (isNumeric(textContent)) {
                                Method method = BoneCPConfig.class.getMethod("set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1), int.class);
                                method.invoke(boneCPConfig, Integer.parseInt(textContent));
                            } else {
                                Method method = BoneCPConfig.class.getMethod("set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1), String.class);
                                method.invoke(boneCPConfig, textContent);
                            }
                        }
                    }
                    map.put(alias, new BoneCP(boneCPConfig));
                }
            }
        }
        finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }


    private void doConfigure( String fileName ) throws ParserConfigurationException, SAXException, IOException ,ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, SQLException {
        doConfigure(new FileInputStream(fileName));
    }

    public static void configure( String fileName ) throws ParserConfigurationException, SAXException, IOException ,ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, SQLException {
        BonecpDriver driver = BonecpDriver.instance();
        driver.doConfigure(fileName);
    }

    public static void configure( InputStream inputStream ) throws ParserConfigurationException, SAXException, IOException ,ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, SQLException {
        BonecpDriver driver = BonecpDriver.instance();
        driver.doConfigure(inputStream);
    }

    public static Connection getConnection(String alias) throws SQLException{
        BonecpDriver driver = BonecpDriver.instance();
        return driver.fetchConnection(alias);
    }

}
