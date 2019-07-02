package com.uff.scimanager.service.phase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uff.scimanager.domain.ScientificProject;
import com.uff.scimanager.dao.phase.PhaseDao;
import com.uff.scimanager.domain.Phase;
import com.uff.scimanager.domain.dto.PhaseDTO;
import com.uff.scimanager.domain.form.PhaseForm;
import com.uff.scimanager.exception.EntityNotFoundException;
import com.uff.scimanager.exception.ExistingEntityException;
import com.uff.scimanager.service.scientific.project.ScientificProjectService;
import com.uff.scimanager.service.task.TaskService;

@Service
public class PhaseService {

	private static final Logger log = LoggerFactory.getLogger(PhaseService.class);
	
	private final ScientificProjectService scientificProjectService;
	private final PhaseDao phaseDao;
	
	@Autowired
	private TaskService taskService;
	
    @Autowired
	public PhaseService(PhaseDao phaseDao, ScientificProjectService scientificProjectService) {
		this.phaseDao = phaseDao;
		this.scientificProjectService = scientificProjectService;
	}
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<PhaseDTO> getAllPhasesByProjectId(Long scientificProjectId, Integer pageNumber, String queryString) {
		log.info("Buscando lista de fases pelo filtro: scientificProjectId = {} ,pageNumber = {}, queryString = {}", scientificProjectId, pageNumber, queryString);
		return PhaseDTO.convertEntityListToDTOList(phaseDao.findAllPaginatedByProjectId(scientificProjectId, pageNumber, queryString));
	}
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<PhaseDTO> getAllDashboardPhasesByProjectId(Long scientificProjectId, Integer pageNumber, String queryString) {
		log.info("Buscando lista de fases pelo filtro: scientificProjectId = {}, pageNumber = {}, queryString = {}", scientificProjectId, pageNumber, queryString);
		return PhaseDTO.convertEntityListToDTOList(phaseDao.findAllPaginatedByProjectId(scientificProjectId, pageNumber, queryString));
	}
    
    public Integer countAllPhases(Long scientificProjectId, String queryString) {
		log.info("Contando lista de fases pelo filtro: queryString = {}, scientificProjectId = {}", queryString, scientificProjectId);
        return phaseDao.getAllPhasesCount(scientificProjectId, queryString);
    }
    
    @Transactional
    public PhaseDTO createPhase(PhaseForm phaseForm) {
    	log.info("Processando criação de fase");
    	ScientificProject scientificProject = scientificProjectService.getScientificProjectEntityById(phaseForm.getProjectId());
    	
    	Phase phase = Phase.builder()
    			           .scientificProject(scientificProject)
    			           .phaseName(phaseForm.getPhaseName())
    			           .allowExecution(phaseForm.getAllowExecution()).build();
    	
    	return PhaseDTO.buildDTOByEntity(phaseDao.saveOrUpdatePhase(phase));
    }
    
    @Transactional
    public PhaseDTO editPhase(Long phaseId, String phaseName, Boolean allowExecution) throws EntityNotFoundException, ExistingEntityException {
    	if (phaseId == null || (phaseName == null || "".equals(phaseName)) || allowExecution == null) {
    		log.info("Dados inválidos de entrada, parâmetros de edição não podem ser vazios.");
    		return null;
    	}
    	
		Phase phase = phaseDao.findById(phaseId);
		if (phase == null) {
			throw new EntityNotFoundException("Erro ao buscar fase. Fase com o id " + phaseId + " não foi encontrada.");
		}
		
		if (!phaseName.equals(phase.getPhaseName()) && validateExistingPhaseName(phase.getScientificProject().getScientificProjectId(), phaseName)) {
			log.info("Fase com a phaseName {} já existe", phaseName);
			throw new ExistingEntityException("fase com o nome " + phaseName + " já existe.");
		}
		
		phase.setPhaseName(phaseName);
		phase.setAllowExecution(allowExecution);
		Phase savedPhase = phaseDao.saveOrUpdatePhase(phase);
		
		if (savedPhase != null) {
			log.info("Nome da fase atualizado com sucesso para {}", phaseName);
			return PhaseDTO.buildDTOByEntity(savedPhase);
		}
		
		log.error("Erro ao editar nome da fase.");
		return null;
    }
    
    public Boolean validateExistingPhaseName(Long scientificProjectId, String phaseName) {
		log.info("Validando se existe fase com o nome {} no projeto científico de id {}", phaseName, scientificProjectId);
		PhaseDTO phaseDTO = getPhaseOfProjectByPhaseName(scientificProjectId, phaseName);
		return phaseDTO != null;
	}
    
    public Phase getPhaseEntityOfProjectByPhaseName(Long scientificProjectId, String phaseName) {
    	Phase phase = phaseDao.getPhaseOfProjectByPhaseName(scientificProjectId, phaseName);
    	
    	if (phase != null) {
    		log.info("Fase de phaseName {}, do projeto científico de id {} buscado com sucesso", phaseName, scientificProjectId);
    		return phase;
    	}
    	
    	log.info("Fase não pode ser encontrado");
        return null;
    }
    
    public Phase getPhaseEntityBySlug(String slug) {
    	Phase phase = phaseDao.findPhaseBySlug(slug);
    	
    	if (phase != null) {
    		log.info("Fase de slug {} buscado com sucesso", slug);
    		return phase;
    	}
    	
    	log.info("Fase não pode ser encontrado");
        return null;
    }
    
    public Phase saveOrUpdatePhase(Phase phase) {
    	return phaseDao.saveOrUpdatePhase(phase);
    }

    private PhaseDTO getPhaseOfProjectByPhaseName(Long scientificProjectId, String phaseName) {
    	Phase phase = phaseDao.getPhaseOfProjectByPhaseName(scientificProjectId, phaseName);
    	
    	if (phase != null) {
    		log.info("Fase de phaseName {}, do projeto científico de id {} buscado com sucesso", phaseName, scientificProjectId);
    		return PhaseDTO.buildDTOByEntity(phase);
    	}
    	
    	log.info("Fase não pode ser encontrado");
        return null;
    }

	@Transactional
	public void removePhase(Long phaseId) {
		Phase phase = phaseDao.findById(phaseId);
		
		if (phase == null) {
			log.error("Fase de id {} não encontrada.", phaseId);
			return;
		}
		
		phaseDao.delete(phase);
	}
	
	@Transactional
	public void deleteByScientificProjectId(Long scientificProjectId) {
		log.info("Removendo fases por scientificProjectId {}", scientificProjectId);
		phaseDao.deleteByScientificProjectId(scientificProjectId);
	}
	
	public Map<String, Integer> countDependencies(Long phaseId) {
		Map<String, Integer> dependencies = new HashMap<String, Integer>();
		
		dependencies.put("tasks", taskService.countAllTasksByPhaseId(phaseId));
		
		return dependencies;
	}
	
	public Integer getPageSize() {
		return phaseDao.getPaginationParameterConfig().getMaxResultsPerPage();
	}

}