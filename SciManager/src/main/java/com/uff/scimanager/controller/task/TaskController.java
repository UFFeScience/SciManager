package com.uff.scimanager.controller.task;

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
import com.uff.scimanager.domain.TaskStatus;
import com.uff.scimanager.domain.dto.ScientificProjectDTO;
import com.uff.scimanager.domain.dto.TaskDTO;
import com.uff.scimanager.domain.dto.TasksChartDTO;
import com.uff.scimanager.domain.dto.filter.FilterDTO;
import com.uff.scimanager.domain.dto.filter.FilterFieldDTO;
import com.uff.scimanager.domain.form.TaskChartFormFilter;
import com.uff.scimanager.domain.form.TaskForm;
import com.uff.scimanager.domain.form.TaskFormFilter;
import com.uff.scimanager.domain.form.TaskFormUpdate;
import com.uff.scimanager.domain.form.validator.TaskFormValidator;
import com.uff.scimanager.domain.security.CurrentUser;
import com.uff.scimanager.service.scientific.project.ScientificProjectService;
import com.uff.scimanager.service.task.TaskService;
import com.uff.scimanager.util.ResponseMessageUtil;

@Controller
@RequestMapping("/task-board")
public class TaskController {
	
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);
    
    private final ScientificProjectService scientificProjectService;
    private final TaskService taskService;
    private final TaskFormValidator taskFormValidator;
    
	@Autowired
	public TaskController(TaskService taskService, ScientificProjectService scientificProjectService, TaskFormValidator taskFormValidator) {
		this.taskService = taskService;
		this.taskFormValidator = taskFormValidator;
		this.scientificProjectService = scientificProjectService;
	}
	
	@InitBinder("taskForm")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(taskFormValidator);
    }
	
	@RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String getTasksHomeData(Model model, Authentication authentication) {
		log.info("Carregando pagina de dashboard de tarefas do usuário");
		
		CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
		
        model.addAttribute("taskDTOs", taskService.getAllTasksOpenForUser(currentUser.getUserId()));
        model.addAttribute("tasksTotal", taskService.getAllTasksOpenForUserCount(currentUser.getUserId()));
    	
    	return "fragments/task/tasks-home-dashboard-fragment";
    }
	
	@PreAuthorize("@scientificProjectService.canAccessScientificProject(principal, #scientificProjectId)")
	@RequestMapping(value = "/api/metric/{scientificProjectId}", method = RequestMethod.GET)
	@ResponseBody
    public TasksChartDTO getMetricsChart(@PathVariable Long scientificProjectId, @Valid TaskChartFormFilter taskChartFormFilter, BindingResult bindingResult) {
		
		if (taskChartFormFilter == null || bindingResult.hasErrors()) {
        	log.error("Erro de validação ao editar usuário \n" + ResponseMessageUtil.formatErrorMessage(bindingResult));
        	return new TasksChartDTO();
        }
		
		log.info("Processando grafico de metricas do projeto científico {}", scientificProjectId);
		taskChartFormFilter.setScientificProjectId(scientificProjectId);
		return taskService.getTaskChart(taskChartFormFilter);
    }
	
	@PreAuthorize("@scientificProjectService.canAccessScientificProject(principal, #scientificProjectId)")
	@RequestMapping(value = "/{scientificProjectId}/dashboard/tasks", method = RequestMethod.GET)
    public String getProjectDashboardTasksList(@PathVariable Long scientificProjectId, 
									    	   @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,	
									    	   Authentication authentication,
									    	   Model model) {
        
        log.info("Carregando tarefas do projeto científico {}", scientificProjectId);
        
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
		TaskFormFilter taskFormFilter = TaskFormFilter.builder().pageNumber(pageNumber).userId(currentUser.getUserId()).myOwn(Boolean.FALSE).build();
		
		ScientificProjectDTO scientificProjectDTO = scientificProjectService.getScientificProjectById(scientificProjectId);
		scientificProjectService.checkPermissionForScientificProject(currentUser.getUserId(), scientificProjectDTO);
		
		model.addAttribute("searchTasksListUrl", "/task-board/" + scientificProjectId + "/dashboard/tasks");
		model.addAttribute("scientificProjectDTO", scientificProjectDTO);
		model.addAttribute("taskDTOs", taskService.getAllProjectDashboardTasks(scientificProjectId, taskFormFilter));
		model.addAttribute("tasksTotal", taskService.countAllTasks(scientificProjectId, TaskFormFilter.builder().userId(currentUser.getUserId()).build()));
		model.addAttribute("taskForm", TaskForm.buildEmptyTaskForm());
		
        return "fragments/task/project-task-list-fragment";
    }
	
	@PreAuthorize("@scientificProjectService.canAccessScientificProject(principal, #scientificProjectId)")
	@RequestMapping(value = "/{scientificProjectId}/list", method = RequestMethod.GET)
    public String getScientificProjectTaskBoardTasks(@PathVariable Long scientificProjectId,
													 TaskFormFilter taskFormFilter,
											   		 Authentication authentication,
											   		 Model model) {
		
		log.info("Carregando pagina de quadro de tarefas do projeto científico {}", scientificProjectId);
		
		CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
		ScientificProjectDTO scientificProjectDTO = scientificProjectService.getScientificProjectById(scientificProjectId);
		scientificProjectService.checkPermissionForScientificProject(currentUser.getUserId(), scientificProjectDTO);
		
		model.addAttribute("scientificProjectDTO", scientificProjectDTO);		
		model.addAttribute("taskForm", TaskForm.buildEmptyTaskForm());

		taskFormFilter.setTaskStatus(TaskStatus.TODO.name());
		taskFormFilter.setUserId(currentUser.getUserId());
		
		model.addAttribute("taskTodoDTOs", taskService.getAllTasks(scientificProjectId, taskFormFilter));
		model.addAttribute("taskTodoTotal", taskService.countAllTasks(scientificProjectId,  taskFormFilter));
		
		taskFormFilter.setTaskStatus(TaskStatus.DOING.name());
		model.addAttribute("taskDoingDTOs", taskService.getAllTasks(scientificProjectId, taskFormFilter));
		model.addAttribute("taskDoingTotal", taskService.countAllTasks(scientificProjectId, taskFormFilter));
		
		taskFormFilter.setTaskStatus(TaskStatus.DONE.name());
		model.addAttribute("taskDoneDTOs", taskService.getAllTasks(scientificProjectId, taskFormFilter));
		model.addAttribute("taskDoneTotal", taskService.countAllTasks(scientificProjectId, taskFormFilter));
		
		FilterDTO filter = FilterDTO.builder().build();
        filter.addFilterField(FilterFieldDTO.builder().name("search").isTextField(Boolean.TRUE).value(taskFormFilter.getQueryString()).label("busca textual").build())
        	  .addFilterField(FilterFieldDTO.builder().name("workflowName").isTextField(Boolean.TRUE).value(taskFormFilter.getWorkflowName()).label("filtro de workflow").build())
        	  .addFilterField(FilterFieldDTO.builder().name("phaseName").isTextField(Boolean.TRUE).value(taskFormFilter.getPhaseName()).label("filtro de fase").build())
        	  .addFilterField(FilterFieldDTO.builder().name("myOwn").value(taskFormFilter.getMyOwn()).build())
        	  .addFilterField(FilterFieldDTO.builder().name("initialDate").value(taskFormFilter.getInitialDate()).label("Data inicial").build())
        	  .addFilterField(FilterFieldDTO.builder().name("finalDate").value(taskFormFilter.getFinalDate()).label("Data final").build());
    	
        model.addAttribute("filter", filter);
        model.addAttribute("searchListUrl", "/task-board/" + scientificProjectId + "/list");
		
        return "fragments/task/task-board-tasks-list-fragment";
    }
	
	@PreAuthorize("@scientificProjectService.canAccessScientificProject(principal, #scientificProjectId)")
	@RequestMapping(value = "/api/{scientificProjectId}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
    public List<TaskDTO> getScientificProjectTaskBoardJson(@PathVariable Long scientificProjectId, TaskFormFilter taskFilterDTO, Authentication authentication) {
		
		log.info("Carregando tarefas do quadro de tarefas do projeto científico {}", scientificProjectId);
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        taskFilterDTO.setUserId(currentUser.getUserId());
		return taskService.getAllTasks(scientificProjectId, taskFilterDTO);
    }
	
	@PreAuthorize("@scientificProjectService.canAccessScientificProject(principal, #scientificProjectId)")
	@RequestMapping(value = "/task-list/{scientificProjectId}", method = RequestMethod.GET)
    public String getScientificProjectTasksList(@PathVariable Long scientificProjectId,
    										    TaskFormFilter taskFormFilter,
											    Authentication authentication,
											    Model model) {
        
        log.info("Carregando tarefas do projeto científico {}", scientificProjectId);
        
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        taskFormFilter.setUserId(currentUser.getUserId());
        
        ScientificProjectDTO scientificProjectDTO = scientificProjectService.getScientificProjectById(scientificProjectId);
		scientificProjectService.checkPermissionForScientificProject(currentUser.getUserId(), scientificProjectDTO);
		
		model.addAttribute("scientificProjectDTO", scientificProjectDTO);
		model.addAttribute("taskForm", TaskForm.buildEmptyTaskForm());
		model.addAttribute("taskDTOs", taskService.getAllTasks(scientificProjectId, taskFormFilter));
		
		PaginationInfo pagination = PaginationInfo.builder()
												  .totalEntities(taskService.countAllTasks(scientificProjectId, taskFormFilter))
												  .actualPageNumber(taskFormFilter.getPageNumber())
												  .pageSize(taskService.getPageSize())
												  .baseUrlLink("/task-board/task-list/" + scientificProjectId).build();
		
		pagination.addUrlParameter("search", taskFormFilter.getQueryString());
		pagination.addUrlParameter("initialDate", taskFormFilter.getInitialDate());
		pagination.addUrlParameter("finalDate", taskFormFilter.getFinalDate());
		pagination.addUrlParameter("phaseName", taskFormFilter.getPhaseName());
		pagination.addUrlParameter("workflowName", taskFormFilter.getWorkflowName());
		pagination.addUrlParameter("taskStatus", (taskFormFilter.getTaskStatus() != null && !"".equals(taskFormFilter.getTaskStatus())) ? 
				TaskStatus.getStatusFromString(taskFormFilter.getTaskStatus()).getStatusName() : null);
		pagination.addUrlParameter("myOwn", taskFormFilter.getMyOwn());
		
		FilterDTO filter = FilterDTO.builder().build();
        filter.addFilterField(FilterFieldDTO.builder().name("search").isTextField(Boolean.TRUE).value(taskFormFilter.getQueryString()).label("busca textual").build())
        	  .addFilterField(FilterFieldDTO.builder().name("workflowName").isTextField(Boolean.TRUE).value(taskFormFilter.getWorkflowName()).label("filtro de workflow").build())
        	  .addFilterField(FilterFieldDTO.builder().name("phaseName").isTextField(Boolean.TRUE).value(taskFormFilter.getPhaseName()).label("filtro de fase").build())
        	  .addFilterField(FilterFieldDTO.builder().name("myOwn").value(taskFormFilter.getMyOwn()).build())
        	  .addFilterField(FilterFieldDTO.builder().name("taskStatus").isTextField(Boolean.TRUE)
        			  .text((taskFormFilter.getTaskStatus() != null && !"".equals(taskFormFilter.getTaskStatus())) ? 
        					  TaskStatus.getStatusFromString(taskFormFilter.getTaskStatus()).getStatusName() : null)
        			  .value(taskFormFilter.getTaskStatus()).label("filtro de status").build())
        	  .addFilterField(FilterFieldDTO.builder().name("initialDate").value(taskFormFilter.getInitialDate()).label("Data inicial").build())
        	  .addFilterField(FilterFieldDTO.builder().name("finalDate").value(taskFormFilter.getFinalDate()).label("Data final").build());
    	
        model.addAttribute("filter", filter);
        model.addAttribute("pagination", pagination);
        model.addAttribute("searchListUrl", pagination.getBaseUrlLink());
		
        return "fragments/task/tasks-list-fragment";
    }
	
	@RequestMapping(value = "/create-task", method = RequestMethod.POST)
	@ResponseBody
	public ResponseMessage handleTaskCreateForm(@Valid @ModelAttribute("taskForm") TaskForm taskForm, BindingResult bindingResult, HttpServletRequest request) {
	    log.info("Processando taskForm  {}, bindingResult  {}", taskForm, bindingResult);

	    try {
		    TaskDTO taskDTO = null;
		    
		    if (bindingResult.hasErrors()) {
		    	log.error("Erro de validação ao criar tarefa \n" + ResponseMessageUtil.formatErrorMessage(bindingResult));
		    	return ResponseMessage.buildErrorMessage(ResponseMessageUtil.formatErrorMessage(bindingResult));
		    }
		    
		    taskDTO = taskService.createTask(taskForm);
	        return ResponseMessageUtil.handleResponseMessage(taskDTO, "Tarefa criada com sucesso.", "Erro inesperado ao criar tarefa.");
	    }
	    catch (Exception e) {
	    	log.error("Erro ao criar tarefa\n{}", Throwables.getStackTraceAsString(e));
	        return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado ao criar tarefa.").build();
	    }
	}
	
	@RequestMapping(value = "/edit-task", method = RequestMethod.POST)
	@ResponseBody
	public ResponseMessage handleEditTask(TaskFormUpdate taskFormUpdate, HttpServletRequest request, BindingResult bindingResult, Authentication authentication) {
		
		if (taskFormUpdate == null || bindingResult.hasErrors()) {
        	log.error("Erro de validação ao editar usuário \n" + ResponseMessageUtil.formatErrorMessage(bindingResult));
        	return ResponseMessage.buildErrorMessage(ResponseMessageUtil.formatErrorMessage(bindingResult));
        }
		
    	log.info("Editando tarefa de id {}", taskFormUpdate.getTaskId());
		TaskDTO taskDTO = null;
    	
    	try {
    		CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        	Long userId = currentUser != null ? currentUser.getUserId() : null;
        	
    		taskDTO = taskService.editTask(taskFormUpdate, userId);
    	} 
        catch (Exception e) {
        	log.error("Erro inesperado ao editar task de id {}\n{}", taskFormUpdate.getTaskId(), Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado, não foi possível editar a tarefa.").build();    	
    	}
    	
        return ResponseMessageUtil.handleResponseMessage(taskDTO, "Tarefa editada com sucesso.", "Erro inesperado ao editar tarefa.");
	}
	
	@RequestMapping(value = "/remove-task", method = RequestMethod.POST)
	@ResponseBody
	public ResponseMessage handleRemoveTask(@RequestParam(value = "taskId", required = true) Long taskId,
							       			HttpServletRequest request, Authentication authentication) {
		log.info("Removendo tarefa de id {}", taskId);
		
    	try {
    		CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    		taskService.removeTask(taskId, currentUser);
    	} 
        catch (Exception e) {
        	log.error("Erro ao excluir tarefa de id {}\n{}", taskId, Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado ao excluir workflow.").build();
    	}
    	
    	return ResponseMessage.builder()
							  .type(ResponseMessage.SUCCESS_MESSAGE_LABEL)
							  .text("Tarefa excluída com sucesso.").build();
	}
	
    @RequestMapping(value = "/edit-task-status", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMessage editTaskStatus(@RequestParam(value="taskId", required = true) Long taskId,
									      @RequestParam(value="newStatus", required = true) String newStatus,
									      Authentication authentication) {
    	
    	log.info("Editando projeto task de id {} com novo status {}", taskId, newStatus);
        TaskDTO taskDTO = null;
        
		try {
			CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        	Long userId = currentUser != null ? currentUser.getUserId() : null;
        	
    		taskDTO = taskService.editTaskStatus(taskId, newStatus, userId);
		} 
		catch (Exception e) {
        	log.error("Erro ao editar task de id {} com novo status {}\n{}", taskId, newStatus, Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
							      .text("Erro inesperado, não foi possível editar o status da tarefa.").build();
    	}
        
        return ResponseMessageUtil.handleResponseMessage(taskDTO, "Status editado com sucesso.", "Erro inesperado ao editar status da tarefa.");
    }
	
}