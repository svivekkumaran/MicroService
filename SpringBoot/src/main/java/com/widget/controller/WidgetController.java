package com.widget.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.widget.model.WidgetModel;

@Controller
public class WidgetController {

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
