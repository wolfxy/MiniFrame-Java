package org.mini.db;


import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;


/**
 * Created with IntelliJ IDEA.
 * User: Wuquancheng
 * Date: 13-5-22
 * Time: 下午6:45
 * To change this template use File | Settings | File Templates.
 */
public class DBManager {

    private static int retry = 5;

    public static void loadConfig(String fileName) {
        InputStream inputStream = null;
        for (int i = 0; i < retry; i++) {
            try {
                if (fileName == null || fileName.length() == 0 ) {
                    inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
                }
                else {
                    inputStream = new FileInputStream(fileName);
                }
                //JAXPConfigurator.configure(new InputSource(configFile), false);
                BonecpDriver.configure(inputStream);
                break;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private DBManager() {
        super();
    }

    public static Connection getConnection(String alias) throws Exception {
        return BonecpDriver.getConnection(alias);
        //return DriverManager.getConnection( "proxool."+alias);
    }
}
