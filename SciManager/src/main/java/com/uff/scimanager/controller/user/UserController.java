package com.uff.scimanager.controller.user;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.base.Throwables;
import com.uff.scimanager.component.PaginationInfo;
import com.uff.scimanager.component.ResponseMessage;
import com.uff.scimanager.domain.dto.ProfileImageDTO;
import com.uff.scimanager.domain.dto.UserDTO;
import com.uff.scimanager.domain.dto.filter.FilterDTO;
import com.uff.scimanager.domain.dto.filter.FilterFieldDTO;
import com.uff.scimanager.domain.form.UserForm;
import com.uff.scimanager.domain.form.UserFormUpdate;
import com.uff.scimanager.domain.form.validator.UserFormValidator;
import com.uff.scimanager.domain.security.CurrentUser;
import com.uff.scimanager.exception.EntityNotFoundException;
import com.uff.scimanager.exception.ExistingEntityException;
import com.uff.scimanager.exception.ExpiredTokenException;
import com.uff.scimanager.service.task.TaskService;
import com.uff.scimanager.service.user.UserService;
import com.uff.scimanager.service.user.security.CurrentUserDetailsService;
import com.uff.scimanager.service.workflow.WorkflowService;
import com.uff.scimanager.util.EncrypterUtils;
import com.uff.scimanager.util.ResponseMessageUtil;

@Controller
@RequestMapping("/user")
public class UserController {
	
	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	
    private final UserService userService;
    private final CurrentUserDetailsService currentUserDetailsService;
    private final WorkflowService workflowService;
    private final TaskService taskService;
    private final UserFormValidator userFormValidator;
	
    @Autowired
	public UserController(UserService userService, CurrentUserDetailsService currentUserDetailsService, 
			              WorkflowService workflowService, TaskService taskService, UserFormValidator userFormValidator) {
    	
		this.userService = userService;
		this.currentUserDetailsService = currentUserDetailsService;
		this.workflowService = workflowService;
		this.taskService = taskService;
		this.userFormValidator = userFormValidator;
	}

    @InitBinder("userForm")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(userFormValidator);
    }
    
    @PreAuthorize("@currentUserService.canAccessUser(principal, #userId)")
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public String getUserPage(Model model, @PathVariable String userId) {
        log.info("Carregando página para o usuário de id {}", userId);
        Long userIdValue;
        
        try {
        	userIdValue = Long.valueOf(userId);
        } 
        catch (Exception e) {
        	log.error("Erro ao fazer o parse do id {}\n" + e.getMessage() , userId);
        	return "error/page-404";
        }
        
        UserDTO userDTO = userService.getUserById(userIdValue);
        
        if (userDTO == null) {
        	log.error("Usuário de id {} não encontrado.", userId);
        	return "error/page-404";
        }
        
        model.addAttribute("searchGroupsListUrl", "/user-group/" + userId + "/user-group-list");
        model.addAttribute("user", userDTO);
        
        model.addAttribute("workflowDTOs", workflowService.getAllWorkflowsOfUser(userDTO != null ? userDTO.getUserId() : null));
        model.addAttribute("workflowsTotal", workflowService.countAllWorkflowsOfUser(userDTO != null ? userDTO.getUserId() : null));
        
        model.addAttribute("taskDTOs", taskService.getAllTasksOpenForUser(userDTO != null ? userDTO.getUserId() : null));
        model.addAttribute("tasksTotal", taskService.getAllTasksOpenForUserCount(userDTO != null ? userDTO.getUserId() : null));
    	
        log.info("Caregando página do usuário de id {}", userId);
        return "user/user-profile";
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/user-list", method = RequestMethod.GET)
    public String getUsersList(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
							   @RequestParam(value = "search", required = false) String queryString,
							   @RequestParam(value = "letter", required = false) String letter,
							   Model model) {
    	
        log.info("Carregando página de listagem de usuários");
        model.addAttribute("userRows", userService.divideUsersByRow(userService.getAllUsers(pageNumber, letter, queryString)));
		
		PaginationInfo pagination = PaginationInfo.builder()
												  .totalEntities(userService.countAllUsers(letter, queryString))
												  .actualPageNumber(pageNumber)
												  .pageSize(userService.getPageSize())
												  .baseUrlLink("/user/user-list").build();
        
		pagination.addUrlParameter("search", queryString);
        pagination.addUrlParameter("letter", letter);
        
        FilterDTO filter = FilterDTO.builder().build();
        filter.addFilterField(FilterFieldDTO.builder().name("search").isTextField(Boolean.TRUE).value(queryString).label("busca textual").build())
        	  .addFilterField(FilterFieldDTO.builder().name("letter").isTextField(Boolean.TRUE).value(letter).label("busca alfabética").build());
        
        model.addAttribute("filter", filter);
        model.addAttribute("pagination", pagination);
        model.addAttribute("searchListUrl", pagination.getBaseUrlLink());
        
        return "fragments/user/user-list-fragment";
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/all-users", method = RequestMethod.GET)
    public String getUsersPage(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
							   @RequestParam(value = "search", required = false) String queryString,
							   @RequestParam(value = "letter", required = false) String letter,
							   Model model) {
    	
        log.info("Carregando página de listagem de usuários");
        model.addAttribute("userForm", UserForm.BuildEmptyUserForm());
        
        FilterDTO filter = FilterDTO.builder().build();
        filter.addFilterField(FilterFieldDTO.builder().name("search").isTextField(Boolean.TRUE).value(queryString).label("busca textual").build())
        	  .addFilterField(FilterFieldDTO.builder().name("letter").isTextField(Boolean.TRUE).value(letter).label("busca alfabética").build());
        
        PaginationInfo pagination = PaginationInfo.builder()
        										  .actualPageNumber(pageNumber)
        										  .baseUrlLink("/user/user-list").build();
        
        model.addAttribute("filter", filter);
        model.addAttribute("searchListUrl", pagination.buildParametersFromFilter(filter));
        
        return "user/user-list";
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/api/all-users", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<UserDTO> getUserJsonList(@RequestParam(value = "search", required = true) String queryString) {
        log.info("Carregando json de usuários buscados por {}", queryString);
        return userService.getAllUsersJson(queryString);
    }
    
    @RequestMapping(value = "/api/all-new-users/{userGroupId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<UserDTO> getUserJsonListNotInGroup(@RequestParam(value = "search", required = true) String queryString,
    											   @PathVariable Long userGroupId) {
        
    	log.info("Carregando json de usuários buscados por {} que não estão no grupo {}", queryString, userGroupId);
        return userService.getAllUsersJsonNotInGroup(queryString, userGroupId);
    }
    
    @RequestMapping(value = "/profile-image/{userId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ProfileImageDTO getUserProfileImage(@PathVariable Long userId) {
        log.info("Carregando imagem de perfil do usuario de id {}", userId);
        return userService.getUserProfileImage(userId);
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/create-user", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMessage handleUserCreateForm(@Valid @ModelAttribute("userForm") UserForm userForm, BindingResult bindingResult,
    								   			HttpServletRequest request, Authentication authentication) {
    	
        log.info("Processando userForm {}, bindingResult = {}", userForm, bindingResult);
        try {
        	CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        	Long userId = currentUser != null ? currentUser.getUserId() : null;
        	UserDTO userDTO = null;
	        
	        if (bindingResult.hasErrors()) {
	        	log.error("Erro de validação ao criar usuário \n" + ResponseMessageUtil.formatErrorMessage(bindingResult));
	        	return ResponseMessage.buildErrorMessage(ResponseMessageUtil.formatErrorMessage(bindingResult));
	        }
	        
        	userDTO = userService.createUser(userForm, userId);
	        return ResponseMessageUtil.handleResponseMessage(userDTO, "Usuário criado com sucesso.", "Erro inesperado ao criar usuário.");
        
        }
        catch(Exception e) {
        	log.error("Erro ao criar tag\n{}", Throwables.getStackTraceAsString(e));
	        return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado ao criar usuário.").build();
        }
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/edit-user-role", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMessage handleEditUserRole(@RequestParam(value="userId", required = true) String userId,
		    						 		  @RequestParam(value="role", required = true) String role, HttpServletRequest request) {
    	
        log.info("Processando edição de role de usuário de id {} para role = {}", userId, role);
        
        UserDTO userDTO = null;
        Long userIdValue = null;
    	
    	try {
    		userIdValue = Long.valueOf(userId);
    		userDTO = userService.edituserRole(userIdValue, role);

    		return ResponseMessageUtil.handleResponseMessage(userDTO, "Permissionamento do usuário editado com sucesso.", "Erro inesperado ao editar permissionamento de usuário.");
    	}
    	catch (Exception e) {
    		log.error("Erro inesperado ao editar user role de user de id {}\n{}", userId, Throwables.getStackTraceAsString(e));
    		return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
							      .text("Erro inesperado ao editar permissionamento de usuário.").build();
    	}
    }
    
    @PreAuthorize("@currentUserService.canAccessUser(principal, #userId)")
    @RequestMapping(value = "/edit-user", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMessage editUser(@Valid UserFormUpdate userFormUpdate, BindingResult bindingResult) {
    	
    	if (userFormUpdate == null || bindingResult.hasErrors()) {
        	log.error("Erro de validação ao editar usuário \n" + ResponseMessageUtil.formatErrorMessage(bindingResult));
        	return ResponseMessage.buildErrorMessage(ResponseMessageUtil.formatErrorMessage(bindingResult));
        }
    	
    	log.info("Editando usuário de id {}", userFormUpdate.getUserId());
    	
    	if (!userFormUpdate.getPassword().equals(userFormUpdate.getRepeatedPassword())) {
    		return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Senhas informadas devem ser iguais.").build();
    	}
    	
    	try {
    		UserDTO userDTO = userService.editUser(userFormUpdate);
			currentUserDetailsService.reauthenticateUser(userFormUpdate);
    	
			return ResponseMessageUtil.handleResponseMessage(userDTO, "Usuário editado com sucesso.", "Erro inesperado ao editar usuário.");
    	}
    	catch (ExistingEntityException e) {
        	log.error("Erro ao editar usuário, o email {} já é usado por outro usuário\n{}", userFormUpdate.getUsername(), Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
        						  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
        						  .text("Já existe outro usuário com o nome " + userFormUpdate.getUsername() + ".").build();
        }
    	catch (Exception e) {
    		log.error("Erro ao editar user de id {}\n{}", userFormUpdate.getUserId(), Throwables.getStackTraceAsString(e));
    		return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
							      .text("Erro inesperado, não foi possível editar informações do usuário.").build();
		} 
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/remove-user", method = RequestMethod.POST)
    @ResponseBody
	public ResponseMessage handleRemoveUser(@RequestParam(value = "userId", required = true) String userId) {
    	log.info("Excluindo usuario de id {}", userId);
    	
    	try {
    		Long userIdValue = Long.valueOf(userId);
    		userService.removeUser(userIdValue);
    	}
    	catch (Exception e) {
    		log.error("Erro ao excluir usuário de id {}\n{}", userId, Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado ao excluir usuário.").build();
    	}
    	
    	return ResponseMessage.builder()
							  .type(ResponseMessage.SUCCESS_MESSAGE_LABEL)
							  .text("Usuário excluído com sucesso.").build();
	}
    
    @PreAuthorize("@currentUserService.canAccessUser(principal, #userId)")
    @RequestMapping(value = "/upload-image", method = RequestMethod.POST)
    public String uploadImage(@RequestParam(value="userId", required = true) Long userId,
							  @RequestParam(value="imageString", required = true) String imageString,
							  RedirectAttributes redirectAttributes, HttpServletRequest request) {
    	
    	log.info("Realizando upload de imagem de perfil para o usuario de id {}", userId);
        UserDTO userDTO = null;
        
		try {
			userDTO = userService.uploadImage(userId, imageString);
		}
		catch (Exception e) {
        	log.error("Erro ao realizar upload de imagem para o usuário de id {}\n{}", userId, Throwables.getStackTraceAsString(e));
        }
		
		ResponseMessageUtil.handleRedirectMessage(redirectAttributes, userDTO, "Upload de imagem feito com sucesso.", "Erro inesperado ao fazer upload de imagem.");
        return "redirect:" + request.getHeader("Referer");
    }
    
    @RequestMapping(value = "/reset-password/request-password", method = RequestMethod.GET)
    public String getUserRequestPasswordPage(Model model, HttpServletRequest request) {
        log.info("Carregando página de request de password");
        model.addAttribute("applicationContext", request.getContextPath());
        return "user/request-password";
    }
    
    @RequestMapping(value = "/reset-password/request-password", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMessage handleNewPasswordRequest(@RequestParam(value="email", required = true) String email,
    										RedirectAttributes redirectAttributes, HttpServletRequest request) {
    	
        log.info("Processando request de novo password");
        try {
        	userService.processPasswordRenewalRequest(email);
		}
		catch (EntityNotFoundException e) {
			log.error("Usuário de email {} não encontrado", email);
            return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Usuário com email especificado não encontrado. Requisite uma conta ao administrador.").build();
        }
        catch (Exception e) {
			log.error("Erro inesperado ao enviar email de requisição de nova senha para o email {}\n{}", email, Throwables.getStackTraceAsString(e));
            return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado ao requisitar nova senha.").build();
        }
        
        return ResponseMessage.builder()
							  .type(ResponseMessage.SUCCESS_MESSAGE_LABEL)
							  .text("E-mail de redefinição de senha enviado com sucesso.").build();
    }
    
    @RequestMapping(value = "/reset-password/new-password", method = RequestMethod.GET)
    public String getUserResetPasswordPage(@RequestParam(value="token", required = true) String token, 
    									   Model model, HttpServletRequest request) {
        
    	log.info("Carregando página de reset de password");
        try {
        	Map<String, Object> userData = EncrypterUtils.getUserResetPasswordToken(token);
        	model.addAttribute("applicationContext", request.getContextPath());
        	
        	if (userData == null || userData.get("userId") == null || userData.get("email") == null) {
        		model.addAttribute(ResponseMessage.ERROR_MESSAGE_LABEL, "Usuário inválido.");
        	}
        	
        	UserDTO userDTO = userService.getUserById((Long) userData.get("userId"));
        	
        	if (userDTO == null) {
        		model.addAttribute(ResponseMessage.ERROR_MESSAGE_LABEL, "Usuário inválido.");
        	}
        }
		catch (ExpiredTokenException e) {
			log.warn("Token {} expirado", token);
			model.addAttribute(ResponseMessage.ERROR_MESSAGE_LABEL, "Tempo de edição expirado. Requisite nova senha.");
		}
        
        return "user/reset-password";
    }
    
    @RequestMapping(value = "/reset-password/edit-password", method = RequestMethod.POST)
    @ResponseBody
    public ResponseMessage editUserPassword(@RequestParam(value="email", required = true) String email,
										    @RequestParam(value="newPassword", required = true) String newPassword,
										    @RequestParam(value="repeatedNewPassword", required = true) String repeatedNewPassword) {
		
    	log.info("Editando senha de usuário de email {}", email);
        UserDTO userDTO = null;
        
		try {
			if (!newPassword.equals(repeatedNewPassword)) {
				log.error("Senha e confirmação de senha devem ser iguais. newPassword {}; repeatedPassword", newPassword, repeatedNewPassword);
	            return ResponseMessage.builder()
									  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
									  .text("Erro inesperado ao editar senha do usuário.").build();
			}
			
			userDTO = userService.editUserPassword(email, newPassword, repeatedNewPassword);
		}
		catch (EntityNotFoundException e) {
        	log.error("Erro ao editar senha de usuário de email {} não encontrado\n{}", email, Throwables.getStackTraceAsString(e));
            return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("E-mail inválido.").build();
        }
		catch (Exception e) {
        	log.error("Erro ao editar senha de usuário de email {}\n{}", email, Throwables.getStackTraceAsString(e));
            return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado ao editar senha do usuário.").build();
		}
        
        return ResponseMessageUtil.handleResponseMessage(userDTO, "Senha de usuário redefinida com sucesso.", "Erro inesperado ao redefinir senha.");
    }
    
}	