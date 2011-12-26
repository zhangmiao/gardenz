package org.walkmanz.gardenz.persistent.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionManager {
    
	private static final String DEFAULT_URL = "jdbc:mysql://172.17.0.120:3306/xhh?user=xhh&password=xinhua&useUnicode=true&characterEncoding=UTF-8";
	
	private static final String DEFAULT_DRIVER = "com.mysql.jdbc.Driver";
	
	
    public static Connection getConnection() throws DaoException {
        Connection conn = null;
        try {
            Class.forName(DEFAULT_DRIVER);
            conn = DriverManager.getConnection(DEFAULT_URL); 
        } catch(ClassNotFoundException e) {
            throw new DaoException("can not find class", e);
        }catch (SQLException e) {
            throw new DaoException("can not get database connection", e);
        } 
        return conn;
    }
    
    public static Connection getConnection(String driver,String url) throws DaoException {
        Connection conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url); 
        } catch(ClassNotFoundException e) {
            throw new DaoException("can not find class", e);
        }catch (SQLException e) {
            throw new DaoException("can not get database connection", e);
        } 
        return conn;
    }
    
    public static void main(String args[]) throws Exception {
    	System.out.println(ConnectionManager.getConnection());
    }
}
