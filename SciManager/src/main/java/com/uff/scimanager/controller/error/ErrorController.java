package com.uff.scimanager.controller.error;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController {
	
	@RequestMapping(value = "/error/{code}")
	public String error(@PathVariable Integer code) {
		return "error/page-" + code;
	}
	
}