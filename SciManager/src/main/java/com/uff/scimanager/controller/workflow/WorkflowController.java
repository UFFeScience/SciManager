package com.uff.scimanager.controller.workflow;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.uff.scimanager.domain.WorkflowExecutionStatus;
import com.uff.scimanager.domain.dto.WorkflowDTO;
import com.uff.scimanager.domain.dto.filter.FilterDTO;
import com.uff.scimanager.domain.dto.filter.FilterFieldDTO;
import com.uff.scimanager.domain.form.WorkflowExecutionFormFilter;
import com.uff.scimanager.domain.form.WorkflowForm;
import com.uff.scimanager.domain.form.WorkflowFormFilter;
import com.uff.scimanager.domain.form.WorkflowFormUpdate;
import com.uff.scimanager.domain.form.validator.WorkflowFormValidator;
import com.uff.scimanager.domain.security.CurrentUser;
import com.uff.scimanager.exception.EntityHasRelationsException;
import com.uff.scimanager.exception.ExistingEntityException;
import com.uff.scimanager.exception.InvalidEntityException;
import com.uff.scimanager.exception.PermissionDeniedException;
import com.uff.scimanager.service.workflow.WorkflowService;
import com.uff.scimanager.util.ResponseMessageUtil;

@Controller
@RequestMapping("/workflow")
public class WorkflowController {
	
	private static final Logger log = LoggerFactory.getLogger(WorkflowController.class);
	
    private final WorkflowService workflowService;
	private final WorkflowFormValidator workflowFormValidator;
	
	@Autowired
	public WorkflowController(WorkflowService workflowService, WorkflowFormValidator workflowFormValidator) {
		this.workflowService = workflowService;
		this.workflowFormValidator = workflowFormValidator;
	}
	
	@InitBinder("workflowForm")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(workflowFormValidator);
    }
	
	@RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String getWorkflowHomeData(Model model, Authentication authentication) {
		log.info("Carregando pagina de dashboard de workflow do usuário");
		
		CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
		Long userId = currentUser != null ? currentUser.getUserId() : null;
		
		List<WorkflowDTO> workflowDTOs = workflowService.getAllWorkflowsOfUser(userId);
		workflowService.checkPermissionsForWorkflows(workflowDTOs, userId);
		
		model.addAttribute("workflowDTOs", workflowDTOs);
        model.addAttribute("workflowsTotal", workflowService.countAllWorkflowsOfUser(userId));
    	
    	return "fragments/workflow/workflows-home-dashboard-fragment";
    }
	
	@PreAuthorize("@workflowService.canAccessWorkflow(principal, #workflowId)")
	@RequestMapping(value = "/{workflowId}/workflow-details", method = RequestMethod.GET)
	public String getWorkflowDetails(@PathVariable Long workflowId, Model model, Authentication authentication) {
    	log.info("Carregando página de detalhes de workflow do id {}", workflowId);
    	
    	CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    	Long userId = currentUser != null ? currentUser.getUserId() : null;
    	WorkflowDTO workflowDTO = workflowService.getWorkflowDTOById(workflowId);
    	workflowService.checkPermissionsForWorkflow(userId, workflowDTO);
    	
    	model.addAttribute("workflowDTO", workflowDTO); 
    	
    	model.addAttribute("searchDashboardTabUrl", "/workflow/" + workflowId + "/dashboard");
    	model.addAttribute("searchDocumentationTabUrl", "/workflow/" + workflowId + "/documentation");
    	model.addAttribute("searchModelFilesTabUrl", "/workflow/model-file/" + workflowId + "/model-files-list");
    	model.addAttribute("searchDetailedGraphTabUrl", "/workflow/graph/" + workflowId + "/detailed");
    	model.addAttribute("searchMacroGraphTabUrl", "/workflow/graph/" + workflowId + "/macro");
    	model.addAttribute("searchExecutionHistoryTabUrl", "/workflow/execution-history-list/" + workflowId);
    	
    	return "workflow/workflow-details"; 
    }
	
	@PreAuthorize("@workflowService.canAccessWorkflow(principal, #workflowId)")
	@RequestMapping(value = "/{workflowId}/workflow-details-data", method = RequestMethod.GET)
	public String getWorkflowDetailsData(@PathVariable Long workflowId, Model model, Authentication authentication) {
    	log.info("Carregando dados de detalhes de workflow do id {}", workflowId);
    	
    	CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    	WorkflowDTO workflowDTO = workflowService.getWorkflowDTOById(workflowId);
    	workflowService.checkPermissionsForWorkflow(currentUser != null ? currentUser.getUserId() : null, workflowDTO);
    	
    	model.addAttribute("workflowDTO", workflowDTO); 
    	model.addAttribute("totalWorkflowExecutions", 
			workflowService.countAllWorkflowExecutionHistory(WorkflowExecutionFormFilter.builder().workflowId(workflowId).build()));
    	model.addAttribute("searchWorkflowDetailsUrl", "/workflow/" + workflowId + "/workflow-details-data");

    	return "fragments/workflow/workflow-details-fragment";
    }
	
	@PreAuthorize("@workflowService.canAccessWorkflow(principal, #workflowId)")
	@RequestMapping(value = "/{workflowId}/dashboard", method = RequestMethod.GET)
	public String getWorkflowDashboard(@PathVariable Long workflowId, Model model, Authentication authentication) {
    	log.info("Carregando página de dashboard de workflow do id {}", workflowId);
    	
    	CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    	WorkflowDTO workflowDTO = workflowService.getWorkflowDTOById(workflowId);
    	workflowService.checkPermissionsForWorkflow(currentUser != null ? currentUser.getUserId() : null, workflowDTO);
    	
    	model.addAttribute("workflowDTO", workflowDTO); 

    	model.addAttribute("searchWorkflowDetailsUrl", "/workflow/" + workflowId + "/workflow-details-data");
    	model.addAttribute("searchModelFilesListUrl", "/workflow/model-file/" + workflowId + "/dashboard/model-files");
    	model.addAttribute("searchWorkflowExecutionListUrl", "/workflow/" + workflowId + "/dashboard/execution-history");
    	
    	return "fragments/workflow/workflow-dashboard-fragment";
    }
	
	@RequestMapping(value = "/{workflowId}/documentation", method = RequestMethod.GET)
    public String getWorkflowDocumentation(@PathVariable Long workflowId, Model model, Authentication authentication) {
    	log.info("Carregando documentação de workflow do id {}", workflowId);
    	
    	CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    	WorkflowDTO workflowDTO = workflowService.getWorkflowDTOById(workflowId);
    	workflowService.checkPermissionsForWorkflow(currentUser != null ? currentUser.getUserId() : null, workflowDTO);
    	
    	model.addAttribute("workflowDTO", workflowDTO); 
    	model.addAttribute("workflowDocumentationHtml", workflowService.getWorkflowDocumentationHtml(workflowId));
    	
    	return "fragments/workflow/workflow-documentation-fragment";
    }
	
	@RequestMapping(value = "/{scientificProjectId}/{scientificExperimentId}/dashboard/workflows", method = RequestMethod.GET)
    public String getDashboardExperimentWorkflows(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
												  @PathVariable Long scientificProjectId,
												  @PathVariable Long scientificExperimentId,
												  Authentication authentication,
												  Model model) {
		
		log.info("Carregando página de listagem de workflows científicos de experimento científico de id{}", scientificExperimentId);
		
		CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
		Long userId = currentUser != null ? currentUser.getUserId() : null;
		
        List<WorkflowDTO> workflowDTOs = workflowService.getAllDashboardWorkflows(pageNumber, scientificExperimentId, userId);
        workflowService.checkPermissionsForWorkflows(workflowDTOs, userId);
		
        WorkflowForm workflowForm = WorkflowForm.buildWorkflowFormWithDefaultSwfms();
		workflowForm.setScientificProjectId(scientificProjectId);
		workflowForm.setScientificExperimentId(scientificExperimentId);
        
		model.addAttribute("workflowForm", workflowForm);
		model.addAttribute("workflowDTOs", workflowDTOs);
		model.addAttribute("searchWorkflowsListUrl", "/workflow/" + scientificProjectId + "/" + scientificExperimentId + "/dashboard/workflows");
		model.addAttribute("workflowsTotal", workflowService.countAllWorkflows(WorkflowFormFilter.builder().scientificExperimentId(scientificExperimentId).build()));
		
        return "fragments/workflow/experiment-workflow-list-fragment";
    }
	
	@PreAuthorize("@workflowService.canAccessWorkflow(principal, #workflowId)")
	@RequestMapping(value = "/{workflowId}/execution-details-list/{workflowExecutionId}", method = RequestMethod.GET)
    public String getWorkflowExecutionMetadataList(@PathVariable Long workflowId, 
    										   	   @PathVariable Long workflowExecutionId,
    										   	   @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
    										   	   Model model) {
		
    	log.info("Carregando página de dados de dados de proveniencia do workflow do id {} e workflowExecutionId {}", workflowId, workflowExecutionId);
    	
    	Integer totalMetadata = workflowService.countAllWorkflowExecutionMetadata(workflowExecutionId);
    	
    	PaginationInfo pagination = PaginationInfo.builder()
												  .totalEntities(totalMetadata)
												  .actualPageNumber(pageNumber)
												  .pageSize(workflowService.getPageSize())
												  .baseUrlLink("/workflow/" + workflowId + "/execution-details/" + workflowExecutionId).build();
    	
    	model.addAttribute("workflowDTO", workflowService.getWorkflowDTOById(workflowId));
    	model.addAttribute("workflowExecutionId", workflowExecutionId);
    	model.addAttribute("totalMetadata", totalMetadata);
    	model.addAttribute("workflowExecutionsMetadata", workflowService.getWorkflowExecutionMetadata(pageNumber, workflowExecutionId));
    	model.addAttribute("pagination", pagination);
    	model.addAttribute("searchListUrl", pagination.getBaseUrlLink());
    	
    	return "fragments/workflow/workflow-execution-metadata-fragment"; 
    }
	
	@PreAuthorize("@workflowService.canAccessWorkflow(principal, #workflowId)")
	@RequestMapping(value = "/{workflowId}/execution-details/{workflowExecutionId}", method = RequestMethod.GET)
    public String getWorkflowExecutionMetadataPage(@PathVariable Long workflowId, 
    										   	   @PathVariable Long workflowExecutionId,
    										   	   @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
    										   	   Model model) {
		
    	log.info("Carregando página de dados de dados de proveniencia do workflow do id {} e workflowExecutionId {}", workflowId, workflowExecutionId);
    	
    	PaginationInfo pagination = PaginationInfo.builder()
												  .actualPageNumber(pageNumber)
												  .baseUrlLink("/workflow/" + workflowId + "/execution-details-list/" + workflowExecutionId).build();
    	
        model.addAttribute("searchListUrl", pagination.getBaseUrlLink());

        return "workflow/workflow-execution-metadata"; 
    }
	
	@PreAuthorize("@workflowService.canAccessWorkflow(principal, #workflowId)")
	@RequestMapping(value = "/{workflowId}/count-metadata/{workflowExecutionId}", method = RequestMethod.GET)
	@ResponseBody
    public Integer getTotalMetadata(@PathVariable Long workflowId, 
			   						@PathVariable Long workflowExecutionId) {
		
		log.info("Carregando contagem de dados de proveniência do workflow de id {} para a execução de id {}", workflowId, workflowExecutionId);
		return workflowService.countAllWorkflowExecutionMetadata(workflowExecutionId);
	}
	
	@PreAuthorize("@workflowService.canAccessWorkflow(principal, #workflowId)")
	@RequestMapping(value = "/{workflowId}/dashboard/execution-history", method = RequestMethod.GET)
    public String getDashboardExecutionHistoryList(@PathVariable Long workflowId,
    										  	   WorkflowExecutionFormFilter workflowExecutionFormFilter,
    										   	   Authentication authentication,
    										   	   Model model) {
    	
    	log.info("Carregando página de dashboard de histórico de execução de workflow de id {}", workflowId);
    	
    	CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    	Long userId = currentUser != null ? currentUser.getUserId() : null;
    	workflowExecutionFormFilter.setUserId(userId);
    	workflowExecutionFormFilter.setWorkflowId(workflowId);
    	
    	model.addAttribute("workflowDTO", workflowService.getWorkflowById(workflowId));
    	model.addAttribute("workflowExecutionHistory", workflowService.getDashboardWorkflowExecutionHistory(workflowExecutionFormFilter));
    	model.addAttribute("workflowExecutionHistoryTotal", workflowService.countAllWorkflowExecutionHistory(workflowExecutionFormFilter));
    	model.addAttribute("searchWorkflowExecutionListUrl", "/workflow/" + workflowId + "/dashboard/execution-history");
        
        return "fragments/workflow/workflow-execution-history-list-fragment";
    }
	
	@PreAuthorize("@workflowService.canAccessWorkflow(principal, #workflowId)")
	@RequestMapping(value = "/execution-history-list/{workflowId}", method = RequestMethod.GET)
    public String getWorkflowsExecutionHistoryList(@PathVariable Long workflowId,
    										  	   WorkflowExecutionFormFilter workflowExecutionFormFilter,
    										   	   Authentication authentication,
    										   	   Model model) {
    	
    	log.info("Carregando página de listagem de histórico de execução de workflow de id {}", workflowId);
    	
    	CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    	Long userId = currentUser != null ? currentUser.getUserId() : null;
    	workflowExecutionFormFilter.setUserId(userId);
    	workflowExecutionFormFilter.setWorkflowId(workflowId);
    	
    	model.addAttribute("workflowDTO", workflowService.getWorkflowById(workflowId));
    	model.addAttribute("workflowExecutionHistory", workflowService.getWorkflowExecutionHistory(workflowExecutionFormFilter));
        
        PaginationInfo pagination = PaginationInfo.builder()
		        								  .totalEntities(workflowService.countAllWorkflowExecutionHistory(workflowExecutionFormFilter))
		        								  .actualPageNumber(workflowExecutionFormFilter.getPageNumber())
		        								  .pageSize(workflowService.getPageSize())
		        								  .baseUrlLink("/workflow/execution-history-list").build();
        
        pagination.addUrlParameter("initialDate", workflowExecutionFormFilter.getInitialDate());
        pagination.addUrlParameter("finalDate", workflowExecutionFormFilter.getFinalDate());
        pagination.addUrlParameter("executionStatus", (workflowExecutionFormFilter.getExecutionStatus() != null && !"".equals(workflowExecutionFormFilter.getExecutionStatus())) ? 
        						   WorkflowExecutionStatus.getExecutionStatusFromString(workflowExecutionFormFilter.getExecutionStatus()).getExecutionStatusName() : null);
        
        FilterDTO filter = FilterDTO.builder().build();
        filter.addFilterField(FilterFieldDTO.builder().name("search").isTextField(Boolean.TRUE).value(workflowExecutionFormFilter.getQueryString()).label("busca textual").build())
        	  .addFilterField(FilterFieldDTO.builder().name("executionStatus").isTextField(Boolean.TRUE).value((workflowExecutionFormFilter.getExecutionStatus() != null && !"".equals(workflowExecutionFormFilter.getExecutionStatus())) ? 
					   WorkflowExecutionStatus.getExecutionStatusFromString(workflowExecutionFormFilter.getExecutionStatus()).getExecutionStatusName() : null).label("filtro de status").build())
        	  .addFilterField(FilterFieldDTO.builder().name("myOwn").value(workflowExecutionFormFilter.getMyOwn()).build())
        	  .addFilterField(FilterFieldDTO.builder().name("initialDate").value(workflowExecutionFormFilter.getInitialDate()).label("Data inicial").build())
        	  .addFilterField(FilterFieldDTO.builder().name("finalDate").value(workflowExecutionFormFilter.getFinalDate()).label("Data final").build());
        
        model.addAttribute("filter", filter);
        model.addAttribute("pagination", pagination);
        model.addAttribute("searchListUrl", pagination.getBaseUrlLink());
        
        return "fragments/workflow/workflow-execution-history-fragment";
    }
	
	@RequestMapping(value = "/user-execution-history-list", method = RequestMethod.GET)
    public String getWorkflowsUserExecutionHistoryList(WorkflowExecutionFormFilter workflowExecutionFormFilter,
										  	   		   Authentication authentication,
										  	   		   Model model) {
    	
    	log.info("Carregando página de listagem de histórico de execução de workflow do usuário logado");
    	
    	CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    	Long userId = currentUser != null ? currentUser.getUserId() : null;
    	
    	workflowExecutionFormFilter.setUserId(userId);
    	workflowExecutionFormFilter.setMyOwn(Boolean.TRUE);
    	
    	model.addAttribute("workflowExecutionHistory", workflowService.getWorkflowExecutionHistory(workflowExecutionFormFilter));
        
        PaginationInfo pagination = PaginationInfo.builder()
		        								  .totalEntities(workflowService.countAllWorkflowExecutionHistory(workflowExecutionFormFilter))
		        								  .actualPageNumber(workflowExecutionFormFilter.getPageNumber())
		        								  .pageSize(workflowService.getPageSize())
		        								  .baseUrlLink("/workflow/user-execution-history-list").build();
        
        pagination.addUrlParameter("initialDate", workflowExecutionFormFilter.getInitialDate());
        pagination.addUrlParameter("finalDate", workflowExecutionFormFilter.getFinalDate());
        pagination.addUrlParameter("executionStatus", (workflowExecutionFormFilter.getExecutionStatus() != null && !"".equals(workflowExecutionFormFilter.getExecutionStatus())) ? 
        						   WorkflowExecutionStatus.getExecutionStatusFromString(workflowExecutionFormFilter.getExecutionStatus()).getExecutionStatusName() : null);
        
        FilterDTO filter = FilterDTO.builder().build();
        filter.addFilterField(FilterFieldDTO.builder().name("search").isTextField(Boolean.TRUE).value(workflowExecutionFormFilter.getQueryString()).label("busca textual").build())
        .addFilterField(FilterFieldDTO.builder().name("workflowName").isTextField(Boolean.TRUE).value(workflowExecutionFormFilter.getWorkflowName()).label("filtro de workflow").build())
        	  .addFilterField(FilterFieldDTO.builder().name("executionStatus").isTextField(Boolean.TRUE).value((workflowExecutionFormFilter.getExecutionStatus() != null && !"".equals(workflowExecutionFormFilter.getExecutionStatus())) ? 
					   WorkflowExecutionStatus.getExecutionStatusFromString(workflowExecutionFormFilter.getExecutionStatus()).getExecutionStatusName() : null).label("filtro de status").build())
        	  .addFilterField(FilterFieldDTO.builder().name("initialDate").value(workflowExecutionFormFilter.getInitialDate()).label("Data inicial").build())
        	  .addFilterField(FilterFieldDTO.builder().name("finalDate").value(workflowExecutionFormFilter.getFinalDate()).label("Data final").build());
        
        model.addAttribute("filter", filter);
        model.addAttribute("pagination", pagination);
        model.addAttribute("searchListUrl", pagination.getBaseUrlLink());
        
        return "fragments/workflow/workflow-user-execution-history-fragment";
    }
	
	@RequestMapping(value = "/execution-history", method = RequestMethod.GET)
    public String getWorkflowsExecutionHistoryPage(WorkflowExecutionFormFilter workflowExecutionFormFilter,
    										   	   Model model) {
    	
    	log.info("Carregando página de listagem de histórico de execução de usuario logado");
    	
    	FilterDTO filter = FilterDTO.builder().build();
        filter.addFilterField(FilterFieldDTO.builder().name("search").isTextField(Boolean.TRUE).value(workflowExecutionFormFilter.getQueryString()).label("busca textual").build())
        	  .addFilterField(FilterFieldDTO.builder().name("workflowName").isTextField(Boolean.TRUE).value(workflowExecutionFormFilter.getWorkflowName()).label("filtro de workflow").build())
        	  .addFilterField(FilterFieldDTO.builder().name("executionStatus").isTextField(Boolean.TRUE).value((workflowExecutionFormFilter.getExecutionStatus() != null && !"".equals(workflowExecutionFormFilter.getExecutionStatus())) ? 
					   WorkflowExecutionStatus.getExecutionStatusFromString(workflowExecutionFormFilter.getExecutionStatus()).getExecutionStatusName() : null).label("filtro de status").build())
        	  .addFilterField(FilterFieldDTO.builder().name("initialDate").value(workflowExecutionFormFilter.getInitialDate()).label("Data inicial").build())
        	  .addFilterField(FilterFieldDTO.builder().name("finalDate").value(workflowExecutionFormFilter.getFinalDate()).label("Data final").build());
    	
        PaginationInfo pagination = PaginationInfo.builder()
		        								  .actualPageNumber(workflowExecutionFormFilter.getPageNumber())
		        								  .baseUrlLink("/workflow/user-execution-history-list").build();
        
        model.addAttribute("filter", filter);
        model.addAttribute("searchListUrl", pagination.buildParametersFromFilter(filter));
        
        return "workflow/workflow-execution-history";
    }
	
	@RequestMapping(value = "/process-execution", method = RequestMethod.POST)
	@ResponseBody
    public ResponseMessage runWorkflow(@RequestParam(value="taskId", required = true) Long taskId, Authentication authentication) {
		log.info("Iniciando execução de workflow da tarefa de id {}", taskId);
    	try {
    		CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    		Long userId = currentUser != null ? currentUser.getUserId() : null;

    		workflowService.runWorkflowAsynchronous(taskId, userId);
    		return ResponseMessage.builder()
								  .type(ResponseMessage.SUCCESS_MESSAGE_LABEL)
								  .text("Execução do workflow iniciada com sucesso. Você será notificado ao término da operação.").build(); 
    	}
    	catch (ExistingEntityException e) {
    		log.error("Erro encontrado ao mandar mensagem de execução do workflow para o RabbitMQ para a task de id {}, já existe execução para o exectag\n{}", taskId, Throwables.getStackTraceAsString(e));
    		return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Não é possível realizar a execução pois já houve uma execução com o mesmo exectag do arquivo modelo especificado.").build(); 
    	}
    	catch (InvalidEntityException e) {
    		log.error("Erro encontrado ao mandar mensagem de execução do workflow para o RabbitMQ para a task de id {}, arquivo modelo deve obrigatoriamente conter exectag\n{}", taskId, Throwables.getStackTraceAsString(e));
    		return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("O arquivo modelo do workflow deve obrigatoriamente conter um atributo workflow_exectag válido.").build(); 
    	}
		catch (PermissionDeniedException e) {
			log.error("Erro encontrado ao realizar execução de workflow da task de id {}, permissão negada.\n{}", taskId, Throwables.getStackTraceAsString(e));
    		return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado ao iniciar a execução do workflow.").build(); 
		}
    	catch (Exception e) {
    		log.error("Erro encontrado ao mandar mensagem de execução do workflow para o RabbitMQ para a task de id {}\n{}", taskId, Throwables.getStackTraceAsString(e));
    		return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado ao iniciar a execução do workflow.").build(); 
    	}
    	
    }
    
	@PreAuthorize("@workflowService.canAccessWorkflow(principal, #workflowId)")
    @RequestMapping(value = "/save-workflow-documentation", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMessage saveWorkflowDocumentation(@RequestParam(value="workflowId", required = true) Long workflowId,
									     		     @RequestParam(value="htmlDocumentation", required = true) String htmlDocumentation) {
    	
    	log.info("salvando documentação html do workflow de id {}", workflowId);
        Documentation workflowDocumentation = null;
        
		try {
			workflowDocumentation = workflowService.saveWorkflowDocumentation(workflowId, htmlDocumentation);
		} 
		catch (Exception e) {
        	log.error("Erro ao salvar documentação de workflow de id {}\n{}", workflowId, Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado, não foi possível salvar documentação do workflow.").build();
    	}
        
        return ResponseMessageUtil.handleResponseMessage(workflowDocumentation, "Documentação do workflow salva com sucesso.", "Erro inesperado ao salvar documentação do workflow.");
    }
	
    @RequestMapping(value = "/{scientificProjectId}/{scientificExperimentId}/workflow-list", method = RequestMethod.GET)
    public String getWorkflowsList(@PathVariable Long scientificProjectId,
								   @PathVariable Long scientificExperimentId,
								   WorkflowFormFilter workflowFormFilter,
								   Authentication authentication,
								   Model model) {
    	
    	log.info("Carregando página de listagem de workflows");
    	
    	CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    	Long userId = currentUser != null ? currentUser.getUserId() : null;
    	workflowFormFilter.setUserId(userId);
    	workflowFormFilter.setScientificExperimentId(scientificExperimentId);
    	
    	List<WorkflowDTO> workflowDTOs = workflowService.getAllWorkflows(workflowFormFilter);
		workflowService.checkPermissionsForWorkflows(workflowDTOs, userId);
		
		WorkflowForm workflowForm = WorkflowForm.buildWorkflowFormWithDefaultSwfms();
		workflowForm.setScientificProjectId(scientificProjectId);
		workflowForm.setScientificExperimentId(scientificExperimentId);
		
		model.addAttribute("workflowForm", workflowForm);
		model.addAttribute("workflowDTOs", workflowDTOs);
		
		PaginationInfo pagination = PaginationInfo.builder()
												  .totalEntities(workflowService.countAllWorkflows(workflowFormFilter))
												  .actualPageNumber(workflowFormFilter.getPageNumber())
												  .pageSize(workflowService.getPageSize())
												  .baseUrlLink("/workflow/" + scientificProjectId + "/" + scientificExperimentId + "/workflow-list").build();
		
		pagination.addUrlParameter("search", workflowFormFilter.getQueryString());
		pagination.addUrlParameter("responsibleGroupName", workflowFormFilter.getResponsibleGroupName());
		pagination.addUrlParameter("myOwn", workflowFormFilter.getMyOwn() != null ? workflowFormFilter.getMyOwn().toString() : null);
		
		FilterDTO filter = FilterDTO.builder().build();
		filter.addFilterField(FilterFieldDTO.builder().name("search").isTextField(Boolean.TRUE).value(workflowFormFilter.getQueryString()).label("busca textual").build())
			  .addFilterField(FilterFieldDTO.builder().name("responsibleGroupName").isTextField(Boolean.TRUE).value(workflowFormFilter.getResponsibleGroupName()).label("filtro de grupo").build())
			  .addFilterField(FilterFieldDTO.builder().name("myOwn").value(workflowFormFilter.getMyOwn()).build());
		
		model.addAttribute("filter", filter);
		model.addAttribute("pagination", pagination);
		model.addAttribute("searchListUrl", pagination.getBaseUrlLink());
    	
    	return "fragments/workflow/workflow-list-fragment";
    }
    
    @RequestMapping(value = "/create-workflow", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMessage handleWorkflowCreateForm(@Valid @ModelAttribute("workflowForm") WorkflowForm workflowForm, BindingResult bindingResult,
    								   	  	        HttpServletRequest request) {
    	
        log.info("Processando workflowForm  {}, bindingResult  {}", workflowForm, bindingResult);
        
        try {
	        WorkflowDTO workflowDTO = null;
	        
	        if (bindingResult.hasErrors()) {
	        	log.error("Erro de validação ao criar workflow \n" + ResponseMessageUtil.formatErrorMessage(bindingResult));
	        	return ResponseMessage.buildErrorMessage(ResponseMessageUtil.formatErrorMessage(bindingResult));
	        }
	        
	    	workflowDTO = workflowService.createWorkflow(workflowForm);
	        return ResponseMessageUtil.handleResponseMessage(workflowDTO, "Workflow criado com sucesso.", "Erro inesperado ao criar workflow.");
        }
        catch(Exception e) {
        	log.error("Erro ao criar workflow\n{}", Throwables.getStackTraceAsString(e));
	        return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado ao criar workflow.").build();
        }
    }
    
    @PreAuthorize("@workflowService.canAccessWorkflow(principal, #workflowFormUpdate.workflowId)")
    @RequestMapping(value = "/edit-workflow", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMessage editWorkflow(@Valid WorkflowFormUpdate workflowFormUpdate, BindingResult bindingResult) {
    	
    	if (workflowFormUpdate == null || bindingResult.hasErrors()) {
        	log.error("Erro de validação ao editar workflow \n" + ResponseMessageUtil.formatErrorMessage(bindingResult));
        	return ResponseMessage.buildErrorMessage(ResponseMessageUtil.formatErrorMessage(bindingResult));
        }
    	
    	log.info("Editando workflow de id {} com novo workfloName {}, novo swfms {}, com scientificProjectId {}, novo scientificExperimentId {} e novo responsibleGroupName {}", 
    			workflowFormUpdate.getWorkflowId(), workflowFormUpdate.getWorkflowName(), workflowFormUpdate.getSwfms(), workflowFormUpdate.getScientificProjectId(), 
    			workflowFormUpdate.getScientificExperimentId(), workflowFormUpdate.getResponsibleGroupName());
    	
        WorkflowDTO workflowDTO = null;
        
		try {
			workflowDTO = workflowService.editWorkflow(workflowFormUpdate);
		} 
		catch (ExistingEntityException e) {
        	log.error("Erro ao editar workflow, o nome {} já é usado por outro workflow\n{}", workflowFormUpdate.getWorkflowName(), Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
        						  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
        						  .text("Já existe outro workflow com o nome " + workflowFormUpdate.getWorkflowName() + ".").build();
        } 
		catch (EntityHasRelationsException e) {
        	log.error("Erro ao editar projeto científico do workflow, o mesmo já possui tarefas associadas a ele\n{}", Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
        						  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
        						  .text("Não é possível editar o workflow, pois existem tarefas associadas aos dados editados.").build();
        } 
		catch (Exception e) {
        	log.error("Erro ao editar workflow de id {} com novo swfms {}, novo scientificProjectId {} e novo responsibleGroupName {}\n{}", 
        			workflowFormUpdate.getWorkflowId(), workflowFormUpdate.getSwfms(), workflowFormUpdate.getScientificProjectId(), workflowFormUpdate.getResponsibleGroupName(), Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado, não foi possível editar o workflow.").build();
    	}
        
        return ResponseMessageUtil.handleResponseMessage(workflowDTO, "Workflow editado com sucesso.", "Erro inesperado ao editar workflow.");
    }
    
    @PreAuthorize("@workflowService.canAccessWorkflow(principal, #workflowId)")
    @RequestMapping(value = "/edit-workflow-version", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMessage editWorkflowVersion(@RequestParam(value="workflowId", required = true) Long workflowId,
											   @RequestParam(value="version", required = true) String version) {
    	
    	log.info("Editando workflow de id {} com nova versão {}", workflowId, version);
        WorkflowDTO workflowDTO = null;
        
		try {
			workflowDTO = workflowService.editWorkflowVersion(workflowId, version);
		} 
		catch (Exception e) {
        	log.error("Erro ao editar workflow de id {} com nova versão {}\n{}", workflowId, version, Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado, não foi possível editar a versão do workflow.").build();
    	}
        
        return ResponseMessageUtil.handleResponseMessage(workflowDTO, "Versão do workflow editada com sucesso.", "Erro inesperado ao editar versão do workflow.");
    }
    
    @PreAuthorize("@workflowService.canAccessWorkflow(principal, #workflowId)")
    @RequestMapping(value = "/remove-workflow", method = RequestMethod.POST)
    @ResponseBody
	public ResponseMessage handleRemoveWorkflow(@RequestParam(value = "workflowId", required = true) String workflowId,
							           			HttpServletRequest request) {
		
    	log.info("Removendo workflow de id {}", workflowId);
    	
    	try {
    		Long workflowIdValue = Long.valueOf(workflowId);
    		workflowService.removeWorkflow(workflowIdValue);
    	} 
        catch (Exception e) {
        	log.error("Erro ao excluir workflow de id {}\n{}", workflowId, Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado ao excluir workflow.").build();
    	}
    	
    	return ResponseMessage.builder()
							  .type(ResponseMessage.SUCCESS_MESSAGE_LABEL)
							  .text("Workflow excluído com sucesso.").build();
	}
    
    @PreAuthorize("@workflowService.canAccessWorkflow(principal, #workflowId)")
    @RequestMapping(value = "/count-dependencies/{workflowId}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Integer> countDependencies(@PathVariable Long workflowId) {
        log.info("Contando dependências do workflow de id {}", workflowId);
    	return workflowService.countDependencies(workflowId);
    }
    
    @RequestMapping(value = "/api/all-workflows", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<WorkflowDTO> getWorkflowJsonList(@RequestParam(value = "search", required = true) String queryString, 
    		                                    Authentication authentication) {
    	
        log.info("Carregando json de workflows buscados por {}", queryString);
        
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    	Long userId = currentUser != null ? currentUser.getUserId() : null;
    	
        return workflowService.getAllWorkflowsJson(queryString, userId);
    }
    
	@RequestMapping(value = "/api/permission/{workflowId}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseMessage getValidationAccess(@PathVariable Long workflowId, Authentication authentication) {
    	log.info("Carregando informações de acesso de workflow do id {}", workflowId);
    	
    	CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    	WorkflowDTO workflowDTO = workflowService.getWorkflowDTOById(workflowId);
    	workflowService.checkPermissionsForWorkflow(currentUser != null ? currentUser.getUserId() : null, workflowDTO);
    	
    	if (workflowDTO.getIsEditablebyUser()) {
    		return ResponseMessage.builder()
    							  .statusCode(HttpStatus.OK.value())
								  .type(ResponseMessage.SUCCESS_MESSAGE_LABEL)
								  .text("Usuário logado tem acesso ao workflow.").build();
    	}
    	
    	return ResponseMessage.builder()
    						  .statusCode(HttpStatus.FORBIDDEN.value())
							  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
							  .text("Usuário logado não tem acesso ao workflow.").build();
    }
    
	@RequestMapping(value = "/count-executions", method = RequestMethod.GET)
	@ResponseBody
    public Integer getTotalWorkflowExecutions(@RequestParam(value="workflowId", required = false) Long workflowId,
    										  Authentication authentication) {
		
		log.info("Carregando contagem de execuções de workflow para o usuário logado");
		
		if (authentication == null) {
    		log.info("Usuário não logado no sistema.");
    		return 0;
    	}
		
		if (workflowId != null) {
			return workflowService.countAllWorkflowExecutionHistoryByWorkflow(workflowId); 
		}
		
		CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    	Long userId = currentUser != null ? currentUser.getUserId() : null;
		
    	WorkflowExecutionFormFilter workflowExecutionFilterDTO = WorkflowExecutionFormFilter.builder()
																		    			  	.executionStatus(WorkflowExecutionStatus.RUNNING.name())
																		    			  	.myOwn(Boolean.TRUE)
																		    			  	.userId(userId).build();
    			
		return workflowService.countAllWorkflowExecutionHistory(workflowExecutionFilterDTO);
	}
	
}