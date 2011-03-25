package com.szas.server;
import java.io.IOException;
import com.szas.data.*;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class SZASServerServlet extends HttpServlet {
	ExampleData exampleData;
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		exampleData = new ExampleData();
		String data = exampleData.read();
		resp.setContentType("text/plain");
		resp.getWriter().println(data);
	}
}
