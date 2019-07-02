package com.uff.scimanager.service.scientific.experiment;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Throwables;
import com.uff.scimanager.dao.documentation.DocumentationDao;
import com.uff.scimanager.dao.scientific.experiment.ScientificExperimentDao;
import com.uff.scimanager.domain.Documentation;
import com.uff.scimanager.domain.Role;
import com.uff.scimanager.domain.ScientificExperiment;
import com.uff.scimanager.domain.ScientificProject;
import com.uff.scimanager.domain.Workflow;
import com.uff.scimanager.domain.dto.ScientificExperimentDTO;
import com.uff.scimanager.domain.dto.UserDTO;
import com.uff.scimanager.domain.dto.WorkflowDTO;
import com.uff.scimanager.domain.form.ScientificExperimentForm;
import com.uff.scimanager.domain.security.CurrentUser;
import com.uff.scimanager.exception.EntityNotFoundException;
import com.uff.scimanager.exception.ExistingEntityException;
import com.uff.scimanager.service.scientific.project.ScientificProjectService;
import com.uff.scimanager.service.workflow.WorkflowService;
import com.uff.scimanager.util.EncrypterUtils;

@Service
public class ScientificExperimentService {

	private static final Logger log = LoggerFactory.getLogger(ScientificExperimentService.class);
	
	private final ScientificExperimentDao scientificExperimentDao;
	private final DocumentationDao experimentDocumentationDao;
	
	@Autowired
	private ScientificProjectService scientificProjectService;
	
	@Autowired
	private WorkflowService workflowService;

    @Autowired
	public ScientificExperimentService(ScientificExperimentDao scientificExperimentDao, DocumentationDao workflowDocumentationDao) {
		this.scientificExperimentDao = scientificExperimentDao;
		this.experimentDocumentationDao = workflowDocumentationDao;
	}
    
    public String getExperimentDocumentationHtml(Long scientificExperimentId) {
    	log.info("Recuperando documentação de workflow pelo id de experimento científico {}", scientificExperimentId);
    	
    	if (scientificExperimentId == null) {
    		return null;
    	}
    	
    	Documentation experimentDocumentation = experimentDocumentationDao.getDocumentationByScientificExperimentId(scientificExperimentId);
    	
    	if (experimentDocumentation == null) {
    		return null;
    	}
    	
    	log.info("Documentação recuperada com sucesso");
    	return experimentDocumentation.getHtmlDocumentationAsString();
    }
    
    public Documentation saveExperimentDocumentation(Long scientificExperimentId, String htmlDocumentation) {
    	log.info("Salvando documentação de workflow de id {}", scientificExperimentId);
    	
    	if (scientificExperimentId == null) {
    		return null;
    	}
    	
    	Documentation experimentDocumentation = experimentDocumentationDao.getDocumentationByScientificExperimentId(scientificExperimentId);
    	
    	if (experimentDocumentation == null) {
    		ScientificExperiment scientificExperiment = scientificExperimentDao.findById(scientificExperimentId);
    		
    		if (scientificExperiment == null) {
    			return null;
    		}
    		
    		experimentDocumentation = Documentation.builder()
    				                               .scientificExperiment(scientificExperiment).build();
    	}
    	
    	try {
    		experimentDocumentation.setHtmlDocumentation(htmlDocumentation.getBytes(EncrypterUtils.DEFAULT_ENCODING));
		} 
    	catch (UnsupportedEncodingException e) {
			log.error("Não foi possível recuperar documentação do experimento científico de id {}\n{}", 
					   scientificExperimentId, Throwables.getStackTraceAsString(e));
			
			return null;
		}
    	return experimentDocumentationDao.saveOrUpdateDocumentation(experimentDocumentation);
    }
    
	public ScientificExperiment getScientificExperimentEntityById(Long scientificExperimentId) {
		ScientificExperiment scientificExperiment = scientificExperimentDao.findById(scientificExperimentId);
		
		if (scientificExperiment != null) {
			log.info("Experimento científico com id {} buscado com sucesso", scientificExperimentId);
			return scientificExperiment;
		}
		
		log.info("Experimento científico com id {}, não encontrado", scientificExperimentId);
        return null;
	}
    
    @Transactional
	public ScientificExperimentDTO getScientificExperimentById(Long scientificExperimentId) {
    	ScientificExperiment scientificExperiment = scientificExperimentDao.findById(scientificExperimentId);
		
		if (scientificExperiment != null) {
			log.info("Experimento científico com id {} buscado com sucesso", scientificExperimentId);
			
			return ScientificExperimentDTO.buildDTOWithChildrenByEntity(scientificExperiment);
		}
		
		log.info("Experimento científico com id {}, não encontrado", scientificExperimentId);
        return null;
	}
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<ScientificExperimentDTO> getAllScientificExperiments(Integer pageNumber, String queryString, Long scientificProjectId, Long userId) {
		log.info("Buscando lista de experimentos científicos pelo filtro: pageNumber = {}, queryString = {}, scientificProjectId = {}, userId = {}", 
				 pageNumber, queryString, scientificProjectId, userId);
		
		return ScientificExperimentDTO.convertEntityListToDTOListWithChildren(scientificExperimentDao.findAllPaginated(pageNumber, queryString, scientificProjectId, userId));
	}
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<ScientificExperimentDTO> getAllDashboardScientificExperiments(Integer pageNumber, Long scientificProjectId, Long userId) {
		log.info("Buscando lista de experimentos científicos pelo filtro: pageNumber = {}, scientificProjectId = {}, userId = {}", pageNumber, scientificProjectId, userId);
		return ScientificExperimentDTO.convertEntityListToDTOListWithChildren(scientificExperimentDao.findAllRelevantPaginated(pageNumber, scientificProjectId, userId));
	}
    
    public Integer countAllScientificExperiments(String queryString, Long scientificProjectId, Long userId) {
		log.info("Contando lista de experimentos científicos pelo filtro: queryString = {}, queryString = {}, scientificProjectId = {}, userId = {}", 
				 queryString,  scientificProjectId, userId);
		
        return scientificExperimentDao.getAllScientificExperimentCount(queryString, scientificProjectId, userId);
    }
    
    public Integer countAllScientificExperiments(String queryString, Long scientificProjectId) {
		log.info("Contando lista de experimentos científicos pelo filtro: queryString = {}, scientificProjectId = {}", queryString, scientificProjectId);
        return scientificExperimentDao.getAllScientificExperimentCount(queryString, scientificProjectId);
    }
    
    @Transactional
    public ScientificExperimentDTO createScientificExperiment(ScientificExperimentForm scientificExperimentForm) {
    	log.info("Processando criação de workflow");
    	
    	ScientificExperiment scientificExperiment = new ScientificExperiment(scientificExperimentForm);
    	ScientificProject scientificProject = scientificProjectService.getScientificProjectEntityById(scientificExperimentForm.getScientificProjectId());
    	
    	if (scientificProject != null) {
    		scientificExperiment.setScientificProject(scientificProject);
    		return ScientificExperimentDTO.buildDTOByEntity(scientificExperimentDao.saveOrUpdateScientificExperiment(scientificExperiment));
    	}
    	
    	log.info("Problema ao processar criação do experimento científico para projeto científico de nome {}", scientificExperimentForm.getScientificProjectId());
    	return null;
    }
    
    @Transactional
    public ScientificExperiment saveOrUpdateScientificExperimentEntity(ScientificExperiment scientificExperiment) {
    	log.info("Iniciando update/save de experimento científico");
    	return scientificExperimentDao.saveOrUpdateScientificExperiment(scientificExperiment);
    }
    
    @Transactional
    public ScientificExperimentDTO editScientificExperiment(Long scientificExperimentId, String experimentName) throws ExistingEntityException, EntityNotFoundException {
    	if (scientificExperimentId == null || (experimentName == null || "".equals(experimentName))) {
    		log.info("Dados inválidos de entrada, parâmetros de edição não podem ser vazios.");
    		return null;
    	}
    	
    	log.info("Processando edição de experimento científico");
    	ScientificExperiment scientificExperiment = scientificExperimentDao.findById(scientificExperimentId);
    	
    	if (scientificExperiment == null) {
    		throw new EntityNotFoundException("Erro ao buscar experimento científico. Experimento científico com o scientificExperimentId " + scientificExperimentId + " não foi encontrada.");
    	}
    	
    		
    	if (!experimentName.equals(scientificExperiment.getExperimentName()) && !validateExistingExperimentName(experimentName)) {
			log.info("Experimento científico com o experimentName {} já existe", experimentName);
			throw new ExistingEntityException("Experimento científico com o experimentName " + experimentName + " já existe");
		}
		
    	scientificExperiment.setExperimentName(experimentName);
    	ScientificExperiment savedScientificExperiment = scientificExperimentDao.saveOrUpdateScientificExperiment(scientificExperiment);
		
		if (savedScientificExperiment != null) {
			log.info("Experimento científico de scientificExperimentId {}, atualizado com sucesso", scientificExperimentId);
			return ScientificExperimentDTO.buildDTOByEntity(savedScientificExperiment);
		}
		
		return ScientificExperimentDTO.buildDTOByEntity(scientificExperimentDao.saveOrUpdateScientificExperiment(scientificExperiment));
    }
    
    private boolean validateExistingExperimentName(String experimentName) {
    	log.info("Validando se existe experimento científico com o nome {}", experimentName);
    	ScientificExperiment scientificExperiment = getByExperimentName(experimentName);
		return scientificExperiment == null;
	}

	public ScientificExperiment getByExperimentName(String experimentName) {
		log.info("Buscando experimento científico por experimentName {}", experimentName);
		return scientificExperimentDao.findByExperimentName(experimentName);
	}
	
	public ScientificExperiment getBySlug(String slug) {
		log.info("Buscando experimento científico por slug {}", slug);
		return scientificExperimentDao.findScientificExperimentBySlug(slug);
	}
	
	@Transactional
	public void deleteByScientificProjectId(Long scientificProjectId) {
		log.info("Removendo experimento científico por scientificProjectId {}", scientificProjectId);
		scientificExperimentDao.deleteByScientificProjectId(scientificProjectId);
	}
	
	public Map<String, Integer> countDependencies(Long scientificExperimentId) {
		Map<String, Integer> dependencies = new HashMap<String, Integer>();
		
		dependencies.put("workflows", workflowService.countAllWorkflowsByExperimentId(scientificExperimentId));
		
		return dependencies;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void removeScientificExperiment(Long scientificExperimentId) {
		ScientificExperiment scientificExperiment = scientificExperimentDao.findById(scientificExperimentId);
		
		if (scientificExperiment == null) {
			log.error("Experimento científico de id {} não encontrado.", scientificExperimentId);
			return;
		}
		
		Documentation experimentDocumentation = experimentDocumentationDao.getDocumentationByScientificExperimentId(scientificExperimentId);
		
		if (experimentDocumentation != null) {
			experimentDocumentationDao.delete(experimentDocumentation);
		}
		
		workflowService.deleteByScientificExperimentId(scientificExperimentId);
		
		scientificExperimentDao.delete(scientificExperiment);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Boolean canAccessScientificExperiment(CurrentUser currentUser, Long scientificExperimentId) {
		log.info("Checando se usuário {} tem acesso ao experimento científico de id {}", currentUser, scientificExperimentId);
		
		if (currentUser.getUserRole().equals(Role.ADMIN)) {
			return Boolean.TRUE;
		}
		
		ScientificExperiment scientificExperiment = scientificExperimentDao.findById(scientificExperimentId);
		
		if (scientificExperiment == null) {
			return Boolean.FALSE;
	    } 
		else {
			log.info("Experimento científico com id {} buscado com sucesso", scientificExperimentId);
			initializeEntities(scientificExperiment);
		}
		
		ScientificExperimentDTO scientificExperimentDTO = ScientificExperimentDTO.buildDTOWithChildrenByEntity(scientificExperiment);
		checkPermissionForScientificExperiment(currentUser.getUserId(), scientificExperimentDTO);
		
		return scientificExperimentDTO.getIsEditablebyUser();
    }
	
	public void checkPermissionsForScientificExperiments(List<ScientificExperimentDTO> scientificExperimentDTOs, Long userId) {
		if (scientificExperimentDTOs == null || scientificExperimentDTOs.isEmpty()) {
			return;
		}
		
		for (ScientificExperimentDTO scientificExperimentDTO : scientificExperimentDTOs) {
			checkPermissionForScientificExperiment(userId, scientificExperimentDTO);
		}
	}
	
	public void checkPermissionForScientificExperiment(Long userId, ScientificExperimentDTO scientificExperimentDTO) {
		if (userId == null || scientificExperimentDTO == null) {
			return;
		}
		
		if (scientificExperimentDTO.getWorkflows() != null) {
			for (WorkflowDTO workflow : scientificExperimentDTO.getWorkflows()) {
			
				for (UserDTO user : workflow.getResponsibleGroup().getGroupUsers()) {
				
					if (user.getUserId().equals(userId)) {
						scientificExperimentDTO.setIsEditablebyUser(Boolean.TRUE);
						return;
					}
				}
			}
		}
	}
	
	private void initializeEntities(ScientificExperiment scientificExperiment) {
		if (scientificExperiment.getWorkflows() != null) {
			scientificExperiment.getWorkflows().size();
			
			for (Workflow workflow : scientificExperiment.getWorkflows()) {
				workflow.getResponsibleGroup().getGroupUsers().size();
			}
		}
	}

	public Integer getPageSize() {
		return scientificExperimentDao.getPaginationParameterConfig().getMaxResultsPerPage();
	}

}