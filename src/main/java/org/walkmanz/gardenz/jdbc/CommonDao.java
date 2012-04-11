
package org.walkmanz.gardenz.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

 
public class CommonDao {

    private final Connection conn;
    
    
    public CommonDao(final Connection conn) throws DaoException {
        this.conn = conn;
    }

    public void begin() throws DaoException{
        if(conn != null) {
            try {
                conn.setAutoCommit(false);
            } catch (SQLException e) {
                throw new DaoException("can not begin transaction", e);
            }
        } else {
            throw new DaoException("connection not opened!");
        }
    }
    
    public void commit() throws DaoException {
        try {
            if (conn != null && !conn.getAutoCommit()) {
                conn.commit();
                conn.setAutoCommit(true);
            } else {
                if (conn == null) {
                    throw new DaoException("connection not opened!");
                } else {
                    throw new DaoException("first begin then commit please!");
                }
            }
        } catch (SQLException e) {
            throw new DaoException("can not commit transaction!", e);
        }
    }
    
    public void rollback() throws DaoException {
        try {
            if (conn != null && !conn.getAutoCommit()) {
                conn.rollback();
                conn.setAutoCommit(true);
            } else {
                if (conn == null) {
                    throw new DaoException("connection not opened!");
                } else {
                    throw new DaoException("first begin then rollback please!");
                }
            }
        } catch (SQLException e) {
            throw new DaoException("can not rollback transaction!", e);
        }
    }
    
    
    private List<Map<String,?>>  convert(ResultSet rs) throws DaoException {

        List<Map<String,?>> retList = new ArrayList<Map<String,?>>();

        try {
            ResultSetMetaData meta = rs.getMetaData();

            int colCount = meta.getColumnCount();

            while (rs.next()) {
                Map<String,Object> recordMap = new HashMap<String,Object>();
                for (int i = 1; i <= colCount; i++) {
                    String name = meta.getColumnName(i);
                    Object value = rs.getObject(i);
                    recordMap.put(name, value);
                }
                retList.add(recordMap);
            }
        } catch (SQLException ex) {
            throw new DaoException("can not convert result set to list of map", ex);
        }
        return retList;
    }
    
    private void apply(PreparedStatement pstmt, List<?> params) throws DaoException {
        try {
            if (params != null && params.size() > 0) {
                Iterator<?> it = params.iterator();
                int index = 1;
                while(it.hasNext()) {
                    
                    Object obj = it.next();
                    if (obj == null) {
                        pstmt.setObject(index, "");
                    } else {
                        pstmt.setObject(index, obj);
                    }
                    index++;
                }
            }
        } catch (SQLException ex) {
            throw new DaoException("can not apply parameter", ex);
        }
    }
    
    public List<Map<String,?>> query(String sql, List<?> params) throws DaoException {
        List<Map<String,?>> result = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            this.apply(pstmt, params);
            rs = pstmt.executeQuery();
            result = this.convert(rs);
        } catch (SQLException ex) {
            throw new DaoException("can not execute query", ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                	
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                	
                }
            }
        }

        return result;
    }
    
    public Object queryOne(String sql, List<?> params) throws DaoException {
        List<Map<String,?>> list = this.query(sql, params);
        
        if(list == null || list.size() == 0) {
            throw new DaoException("data not exist");
        } else {
            Map<String,?> record = (Map<String,?>)list.get(0);
            if(record == null || record.size() == 0 ) {
                throw new DaoException("data not exist");
            } else {
                return record.values().toArray()[0];
            }
        }
    }
    
    public int execute(String sql, List<?> params) throws DaoException {
        int ret = 0;
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            this.apply(pstmt, params);
            ret = pstmt.executeUpdate();
        }catch(SQLException ex) {
            throw new DaoException("", ex);
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                }
            }
        }
        
        return ret;
    }
    
    public List<Map<String,?>>[] queryBatch(String[] sqlArray, List<?>[] paramArray) throws DaoException {
    	List<List<Map<String,?>>> rets = new ArrayList<List<Map<String,?>>>();
        if(sqlArray.length != paramArray.length) {
            throw new DaoException("sql size not equal parameter size");
        } else {
            for(int i = 0; i < sqlArray.length; i++) {
                String sql = sqlArray[i];
                List<?> param = paramArray[i];
                List<Map<String,?>> ret = this.query(sql, param);
                rets.add(ret);
            }
            return (List<Map<String,?>>[])rets.toArray();
        }
    }
    
    public int[] executeBatch(String[] sqlArray, List<?>[] paramArray) throws DaoException {
        List<Integer> rets = new ArrayList<Integer>();
        if(sqlArray.length != paramArray.length) {
            throw new DaoException("sql size not equal parameter size");
        } else {
            for(int i = 0; i < sqlArray.length; i++) {
                int ret = this.execute(sqlArray[i], paramArray[i]);
                rets.add(new Integer(ret));
            }
            
            int[] retArray = new int[rets.size()];
            for(int i = 0; i < retArray.length; i++) {
                retArray[i] = ((Integer)rets.get(i)).intValue();
            }
            
            return retArray;
        }
    }
    
    public void close() throws DaoException{
        try {
            if (conn != null && conn.getAutoCommit()) {
                conn.close();
            } else {
                if(conn == null) {
                    throw new DaoException("can not close null connection, first new then close");
                } else {
                    throw new DaoException("transaction is running, rollbakc or commit befor close please.");
                }
            }
        } catch (SQLException ex) {
            throw new DaoException("Can not close common dao");
        }
    }
}

