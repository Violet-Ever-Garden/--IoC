package com.hk.dbservlet;

import com.hk.container.sterotype.Autowired;
import com.hk.container.sterotype.Component;
import com.hk.dao.StudentMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
@Component
public class DoServlet {

    @Autowired
    StudentMapper studentMapper;

    public void doPost(HttpServletRequest request, HttpServletResponse response)

            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        String methodName=request.getParameter("methodName");
        int method=Integer.parseInt(methodName);
        try {
            switch(method)
            {
                case 0:
                    studentMapper.insert(request,response);
                case 1:
                    studentMapper.difpage(request,response);
                    break;
                case 2:
                    studentMapper.delete(request,response);
                    break;
                case 3:
                    studentMapper.update(request,response);
                    break;
                case 4:
                    studentMapper.update1(request,response);
                    break;
                case 5:
                    studentMapper.dispatch(request,response);
                    break;
            }
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
