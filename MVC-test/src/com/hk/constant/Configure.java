package com.hk.constant;

import bean.Table;

import java.util.HashMap;

/**
 * @author haokai
 * 常量定义
 * 配置类
 */
public class Configure {
    public static HashMap<String, Table> tableMap;

    static {
        tableMap = new HashMap<>();
        Table table111 = new Table("jdbc:mysql://101.200.215.126:3306/t_user_1?user=root&password=hk123456&useUnicode=true&characterEncoding=UTF8&useSSL=false",
                "student","E:\\大三上\\集成\\mvc-test\\MVC-test\\WebContent\\mapper\\StudentMapper_1.xml"
        );
        tableMap.put("111",table111);

        Table table222 = new Table("jdbc:mysql://101.200.215.126:3306/t_user_2?user=root&password=hk123456&useUnicode=true&characterEncoding=UTF8&useSSL=false",
                "student_1","E:\\大三上\\集成\\mvc-test\\MVC-test\\WebContent\\mapper\\StudentMapper_2.xml"
        );
        tableMap.put("222",table222);
    }



    public static String characterEncoding = "UTF-8";
}
