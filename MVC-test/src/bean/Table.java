package bean;

import com.hk.dbservlet.ParsingXML;

import java.util.List;

public class Table {
    String dataBase;
    String tableName;
    String xmlLocation;
    List<String> list;

    public Table(String dataBase,String tableName,String xmlLocation){
        this.dataBase = dataBase;
        this.tableName = tableName;
        this.xmlLocation = xmlLocation;
        this.list = ParsingXML.parsing(xmlLocation);
    }

    public String getDataBase() {
        return dataBase;
    }

    public void setDataBase(String dataBase) {
        this.dataBase = dataBase;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getXmlLocation() {
        return xmlLocation;
    }

    public void setXmlLocation(String xmlLocation) {
        this.xmlLocation = xmlLocation;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
