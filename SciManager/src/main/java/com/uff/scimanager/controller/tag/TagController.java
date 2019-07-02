package com.uff.scimanager.controller.tag;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Throwables;
import com.uff.scimanager.component.PaginationInfo;
import com.uff.scimanager.component.ResponseMessage;
import com.uff.scimanager.domain.dto.TagDTO;
import com.uff.scimanager.domain.dto.filter.FilterDTO;
import com.uff.scimanager.domain.dto.filter.FilterFieldDTO;
import com.uff.scimanager.domain.form.TagForm;
import com.uff.scimanager.domain.form.validator.TagFormValidator;
import com.uff.scimanager.domain.security.CurrentUser;
import com.uff.scimanager.exception.ExistingEntityException;
import com.uff.scimanager.service.tag.TagService;
import com.uff.scimanager.util.ResponseMessageUtil;

@Controller
@RequestMapping("/tag")
public class TagController {
	
	private static final Logger log = LoggerFactory.getLogger(TagController.class);
	
    private final TagService tagService;
	private final TagFormValidator tagFormValidator;
	
	@Autowired
	public TagController(TagService tagService, TagFormValidator tagFormValidator) {
		this.tagService = tagService;
		this.tagFormValidator = tagFormValidator;
	}
	
	@InitBinder("tagForm")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(tagFormValidator);
    }
	
	@PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/tag-list", method = RequestMethod.GET)
    public String getTagsList(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
							  @RequestParam(value = "search", required = false) String queryString,
							  Model model) {
        
        log.info("Carregando página de listagem de tags");

		model.addAttribute("tagDTOs", tagService.getAllTags(pageNumber, queryString));
		
		PaginationInfo pagination = PaginationInfo.builder()
												  .totalEntities(tagService.countAllTags(queryString))
												  .actualPageNumber(pageNumber)
												  .pageSize(tagService.getPageSize())
												  .baseUrlLink("/tag/tag-list").build();	
        
		pagination.addUrlParameter("search", queryString);
        
        FilterDTO filter = FilterDTO.builder().build();
        filter.addFilterField(FilterFieldDTO.builder().name("search").isTextField(Boolean.TRUE).value(queryString).label("busca textual").build());
        
        model.addAttribute("filter", filter);
        model.addAttribute("pagination", pagination);
        model.addAttribute("searchListUrl", pagination.getBaseUrlLink());
        
        return "fragments/tag/tag-list-fragment";
    }
	
	@PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/all-tags", method = RequestMethod.GET)
    public String getTagsPage(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
						  	  @RequestParam(value = "search", required = false) String queryString,
						  	  Model model) {
        
        log.info("Carregando página de listagem de tags");
        model.addAttribute("tagForm", TagForm.buildEmptyTagForm());
        
        FilterDTO filter = FilterDTO.builder().build();
        filter.addFilterField(FilterFieldDTO.builder().name("queryString").isTextField(Boolean.TRUE).value(queryString).label("busca textual").build());
        
        PaginationInfo pagination = PaginationInfo.builder()
        										  .actualPageNumber(pageNumber)
        										  .baseUrlLink("/tag/tag-list").build();
        
        model.addAttribute("filter", filter);
        model.addAttribute("searchListUrl", pagination.buildParametersFromFilter(filter));
        
        return "tag/tag-list";
    }
    
    @RequestMapping(value = "/api/all-tags", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getTagJsonList(@RequestParam(value = "search", required = true) String queryString) {
    	log.info("Carregando listagem de todas as tags buscado por {}", queryString);
        return tagService.getAllTagsNamesJson(queryString);
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/create-tag", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMessage handleTagCreateForm(@Valid @ModelAttribute("tagForm") TagForm tagForm, BindingResult bindingResult,
    								  		   HttpServletRequest request) {
    	
        log.info("Processando tagForm  {}, bindingResult  {}", tagForm, bindingResult);
        try {
	        TagDTO tagDTO = null;
	        
	        if (bindingResult.hasErrors()) {
	        	log.error("Erro de validação ao criar tag \n" + ResponseMessageUtil.formatErrorMessage(bindingResult));
	        	return ResponseMessage.buildErrorMessage(ResponseMessageUtil.formatErrorMessage(bindingResult));
	        }
	        
	    	tagDTO = tagService.createTag(tagForm);
	        return ResponseMessageUtil.handleResponseMessage(tagDTO, "Tag criada com sucesso.", "Erro inesperado ao criar tag.");

        }
        catch(Exception e) {
        	log.error("Erro ao criar tag\n{}", Throwables.getStackTraceAsString(e));
	        return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado ao criar tag.").build();
        }
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/edit-tag", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMessage editTag(@RequestParam(value="tagId", required = true) Long tagId,
								   @RequestParam(value="tagName", required = true) String tagName) {
    	
    	log.info("Editando tag de id {} com nova tagName {}", tagId, tagId);
        TagDTO tagDTO = null;
        
		try {
			tagDTO = tagService.editTag(tagId, tagName);
		}
		catch (ExistingEntityException e) {
        	log.error("Erro ao editar tag, o nome {} já é usado por outra tag\n{}", tagName, Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
        						  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
        						  .text("Já existe outra tag com o nome " + tagName + ".").build();
        } 
		catch (Exception e) {
        	log.error("Erro ao editar tag de id {} com nova tagName {}\n{}", tagId, tagName, Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado, não foi possível editar a tag.").build();
    	}
        
        return ResponseMessageUtil.handleResponseMessage(tagDTO, "Tag editada com sucesso.", "Erro inesperado ao editar tag.");
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/remove-tag", method = RequestMethod.POST)
    @ResponseBody
	public ResponseMessage handleRemoveTag(@RequestParam(value = "tagId", required = true) String tagId,
							      		   Authentication authentication, HttpServletRequest request) {
    	
    	log.info("Excluindo tag de id {}", tagId);
    	
    	try {
    		CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        	Long userId = currentUser != null ? currentUser.getUserId() : null;
        	
    		Long tagIdValue = Long.valueOf(tagId);
    		tagService.removeTag(tagIdValue, userId);
    	}
    	catch (Exception e) {
    		log.error("Erro ao excluir tag de id {}\n{}", tagId, Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado ao excluir tag.").build();
    	}
    	
    	return ResponseMessage.builder()
							  .type(ResponseMessage.SUCCESS_MESSAGE_LABEL)
							  .text("Tag excluída com sucesso.").build();
	}
    
}