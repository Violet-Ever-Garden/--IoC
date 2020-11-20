package com.hk.connecter;

import bean.Table;
import com.hk.constant.Configure;
import com.hk.container.sterotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
@Component
public class Connector {

    public Table getTable(String id){
        return Configure.tableMap.get(id.substring(0,3));
    }

    public Connection connect(String s) throws ClassNotFoundException, SQLException {
        Connection conn=null;
        Class.forName("com.mysql.jdbc.Driver");
        String url = getTable(s).getDataBase();
        conn= DriverManager.getConnection(url);
        System.out.println("==== connected ===");
        return conn;
    }

    //关闭数据库资源
    public void close(Statement stat, Connection conn) throws SQLException{
        if(stat!=null){
            stat.close();
        }
        if(conn!=null){
            conn.close();
        }
    }
}
