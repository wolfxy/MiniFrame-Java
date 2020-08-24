package org.mini.db;

import org.apache.log4j.Logger;

import java.sql.Connection;

/**
 * Created by Wuquancheng on 14-8-21.
 */
public class DataBase {

    private Connection connection = null;
    private String dbname = null;

    private static Logger logger = Logger.getLogger(DataBase.class);

    boolean autoCommit = true;

    public DataBase (String dbname) throws MiniRunTimeException {
        this(dbname,true);
    }

    public DataBase (String dbname, boolean autoCommit) throws MiniRunTimeException {
        this.dbname = dbname;
        this.autoCommit = autoCommit;
        this.getConnection();
    }

    public void finalize() {
        closeConnection();
    }

    protected void closeConnection () {
        try {
            if ( connection != null ) {
                connection.close();
                connection = null;
            }
        }
        catch ( Exception e ) {
            logger.error("closeConnection",e);
        }
    }

    public Connection getConnection() throws MiniRunTimeException {
        initConnection();
        return this.connection;
    }

    private void initConnection() throws MiniRunTimeException {
        if (this.connection == null) {
            try {
                this.connection = DBManager.getConnection(dbname);
                this.connection.setAutoCommit(this.autoCommit);
            }
            catch ( Exception e) {
                try {
                    this.connection = DBManager.getConnection(dbname);
                    this.connection.setAutoCommit(this.autoCommit);
                }
                catch ( Exception e2) {
                    throw new MiniRunTimeException(e2);
                }
            }
        }
    }

    public void close (){
        closeConnection();
    }

    public static void closeDatabase(DataBase dataBase) {
        if (dataBase != null) {
            dataBase.close();
        }
    }

}
