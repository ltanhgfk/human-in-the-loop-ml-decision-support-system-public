package dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import util.DbUtil;

public class DBAction {
	private Connection connection;
	 
    public DBAction() throws Exception {
        connection = DbUtil.getConnection();
    }
    
    public void closeConnection() { 
        try { 
            connection.close();
        }
        catch (SQLException e) { 
            e.printStackTrace();
        }
    } 

    public ResultSet executeSelectStatments(String strSQL) throws SQLException{ 
        ResultSet resultSet = null;
        Statement statement = null; 
        try { 
            statement = connection.createStatement();
            resultSet = statement.executeQuery(strSQL); 
        }catch (SQLException e) { 
            e.printStackTrace();
        } 
        return resultSet; 
    } 

    public int executeInsertStatments(String strSQL) throws SQLException{ 
        Statement statement = null;
        int kq = 0;
        try { 
            statement = connection.createStatement();            
            kq = statement.executeUpdate(strSQL); 
        }catch (SQLException e) { 
            e.printStackTrace();
        }finally { 
            statement.close(); 
        } 
        return kq;
    } 
    

    public int executeUpdateStatments(String strSQL) throws SQLException{ 
        Statement statement = null; 
        int kq = 0;
        try { 
            statement = connection.createStatement(); 
            kq = statement.executeUpdate(strSQL); 
        } catch (SQLException e) { 
            e.printStackTrace(); 
        } finally { 
            statement.close(); 
        }
        return kq;
    } 

    public int executeDeleteStatments(String strSQL) throws SQLException{ 
        Statement statement = null; 
        int kq = 0;
        try { 
            statement = connection.createStatement(); 
            kq = statement.executeUpdate(strSQL); 
        } catch (SQLException e) { 
            e.printStackTrace(); 
        } finally { 
            statement.close(); 
        } 
        return kq;
    }
}
