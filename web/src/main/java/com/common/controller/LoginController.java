package com.common.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.common.model.PersonForm;

@Controller
@RequestMapping("/login")
public class LoginController {

	@RequestMapping(method={RequestMethod.GET, RequestMethod.POST})
	public String loginView(HttpServletRequest request,HttpServletResponse response){
//		request.setAttribute("person", new PersonForm());
//		System.out.println("LoginView called");
		return "login";
	}
}
