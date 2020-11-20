package com.hk.dao;

import bean.StudentInfo;
import bean.Page;
import com.hk.connecter.Connector;
import com.hk.constant.Configure;
import bean.Table;
import com.hk.container.sterotype.Autowired;
import com.hk.container.sterotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class StudentMapper {

    @Autowired
    Connector connector;

    //插入方法
    public void insert(HttpServletRequest request, HttpServletResponse response) throws ClassNotFoundException, SQLException {
        Connection conn=null;
        Statement stat=null;
        String id=request.getParameter("id");

        String name=request.getParameter("name");
        String age=request.getParameter("age");
        String gender=request.getParameter("gender");
        String major=request.getParameter("major");
        conn=connector.connect(id);
        stat=conn.createStatement();
        //获取参数列表
        List<String> list = connector.getTable(id).getList();
        System.out.println("start");
        stat.execute("insert into student("+list.get(0)+","+list.get(1)+","+list.get(2)+","+list.get(3)+","+list.get(4)+") values("+id+",'"+name+"',"+age+",'"+gender+"','"+major+"')");
        System.out.println("end");
        connector.close(stat,conn);
        System.out.println("close");
    }
    //查询方法
    public ArrayList<StudentInfo> select(String id, String name) throws ClassNotFoundException, SQLException{
        Connection conn=null;
        Statement stat=null;
        ResultSet rs=null;
        ArrayList<StudentInfo> result = new ArrayList<>();
        System.out.println("id===>"+ (id==null?"null":id));
        if("".equals(id)||id==null){
            for(Map.Entry<String, Table> entry: Configure.tableMap.entrySet()){
                String key = entry.getKey();
                conn=connector.connect(key);
                stat=conn.createStatement();
                //获取table
                Table table = Configure.tableMap.get(key);
                if("".equals(name)){
                    rs=stat.executeQuery("select * from "+table.getTableName());
                }else {
                    rs=stat.executeQuery("select * from "+table.getTableName()+ "where "+table.getList().get(1)+"='"+name+"'");
                }
                getResult(rs,result,table.getList());
                connector.close(stat,conn);
            }
            return result;
        }else {
            conn=connector.connect(id);
            stat=conn.createStatement();
            //获取table
            Table table = connector.getTable(id);
            if("".equals(name)|| name == null){
                rs=stat.executeQuery("select * from "+table.getTableName()+" where "+table.getList().get(0)+"="+id);
            }else {
                rs=stat.executeQuery("select * from "+table.getTableName()+" where "+table.getList().get(0)+"="+id+" and "+table.getList().get(1)+"='"+name+"'");
            }
            getResult(rs,result,table.getList());
            connector.close(stat,conn);
            return result;
        }

    }

    public ArrayList<StudentInfo> getResult(ResultSet rs,ArrayList<StudentInfo> result,List<String> list)throws ClassNotFoundException, SQLException{
        list.forEach(System.out::println);
        while(rs.next())
        {
            StudentInfo st=new StudentInfo();
            st.setId(rs.getInt(list.get(0)));
            st.setName(rs.getString(list.get(1)));
            st.setAge(rs.getInt(list.get(2)));
            st.setGender(rs.getString(list.get(3)));
            st.setMajor(rs.getString(list.get(4)));
            result.add(st);
        }
        if(rs!=null){
            rs.close();
        }
        return result;
    }


    public void delete(HttpServletRequest request, HttpServletResponse response) throws ClassNotFoundException, SQLException, ServletException, IOException {
        Connection conn=null;
        Statement stat=null;
        String id2=request.getParameter("id");
        conn=connector.connect(id2);
        Table table = connector.getTable(id2);
        stat=conn.createStatement();
        stat.execute("delete from "+table.getTableName()+" where "+table.getList().get(0)+"="+id2+"");
        request.getRequestDispatcher("delete.jsp").forward(request, response);
    }
    //信息修改方法
    public void update1(HttpServletRequest request, HttpServletResponse response) throws ClassNotFoundException, SQLException, ServletException, IOException{
        String id4=request.getParameter("id");
        request.setAttribute("result", select(id4,""));
        request.getRequestDispatcher("update1.jsp").forward(request, response);
    }
    public void update(HttpServletRequest request, HttpServletResponse response) throws ClassNotFoundException, SQLException, ServletException, IOException{
        Connection conn=null;
        Statement stat=null;
        String id3=request.getParameter("id");
        String name3=request.getParameter("name");
        String age3=request.getParameter("age");
        String gender3=request.getParameter("gender");
        String major3=request.getParameter("major");
        conn=connector.connect(id3);
        stat=conn.createStatement();
        Table table = connector.getTable(id3);
        stat.execute("update "+table.getTableName()+" set "+table.getList().get(0)+"="+id3+","
                +table.getList().get(1)+"='"+name3+"',"
                +table.getList().get(2)+"="+age3+","
                +table.getList().get(3)+"='"+gender3+"'," +
                ""+table.getList().get(4)+"='"+major3+"' where "+table.getList().get(0)+"="+id3+"");
        request.setAttribute("result", select(id3,""));
        request.getRequestDispatcher("update.jsp").forward(request, response);

    }


    //条件查询跳转
    public void dispatch(HttpServletRequest request, HttpServletResponse response) throws ClassNotFoundException, SQLException, ServletException, IOException{
        String id5=request.getParameter("id");
        String name5=request.getParameter("name");
        if(select(id5,name5).isEmpty()){
            request.getRequestDispatcher("selectnothing.jsp").forward(request, response);
        }
        else{
            request.setAttribute("result", select(id5,name5));
            request.getRequestDispatcher("idnameselect.jsp").forward(request, response);
        }
    }
    //设置分页相关参数方法
    public Page setpage(HttpServletRequest request, HttpServletResponse response) throws ClassNotFoundException, SQLException{
        String crd=request.getParameter("currentRecord");
        //String id=request.getParameter("id");
        //  String name=request.getParameter("name");
        ArrayList<StudentInfo> result=select("","");
        Page pager=new Page();
        pager.setTotalRecord(result.size());
        pager.setTotalPage(result.size(),pager.getPageSize());
        if(crd!=null)
        {
            int currentRecord=Integer.parseInt(crd);
            pager.setCurrentRecord(currentRecord);
            pager.setCurrentPage(currentRecord,pager.getPageSize());
        }
        return pager;
    }
    //获得分页显示的子集
    public void difpage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ClassNotFoundException, SQLException{
        // String id=request.getParameter("id");
        //    String name=request.getParameter("name");
        ArrayList<StudentInfo> result=select("","");
        Page pager=new Page();
        pager=setpage(request,response);
        List<StudentInfo> subResult=null;
        int currentRecord=pager.getCurrentRecord();
        if(currentRecord==0){
            if(pager.getTotalRecord()<8){
                subResult=(List<StudentInfo>) result.subList(0,pager.getTotalRecord());
            }
            else{
                subResult=(List<StudentInfo>) result.subList(0,pager.getPageSize());
            }
        }
        else if(pager.getCurrentRecord()+pager.getPageSize()<result.size())
        {
            subResult=(List<StudentInfo>) result.subList(pager.getCurrentRecord(),pager.getCurrentRecord()+pager.getPageSize());
        }
        else
        {
            subResult=(List<StudentInfo>) result.subList(pager.getCurrentRecord(),result.size());
        }
        request.setAttribute("pager", pager);
        request.setAttribute("subResult", subResult);
        request.getRequestDispatcher("layout.jsp").forward(request, response);
    }
}
