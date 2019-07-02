package com.uff.scimanager.service.scientific.project;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uff.scimanager.dao.documentation.DocumentationDao;
import com.uff.scimanager.dao.scientific.project.ScientificProjectDao;
import com.uff.scimanager.domain.Documentation;
import com.uff.scimanager.domain.Phase;
import com.uff.scimanager.domain.ScientificExperiment;
import com.uff.scimanager.domain.ScientificProject;
import com.uff.scimanager.domain.User;
import com.uff.scimanager.domain.UserGroup;
import com.uff.scimanager.domain.Workflow;
import com.uff.scimanager.domain.dto.ScientificProjectDTO;
import com.uff.scimanager.domain.dto.UserDTO;
import com.uff.scimanager.domain.dto.WorkflowDTO;
import com.uff.scimanager.domain.form.ScientificProjectFrom;
import com.uff.scimanager.domain.security.CurrentUser;
import com.uff.scimanager.exception.EntityNotFoundException;
import com.uff.scimanager.exception.ExistingEntityException;
import com.uff.scimanager.service.phase.PhaseService;
import com.uff.scimanager.service.scientific.experiment.ScientificExperimentService;
import com.uff.scimanager.service.task.TaskService;
import com.uff.scimanager.service.user.UserService;
import com.uff.scimanager.service.user.group.UserGroupService;
import com.uff.scimanager.service.workflow.WorkflowService;

@Service
public class ScientificProjectService {

	private static final Logger log = LoggerFactory.getLogger(ScientificProjectService.class);
	
	@Autowired	
	private WorkflowService workflowService;
	
	@Autowired	
	private ScientificExperimentService scientificExperimentService;
	
	@Autowired	
	private TaskService taskService;
	
	@Autowired	
	private UserService userService;
	
	@Autowired	
	private UserGroupService userGroupService;
	
	@Autowired	
	private PhaseService phaseService;
	
	private final ScientificProjectDao scientificProjectDao;
	private final DocumentationDao projectDocumentationDao;
	
    @Autowired
	public ScientificProjectService(ScientificProjectDao scientificProjectDao, DocumentationDao projectDocumentationDao) {
		this.scientificProjectDao = scientificProjectDao;
		this.projectDocumentationDao = projectDocumentationDao;
	}
    
    public String getProjectDocumentationHtml(Long scientificProjectId) {
    	log.info("Recuperando documentação de projeto científico pelo id de projeto científico {}", scientificProjectId);
    	
    	if (scientificProjectId == null) {
    		return null;
    	}
    	
    	Documentation projectDocumentation = projectDocumentationDao.getDocumentationByScientificProjectId(scientificProjectId);
    	
    	if (projectDocumentation == null) {
    		return null;
    	}
    	
    	log.info("Documentação recuperada com sucesso");
    	return projectDocumentation.getHtmlDocumentationAsString();
    }
    
    public Documentation saveProjectDocumentation(Long scientificProjectId, String htmlDocumentation) {
    	log.info("Salvando documentação de projeto científico de id {}", scientificProjectId);
    	
    	if (scientificProjectId == null) {
    		return null;
    	}
    	
    	Documentation projectDocumentation = projectDocumentationDao.getDocumentationByScientificProjectId(scientificProjectId);
    	
    	if (projectDocumentation == null) {
    		ScientificProject scientificProject = scientificProjectDao.findById(scientificProjectId);
    		
    		if (scientificProject == null) {
    			return null;
    		}
    		
    		projectDocumentation = Documentation.builder()
    				                            .scientificProject(scientificProject).build();
    	}
    	
    	projectDocumentation.setHtmlDocumentation(htmlDocumentation.getBytes(StandardCharsets.UTF_8));
    	return projectDocumentationDao.saveOrUpdateDocumentation(projectDocumentation);
    }
	
    @Transactional(propagation = Propagation.REQUIRES_NEW)
	public ScientificProjectDTO getScientificProjectById(Long scientificProjectId) {
    	ScientificProject scientificProject = scientificProjectDao.findById(scientificProjectId);
		
		if (scientificProject != null) {
			log.info("Projeto científico com id {} buscado com sucesso", scientificProjectId);
			return ScientificProjectDTO.buildDTOWithChildrenByEntity(scientificProject);
		}
		
		log.info("Projeto científico com id {}, não encontrado", scientificProjectId);
        return null;
    }
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ScientificProjectDTO getScientificProjectAndChildrenById(Long scientificProjectId) {
		ScientificProject scientificProject = scientificProjectDao.findById(scientificProjectId);
		
		if (scientificProject != null) {
			log.info("Projeto científico com id {} buscado com sucesso", scientificProjectId);
			initializeEntities(scientificProject);
			return ScientificProjectDTO.buildDTOWithChildrenByEntity(scientificProject);
		}
		
		log.info("Projeto científico com id {}, não encontrado", scientificProjectId);
        return null;
    }
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ScientificProject getScientificProjectEntityAndChildrenById(Long scientificProjectId) {
		ScientificProject scientificProject = scientificProjectDao.findById(scientificProjectId);
		
		if (scientificProject != null) {
			log.info("Projeto científico com id {} buscado com sucesso", scientificProjectId);
			
			initializeEntities(scientificProject);
			
			return scientificProject;
		}
		
		log.info("Projeto científico com id {}, não encontrado", scientificProjectId);
        return null;
    }

	private void initializeEntities(ScientificProject scientificProject) {
		if (scientificProject.getPhases() != null) {
			scientificProject.getPhases().size();
		}
		
		if (scientificProject.getWorkflows() != null) {
			scientificProject.getWorkflows().size();
			
			for (Workflow workflow : scientificProject.getWorkflows()) {
				workflow.getResponsibleGroup().getGroupUsers().size();
			}
		}
	}
	
	public ScientificProject getScientificProjectEntityById(Long scientificProjectId) {
		ScientificProject scientificProject = scientificProjectDao.findById(scientificProjectId);
		
		if (scientificProject != null) {
			log.info("Projeto científico com id {} buscado com sucesso", scientificProjectId);
			return scientificProject;
		}
		
		log.info("Projeto científico com id {}, não encontrado", scientificProjectId);
        return null;
    }
	
	public ScientificProjectDTO getScientificProjectByProjectName(String projectName) {
		ScientificProject scientificProject = scientificProjectDao.findScientificProjectByProjectName(projectName);
    	
    	if (scientificProject != null) {
    		log.info("Projeto científico buscado pelo projectName {}, encontrado com sucesso", projectName);
    		return ScientificProjectDTO.buildDTOByEntity(scientificProject);
    	}
    	
    	log.info("Projeto científico com projectName {}, não encontrado", projectName);
        return null;
	}
	
	public ScientificProject getScientificProjectEntityByProjectName(String projectName) {
		ScientificProject scientificProject = scientificProjectDao.findScientificProjectByProjectName(projectName);
    	
    	if (scientificProject != null) {
    		log.info("Projeto científico buscado pelo projectName {}, encontrado com sucesso", projectName);
    		return scientificProject;
    	}
    	
    	log.info("Projeto científico com projectName {}, não encontrado", projectName);
        return null;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<ScientificProjectDTO> getAllScientificProjects(Integer pageNumber, String queryString, Long userId) {
		log.info("Buscando lista de projetos científicos pelo filtro: pageNumber = {}, queryString = {}, userId = {}", 
				 pageNumber, queryString, userId);
		
		List<ScientificProject> scientificProjects = scientificProjectDao.findAllPaginated(pageNumber, queryString, userId);
		
		if (scientificProjects != null && !scientificProjects.isEmpty()) {
			for (ScientificProject scientificProject : scientificProjects) {
				initializeEntities(scientificProject);
			}
		}
		
		return ScientificProjectDTO.convertEntityListToDTOListWithChildren(scientificProjects);
    }
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<ScientificProjectDTO> getAllScientificProjectsBoards(Integer pageNumber, String queryString, Long userId) {
		log.info("Buscando lista de projetos científicos pelo filtro: pageNumber = {}, queryString = {}, userId = {}", 
				 pageNumber, queryString, userId);
		
		List<ScientificProject> scientificProjects = scientificProjectDao.findAllBoardsPaginated(pageNumber, queryString, userId);
		
		if (scientificProjects != null && !scientificProjects.isEmpty()) {
			for (ScientificProject scientificProject : scientificProjects) {
				initializeEntities(scientificProject);
			}
		}
		
		return ScientificProjectDTO.convertEntityListToDTOListWithChildren(scientificProjects);
    }
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<ScientificProjectDTO> getAllUserScientificProjectsBoards(CurrentUser currentUser) {
		log.info("Buscando lista de projetos científicos pelo filtro: userId = {}", currentUser.getUserId());
		
		List<ScientificProject> scientificProjects = scientificProjectDao.findAllRelevantPaginated(currentUser);
		
		if (scientificProjects != null && !scientificProjects.isEmpty()) {
			for (ScientificProject scientificProject : scientificProjects) {
				initializeEntities(scientificProject);
			}
		}
		
		List<ScientificProjectDTO> scientificProjectDTOs = ScientificProjectDTO.convertEntityListToDTOListWithChildren(scientificProjects);
		checkPermissionsForScientificProjects(scientificProjectDTOs, currentUser.getUserId());
		
		return scientificProjectDTOs;
    }
	
	@Transactional
    public ScientificProjectDTO createScientificProject(ScientificProjectFrom scientificProjectForm) {
    	return ScientificProjectDTO.buildDTOByEntity(
    				scientificProjectDao.saveOrUpdateScientificProject(new ScientificProject(scientificProjectForm)));
    }
    
    public Integer countAllScientificProjects(String queryString, Long userId) {
		log.info("Conta lista de projetos científicos pelo filtro: queryString = {}, userId = {}", queryString, userId);
        return scientificProjectDao.getAllScientificProjectsCount(queryString, userId);
    }
    
    public Integer countAllScientificProjectsBoards(String queryString, Long userId) {
		log.info("Conta lista de projetos científicos pelo filtro: queryString = {}, userId = {}", queryString, userId);
        return scientificProjectDao.getAllScientificProjectsBoardsCount(queryString, userId);
    }

	public ScientificProjectDTO editScientificProject(Long scientificProjectId, String projectName) throws ExistingEntityException, EntityNotFoundException {
		if (scientificProjectId == null || (projectName == null || "".equals(projectName))) {
			log.info("Dados de entrada inválidos, scientificProjectId ou projectName nulos");
			return null;
		}
		
		ScientificProject scientificProject = scientificProjectDao.findById(scientificProjectId);
		
		if (scientificProject == null) {
    		throw new EntityNotFoundException("Erro ao buscar projeto científico. Projeto científico com o scientificProjectId " + scientificProjectId + " não foi encontrada.");
		}
		
		if (!projectName.equals(scientificProject.getProjectName()) && !validateExistingProjectName(projectName)) {
			log.info("Projeto científico com o projectName {} já existe", projectName);
			throw new ExistingEntityException("Projeto científico com o projectName " + projectName + " já existe");
		}
		
		scientificProject.setProjectName(projectName);
		ScientificProject savedScientificProject = scientificProjectDao.saveOrUpdateScientificProject(scientificProject); 
		
		if (savedScientificProject != null) {
			log.info("Projeto científico atualizado com sucesso.");
			return ScientificProjectDTO.buildDTOByEntity(savedScientificProject);
		}
		
		log.error("Erro ao atualizar projeto científico com workflow criado.");
		return null;
	}
	
	private boolean validateExistingProjectName(String projectName) {
    	log.info("Validando se existe projeto científico com o nome {}", projectName);
    	ScientificProjectDTO scientificProjectDTO = getScientificProjectByProjectName(projectName);
		return scientificProjectDTO == null;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void removeScientificProject(Long scientificProjectId) {
		ScientificProject scientificProject = scientificProjectDao.findById(scientificProjectId);
		
		if (scientificProject == null) {
			log.error("Projeto científico de id {} não encontrado.", scientificProjectId);
			return;
		}
		
		Documentation projectDocumentation = projectDocumentationDao.getDocumentationByScientificProjectId(scientificProjectId);
		
		if (projectDocumentation != null) {
			projectDocumentationDao.delete(projectDocumentation);
		}
		
		taskService.deleteByScientificProjectId(scientificProjectId);
		phaseService.deleteByScientificProjectId(scientificProjectId);
		workflowService.deleteByScientificProjectId(scientificProjectId);
		scientificExperimentService.deleteByScientificProjectId(scientificProjectId);
		
		scientificProjectDao.delete(scientificProject);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Boolean canAccessScientificProject(CurrentUser currentUser, Long scientificProjectId) {
		log.info("Checando se usuário {} tem acesso ao projeto científico de id {}", currentUser, scientificProjectId);
		
		ScientificProject scientificProject = scientificProjectDao.findById(scientificProjectId);
		
		if (scientificProject == null) {
			return Boolean.FALSE;
	    } 
		else {
			log.info("Projeto científico com id {} buscado com sucesso", scientificProjectId);
			initializeEntities(scientificProject);
		}
		
		ScientificProjectDTO scientificProjectDTO = ScientificProjectDTO.buildDTOWithChildrenByEntity(scientificProject);
		checkPermissionForScientificProject(currentUser.getUserId(), scientificProjectDTO);
		
		return scientificProjectDTO.getIsEditablebyUser();
    }
	
	public void checkPermissionsForScientificProjects(List<ScientificProjectDTO> scientificProjectDTOs, Long userId) {
		if (scientificProjectDTOs == null || scientificProjectDTOs.isEmpty()) {
			return;
		}
		
		for (ScientificProjectDTO scientificProjectDTO : scientificProjectDTOs) {
			checkPermissionForScientificProject(userId, scientificProjectDTO);
		}
	}
	
	public void checkPermissionForScientificProject(Long userId, ScientificProjectDTO scientificProjectDTO) {
		if (userId == null || scientificProjectDTO == null) {
			return;
		}
		
		if (scientificProjectDTO.getWorkflows() != null) {
			for (WorkflowDTO workflow : scientificProjectDTO.getWorkflows()) {
			
				for (UserDTO user : workflow.getResponsibleGroup().getGroupUsers()) {
				
					if (user.getUserId().equals(userId)) {
						scientificProjectDTO.setIsEditablebyUser(Boolean.TRUE);
						workflow.setIsEditablebyUser(Boolean.TRUE);
					}
				}
			}
		}
	}
	
	public Integer getPageSize() {
		return scientificProjectDao.getPaginationParameterConfig().getMaxResultsPerPage();
	}

	public void syncScientificProject(ScientificProject scientificProject) {
		if (scientificProject == null) {
			log.info("Projeto nulo não pôde ser sincronizado.");
		}
			
		ScientificProject scientificProjectDatabase = scientificProjectDao.findScientificProjectBySlug(scientificProject.getSlug());
		
		if (scientificProjectDatabase == null) {
			scientificProjectDatabase = getScientificProjectEntityByProjectName(scientificProject.getProjectName());
		}
		
		if (scientificProjectDatabase == null) {
			scientificProjectDatabase = scientificProjectDao.saveOrUpdateScientificProject(
					ScientificProject.builder()
						.projectName(scientificProject.getProjectName())
						.slug(scientificProject.getSlug())
						.build());
			
		} else {
			scientificProjectDatabase.setSlug(scientificProject.getSlug());
			scientificProjectDao.saveOrUpdateScientificProject(scientificProjectDatabase);
		}
		
		syncPhases(scientificProject, scientificProjectDatabase);	
		syncScientificExperiments(scientificProject, scientificProjectDatabase);
	}

	private void syncScientificExperiments(ScientificProject scientificProject,
			ScientificProject scientificProjectDatabase) {
		
		if (scientificProject.getScientificExperiments() != null && !scientificProject.getScientificExperiments().isEmpty()) {
			for (ScientificExperiment scientificExperiment : scientificProject.getScientificExperiments()) {
				
				ScientificExperiment scientificExperimentDatabase = scientificExperimentService.
						getBySlug(scientificExperiment.getSlug());
				
				if (scientificExperimentDatabase == null) {
					scientificExperimentDatabase = scientificExperimentService.
							getByExperimentName(scientificExperiment.getExperimentName());
				}
				
				if (scientificExperimentDatabase == null) {
					scientificExperimentDatabase = scientificExperimentService.saveOrUpdateScientificExperimentEntity(
							ScientificExperiment.builder()
								.scientificProject(scientificProjectDatabase)
								.experimentName(scientificExperiment.getExperimentName())
								.slug(scientificExperiment.getSlug())
								.build());
					
				} else {
					scientificExperimentDatabase.setSlug(scientificExperiment.getSlug());
					scientificExperimentService.saveOrUpdateScientificExperimentEntity(scientificExperimentDatabase);
				}
				
				syncWorkflows(scientificProjectDatabase, scientificExperiment, scientificExperimentDatabase);
			}
		}
	}

	private void syncWorkflows(ScientificProject scientificProjectDatabase, ScientificExperiment scientificExperiment,
			ScientificExperiment scientificExperimentDatabase) {
		
		if (scientificExperiment.getWorkflows() != null && !scientificExperiment.getWorkflows().isEmpty()) {
			for (Workflow workflow : scientificExperiment.getWorkflows()) {
				
				syncUserGroup(workflow.getResponsibleGroup());
				
				Workflow workflowDatabase = workflowService.getBySlug(workflow.getSlug());
				
				if (workflowDatabase == null) {
					workflowDatabase = workflowService.
							getByWorkflowName(workflow.getWorkflowName());
				}
				
				if (workflowDatabase == null) {
					workflowDatabase = workflowService.saveOrUpdateWorkflowEntity(
							Workflow.builder()
								.slug(workflow.getSlug())
								.currentVersion(workflow.getCurrentVersion())
								.swfms(workflow.getSwfms())
								.scientificProject(scientificProjectDatabase)
								.scientificExperiment(scientificExperimentDatabase)
								.workflowName(workflow.getWorkflowName())
								.build());
					
				} else {
					workflowDatabase.setSlug(workflow.getSlug());
					workflowService.saveOrUpdateWorkflowEntity(workflowDatabase);
				}
			}
		}
	}

	private void syncUserGroup(UserGroup responsibleGroup) {
		if (responsibleGroup != null) {
			Set<User> syncUsers = syncUsers(responsibleGroup.getGroupUsers());
			
			UserGroup userGroupDatabase = userGroupService.getUserGroupEntityBySlug(responsibleGroup.getSlug());
			
			if (userGroupDatabase == null) {
				userGroupDatabase = userGroupService.getUserGroupEntityByGroupName(responsibleGroup.getGroupName());
			}
			
			if (userGroupDatabase == null) {
				userGroupDatabase = new UserGroup();
				userGroupDatabase.setGroupName(responsibleGroup.getGroupName());
				userGroupDatabase.setSlug(responsibleGroup.getSlug());
				
				if (syncUsers != null && !syncUsers.isEmpty()) {
					for (User syncUser : syncUsers) {
						userGroupDatabase.addUserToUserGroup(syncUser);
					}
				}
				
				userGroupService.saveOrUpdateUserGroup(userGroupDatabase);
				
			} else {
				userGroupDatabase.setSlug(responsibleGroup.getSlug());
				
				if (syncUsers != null && !syncUsers.isEmpty()) {
					for (User syncUser : syncUsers) {
						userGroupDatabase.addUserToUserGroup(syncUser);
					}
				}
				
				userGroupService.saveOrUpdateUserGroup(userGroupDatabase);
			}
			
		}
	}

	private Set<User> syncUsers(Set<User> groupUsers) {
		Set<User> syncUsers = new HashSet<User>();
		
		if (groupUsers != null && !groupUsers.isEmpty()) {
			return syncUsers;
		}
		
		for (User groupUser : groupUsers) {
			User syncUser = userService.syncUser(groupUser);
			
			if (syncUser != null) {
				syncUsers.add(syncUser);
			}
		}
		
		return syncUsers;
	}

	private void syncPhases(ScientificProject scientificProject, ScientificProject scientificProjectDatabase) {
		if (scientificProject.getPhases() != null && !scientificProject.getPhases().isEmpty()) {
			for (Phase phase : scientificProject.getPhases()) {
				
				Phase phaseDatabase = phaseService.getPhaseEntityBySlug(phase.getSlug());
				
				if (phaseDatabase == null) {
					phaseDatabase = phaseService.
							getPhaseEntityOfProjectByPhaseName(scientificProjectDatabase.getScientificProjectId(), phase.getPhaseName());
				}
				
				if (phaseDatabase == null) {
					phaseDatabase = phaseService.saveOrUpdatePhase(
							Phase.builder()
							.scientificProject(scientificProjectDatabase)
							.phaseName(phase.getPhaseName())
							.slug(phase.getSlug())
							.build());
					
				} else {
					phaseDatabase.setSlug(phase.getSlug());
					phaseService.saveOrUpdatePhase(phaseDatabase);
				}
			}
		}
	}

}