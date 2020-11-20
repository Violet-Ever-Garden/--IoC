package com.hk.dbservlet;

import java.io.IOException;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.hk.container.context.ClassPathXmlApplicationContext;


public class AllServlet extends HttpServlet{
	/**
	 * 
	 */

	ClassPathXmlApplicationContext container = new ClassPathXmlApplicationContext("bean.xml");



	private static final long serialVersionUID = 1L;



	 //doPost方法
	 @Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 DoServlet servlet = (DoServlet)container.getBean("doServlet");
		 servlet.doPost(request,response);
	 }
	//doGet方法
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
       doPost(request,response);
    }



}
