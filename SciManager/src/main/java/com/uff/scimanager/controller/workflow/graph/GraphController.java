package com.uff.scimanager.controller.workflow.graph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Throwables;
import com.uff.scimanager.component.ResponseMessage;
import com.uff.scimanager.domain.WorkflowGraph;
import com.uff.scimanager.service.workflow.WorkflowService;
import com.uff.scimanager.util.ResponseMessageUtil;

@Controller
@RequestMapping("/workflow/graph")
public class GraphController {
	
	private static final Logger log = LoggerFactory.getLogger(GraphController.class);
	
    private final WorkflowService workflowService;
	
	@Autowired
	public GraphController(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}
	
	@PreAuthorize("@workflowService.canAccessWorkflow(principal, #workflowId)")
	@RequestMapping(value = "/{workflowId}/detailed", method = RequestMethod.GET)
    public String getDetailedWorkflowGraph(@PathVariable Long workflowId, Model model) {
    	log.info("Carregando página de grafo detalhado do workflow de id {}", workflowId);
    	
    	model.addAttribute("workflowDTO", workflowService.getWorkflowDTOById(workflowId)); 
    	model.addAttribute("workflowGraphCode", workflowService.getDetailedWorkflowGraphCode(workflowId));
    	
    	return "fragments/graph/detailed-graph-fragment";
    }
	
	@PreAuthorize("@workflowService.canAccessWorkflow(principal, #workflowId)")
	@RequestMapping(value = "/{workflowId}/macro", method = RequestMethod.GET)
    public String getMacroWorkflowGraph(@PathVariable Long workflowId, Model model) {
    	log.info("Carregando página de grafo detalhado do workflow de id {}", workflowId);
    	
    	model.addAttribute("workflowDTO", workflowService.getWorkflowDTOById(workflowId)); 
    	model.addAttribute("workflowGraphCode", workflowService.getMacroWorkflowGraphCode(workflowId));
    	
    	return "fragments/graph/macro-graph-fragment";
    }
    
	@PreAuthorize("@workflowService.canAccessWorkflow(principal, #workflowId)")
    @RequestMapping(value = "/save-detailed-workflow-graph", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMessage saveDetailedGraphDocumentation(@RequestParam(value="workflowId", required = true) Long workflowId,
									     		     	  @RequestParam(value="graphCode", required = true) String graphCode) {
    	
    	log.info("salvando codigo do grafo do workflow de id {}", workflowId);
        WorkflowGraph WorkflowGraph = null;
        
		try {
			WorkflowGraph = workflowService.saveDetailedWorkflowGraph(workflowId, graphCode);
		} 
		catch (Exception e) {
        	log.error("Erro ao salvar grafo do workflow de id {}\n{}", workflowId, Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado, não foi possível salvar o grafo do workflow.").build();
    	}
        
        return ResponseMessageUtil.handleResponseMessage(WorkflowGraph, "Grafo do workflow salva com sucesso.", "Erro inesperado ao salvar grafo do workflow.");
    }
    
	@PreAuthorize("@workflowService.canAccessWorkflow(principal, #workflowId)")
    @RequestMapping(value = "/save-macro-workflow-graph", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMessage saveMacroGraphDocumentation(@RequestParam(value="workflowId", required = true) Long workflowId,
									     		       @RequestParam(value="graphCode", required = true) String graphCode) {
    	
    	log.info("salvando codigo do grafo do workflow de id {}", workflowId);
        WorkflowGraph WorkflowGraph = null;
        
		try {
			WorkflowGraph = workflowService.saveMacroWorkflowGraph(workflowId, graphCode);
		} 
		catch (Exception e) {
        	log.error("Erro ao salvar grafo do workflow de id {}\n{}", workflowId, Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado, não foi possível salvar o grafo do workflow.").build();
    	}
        
        return ResponseMessageUtil.handleResponseMessage(WorkflowGraph, "Grafo do workflow salva com sucesso.", "Erro inesperado ao salvar grafo do workflow.");
    }
    
}