package com.hk.dbservlet;

import com.hk.constant.Configure;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class ParsingXML {
    public static void main(String[] args) throws IOException {
        ParsingXML.parsing(Configure.tableMap.get("111").getXmlLocation());

    }

    public static List<String> parsing(String xmlLocation)  {
        ArrayList<String> properties = new ArrayList<>();
        try {
            //读取xml文件内容
            FileInputStream is = new FileInputStream(xmlLocation);
            InputStreamReader streamReader = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(streamReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            String xml = new String(stringBuilder);


            //创建DocumentBuilderFactory实例,指定DocumentBuilder
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbf.newDocumentBuilder();
            //从xml字符串中读取数据
            InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
            Document doc = builder.parse(inputStream);
            //取的根元素
            Element root = (Element) doc.getDocumentElement();
            //得到根元素所有子元素的集合
            NodeList nodelist = root.getChildNodes();
            //得到list集合的size()
            int size = nodelist.getLength();
            //新建参数列表
            /*
            我们定义参数列表中的数据必须按照StudentInfo的的顺序填写
             */
            for(int i = 0;i < size;i++){
                Node node = nodelist.item(i);
                if(node.getNodeName()!="#text"){
                    properties.add(node.getTextContent());
                    System.out.println(nodelist.item(i).getNodeName()+"==="+node.getTextContent());
                }

            }

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return properties;
    }


    public static void createXml(Connection conn, String path) {
        try {
            // 创建解析器工厂
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = factory.newDocumentBuilder();
            Document document = db.newDocument();
            // 不显示standalone="no"
            document.setXmlStandalone(true);
            /*根节点*/
            Element mapper = document.createElement("mapper");
            // 向mapper根节点中添加子节点property
            List<Element> propertys=new ArrayList<>();
            /*
            Element property = document.createElement("property");
            Element property2 = document.createElement("property");
            Element property3 = document.createElement("property");
            Element property4 = document.createElement("property");
            Element property5 = document.createElement("property");
            propertys.add(property);
            propertys.add(property2);
            propertys.add(property3);
            propertys.add(property4);
            propertys.add(property5);*/
            for(int i = 0 ; i< 5;i++){
                propertys.add(document.createElement("property"));
            }
            PreparedStatement preparedStatement = conn.prepareStatement("select * from student");
            //结果集元数据
            ResultSetMetaData resultSetMetaData = preparedStatement.getMetaData();
            //表列数
            int size = resultSetMetaData.getColumnCount();
            List<String> columnNames = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                columnNames.add(resultSetMetaData.getColumnName(i + 1));
            }
            for (int i = 0; i < size; i++) {
                propertys.get(i).setTextContent(columnNames.get(i));
                mapper.appendChild(propertys.get(i));
            }
            document.appendChild(mapper);
            // 创建TransformerFactory对象
            TransformerFactory tff = TransformerFactory.newInstance();
            // 创建 Transformer对象
            Transformer tf = tff.newTransformer();
            // 输出内容是否使用换行
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            // 创建xml文件并写入内容
            tf.transform(new DOMSource(document), new StreamResult(new File(path)));
            System.out.println("生成xml成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("生成xml失败");
        }
    }
}
