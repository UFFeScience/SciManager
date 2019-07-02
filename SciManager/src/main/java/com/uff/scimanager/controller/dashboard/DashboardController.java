package com.uff.scimanager.controller.dashboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class DashboardController {
	
    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);
    
	@RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String getDashboardPage(Authentication authentication, Model model) {
		log.info("Carregando pagina inicial de dashboard");
		
		model.addAttribute("searchProjectsUrl", "/scientific-project/dashboard");
		model.addAttribute("searchWorkflowsUrl", "/workflow/dashboard");
		model.addAttribute("searchTasksUrl", "/task-board/dashboard");
		
        return "dashboard/dashboard";
    }
	
}