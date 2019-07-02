package com.uff.scimanager.controller.user.group;

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
import com.uff.scimanager.domain.Role;
import com.uff.scimanager.domain.dto.UserGroupDTO;
import com.uff.scimanager.domain.dto.filter.FilterFieldDTO;
import com.uff.scimanager.domain.dto.filter.FilterDTO;
import com.uff.scimanager.domain.form.UserGroupForm;
import com.uff.scimanager.domain.form.validator.UserGroupFormValidator;
import com.uff.scimanager.domain.security.CurrentUser;
import com.uff.scimanager.exception.EntityNotFoundException;
import com.uff.scimanager.exception.ExistingEntityException;
import com.uff.scimanager.service.user.group.UserGroupService;
import com.uff.scimanager.util.ResponseMessageUtil;

@Controller
@RequestMapping("/user-group")
public class UserGroupController {
	
	private static final Logger log = LoggerFactory.getLogger(UserGroupController.class);
	
    private final UserGroupService userGroupService;
    private final UserGroupFormValidator userGroupFormValidator;
    
    @Autowired
	public UserGroupController(UserGroupService userGroupService, UserGroupFormValidator userGroupFormValidator) {
		this.userGroupService = userGroupService;
		this.userGroupFormValidator = userGroupFormValidator;
    }
    
    @InitBinder("userGroupForm")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(userGroupFormValidator);
    }
    
    @PreAuthorize("@currentUserService.canAccessUser(principal, #userId)")
    @RequestMapping(value = "/{userId}/user-group-list", method = RequestMethod.GET)
    public String getUserGroupsList(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
								    @RequestParam(value = "search", required = false) String queryString,
								    @PathVariable Long userId,
								    Authentication authentication,
								    Model model) {
    	
        log.info("Carregando pagina de listagem de grupos usuarios");
		
        model.addAttribute("userGroupDTOs", userGroupService.getAllUserGroups(pageNumber, queryString, userId));
        
        PaginationInfo pagination = PaginationInfo.builder()
			        							  .totalEntities(userGroupService.countAllUserGroups(queryString, userId))
			        							  .actualPageNumber(pageNumber)
			        							  .pageSize(userGroupService.getPageSize())
			        							  .baseUrlLink("/user-group/" + userId + "/user-group-list").build();
        
        pagination.addUrlParameter("search", queryString);
        
        FilterDTO filter = FilterDTO.builder().build();
        filter.addFilterField(FilterFieldDTO.builder().name("search").isTextField(Boolean.TRUE).value(queryString).label("busca textual").build());
        
        model.addAttribute("searchGroupsListUrl", "/user-group/" + userId + "/user-group-list");
        model.addAttribute("filter", filter);
        model.addAttribute("pagination", pagination);
        model.addAttribute("searchListUrl", pagination.getBaseUrlLink());
        
        return "fragments/user-group/profile-user-group-list-fragment";
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/user-group-list", method = RequestMethod.GET)
    public String getUserGroupsList(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
								    @RequestParam(value = "search", required = false) String queryString,
								    @RequestParam(value = "myOwn", required = false) Boolean myOwn,
								    Authentication authentication,
								    Model model) {
    	
        log.info("Carregando pagina de listagem de grupos usuarios");
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
		Long userId = currentUser != null ? currentUser.getUserId() : null;
		
        model.addAttribute("userGroupDTOs", userGroupService.getAllUserGroups(pageNumber, queryString, Boolean.TRUE.equals(myOwn) ? userId : null));
        
        PaginationInfo pagination = PaginationInfo.builder()
			        							  .totalEntities(userGroupService.countAllUserGroups(queryString, Boolean.TRUE.equals(myOwn) ? userId : null))
			        							  .actualPageNumber(pageNumber)
			        							  .pageSize(userGroupService.getPageSize())
			        							  .baseUrlLink("/user-group/user-group-list").build();
        
        pagination.addUrlParameter("search", queryString);
        pagination.addUrlParameter("myOwn", myOwn != null ? myOwn.toString() : null);
        
        FilterDTO filter = FilterDTO.builder().build();
        filter.addFilterField(FilterFieldDTO.builder().name("search").isTextField(Boolean.TRUE).value(queryString).label("busca textual").build())
        	  .addFilterField(FilterFieldDTO.builder().name("myOwn").value(myOwn).build());
        
        model.addAttribute("filter", filter);
        model.addAttribute("pagination", pagination);
        model.addAttribute("searchListUrl", pagination.getBaseUrlLink());
        
        return "fragments/user-group/user-group-list-fragment";
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/all-user-groups", method = RequestMethod.GET)
    public String getUserGroupPage(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
								   @RequestParam(value = "search", required = false) String queryString,
								   @RequestParam(value = "myOwn", required = false) Boolean myOwn,
								   Model model) {
    	
        log.info("Carregando pagina de listagem de grupos usuarios");
        model.addAttribute("userGroupForm", UserGroupForm.buildEmptyUserGroupForm());
        
        FilterDTO filter = FilterDTO.builder().build();
        filter.addFilterField(FilterFieldDTO.builder().name("search").isTextField(Boolean.TRUE).value(queryString).label("busca textual").build())
        	  .addFilterField(FilterFieldDTO.builder().name("myOwn").value(myOwn).build());
        
        PaginationInfo pagination = PaginationInfo.builder()
												  .actualPageNumber(pageNumber)
												  .baseUrlLink("/user-group/user-group-list").build();

		model.addAttribute("filter", filter);
		model.addAttribute("searchListUrl", pagination.buildParametersFromFilter(filter));
		
        return "user-group/user-group-list";
    }
    
    @RequestMapping(value = "/api/all-user-goups", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<UserGroupDTO> getUserGroupJsonList(@RequestParam(value = "search", required = true) String queryString,
    											  Authentication authentication) {
    	
        log.info("Carregando json de grupos de usuários buscados por {}", queryString);
        
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
		Long userId = currentUser != null && !currentUser.getUserRole().equals(Role.ADMIN) ? currentUser.getUserId() : null;
		
        return userGroupService.getAllUserGroupsJson(queryString, userId);
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/create-user-group", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMessage handleUserGroupCreateForm(@Valid @ModelAttribute("userGroupForm") UserGroupForm userGroupForm, BindingResult bindingResult,
			   								         HttpServletRequest request, Authentication authentication) {
    	
        log.info("Processando criação do grupo com o nome {}", userGroupForm.getGroupName());
        try {
	        UserGroupDTO userGroupDTO = null;
	        
	        if (bindingResult.hasErrors()) {
	        	log.error("Erro de validação ao criar grupo de usuários \n" + ResponseMessageUtil.formatErrorMessage(bindingResult));
	        	return ResponseMessage.buildErrorMessage(ResponseMessageUtil.formatErrorMessage(bindingResult));
	        }
	        
	        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
	    	Long userId = currentUser != null ? currentUser.getUserId() : null;
	    	
        	userGroupDTO = userGroupService.createUserGroup(userGroupForm, userId);
	        return ResponseMessageUtil.handleResponseMessage(userGroupDTO, "Grupo criado com sucesso.", "Erro inesperado ao criar grupo.");
        } 
        catch (EntityNotFoundException e) {
        	log.error("Erro inesperado ao criar grupo\n{}", Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado ao criar grupo.").build();
        }
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/add-users", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMessage addUsersToUserGroup(@RequestParam(value="userGroupId", required = true) Long userGroupId,
		    								   @RequestParam(value="emails", required = false) List<String> emails,
		    								   HttpServletRequest request, Authentication authentication) {
    	
        log.info("Adicionando usuários = {} ao grupo de ID = {}", emails, userGroupId);
        try {
        	UserGroupDTO userGruopDTO = null;
        	CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
	    	Long userId = currentUser != null ? currentUser.getUserId() : null;
	    	
        	userGruopDTO = userGroupService.addUsersToUserGroup(userGroupId, emails, userId);
	        return ResponseMessageUtil.handleResponseMessage(userGruopDTO, "Usuário(s) adicionado(s) ao grupo com sucesso.", "Erro inesperado ao adicionar usuário(s) ao grupo.");

        } 
        catch (EntityNotFoundException e) {
        	log.error("Erro ao adicionar usuários ao grupo {}\n{}", userGroupId, Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado ao adicionar usuário(s) ao grupo.").build();
        }
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/edit-name", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMessage editUserGroupName(@RequestParam(value="userGroupId", required = true) Long userGroupId,
    										 @RequestParam(value="groupName", required = true) String groupName) {
    	
        log.info("Editando nome do grupo de id {} para {}", userGroupId, groupName);
        UserGroupDTO userGruopDTO = null;
        
        try {
        	userGruopDTO = userGroupService.editUserGroupName(userGroupId, groupName);
        } 
        catch (ExistingEntityException e) {
        	log.error("Erro ao editar grupo, o nome {} já é usado por outro grupo\n{}", groupName, Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
        						  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
        						  .text("Já existe outro grupo com o nome " + groupName + ".").build();
        } 
        catch (EntityNotFoundException e) {
        	log.error("Erro inesperado ao buscar grupo com id {}\n{}", userGroupId, Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
        					   	  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
        						  .text("Erro inesperado, não foi possível alterar o nome do grupo.").build();
		}
        
        return ResponseMessageUtil.handleResponseMessage(userGruopDTO, "Nome do grupo atualizado com sucesso.", "Erro inesperado ao atualizar nome do grupo.");
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/remove-user-group", method = RequestMethod.POST)
    @ResponseBody
	public ResponseMessage handleRemoveUserGroup(@RequestParam(value = "userGroupId", required = true) String userGroupId,
								       		   	 HttpServletRequest request, Authentication authentication) {
		
    	log.info("Removendo grupo de id {}", userGroupId);
    	
    	try {
    		CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    		Long userId = currentUser != null ? currentUser.getUserId() : null;
    		Long userGroupIdValue = Long.valueOf(userGroupId);
    		userGroupService.removeUserGroup(userGroupIdValue, userId);
    	}
        catch (Exception e) {
        	log.error("Erro inesperado ao remover userGroup de id {}\n{}", userGroupId, Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado ao excluir grupo de usuários.").build();
    	}
    	return ResponseMessage.builder()
							  .type(ResponseMessage.SUCCESS_MESSAGE_LABEL)
							  .text("Grupo de usuários excluído com sucesso.").build();
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/remove-user", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMessage removeUserFromUserGroup(@RequestParam(value="userGroupId", required = true) Long userGroupId,
    											   @RequestParam(value="userId", required = true) Long userId) {
    	
        log.info("Removendo usuário de id {} do grupo de id {}", userId, userGroupId);
        UserGroupDTO userGruopDTO = null;
        
        try {
        	userGruopDTO = userGroupService.removeUserFromUserGroup(userGroupId, userId);
        } 
        catch (EntityNotFoundException e) {
        	log.error("Erro ao remover usuario do grupo, usuário não encontrado\n{}", Throwables.getStackTraceAsString(e));
        } 
        catch (Exception e) {
        	log.error("Erro inesperado ao buscar grupo com id {}\n{}", userGroupId, Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
        						  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
        						  .text("Erro inesperado, não foi possível remover o usuário do grupo.").build();
        }
        
        return ResponseMessageUtil.handleResponseMessage(userGruopDTO, "Usuário removido com sucesso.", "Erro inesperado ao remover usuá rio do grupo.");
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/count-dependencies/{userGroupId}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Integer> countDependencies(@PathVariable Long userGroupId) {
        log.info("Contando dependências de grupo de usuários de id {}", userGroupId);
    	return userGroupService.countDependencies(userGroupId);
    }
    
}	