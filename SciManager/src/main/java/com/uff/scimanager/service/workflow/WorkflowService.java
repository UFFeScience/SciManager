package com.uff.scimanager.service.workflow;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Throwables;
import com.uff.scimanager.amqp.WorkflowExecutionSender;
import com.uff.scimanager.dao.documentation.DocumentationDao;
import com.uff.scimanager.dao.workflow.WorkflowDao;
import com.uff.scimanager.dao.workflow.WorkflowExecutionDao;
import com.uff.scimanager.dao.workflow.WorkflowExecutionMetadataDao;
import com.uff.scimanager.dao.workflow.WorkflowGraphDao;
import com.uff.scimanager.domain.Documentation;
import com.uff.scimanager.domain.ModelFile;
import com.uff.scimanager.domain.ScientificExperiment;
import com.uff.scimanager.domain.ScientificProject;
import com.uff.scimanager.domain.Task;
import com.uff.scimanager.domain.User;
import com.uff.scimanager.domain.UserGroup;
import com.uff.scimanager.domain.Workflow;
import com.uff.scimanager.domain.WorkflowExecution;
import com.uff.scimanager.domain.WorkflowExecutionStatus;
import com.uff.scimanager.domain.WorkflowGraph;
import com.uff.scimanager.domain.dto.ModelFileDTO;
import com.uff.scimanager.domain.dto.UserDTO;
import com.uff.scimanager.domain.dto.WorkflowDTO;
import com.uff.scimanager.domain.dto.WorkflowExecutionDTO;
import com.uff.scimanager.domain.dto.amqp.WorkflowExecutionMessageDTO;
import com.uff.scimanager.domain.dto.swfms.scicumuls.WorkflowExecutionMetadataDTO;
import com.uff.scimanager.domain.form.WorkflowExecutionFormFilter;
import com.uff.scimanager.domain.form.WorkflowForm;
import com.uff.scimanager.domain.form.WorkflowFormFilter;
import com.uff.scimanager.domain.form.WorkflowFormUpdate;
import com.uff.scimanager.domain.security.CurrentUser;
import com.uff.scimanager.exception.EntityHasRelationsException;
import com.uff.scimanager.exception.EntityNotFoundException;
import com.uff.scimanager.exception.ExistingEntityException;
import com.uff.scimanager.exception.InvalidEntityException;
import com.uff.scimanager.exception.PermissionDeniedException;
import com.uff.scimanager.service.model.file.ModelFileService;
import com.uff.scimanager.service.scientific.experiment.ScientificExperimentService;
import com.uff.scimanager.service.scientific.project.ScientificProjectService;
import com.uff.scimanager.service.task.TaskService;
import com.uff.scimanager.service.user.UserService;
import com.uff.scimanager.service.user.group.UserGroupService;
import com.uff.scimanager.util.CalendarDateUtils;
import com.uff.scimanager.util.EncrypterUtils;
import com.uff.scimanager.util.XmlParser;

@Service
public class WorkflowService {

	private static final Logger log = LoggerFactory.getLogger(WorkflowService.class);
	
	private final WorkflowDao workflowDao;
	private final WorkflowExecutionDao workflowExecutionDao;
	private final WorkflowExecutionMetadataDao workflowExecutionMetadataDao;
	private final WorkflowExecutionSender workflowExecutionSender;
	private final DocumentationDao workflowDocumentationDao;
	private final WorkflowGraphDao workflowGraphDao;
	
	@Autowired
	private ScientificProjectService scientificProjectService;
	
	@Autowired
	private ScientificExperimentService scientificExperimentService;
	
	@Autowired
	private ModelFileService modelFileService;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private UserGroupService userGroupService;
	
	@Autowired
	private UserService userService;

    @Autowired
	public WorkflowService(WorkflowDao workflowDao, WorkflowExecutionSender workflowExecutionSender, 
						   DocumentationDao workflowDocumentationDao, WorkflowGraphDao workflowGraphDao, 
						   WorkflowExecutionDao workflowExecutionDao, WorkflowExecutionMetadataDao workflowExecutionMetadataDao) {
    	
		this.workflowDao = workflowDao;
		this.workflowExecutionSender = workflowExecutionSender;
		this.workflowDocumentationDao = workflowDocumentationDao;
		this.workflowGraphDao = workflowGraphDao;
		this.workflowExecutionDao = workflowExecutionDao;
		this.workflowExecutionMetadataDao = workflowExecutionMetadataDao;
	}
    
    public String getDetailedWorkflowGraphCode(Long workflowId) {
    	log.info("Recuperando grafo de workflow pelo id de workflow {}", workflowId);
    	
    	if (workflowId == null) {
    		return null;
    	}
    	
    	WorkflowGraph workflowGraph = workflowGraphDao.getDetailedGraphByWorkflowId(workflowId);
    	
    	if (workflowGraph == null) {
    		return null;
    	}
    	
    	log.info("Grafo recuperada com sucesso");
    	return workflowGraph.getGraphCode();
    }
    
    public String getMacroWorkflowGraphCode(Long workflowId) {
    	log.info("Recuperando grafo de workflow pelo id de workflow {}", workflowId);
    	
    	if (workflowId == null) {
    		return null;
    	}
    	
    	WorkflowGraph workflowGraph = workflowGraphDao.getMacroGraphByWorkflowId(workflowId);
    	
    	if (workflowGraph == null) {
    		return null;
    	}
    	
    	log.info("Grafo recuperada com sucesso");
    	return workflowGraph.getGraphCode();
    }
    
    public WorkflowGraph saveDetailedWorkflowGraph(Long workflowId, String graphCode) {
    	log.info("Salvando grafo detalhado do workflow de id {}", workflowId);
    	
    	if (workflowId == null) {
    		return null;
    	}
    	
    	WorkflowGraph workflowGraph = workflowGraphDao.getDetailedGraphByWorkflowId(workflowId);
    	
    	if (workflowGraph == null) {
    		Workflow workflow = workflowDao.findById(workflowId);
    		
    		if (workflow == null) {
    			return null;
    		}
    		
    		workflowGraph = WorkflowGraph.builder().workflow(workflow).build();
    	}
    	
    	workflowGraph.setGraphCode("".equals(graphCode) ? null : graphCode);
    	workflowGraph.setIsDetailed(Boolean.TRUE);
    	
    	return workflowGraphDao.saveOrUpdateWorkflowGraph(workflowGraph);
    }
    
    public WorkflowGraph saveMacroWorkflowGraph(Long workflowId, String graphCode) {
    	log.info("Salvando grafo macro do workflow de id {}", workflowId);
    	
    	if (workflowId == null) {
    		return null;
    	}
    	
    	WorkflowGraph workflowGraph = workflowGraphDao.getMacroGraphByWorkflowId(workflowId);
    	
    	if (workflowGraph == null) {
    		Workflow workflow = workflowDao.findById(workflowId);
    		
    		if (workflow == null) {
    			return null;
    		}
    		
    		workflowGraph = WorkflowGraph.builder()
    				                     .workflow(workflow).build();
    	}
    	
    	workflowGraph.setGraphCode(graphCode);
    	workflowGraph.setIsDetailed(Boolean.FALSE);
    	
    	return workflowGraphDao.saveOrUpdateWorkflowGraph(workflowGraph);
    }
    
    public String getWorkflowDocumentationHtml(Long workflowId) {
    	log.info("Recuperando documentação de workflow pelo id de workflow {}", workflowId);
    	
    	if (workflowId == null) {
    		return null;
    	}
    	
    	Documentation workflowDocumentation = workflowDocumentationDao.getDocumentationByWorkflowId(workflowId);
    	
    	if (workflowDocumentation == null) {
    		return null;
    	}
    	
    	log.info("Documentação recuperada com sucesso");
    	return workflowDocumentation.getHtmlDocumentationAsString();
    }
    
    public Documentation saveWorkflowDocumentation(Long workflowId, String htmlDocumentation) {
    	log.info("Salvando documentação de workflow de id {}", workflowId);
    	
    	if (workflowId == null) {
    		return null;
    	}
    	
    	Documentation workflowDocumentation = workflowDocumentationDao.getDocumentationByWorkflowId(workflowId);
    	
    	if (workflowDocumentation == null) {
    		Workflow workflow = workflowDao.findById(workflowId);
    		
    		if (workflow == null) {
    			return null;
    		}
    		
    		workflowDocumentation = Documentation.builder()
    				                             .workflow(workflow).build();
    	}
    	
    	try {
			workflowDocumentation.setHtmlDocumentation(htmlDocumentation.getBytes(EncrypterUtils.DEFAULT_ENCODING));
		} 
    	catch (UnsupportedEncodingException e) {
			log.error("Não foi possível recuperar documentação do workflow de id {}\n{}", workflowId, Throwables.getStackTraceAsString(e));
			return null;
		}
    	return workflowDocumentationDao.saveOrUpdateDocumentation(workflowDocumentation);
    }
    
	public Workflow getWorkflowById(Long workflowId) {
		Workflow workflow = workflowDao.findById(workflowId);
		
		if (workflow != null) {
			log.info("Workflow com id {} buscado com sucesso", workflowId);
			return workflow;
		}
		
		log.info("Workflow com id {}, não encontrado", workflowId);
        return null;
	}
    
    @Transactional
	public WorkflowDTO getWorkflowDTOById(Long workflowId) {
		Workflow workflow = workflowDao.findById(workflowId);
		
		if (workflow != null) {
			log.info("Workflow com id {} buscado com sucesso", workflowId);
			
			WorkflowDTO workflowDTO = WorkflowDTO.buildDTOByEntity(workflow);
			ModelFileDTO modelFileDTO = modelFileService.getCurrentModelFileOfWorkflow(workflowId);
			workflowDTO.setCurrentModelFile(modelFileDTO);
			
			return workflowDTO;
		}
		
		log.info("Workflow com id {}, não encontrado", workflowId);
        return null;
	}
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<WorkflowDTO> getAllWorkflows(WorkflowFormFilter workflowFormFilter) {
		log.info("Buscando lista de workflows pelo filtro: pageNumber = {}, queryString = {}, responsibleGroupName = {}, userId = {}", 
				workflowFormFilter.getPageNumber(), workflowFormFilter.getQueryString(), workflowFormFilter.getResponsibleGroupName(), workflowFormFilter.getUserId());
		
		return WorkflowDTO.convertEntityListToDTOList(workflowDao.findAllPaginated(workflowFormFilter));
	}
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<WorkflowDTO> getAllDashboardWorkflows(Integer pageNumber, Long scientificExperimentId, Long userId) {
		log.info("Buscando lista de workflows pelo filtro: pageNumber = {}, scientificExperimentId = {}, userId = {}", pageNumber, scientificExperimentId, userId);
		return WorkflowDTO.convertEntityListToDTOList(workflowDao.findAllRelevantPaginated(pageNumber, scientificExperimentId, userId));
	}
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<WorkflowDTO> getAllWorkflowsOfUser(Long userId) {
		log.info("Buscando lista de workflows pelo filtro: userId = {}", userId);
		return WorkflowDTO.convertEntityListToDTOList(workflowDao.findAllOfUser(userId));
	}
    
    public Integer countAllWorkflowsOfUser(Long userId) {
		log.info("Contando workflows pelo filtro: userId = {}", userId);
		return workflowDao.countAllOfUser(userId);
	}
    
	public Integer countAllWorkflowsOfUserGroup(Long userGroupId) {
		log.info("Contando workflows pelo filtro: userGroupId = {}", userGroupId);
		return workflowDao.countAllOfUserGroup(userGroupId);
	}
	
	public Integer countAllWorkflowsByExperimentId(Long scientificExperimentId) {
		log.info("Contando workflows pelo filtro: scientificExperimentId = {}", scientificExperimentId);
		return workflowDao.countAllOfScientificExperiment(scientificExperimentId);
	}
	
	public Integer countAllWorkflowsExecutionByModelFileId(Long modelFileId) {
		log.info("Contando workflowExecutions pelo filtro: modelFileId = {}", modelFileId);
		return workflowExecutionDao.countAllOfModelFile(modelFileId);
	}
    
    public Integer countAllWorkflows(WorkflowFormFilter workflowFormFilter) {
		log.info("Contando lista de workflows pelo filtro: queryString = {}, queryString = {}, responsibleGroupName = {}, userId = {}", 
				workflowFormFilter.getQueryString(), workflowFormFilter.getResponsibleGroupName(), workflowFormFilter.getUserId());
		
        return workflowDao.getAllWorkflowsCount(workflowFormFilter);
    }
    
    @Transactional
    public WorkflowDTO createWorkflow(WorkflowForm workflowForm) {
    	log.info("Processando criação de workflow");
    	
    	Workflow workflow = Workflow.buildWorkflowFromWorkflowForm(workflowForm);
    	ScientificProject scientificProject = scientificProjectService.getScientificProjectEntityById(workflowForm.getScientificProjectId());
    	ScientificExperiment scientificExperiment = scientificExperimentService.getScientificExperimentEntityById(workflowForm.getScientificExperimentId());
    	UserGroup responsibleGroup = userGroupService.getUserGroupEntityByGroupName(workflowForm.getResponsibleGroupName());
    	
    	if (scientificProject != null && responsibleGroup != null && scientificExperiment != null) {
    		workflow.setScientificProject(scientificProject);
    		workflow.setScientificExperiment(scientificExperiment);
    		workflow.setResponsibleGroup(responsibleGroup);
    		
    		return WorkflowDTO.buildDTOByEntity(workflowDao.saveOrUpdateWorkflow(workflow));
    	}
    	
    	log.info("Problema ao processar criação do workflow para projeto científico de id {}, experimento científico de id {} e grupo responsável {}", 
				 workflowForm.getScientificProjectId(), workflowForm.getScientificExperimentId(), workflowForm.getResponsibleGroupName());
    	
    	return null;
    }
    
    @Transactional
    public Workflow saveOrUpdateWorkflowEntity(Workflow workflow) {
    	log.info("Iniciando update/save de workflow");
    	return workflowDao.saveOrUpdateWorkflow(workflow);
    }
    
    @Transactional
    public WorkflowDTO editWorkflow(WorkflowFormUpdate workflowFormUpdate) throws ExistingEntityException, EntityNotFoundException, EntityHasRelationsException {
    	log.info("Processando edição de workflow");
    	Workflow workflow = workflowDao.findById(workflowFormUpdate.getWorkflowId());
    	
    	validateEditWorkflow(workflowFormUpdate, workflow);
    	
    	ScientificProject scientificProject = scientificProjectService.getScientificProjectEntityById(workflowFormUpdate.getScientificProjectId());
    	ScientificExperiment scientificExperiment = scientificExperimentService.getScientificExperimentEntityById(workflowFormUpdate.getScientificExperimentId());
    	UserGroup responsibleGroup = userGroupService.getUserGroupEntityByGroupName(workflowFormUpdate.getResponsibleGroupName());
    	
    	if (scientificProject != null && responsibleGroup != null && workflow != null && scientificExperiment != null) {
        	return editWorkflowData(workflowFormUpdate, workflow, scientificProject, scientificExperiment, responsibleGroup);
    	}
    	
    	log.info("Problema ao processar edição do workflow com projeto científico de id {} e novo grupo responsável {}", workflowFormUpdate.getScientificProjectId(), responsibleGroup);
    	return null;
    }

	private WorkflowDTO editWorkflowData(WorkflowFormUpdate workflowFormUpdate, Workflow workflow, ScientificProject scientificProject, 
										ScientificExperiment scientificExperiment, UserGroup responsibleGroup) throws ExistingEntityException {
		
		if (!workflowFormUpdate.getWorkflowName().equals(workflow.getWorkflowName()) && 
			!validateExistingWorkflowName(workflowFormUpdate.getWorkflowName())) {
			
			log.info("Workflow com o workflowName {} já existe", workflowFormUpdate.getWorkflowName());
			throw new ExistingEntityException("Workflow com o workflowName " + workflowFormUpdate.getWorkflowName() + " já existe");
		}
		
		workflow.setWorkflowName(workflowFormUpdate.getWorkflowName());
		workflow.setSwfms(workflowFormUpdate.getSwfms());
		workflow.setScientificProject(scientificProject);
		workflow.setScientificExperiment(scientificExperiment);
		workflow.setResponsibleGroup(responsibleGroup);
		
		Workflow savedWorkflow = workflowDao.saveOrUpdateWorkflow(workflow);
		
		if (savedWorkflow != null) {
			log.info("Workflow de workflowId {}, atualizado com sucesso", workflowFormUpdate.getWorkflowId());
			return WorkflowDTO.buildDTOByEntity(savedWorkflow);
		}
		
		return WorkflowDTO.buildDTOByEntity(workflowDao.saveOrUpdateWorkflow(workflow));
	}

	private void validateEditWorkflow(WorkflowFormUpdate workflowFormUpdate, Workflow workflow) throws EntityNotFoundException, EntityHasRelationsException {
		
		if (workflow == null) {
    		throw new EntityNotFoundException("Erro ao buscar workflow. Workflow com o workflowId " + workflowFormUpdate.getWorkflowId() + " não foi encontrada.");
    	}
    	
    	if (!workflow.getResponsibleGroup().getGroupName().equals(workflowFormUpdate.getResponsibleGroupName())) {
    		if (taskService.countAllTasksByWorkflowIdAndGroup(workflowFormUpdate.getWorkflowId(), workflow.getResponsibleGroup()) > 0) {
        		throw new EntityHasRelationsException("Erro ao tentar atualizar grupo do workflow de workflowId " + workflowFormUpdate.getWorkflowId() + ", o mesmo já possui tarefas associadas a ele");
        	}
    	}
    	
    	if (!workflow.getScientificProject().getScientificProjectId().equals(workflowFormUpdate.getScientificProjectId())) {
    		if (taskService.countAllTasksByWorkflowId(workflowFormUpdate.getWorkflowId()) > 0) {
        		throw new EntityHasRelationsException("Erro ao tentar atualizar projeto científico do workflow de workflowId " + workflowFormUpdate.getWorkflowId() + ", o mesmo já possui tarefas associadas a ele");
        	}
    	}
	}
    
    private boolean validateExistingWorkflowName(String workflowName) {
    	log.info("Validando se existe workflow com o nome {}", workflowName);
		Workflow workflow = getByWorkflowName(workflowName);
		return workflow == null;
	}

	@Transactional
    public WorkflowDTO editWorkflowVersion(Long workflowId, String version) {
    	if (workflowId == null || version == null || "".equals(version)) {
    		log.info("Dados inválidos de entrada, parâmetros de edição não podem ser vazios.");
    		return null;
    	}
    	
    	log.info("Processando edição de workflow");
    	Workflow workflow = workflowDao.findById(workflowId);
    	
    	if (workflow != null) {
    		workflow.setCurrentVersion(version);
    		
    		return WorkflowDTO.buildDTOByEntity(workflowDao.saveOrUpdateWorkflow(workflow));
    	}
    	
    	log.info("Problema ao processar edição do workflow de id {} com nova versão {}", workflowId, version);
    	return null;
    }
	
	public Workflow getByWorkflowName(String workflowName) {
		log.info("Buscando workflow por workflowName {}", workflowName);
		return workflowDao.findByWorkflowName(workflowName);
	}
	
	public Workflow getBySlug(String slug) {
		log.info("Buscando workflow por slug {}", slug);
		return workflowDao.findWorkflowBySlug(slug);
	}
	
	@Transactional
	public void removeWorkflow(Long workflowId) {
		log.info("Removendo workflow de id {}", workflowId);
		Workflow workflow = workflowDao.findById(workflowId);
		
		if (workflow == null) {
			log.error("Workflow de id {} não encontrado.", workflowId);
			return;
		}
		
		Documentation workflowDocumentation = workflowDocumentationDao.getDocumentationByWorkflowId(workflowId);
		
		if (workflowDocumentation != null) {
			workflowDocumentationDao.delete(workflowDocumentation);
		}
		
		workflowGraphDao.deleteByWorkflowId(workflowId);
		
		workflowDao.delete(workflow);
	}
	
	@Transactional
	public void deleteByScientificProjectId(Long scientificProjectId) {
		log.info("Removendo workflows por scientificProjectId {}", scientificProjectId);
		workflowDao.deleteByScientificProjectId(scientificProjectId);
	}
	
	public void deleteByScientificExperimentId(Long scientificExperimentId) {
		log.info("Removendo workflows por scientificExperimentId {}", scientificExperimentId);
		workflowDao.deleteByScientificExperimentId(scientificExperimentId);
	}
	
	public Map<String, Integer> countDependencies(Long workflowId) {
		Map<String, Integer> dependencies = new HashMap<String, Integer>();
		
		dependencies.put("tasks", taskService.countAllTasksByWorkflowId(workflowId));
		
		return dependencies;
	}
	
	public Boolean canAccessWorkflow(Long userId, WorkflowDTO workflow) {
		if (workflow == null) {
			return Boolean.FALSE;
	    }
		
		log.info("Checando se usuário de userId {} tem acesso ao workflow de id {}", userId, workflow.getWorkflowId());
				
		checkPermissionsForWorkflow(userId, workflow);
		
		return workflow.getIsEditablebyUser();
    }
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Boolean canAccessWorkflow(CurrentUser currentUser, Long workflowId) {
		log.info("Checando se usuário {} tem acesso ao workflow de id {}", currentUser.getUsername(), workflowId);
		
		Workflow workflow = workflowDao.findById(workflowId);
		
		if (workflow == null) {
			return Boolean.FALSE;
	    }
		
		WorkflowDTO workflowDTO = WorkflowDTO.buildDTOByEntity(workflow);
		checkPermissionsForWorkflow(currentUser.getUserId(), workflowDTO);
		
		return workflowDTO.getIsEditablebyUser();
    }
	
	public void checkPermissionsForWorkflows(List<WorkflowDTO> workflowDTOs, Long userId) {
		if (workflowDTOs == null || workflowDTOs.isEmpty()) {
			return;
		}
		
		for (WorkflowDTO workflow : workflowDTOs) {
			checkPermissionsForWorkflow(userId, workflow);
		}
	}

	public void checkPermissionsForWorkflow(Long userId, WorkflowDTO workflow) {
		if (workflow == null) {
			return;
		}
		
		for (UserDTO user : workflow.getResponsibleGroup().getGroupUsers()) {
		
			if (user.getUserId().equals(userId)) {
				workflow.setIsEditablebyUser(Boolean.TRUE);
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void runWorkflowAsynchronous(Long taskId, Long userId) throws EntityNotFoundException, InvalidEntityException, ExistingEntityException, PermissionDeniedException {
		log.info("Iniciando processo de criacao e envio de comando de execucao de Workflow da tarefa de id {} e usuario de userId {}", taskId, userId);
		
		Task task = taskService.getTaskById(taskId);
		Workflow workflow = task.getWorkflow();
		
		if (!canAccessWorkflow(userId, WorkflowDTO.buildDTOByEntity(workflow))) {
			throw new PermissionDeniedException(String.format("Não foi possível realizar operação de execução de workflow, usuário de userId [%s] não tem privilégios suficientes.", userId));
		}
		
		ModelFile modelFile = modelFileService.getCurrentModelFileEntityOfWorkflow(workflow.getWorkflowId());
		User user = userService.getUserEntityById(userId);
		
		if (workflow == null || modelFile == null || user == null) {
			log.error("Erro ao enviar mensagem de exeucução de sistema de workflow, entidades nulas.");
			throw new EntityNotFoundException("Não foi possível encontrar ou workflow ou user ou modelFile");
		}
		
		WorkflowExecution workflowExecution = WorkflowExecution.builder()
															   .workflow(modelFile.getWorkflow())
															   .modelFile(modelFile)
															   .userAgent(user)
															   .execTag(XmlParser.getXmlAttributeValue("workflow_exectag", 
																	   	"constraint", modelFile.getFileContenAsString()))
															   .executionDate(Calendar.getInstance(TimeZone.getTimeZone(CalendarDateUtils.DATE_LOCALE)))
															   .executionStatus(WorkflowExecutionStatus.RUNNING)
															   .swfms(workflow.getSwfms())
															   .workflowVersion(workflow.getCurrentVersion())
															   .build();
		
		if (workflowExecution.getExecTag() == null || "".equals(workflowExecution.getExecTag())) {
			throw new InvalidEntityException("Arquivo modelo deve conter atributo 'workflow_exectag' para ser executado");
		}
		
		if (workflowExecutionDao.getAllWorkflowExecutionsCount(workflowExecution.getExecTag()) > 0) {
			throw new ExistingEntityException(String.format("Já existe execução para o execTag '%s'", workflowExecution.getExecTag()));
		}
		
		WorkflowExecution workflowExecutionPersisted = workflowExecutionDao.saveOrUpdateWorkflowExecution(workflowExecution);

		workflowExecutionSender.sendMessage(WorkflowExecutionMessageDTO.buildDTOFromEntity(workflowExecutionPersisted));
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<WorkflowExecutionDTO> getWorkflowDashboardExecutionHistory(WorkflowExecutionFormFilter workflowExecutionFormFilter) {
		log.info("Buscando lista de histórico de execuções relevantes de workflow de id {} pelo filtro: pageNumber = {}, queryString = {}, initialDate = {}, finalDate = {}, executionStatus = {}, userId = {}", 
				workflowExecutionFormFilter.getWorkflowId(), workflowExecutionFormFilter.getPageNumber(), workflowExecutionFormFilter.getQueryString(), 
				workflowExecutionFormFilter.getInitialDate(), workflowExecutionFormFilter.getFinalDate(), workflowExecutionFormFilter.getExecutionStatus(), 
				workflowExecutionFormFilter.getUserId());

		return WorkflowExecutionDTO.convertEntityListToDTOList(workflowExecutionDao.findAllrelevantPaginated(workflowExecutionFormFilter));
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<WorkflowExecutionDTO> getWorkflowExecutionHistory(WorkflowExecutionFormFilter workflowExecutionFormFilter) {
		log.info("Buscando lista de histórico de execução de workflow de id {} e workflowName {} pelo filtro: pageNumber = {}, queryString = {}, initialDate = {}, finalDate = {}, executionStatus = {}, userId = {}", 
				workflowExecutionFormFilter.getWorkflowId(), workflowExecutionFormFilter.getWorkflowName(), workflowExecutionFormFilter.getPageNumber(), workflowExecutionFormFilter.getQueryString(), 
				workflowExecutionFormFilter.getInitialDate(), workflowExecutionFormFilter.getFinalDate(), workflowExecutionFormFilter.getExecutionStatus(), 
				workflowExecutionFormFilter.getUserId());

		return WorkflowExecutionDTO.convertEntityListToDTOList(workflowExecutionDao.findAllPaginated(workflowExecutionFormFilter));
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<WorkflowExecutionDTO> getDashboardWorkflowExecutionHistory(WorkflowExecutionFormFilter workflowExecutionFormFilter) {
		log.info("Buscando lista de histórico de execução relevantes de workflow de id {} pelo filtro: , userId = {}", workflowExecutionFormFilter.getWorkflowId(),
				workflowExecutionFormFilter.getUserId());
		
		return WorkflowExecutionDTO.convertEntityListToDTOList(workflowExecutionDao.findAllRelevantPaginated(workflowExecutionFormFilter));
	}

	public Integer countAllWorkflowExecutionHistory(WorkflowExecutionFormFilter workflowExecutionFormFilter) {
		log.info("Buscando contagem de histórico de execução de workflow de id {} e workflowName {} pelo filtro: queryString = {}, initialDate = {}, finalDate = {}, executionStatus = {}, usuário = {}", 
				workflowExecutionFormFilter.getWorkflowId(), workflowExecutionFormFilter.getWorkflowName(), workflowExecutionFormFilter.getQueryString(), workflowExecutionFormFilter.getInitialDate(), 
				workflowExecutionFormFilter.getFinalDate(), workflowExecutionFormFilter.getExecutionStatus(), workflowExecutionFormFilter.getUserId());
		
		return workflowExecutionDao.getAllWorkflowExecutionsCount(workflowExecutionFormFilter);
	}
	
	public Integer countAllWorkflowExecutionHistoryByWorkflow(Long workflowId) {
		log.info("Buscando contagem de histórico de execução de workflow de id {} ", workflowId);
		
		ModelFile modelFile = modelFileService.getCurrentModelFileEntityOfWorkflow(workflowId);
		
		if (modelFile != null) {
			WorkflowExecutionFormFilter workflowExecutionFilterDTO = WorkflowExecutionFormFilter.builder()
																							    .execTag(modelFile.getExecTag()).build();
			
			return workflowExecutionDao.getAllWorkflowExecutionsCount(workflowExecutionFilterDTO);
		}
		
		log.info("Não foi encontrado modelFile corrente para o workflow de id {}", workflowId);
		return 0;
	}
	
	public List<WorkflowExecutionMetadataDTO> getWorkflowExecutionMetadata(Integer pageNumber, Long workflowExecutionId) {
		WorkflowExecution workflowExecution = workflowExecutionDao.findById(workflowExecutionId);
		
		if (workflowExecution == null) {
			log.debug("Não foi encontrado nenhum registro de execução de workflow para o workflowExecutionId {}", workflowExecutionId);
			return null;
		}
		
		return WorkflowExecutionMetadataDTO.convertEntityListNoChildToDTOList(workflowExecutionMetadataDao.getExecutionMetadataByExecTag(pageNumber, workflowExecution.getExecTag()));
	}
	
	public Integer countAllWorkflowExecutionMetadata(Long workflowExecutionId) {
		WorkflowExecution workflowExecution = workflowExecutionDao.findById(workflowExecutionId);
		
		if (workflowExecution == null) {
			log.debug("Não foi encontrado nenhuma contagem de execução de workflow para o workflowExecutionId {}", workflowExecutionId);
			return null;
		}
		
		return workflowExecutionMetadataDao.countExecutionMetadataByExecTag(workflowExecution.getExecTag());
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<WorkflowDTO> getAllWorkflowsJson(String queryString, Long userId) {
		log.info("Buscando lista de workflows pelo filtro: queryString = {}", queryString);
		return WorkflowDTO.convertEntityListToDTOList(workflowDao.findAllByQueryString(queryString, userId));
	}
	
	public Integer getPageSize() {
		return workflowDao.getPaginationParameterConfig().getMaxResultsPerPage();
	}

}