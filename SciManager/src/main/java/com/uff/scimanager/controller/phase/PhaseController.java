package com.uff.scimanager.controller.phase;

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
import com.uff.scimanager.domain.dto.ScientificProjectDTO;
import com.uff.scimanager.domain.dto.PhaseDTO;
import com.uff.scimanager.domain.dto.filter.FilterFieldDTO;
import com.uff.scimanager.domain.dto.filter.FilterDTO;
import com.uff.scimanager.domain.form.PhaseForm;
import com.uff.scimanager.domain.form.validator.PhaseFormValidator;
import com.uff.scimanager.domain.security.CurrentUser;
import com.uff.scimanager.exception.ExistingEntityException;
import com.uff.scimanager.service.phase.PhaseService;
import com.uff.scimanager.service.scientific.project.ScientificProjectService;
import com.uff.scimanager.util.ResponseMessageUtil;

@Controller
@RequestMapping("/scientific-project/phase")
public class PhaseController {
	
	private static final Logger log = LoggerFactory.getLogger(PhaseController.class);
	
	private final ScientificProjectService scientificProjectService;
    private final PhaseService phaseService;
	private final PhaseFormValidator phaseFormValidator;
	
	@Autowired
	public PhaseController(PhaseService phaseService, ScientificProjectService scientificProjectService, PhaseFormValidator phaseFormValidator) {
		this.phaseService = phaseService;
		this.scientificProjectService = scientificProjectService;
		this.phaseFormValidator = phaseFormValidator;
	}
	
	@InitBinder("phaseForm")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(phaseFormValidator);
    }
	
	@PreAuthorize("@scientificProjectService.canAccessScientificProject(principal, #scientificProjectId) || hasAuthority('ADMIN')")
    @RequestMapping(value = "/{scientificProjectId}/dashboard/phases", method = RequestMethod.GET)
    public String getDashboardPhases(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
						    	   	@PathVariable Long scientificProjectId,
						    	   	Model model, Authentication authentication) {
    	
    	log.info("Carregando página de fases do projeto de id {}", scientificProjectId);
        model.addAttribute("phaseDTOs", phaseService.getAllPhasesByProjectId(scientificProjectId, pageNumber, null));
        
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    	Long userId = currentUser != null ? currentUser.getUserId() : null;
    	
        ScientificProjectDTO scientificProjectDTO = scientificProjectService.getScientificProjectAndChildrenById(scientificProjectId);
        scientificProjectService.checkPermissionForScientificProject(userId, scientificProjectDTO);
        
        model.addAttribute("phaseForm", PhaseForm.buildEmptyPhaseForm());
        model.addAttribute("scientificProjectDTO", scientificProjectDTO);
        model.addAttribute("searchPhasesListUrl", "/scientific-project/phase/" + scientificProjectId + "/dashboard/phases");
        
        return "fragments/phase/project-phase-list-fragment";
    }

	@PreAuthorize("@scientificProjectService.canAccessScientificProject(principal, #scientificProjectId) || hasAuthority('ADMIN')")
    @RequestMapping(value = "/{scientificProjectId}/phases-list", method = RequestMethod.GET)
    public String getPhasesList(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
							   @RequestParam(value = "search", required = false) String queryString,
							   @PathVariable Long scientificProjectId,
							   Model model, Authentication authentication) {
    	
    	log.info("Carregando página de fases do projeto de id {}", scientificProjectId);
        model.addAttribute("phaseDTOs", phaseService.getAllPhasesByProjectId(scientificProjectId, pageNumber, queryString));
        
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    	Long userId = currentUser != null ? currentUser.getUserId() : null;
    	
        ScientificProjectDTO scientificProjectDTO = scientificProjectService.getScientificProjectAndChildrenById(scientificProjectId);
        scientificProjectService.checkPermissionForScientificProject(userId, scientificProjectDTO);
        
        model.addAttribute("phaseForm", new PhaseForm());
        model.addAttribute("scientificProjectDTO", scientificProjectDTO);
        
        PaginationInfo pagination = PaginationInfo.builder()
		        								  .totalEntities(phaseService.countAllPhases(scientificProjectId, queryString))
		        								  .actualPageNumber(pageNumber)
		        								  .pageSize(phaseService.getPageSize())
		        								  .baseUrlLink("/scientific-project/phase/" + scientificProjectId + "/phases-list").build();
        
        pagination.addUrlParameter("search", queryString);
        
        FilterDTO filter = FilterDTO.builder().build();
        filter.addFilterField(FilterFieldDTO.builder().name("queryString").isTextField(Boolean.TRUE).value(queryString).label("busca textual").build());
        
        model.addAttribute("filter", filter);
        model.addAttribute("pagination", pagination);
        model.addAttribute("searchListUrl", pagination.getBaseUrlLink());
        
        return "fragments/phase/phase-list-fragment";
    }
	
    @PreAuthorize("@scientificProjectService.canAccessScientificProject(principal, #phaseForm.getProjectId()) || hasAuthority('ADMIN')")
    @RequestMapping(value = "/create-phase", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMessage handlePhaseCreateForm(@Valid @ModelAttribute("phaseForm") PhaseForm phaseForm, BindingResult bindingResult,
    								   	         HttpServletRequest request) {
    	
        log.info("Processando phaseForm  {}, bindingResult  {}", phaseForm, bindingResult);
        try {
	        PhaseDTO phaseDTO = null;
	        
	        if (bindingResult.hasErrors()) {
	        	log.error("Erro de validação ao criar fase \n" + ResponseMessageUtil.formatErrorMessage(bindingResult));
	        	return ResponseMessage.buildErrorMessage(ResponseMessageUtil.formatErrorMessage(bindingResult));
	        }
	        
	        phaseDTO = phaseService.createPhase(phaseForm);
	        return ResponseMessageUtil.handleResponseMessage(phaseDTO, "Fase criada com sucesso.", "Erro inesperado ao criar fase.");
        }
        catch(Exception e) {
        	log.error("Erro ao criar fase\n{}", Throwables.getStackTraceAsString(e));
	        return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado ao criar fase.").build();
        }
    }
    
    @PreAuthorize("@scientificProjectService.canAccessScientificProject(principal, #scientificProjectId) || hasAuthority('ADMIN')")
    @RequestMapping(value = "/edit-phase", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMessage editPhase(@RequestParam(value="phaseId", required = true) Long phaseId,
								     @RequestParam(value="phaseName", required = true) String phaseName,
								     @RequestParam(value="allowExecution", required = true) Boolean allowExecution,
								     @RequestParam(value="scientificProjectId", required = true) String scientificProjectId) {
    	
    	log.info("Editando phase de id {} com novo phaseName {} e allowExecution {}", phaseId, phaseId, allowExecution);
        PhaseDTO phaseDTO = null;
        
		try {
			phaseDTO = phaseService.editPhase(phaseId, phaseName, allowExecution);
		}
		catch (ExistingEntityException e) {
        	log.error("Erro ao editar fase, o nome {} já é usado por outra fase\n{}", phaseName, Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
        						  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
        						  .text("Já existe outra fase com o nome " + phaseName + ".").build();
        } 
		catch (Exception e) {
        	log.error("Erro ao editar fase de id {} com novo phaseName {} e allowExecution {}\n{}", 
        			   phaseId, phaseName, allowExecution, Throwables.getStackTraceAsString(e));
        	
        	return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado, não foi possível editar a fase.").build();
    	}
        
        return ResponseMessageUtil.handleResponseMessage(phaseDTO, "Fase editada com sucesso.", "Erro inesperado ao editar fase.");
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/remove-phase", method = RequestMethod.POST)
    @ResponseBody
	public ResponseMessage handleRemovePhase(@RequestParam(value = "phaseId", required = true) String phaseId, HttpServletRequest request) {
    	log.info("Excluindo fase de id {}", phaseId);
    	
    	try {
    		Long phaseIdValue = Long.valueOf(phaseId);
    		phaseService.removePhase(phaseIdValue);
    	}
    	catch (Exception e) {
    		log.error("Erro inesperado ao remover phase de id {}\n{}", phaseId, Throwables.getStackTraceAsString(e));
    		return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado ao excluir fase.").build();
    	}
    	
	    return ResponseMessage.builder()
							  .type(ResponseMessage.SUCCESS_MESSAGE_LABEL)
							  .text("Fase excluída com sucesso.").build();
	}
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/count-dependencies/{phaseId}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Integer> countDependencies(@PathVariable Long phaseId) {
        log.info("Contando dependências da fase de id {}", phaseId);
    	return phaseService.countDependencies(phaseId);
    }
    
}