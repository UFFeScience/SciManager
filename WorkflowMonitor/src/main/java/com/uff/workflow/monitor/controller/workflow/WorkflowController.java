package com.uff.workflow.monitor.controller.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Throwables;
import com.uff.workflow.monitor.service.workflow.WorkflowService;

@Controller
@RequestMapping("/workflow")
public class WorkflowController {
	
	private static final Logger log = LoggerFactory.getLogger(WorkflowController.class);
	
    private final WorkflowService workflowService;
	
	@Autowired
	public WorkflowController(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}
	
	@RequestMapping(value = "/update-execution-status", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<String> updateWorkflowExecutionStatus() {
    	log.info("Atualizando status de execuções de workflows.");
    	try {
    		workflowService.updateWorkflowEecutions();
    		return ResponseEntity.ok("Execuções atualizadas com sucesso."); 
    	}
    	catch (Exception e) {
    		log.error("Erro ao atualizar status de execuções\n{}.", Throwables.getStackTraceAsString(e));
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar execuções de workflows.");
    	}
    	
    }
	
}