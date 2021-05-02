package com.widget.controller;

import java.net.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WidgetController {

	@RequestMapping("/DynamicWidget")
	public ModelAndView Widget(String Sname)
	{
		System.out.println("Reached Here");
		ModelAndView mv=new ModelAndView();
		System.out.println("Serviice name "+Sname);
		mv.setViewName("welcome");
		mv.addObject("Servicename", Sname);
		return mv;
	}
}
