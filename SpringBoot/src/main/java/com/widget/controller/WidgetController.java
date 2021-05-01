package com.widget.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class WidgetController {

	@RequestMapping("/DynamicWidget")
	public String Widget()
	{
		System.out.println("Reached Here");
		return "welcome.jsp";
	}
}
