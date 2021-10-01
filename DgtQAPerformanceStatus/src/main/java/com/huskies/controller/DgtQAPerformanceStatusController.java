package com.huskies.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.huskies.model.WidgetModel;

@RestController
public class DgtQAPerformanceStatusController {

	@RequestMapping("/")
	public String ContactUS()
	{
		System.out.println("Reached Welcome");
		return "welcome";
	}
	
	@RequestMapping("/DynamicWidget")
	public ModelAndView Widget(WidgetModel SReq)
	{
		System.out.println("Reached WidgetController");
		ModelAndView mv=new ModelAndView();
		System.out.println("Serviice name "+SReq.getGmsServiceNm());
		mv.addObject("WidgetModel", SReq);
		mv.setViewName("welcome");
		return mv;
	}
	
}
