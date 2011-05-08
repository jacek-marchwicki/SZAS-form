package com.szas.server;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class QuestionnairesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final Logger log =
		Logger.getLogger(QuestionnairesServlet.class.getName());

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		UserService userService = UserServiceFactory.getUserService();
        String thisURL = req.getRequestURI();
        StringBuilder urlBuilder = new StringBuilder(thisURL);
        urlBuilder.append("?");
        @SuppressWarnings("unchecked")
		Map<String,String[]> parameterMap = req.getParameterMap();
        for (String key : parameterMap.keySet()) {
        	String[] values = parameterMap.get(key);
        	for (String value : values) {
	        	urlBuilder.append(URLEncoder.encode(key,"UTF-8"));
	        	urlBuilder.append("=");
	        	urlBuilder.append(URLEncoder.encode(value,"UTF-8"));
	        	urlBuilder.append("&");
        	}
        }
        String url = urlBuilder.toString();
        log.info("Url: " + urlBuilder.toString());
		if (req.getUserPrincipal() != null) {
			User user = userService.getCurrentUser();
			req.setAttribute("email", user.getEmail());
			//req.setAttribute("logoutUrl", userService.createLogoutURL(url));
			req.setAttribute("logoutUrl", userService.createLogoutURL("/"));
			RequestDispatcher dispatcher =
				req.getRequestDispatcher("/WEB-INF/jsp/Questionnaires.jsp");
			dispatcher.forward(req, resp);
		} else {
			String loginURL = userService.createLoginURL(url);
			resp.sendRedirect(loginURL);
		}
		
	}
}
