package com.uff.scimanager.controller.scientific.project;

import java.util.List;

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
import com.uff.scimanager.domain.TaskStatus;
import com.uff.scimanager.domain.dto.ScientificProjectDTO;
import com.uff.scimanager.domain.dto.filter.FilterDTO;
import com.uff.scimanager.domain.dto.filter.FilterFieldDTO;
import com.uff.scimanager.domain.form.PhaseForm;
import com.uff.scimanager.domain.form.ScientificExperimentForm;
import com.uff.scimanager.domain.form.ScientificProjectFrom;
import com.uff.scimanager.domain.form.TaskForm;
import com.uff.scimanager.domain.form.TaskFormFilter;
import com.uff.scimanager.domain.form.WorkflowFormFilter;
import com.uff.scimanager.domain.form.validator.ScientificProjectFormValidator;
import com.uff.scimanager.domain.security.CurrentUser;
import com.uff.scimanager.exception.ExistingEntityException;
import com.uff.scimanager.service.scientific.experiment.ScientificExperimentService;
import com.uff.scimanager.service.scientific.project.ScientificProjectService;
import com.uff.scimanager.service.task.TaskService;
import com.uff.scimanager.service.workflow.WorkflowService;
import com.uff.scimanager.util.ResponseMessageUtil;

@Controller
@RequestMapping("/scientific-project")
public class ScientificProjectController {
	
	private static final Logger log = LoggerFactory.getLogger(ScientificProjectController.class);
	
    private final ScientificProjectService scientificProjectService;
    private final ScientificExperimentService scientificExperimentService;
    private final WorkflowService workflowService;
    private final TaskService taskService;
	private final ScientificProjectFormValidator scientificProjectFormValidator;
	
	@Autowired
	public ScientificProjectController(ScientificProjectService scientificProjectService, ScientificExperimentService scientificExperimentService, 
			WorkflowService workflowService, TaskService taskService, ScientificProjectFormValidator scientificProjectFormValidator) {
		this.scientificProjectService = scientificProjectService;
		this.scientificExperimentService = scientificExperimentService;
		this.workflowService = workflowService;
		this.taskService = taskService;
		this.scientificProjectFormValidator = scientificProjectFormValidator;
	}
	
	@InitBinder("scientificProjectForm")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(scientificProjectFormValidator);
    }
	
	@RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String getScientificProjectHomeData(Model model, Authentication authentication) {
		log.info("Carregando pagina de dashboard de projeto do usuário");
		
		CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
		Long userId = currentUser != null && !currentUser.getUserRole().equals(Role.ADMIN) ? currentUser.getUserId() : null;
		
		model.addAttribute("scientificProjectDTOs", scientificProjectService.getAllUserScientificProjectsBoards(currentUser));
		model.addAttribute("scientificProjectsTotal", scientificProjectService.countAllScientificProjectsBoards(null, userId));
    	
    	return "fragments/scientific-project/projects-home-dashboard-fragment";
    }
	
	@PreAuthorize("@scientificProjectService.canAccessScientificProject(principal, #scientificProjectId) || hasAuthority('ADMIN')")
    @RequestMapping(value = "/{scientificProjectId}/project-details", method = RequestMethod.GET)
	public String getProjectDetails(@PathVariable Long scientificProjectId, Model model, Authentication authentication) {
    	log.info("Carregando página de detalhes de projeto científico do id {}", scientificProjectId);
    	
    	CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    	ScientificProjectDTO scientificProjectDTO = scientificProjectService.getScientificProjectAndChildrenById(scientificProjectId);
    	scientificProjectService.checkPermissionForScientificProject(currentUser != null ? currentUser.getUserId() : null, scientificProjectDTO);
    	
    	model.addAttribute("scientificProjectDTO", scientificProjectDTO); 
    	
    	model.addAttribute("searchDashboardTabUrl", "/scientific-project/" + scientificProjectId + "/dashboard");
    	model.addAttribute("searchExperimentsTabUrl", "/scientific-experiment/" + scientificProjectId + "/scientific-experiments-list");
    	model.addAttribute("searchPhasesTabUrl", "/scientific-project/phase/" + scientificProjectId + "/phases-list");
    	model.addAttribute("searchDocumentationTabUrl", "/scientific-project/" + scientificProjectId + "/documentation");
    	model.addAttribute("searchTaskBoardTabUrl", "/task-board/" + scientificProjectId + "/list");
    	model.addAttribute("searchTaskListTabUrl", "/task-board/task-list/" + scientificProjectId);
    	
    	return "scientific-project/project-details"; 
    }
	
	@PreAuthorize("@scientificProjectService.canAccessScientificProject(principal, #scientificProjectId) || hasAuthority('ADMIN')")
    @RequestMapping(value = "/{scientificProjectId}/project-details-data", method = RequestMethod.GET)
	public String getProjectDetailsData(@PathVariable Long scientificProjectId, Model model, Authentication authentication) {
    	log.info("Carregando dados de detalhes de projeto científico do id {}", scientificProjectId);
    	
    	CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    	ScientificProjectDTO scientificProjectDTO = scientificProjectService.getScientificProjectAndChildrenById(scientificProjectId);
    	scientificProjectService.checkPermissionForScientificProject(currentUser != null ? currentUser.getUserId() : null, scientificProjectDTO);
    	
    	model.addAttribute("scientificProjectDTO", scientificProjectDTO); 
    	model.addAttribute("totalExperiments", scientificExperimentService.countAllScientificExperiments(null, scientificProjectId));
    	model.addAttribute("totalWorkflows", workflowService.countAllWorkflows(WorkflowFormFilter.builder().scientificProjectId(scientificProjectDTO.getScientificProjectId()).build()));
    	
		TaskFormFilter taskFormFilter = TaskFormFilter.builder().taskStatus(TaskStatus.TODO.name()).build();
		taskFormFilter.setUserId(currentUser.getUserId());
    	
		model.addAttribute("totalTasksTodo", taskService.countAllTasks(scientificProjectId, taskFormFilter));
    	taskFormFilter.setTaskStatus(TaskStatus.DOING.name());
    	model.addAttribute("totalTasksDoing", taskService.countAllTasks(scientificProjectId, taskFormFilter));
    	taskFormFilter.setTaskStatus(TaskStatus.DONE.name());
    	
    	model.addAttribute("totalTasksDone", taskService.countAllTasks(scientificProjectId, taskFormFilter));
    	model.addAttribute("totalTasksLate", taskService.countAllLateTasks(scientificProjectId, taskFormFilter));
    	model.addAttribute("searchProjectDetailsUrl", "/scientific-project/" + scientificProjectDTO.getScientificProjectId() + "/project-details-data");

    	return "fragments/scientific-project/project-details-fragment";
    }
	
	@PreAuthorize("@scientificProjectService.canAccessScientificProject(principal, #scientificProjectId) || hasAuthority('ADMIN')")
    @RequestMapping(value = "/{scientificProjectId}/dashboard", method = RequestMethod.GET)
	public String getProjectDashboard(@PathVariable Long scientificProjectId, Model model, Authentication authentication) {
    	log.info("Carregando página de dashboard de projeto científico do id {}", scientificProjectId);
    	
    	CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    	ScientificProjectDTO scientificProjectDTO = scientificProjectService.getScientificProjectAndChildrenById(scientificProjectId);
    	scientificProjectService.checkPermissionForScientificProject(currentUser != null ? currentUser.getUserId() : null, scientificProjectDTO);
    	
    	model.addAttribute("scientificProjectDTO", scientificProjectDTO); 
    	model.addAttribute("phaseForm", PhaseForm.buildEmptyPhaseForm());
    	model.addAttribute("scientificExperimentForm", ScientificExperimentForm.buildEmptyScientificExperimentForm());
    	model.addAttribute("taskForm", TaskForm.buildEmptyTaskForm());

    	model.addAttribute("searchExperimentsListUrl", "/scientific-experiment/" + scientificProjectId + "/dashboard/scientific-experiments");
    	model.addAttribute("searchProjectDetailsUrl", "/scientific-project/" + scientificProjectId + "/project-details-data");
    	model.addAttribute("searchPhasesListUrl", "/scientific-project/phase/" + scientificProjectId + "/dashboard/phases");
    	model.addAttribute("searchTasksListUrl", "/task-board/" + scientificProjectId + "/dashboard/tasks");
    	
    	return "fragments/scientific-project/project-dashboard-fragment";
    }
	
	@RequestMapping(value = "/{scientificProjectId}/documentation", method = RequestMethod.GET)
    public String getScientificProjectDocumentation(@PathVariable Long scientificProjectId, Model model, Authentication authentication) {
    	log.info("Carregando página de documentação de projeto científico do id {}", scientificProjectId);
    	
    	CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    	ScientificProjectDTO scientificProjectDTO = scientificProjectService.getScientificProjectAndChildrenById(scientificProjectId);
    	scientificProjectService.checkPermissionForScientificProject(currentUser != null ? currentUser.getUserId() : null, scientificProjectDTO);
    	
    	model.addAttribute("scientificProjectDTO", scientificProjectDTO); 
    	model.addAttribute("projectDocumentationHtml", scientificProjectService.getProjectDocumentationHtml(scientificProjectId));
    	
    	return "fragments/scientific-project/project-documentation-fragment";
    }
    
    @PreAuthorize("@scientificProjectService.canAccessScientificProject(principal, #scientificProjectId)")
    @RequestMapping(value = "/save-project-documentation", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMessage saveProjectDocumentation(@RequestParam(value="scientificProjectId", required = true) Long scientificProjectId, 
    		@RequestParam(value="htmlDocumentation", required = true) String htmlDocumentation) {
    	
    	log.info("salvando documentação html do projeto científico de id {}", scientificProjectId);
        Documentation Documentation = null;
        
		try {
			Documentation = scientificProjectService.saveProjectDocumentation(scientificProjectId, htmlDocumentation);
		} 
		catch (Exception e) {
        	log.error("Erro ao salvar documentação de projeto científico de id {\n{}}", scientificProjectId, Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado, não foi possível salvar documentação do projeto científico.").build();
    	}
        
        return ResponseMessageUtil.handleResponseMessage(Documentation, "Documentação do projeto científico salva com sucesso.", "Erro inesperado ao salvar documentação do projeto científico.");
    }
    
    @RequestMapping(value = "/scientific-project-list", method = RequestMethod.GET)
    public String getAllScientificProjectsList(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber, 
    		@RequestParam(value = "search", required = false) String queryString,
    		@RequestParam(value = "myOwn", required = false) Boolean myOwn, 
    		Authentication authentication, 
    		Model model) {
    	
    	log.info("Carregando página de listagem de projetos científicos");
    	
    	CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    	Long userId = currentUser != null && !currentUser.getUserRole().equals(Role.ADMIN) ? currentUser.getUserId() : null;
		
        List<ScientificProjectDTO> scientificProjectDTOs = scientificProjectService.getAllScientificProjectsBoards(pageNumber, queryString, Boolean.TRUE.equals(myOwn) ? userId : null);
		scientificProjectService.checkPermissionsForScientificProjects(scientificProjectDTOs, userId);
		model.addAttribute("scientificProjectDTOs", scientificProjectDTOs);
		
		PaginationInfo pagination = PaginationInfo.builder()
												  .totalEntities(scientificProjectService.countAllScientificProjectsBoards(queryString, Boolean.TRUE.equals(myOwn) ? userId : null))
												  .actualPageNumber(pageNumber)
												  .pageSize(scientificProjectService.getPageSize())
												  .baseUrlLink("/scientific-project/scientific-project-list").build();	
        
		pagination.addUrlParameter("search", queryString);
        pagination.addUrlParameter("myOwn", myOwn != null ? myOwn.toString() : null);
        
        FilterDTO filter = FilterDTO.builder().build();
        filter.addFilterField(FilterFieldDTO.builder().name("search").isTextField(Boolean.TRUE).value(queryString).label("busca textual").build())
        	  .addFilterField(FilterFieldDTO.builder().name("myOwn").value(myOwn).build());
        
        model.addAttribute("filter", filter);
        model.addAttribute("pagination", pagination);
        model.addAttribute("searchListUrl", pagination.getBaseUrlLink());
        
        return "fragments/scientific-project/scientific-project-list-fragment";
    }
	
    @RequestMapping(value = "/all-scientific-projects", method = RequestMethod.GET)
    public String getAllScientificProjectsPage(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber, 
    		@RequestParam(value = "search", required = false) String queryString, 
    		@RequestParam(value = "myOwn", required = false) Boolean myOwn, 
    		Model model) {
    	
    	log.info("Carregando página de listagem de projetos científicos");
        model.addAttribute("scientificProjectForm", ScientificProjectFrom.buildEmptyScientificProjectForm());
        
        FilterDTO filter = FilterDTO.builder().build();
        filter.addFilterField(FilterFieldDTO.builder().name("queryString").isTextField(Boolean.TRUE).value(queryString).label("busca textual").build())
        	  .addFilterField(FilterFieldDTO.builder().name("myOwn").value(myOwn).build());
        
        PaginationInfo pagination = PaginationInfo.builder()
        										  .actualPageNumber(pageNumber)
        										  .baseUrlLink("/scientific-project/scientific-project-list").build();
        
        model.addAttribute("filter", filter);
        model.addAttribute("searchListUrl", pagination.buildParametersFromFilter(filter));
        
        return "scientific-project/scientific-project-list";
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/create-scientific-project", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMessage handleScientificProjectCreateForm(@Valid @ModelAttribute("scientificProjectForm") ScientificProjectFrom scientificProjectForm, BindingResult bindingResult,
    								   	      				 HttpServletRequest request) {
    	
        log.info("Processando scientificProjectForm  {}, bindingResult  {}", scientificProjectForm, bindingResult);
        try {
	        ScientificProjectDTO scientificProjectDTO = null;
	        
	        if (bindingResult.hasErrors()) {
	        	log.error("Erro de validação ao criar projeto científico \n" + ResponseMessageUtil.formatErrorMessage(bindingResult));
	        	return ResponseMessage.buildErrorMessage(ResponseMessageUtil.formatErrorMessage(bindingResult));
	        }
	        
	    	scientificProjectDTO = scientificProjectService.createScientificProject(scientificProjectForm);
	        return ResponseMessageUtil.handleResponseMessage(scientificProjectDTO, "Projeto científico criado com sucesso.", "Erro inesperado ao criar projeto científico.");
        }
        catch(Exception e) {
        	log.error("Erro ao criar projeto cientifico\n{}", Throwables.getStackTraceAsString(e));
	        return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado ao criar projeto científico.").build();
        }
    }
    
    @PreAuthorize("@scientificProjectService.canAccessScientificProject(principal, #scientificProjectId) || hasAuthority('ADMIN')")
    @RequestMapping(value = "/edit-scientific-project", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMessage editScientificfProject(@RequestParam(value="scientificProjectId", required = true) Long scientificProjectId, 
    		@RequestParam(value="projectName", required = true) String projectName) {
    	
    	log.info("Editando projeto científico de id {} com novo projectName {}", scientificProjectId, projectName);
        ScientificProjectDTO scientificProjectDTO = null;
        
		try {
    		scientificProjectDTO = scientificProjectService.editScientificProject(scientificProjectId, projectName);
		} 
		catch (ExistingEntityException e) {
        	log.error("Erro ao editar projeto científico, o nome {} já é usado por outro projeto científico\n{}", projectName, Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
        						  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
        						  .text("Já existe outro projeto científico com o nome " + projectName + ".").build();
        } 
		catch (Exception e) {
        	log.error("Erro ao editar projeto científico de id {} com novo projectName {}\n{}", 
        			   scientificProjectId, projectName, Throwables.getStackTraceAsString(e));
        	
        	return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado, não foi possível editar o projeto científico.").build();
    	}
        
        return ResponseMessageUtil.handleResponseMessage(scientificProjectDTO, "Projeto científico editado com sucesso.", "Erro inesperado ao editar projeto científico.");
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/remove-scientific-project", method = RequestMethod.POST)
    @ResponseBody
	public ResponseMessage handleRemoveScientificProject(@RequestParam(value = "scientificProjectId", required = true) String scientificProjectId, 
			HttpServletRequest request) {
    	
    	log.info("Excluindo projeto cientifico de id {}", scientificProjectId);
    	
    	try {
    		Long scientificProjectIdValue = Long.valueOf(scientificProjectId);
    		scientificProjectService.removeScientificProject(scientificProjectIdValue);
    	}
    	catch (Exception e) {
    		log.error("Erro ao excluir projeto científico de id {}\n{}", scientificProjectId, Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado ao excluir projeto científico.").build();
    	}
    	
    	return ResponseMessage.builder()
							  .type(ResponseMessage.SUCCESS_MESSAGE_LABEL)
							  .text("Projeto científico excluído com sucesso.").build();
	}
    
}