package com.widget.controller;

import java.net.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class WidgetController {

	@RequestMapping("/DynamicWidget")
	public String Widget(String Sname, HttpSession sess)
	{
		System.out.println("Reached Here");
		System.out.println("Serviice name "+Sname);
		sess.setAttribute("Servicename", Sname);
		return "welcome";
	}
}
