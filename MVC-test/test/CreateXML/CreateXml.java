package CreateXML;

import com.hk.constant.Configure;
import com.hk.dbservlet.ParsingXML;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CreateXml {
    public static void main(String[] args) throws SQLException {
        String path = "C:\\Users\\Hasee\\Downloads";
        String filename = "testFile.xml";
        String url = Configure.tableMap.get("111").getDataBase();
        Connection connection = DriverManager.getConnection(url);
        ParsingXML.createXml(connection,path+ "\\"+filename);
        System.out.println(111);
    }
}
