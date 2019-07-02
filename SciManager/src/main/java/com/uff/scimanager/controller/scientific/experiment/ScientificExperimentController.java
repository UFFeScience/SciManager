package com.uff.scimanager.controller.scientific.experiment;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Throwables;
import com.uff.scimanager.component.PaginationInfo;
import com.uff.scimanager.component.ResponseMessage;
import com.uff.scimanager.domain.Documentation;
import com.uff.scimanager.domain.Role;
import com.uff.scimanager.domain.dto.ScientificExperimentDTO;
import com.uff.scimanager.domain.dto.filter.FilterDTO;
import com.uff.scimanager.domain.dto.filter.FilterFieldDTO;
import com.uff.scimanager.domain.form.ScientificExperimentForm;
import com.uff.scimanager.domain.form.WorkflowForm;
import com.uff.scimanager.domain.form.WorkflowFormFilter;
import com.uff.scimanager.domain.form.validator.ScientificExperimentFormValidator;
import com.uff.scimanager.domain.security.CurrentUser;
import com.uff.scimanager.exception.ExistingEntityException;
import com.uff.scimanager.service.scientific.experiment.ScientificExperimentService;
import com.uff.scimanager.service.workflow.WorkflowService;
import com.uff.scimanager.util.ResponseMessageUtil;

@Controller
@RequestMapping("/scientific-experiment")
public class ScientificExperimentController {
	
	private static final Logger log = LoggerFactory.getLogger(ScientificExperimentController.class);
	
    private final ScientificExperimentService scientificExperimentService;
    private final WorkflowService workflowService;
	private final ScientificExperimentFormValidator scientificExperimentFormValidator;
	
	@Autowired
	public ScientificExperimentController(ScientificExperimentService scientificExperimentService, WorkflowService workflowService, 
										  ScientificExperimentFormValidator scientificExperimentFormValidator) {
		
		this.scientificExperimentService = scientificExperimentService;
		this.workflowService = workflowService;
		this.scientificExperimentFormValidator = scientificExperimentFormValidator;
	}
	
	@InitBinder("scientificExperimentForm")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(scientificExperimentFormValidator);
    }
	
	@PreAuthorize("@scientificExperimentService.canAccessScientificExperiment(principal, #scientificExperimentId)")
    @RequestMapping(value = "/{scientificExperimentId}/experiment-details", method = RequestMethod.GET)
	public String getExperimentDetails(@PathVariable Long scientificExperimentId, Model model, Authentication authentication) {
    	log.info("Carregando página de detalhes de experimento científico do id {}", scientificExperimentId);
    	
    	CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    	ScientificExperimentDTO scientificExperimentDTO = scientificExperimentService.getScientificExperimentById(scientificExperimentId);
    	scientificExperimentService.checkPermissionForScientificExperiment(currentUser != null ? currentUser.getUserId() : null, scientificExperimentDTO);
    	
    	model.addAttribute("scientificExperimentDTO", scientificExperimentDTO); 
    	
    	model.addAttribute("searchDashboardTabUrl", "/scientific-experiment/" + scientificExperimentId + "/dashboard");
    	model.addAttribute("searchWorkflowsTabUrl", "/workflow/" + scientificExperimentDTO.getScientificProject().getScientificProjectId() + "/" + scientificExperimentId + "/workflow-list");
    	model.addAttribute("searchDocumentationTabUrl", "/scientific-experiment/" + scientificExperimentId + "/documentation");
    	
    	return "scientific-experiment/experiment-details"; 
    }
	
	@PreAuthorize("@scientificExperimentService.canAccessScientificExperiment(principal, #scientificExperimentId)")
    @RequestMapping(value = "/{scientificExperimentId}/experiment-details-data", method = RequestMethod.GET)
	public String getExperimentDetailsData(@PathVariable Long scientificExperimentId, Model model, Authentication authentication) {
    	log.info("Carregando dados de detalhes de experimento científico do id {}", scientificExperimentId);
    	
    	CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    	ScientificExperimentDTO scientificExperimentDTO = scientificExperimentService.getScientificExperimentById(scientificExperimentId);
    	scientificExperimentService.checkPermissionForScientificExperiment(currentUser != null ? currentUser.getUserId() : null, scientificExperimentDTO);
    	
    	model.addAttribute("scientificExperimentDTO", scientificExperimentDTO); 
    	model.addAttribute("totalWorkflows", workflowService.countAllWorkflows(WorkflowFormFilter.builder().scientificExperimentId(scientificExperimentId).build()));
    	model.addAttribute("searchExperimentDetailsUrl", "/scientific-experiment/" + scientificExperimentId + "/experiment-details-data");

    	return "fragments/scientific-experiment/experiment-details-fragment";
    }

	@PreAuthorize("@scientificExperimentService.canAccessScientificExperiment(principal, #scientificExperimentId)")
    @RequestMapping(value = "/{scientificExperimentId}/dashboard", method = RequestMethod.GET)
	public String getExperimentDashboard(@PathVariable Long scientificExperimentId, Model model, Authentication authentication) {
    	log.info("Carregando página de dashboard de experimento científico do id {}", scientificExperimentId);
    	
    	CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    	ScientificExperimentDTO scientificExperimentDTO = scientificExperimentService.getScientificExperimentById(scientificExperimentId);
    	scientificExperimentService.checkPermissionForScientificExperiment(currentUser != null ? currentUser.getUserId() : null, scientificExperimentDTO);
    	
    	model.addAttribute("scientificExperimentDTO", scientificExperimentDTO); 
    	WorkflowForm workflowForm = WorkflowForm.buildWorkflowFormWithDefaultSwfms();
 		workflowForm.setScientificProjectId(scientificExperimentDTO.getScientificProject().getScientificProjectId());
 		workflowForm.setScientificExperimentId(scientificExperimentId);
 		model.addAttribute("workflowForm", workflowForm);
 		
    	model.addAttribute("searchExperimentDetailsUrl", "/scientific-experiment/" + scientificExperimentId + "/experiment-details-data");
		model.addAttribute("searchWorkflowsListUrl", "/workflow/" + scientificExperimentDTO.getScientificProject().getScientificProjectId() + "/" + scientificExperimentId + "/dashboard/workflows");
    	
    	return "fragments/scientific-experiment/experiment-dashboard-fragment";
    }
	
	@RequestMapping(value = "/{scientificExperimentId}/documentation", method = RequestMethod.GET)
    public String getScientificExperiementDocumentation(@PathVariable Long scientificExperimentId, Model model, Authentication authentication) {
    	log.info("Carregando página de documentação de experimento científico do id {}", scientificExperimentId);
    	
    	CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    	ScientificExperimentDTO scientificExperimentDTO = scientificExperimentService.getScientificExperimentById(scientificExperimentId);
    	scientificExperimentService.checkPermissionForScientificExperiment(currentUser != null ? currentUser.getUserId() : null, scientificExperimentDTO);
    	
    	model.addAttribute("scientificExperimentDTO", scientificExperimentDTO); 
    	model.addAttribute("experimentDocumentationHtml", scientificExperimentService.getExperimentDocumentationHtml(scientificExperimentId));
    	
    	return "fragments/scientific-experiment/experiment-documentation-fragment";
    }
    
	@PreAuthorize("@scientificExperimentService.canAccessScientificExperiment(principal, #scientificExperimentId)")
    @RequestMapping(value = "/save-experiment-documentation", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMessage saveExperimentDocumentation(@RequestParam(value="scientificExperimentId", required = true) Long scientificExperimentId,
								     		     	   @RequestParam(value="htmlDocumentation", required = true) String htmlDocumentation) {
    	
    	log.info("salvando documentação html do experimento científico de id {}", scientificExperimentId);
        Documentation documentation = null;
        
		try {
			documentation = scientificExperimentService.saveExperimentDocumentation(scientificExperimentId, htmlDocumentation);
		} 
		catch (Exception e) {
        	log.error("Erro ao salvar documentação de experimento científico de id {}\n{}", scientificExperimentId, Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado, não foi possível salvar documentação do experimento científico.").build();
    	}
        
        return ResponseMessageUtil.handleResponseMessage(documentation, "Documentação do experimento científico salva com sucesso.", "Erro inesperado ao salvar documentação do experimento científico.");
    }
	
	@RequestMapping(value = "/{scientificProjectId}/dashboard/scientific-experiments", method = RequestMethod.GET)
    public String getDashboardScientificExperiments(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
												  	@PathVariable Long scientificProjectId,
												  	Authentication authentication,
												  	Model model) {
		
		log.info("Carregando página de listagem de experimentos científicos de projeto científico de id{}", scientificProjectId);
		
		CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
		Long userId = currentUser != null ? currentUser.getUserId() : null;
		
        List<ScientificExperimentDTO> scientificExperimentDTOs = scientificExperimentService.getAllDashboardScientificExperiments(pageNumber, scientificProjectId, userId);
		scientificExperimentService.checkPermissionsForScientificExperiments(scientificExperimentDTOs, userId);
		
		model.addAttribute("scientificProjectId", scientificProjectId);
		model.addAttribute("scientificExperimentForm", ScientificExperimentForm.buildEmptyScientificExperimentForm());
		model.addAttribute("scientificExperimentDTOs", scientificExperimentDTOs);
		model.addAttribute("searchExperimentsListUrl", "/scientific-experiment/" + scientificProjectId + "/dashboard/scientific-experiments");
		model.addAttribute("scientificExperimentsTotal", scientificExperimentService.countAllScientificExperiments(null, scientificProjectId));
		
        return "fragments/scientific-experiment/project-experiment-list-fragment";
    }

	@RequestMapping(value = "/{scientificProjectId}/scientific-experiments-list", method = RequestMethod.GET)
    public String getAllScientificExperimentsList(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
											   	  @RequestParam(value = "search", required = false) String queryString,
											   	  @RequestParam(value = "myOwn", required = false) Boolean myOwn,
											   	  @PathVariable Long scientificProjectId,
											   	  Authentication authentication,
											   	  Model model) {
		
		log.info("Carregando página de listagem de experimentos científicos");
		
		CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
		Long userId = currentUser != null && !currentUser.getUserRole().equals(Role.ADMIN) ? currentUser.getUserId() : null;
		
        List<ScientificExperimentDTO> scientificExperimentDTOs = scientificExperimentService.getAllScientificExperiments(pageNumber, queryString, scientificProjectId, 
				   Boolean.TRUE.equals(myOwn) ? userId : null);
		scientificExperimentService.checkPermissionsForScientificExperiments(scientificExperimentDTOs, userId);
		
		model.addAttribute("scientificProjectId", scientificProjectId);
		model.addAttribute("scientificExperimentForm", ScientificExperimentForm.buildEmptyScientificExperimentForm());
		model.addAttribute("scientificExperimentDTOs", scientificExperimentDTOs);
		
		PaginationInfo pagination = PaginationInfo.builder()
												  .totalEntities(scientificExperimentService.countAllScientificExperiments(queryString, scientificProjectId,
														  Boolean.TRUE.equals(myOwn) ? userId : null))
												  .actualPageNumber(pageNumber)
												  .pageSize(scientificExperimentService.getPageSize())
												  .baseUrlLink("/scientific-experiment/scientific-experiment-list").build();
		
		pagination.addUrlParameter("search", queryString);
		pagination.addUrlParameter("myOwn", myOwn != null ? myOwn.toString() : null);
		
		FilterDTO filter = FilterDTO.builder().build();
		filter.addFilterField(FilterFieldDTO.builder().name("search").isTextField(Boolean.TRUE).value(queryString).label("busca textual").build())
			  .addFilterField(FilterFieldDTO.builder().name("myOwn").value(myOwn).build());
		
		model.addAttribute("filter", filter);
		model.addAttribute("pagination", pagination);
		model.addAttribute("searchListUrl", pagination.getBaseUrlLink());
        
        return "fragments/scientific-experiment/scientific-experiment-list-fragment";
    }
    
    @RequestMapping(value = "/create-scientific-experiment", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMessage handleScientificExperimentCreateForm(@Valid @ModelAttribute("scientificExperimentForm") ScientificExperimentForm scientificExperimentForm, BindingResult bindingResult,
    								   	   			   			HttpServletRequest request) {
    	
        log.info("Processando scientificExperimentForm  {}, bindingResult  {}", scientificExperimentForm, bindingResult);
        try {
        	ScientificExperimentDTO scientificExperimentDTO = null;
	        
	        if (bindingResult.hasErrors()) {
	        	log.error("Erro de validação ao criar experimento científico \n" + ResponseMessageUtil.formatErrorMessage(bindingResult));
	        	return ResponseMessage.buildErrorMessage(ResponseMessageUtil.formatErrorMessage(bindingResult));
	        }
	        
	        scientificExperimentDTO = scientificExperimentService.createScientificExperiment(scientificExperimentForm);
	        return ResponseMessageUtil.handleResponseMessage(scientificExperimentDTO, "Experimento científico criado com sucesso.", "Erro inesperado ao criar experimento científico.");

        }
        catch(Exception e) {
        	log.error("Erro ao criar experimento cientifico\n{}", Throwables.getStackTraceAsString(e));
	        return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado ao criar experimento científico.").build();
        }
    }
    
    @PreAuthorize("@scientificExperimentService.canAccessScientificExperiment(principal, #scientificExperimentId)")
    @RequestMapping(value = "/edit-scientific-experiment", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMessage editScientificExperiment(@RequestParam(value="scientificExperimentId", required = true) Long scientificExperimentId,
			    									@RequestParam(value="experimentName", required = true) String experimentName) {
    	
    	log.info("Editando experimento científico de id {} com novo experimentName {}", scientificExperimentId, experimentName);
    	ScientificExperimentDTO scientificExperimentDTO = null;
        
		try {
			scientificExperimentDTO = scientificExperimentService.editScientificExperiment(scientificExperimentId, experimentName);
		} 
		catch (ExistingEntityException e) {
        	log.error("Erro ao editar experimento científico, o nome {} já é usado por outro experimento científico\n{}", experimentName, Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
        						  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
        						  .text("Já existe outro experimento científico com o nome " + experimentName + ".").build();
        } 
		catch (Exception e) {
        	log.error("Erro ao editar experimento científico de id {} novo experimentname {}\n{}", 
        			   scientificExperimentId, experimentName, Throwables.getStackTraceAsString(e));
        	
        	return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado, não foi possível editar o workflow.").build();
    	}
        
        return ResponseMessageUtil.handleResponseMessage(scientificExperimentDTO, "Experimento científico editado com sucesso.", "Erro inesperado ao editar experimento científico.");
    }
    
    @PreAuthorize("@scientificExperimentService.canAccessScientificExperiment(principal, #scientificExperimentId)")
    @RequestMapping(value = "/remove-scientific-experiment", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMessage handleRemoveScientificExperiment(@RequestParam(value = "scientificExperimentId", required = true) String scientificExperimentId,
							           			            HttpServletRequest request) {
		
    	log.info("Removendo experimento de id {}", scientificExperimentId);
    	
    	try {
    		Long scientificExperimentIdValue = Long.valueOf(scientificExperimentId);
    		scientificExperimentService.removeScientificExperiment(scientificExperimentIdValue);
    	}
    	catch (Exception e) {
    		log.error("Erro ao excluir experimento científico de id {}\n{}", scientificExperimentId, Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado ao excluir experimento científico.").build();
    	}
    	
    	return ResponseMessage.builder()
							  .type(ResponseMessage.SUCCESS_MESSAGE_LABEL)
							  .text("Experimento científico excluído com sucesso.").build();
	}
    
    @PreAuthorize("@scientificExperimentService.canAccessScientificExperiment(principal, #scientificExperimentId)")
    @RequestMapping(value = "/count-dependencies/{scientificExperimentId}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Integer> countDependencies(@PathVariable Long scientificExperimentId) {
        log.info("Contando dependências do experimento científico de id {}", scientificExperimentId);
    	return scientificExperimentService.countDependencies(scientificExperimentId);
    }
    
}