package com.uff.scimanager.service.user.group;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uff.scimanager.amqp.NotificationMessageSender;
import com.uff.scimanager.dao.user.group.UserGroupDao;
import com.uff.scimanager.domain.NotificationType;
import com.uff.scimanager.domain.Task;
import com.uff.scimanager.domain.User;
import com.uff.scimanager.domain.UserGroup;
import com.uff.scimanager.domain.dto.UserGroupDTO;
import com.uff.scimanager.domain.dto.amqp.NotificationMessageDTO;
import com.uff.scimanager.domain.form.UserGroupForm;
import com.uff.scimanager.exception.EntityNotFoundException;
import com.uff.scimanager.exception.ExistingEntityException;
import com.uff.scimanager.service.task.TaskService;
import com.uff.scimanager.service.user.UserService;
import com.uff.scimanager.service.workflow.WorkflowService;

@Service
public class UserGroupService {
	
	private static final Logger log = LoggerFactory.getLogger(UserGroupService.class);
	
	private final UserGroupDao userGroupDao;
	private final NotificationMessageSender notificationMessageSender;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private WorkflowService workflowService;
	
	@Autowired
	public UserGroupService(UserGroupDao userGroupDao, NotificationMessageSender notificationMessageSender) {
		this.userGroupDao = userGroupDao;
		this.notificationMessageSender = notificationMessageSender;
	}
	
    public UserGroupDTO getUserGroupById(long userGroupId) {
    	UserGroup userGroup = userGroupDao.findById(userGroupId);
    	
    	if (userGroup != null) {
    		log.info("Grupo de id {} buscado com sucesso", userGroupId);
    		return UserGroupDTO.buildDTOWithChildrenByEntity(userGroup);
    	}
    	
    	log.info("Grupo com id {} não encontrado", userGroupId);
        return null;
    }
    
    public UserGroup getUserGroupEntityById(long userGroupId) {
    	UserGroup userGroup = userGroupDao.findById(userGroupId);
    	
    	if (userGroup != null) {
    		log.info("Grupo de id {} buscado com sucesso", userGroupId);
    		return userGroup;
    	}
    	
    	log.info("Grupo com id {} não encontrado", userGroupId);
        return null;
    }
    
    public UserGroupDTO getUserGroupByGroupName(String groupName) {
    	UserGroup userGroup = userGroupDao.getUserGroupByGroupName(groupName);
    	
    	if (userGroup != null) {
    		log.info("Grupo de groupName {}, buscado com sucesso", groupName);
    		return UserGroupDTO.buildDTOWithChildrenByEntity(userGroup);
    	}
    	
    	log.info("Grupo não pode ser encontrado");
        return null;
    }
    
    public UserGroup getUserGroupEntityBySlug(String slug) {
    	UserGroup userGroup = userGroupDao.findUserGroupBySlug(slug);
    	
    	if (userGroup != null) {
    		log.info("Grupo de slug {}, buscado com sucesso", slug);
    		return userGroup;
    	}
    	
    	log.info("Grupo não pode ser encontrado");
        return null;
    }
    
    public UserGroup saveOrUpdateUserGroup(UserGroup userGroup) {
        return userGroupDao.saveOrUpdateUserGroup(userGroup);
    }
    
    public UserGroup getUserGroupEntityByGroupName(String groupName) {
    	UserGroup userGroup = userGroupDao.getUserGroupByGroupName(groupName);
    	
    	if (userGroup != null) {
    		log.info("Grupo de groupName {}, buscado com sucesso", groupName);
    		return userGroup;
    	}
    	
    	log.info("Grupo não pode ser encontrado");
        return null;
    }
    
    @Transactional
	public List<UserGroupDTO> getAllUserGroups(Integer pageNumber, String queryString, Long userId) {
		log.info("Processando busca de todos os grupos pelos filtros: pageNumber = {}, queryString = {}, usuário = {}", pageNumber, queryString, userId);
		return UserGroupDTO.convertEntityListToDTOList(userGroupDao.findAllPaginated(pageNumber, queryString, userId));
	}
    
    public Integer countAllUserGroups(String queryString, Long userId) {
    	log.info("Contando total de grupos pelos filtros:  queryString = {}, userId = {}", queryString, userId);
        return userGroupDao.getAllUserGroupsCount(queryString, userId);
    }
    
    @Transactional
    public UserGroupDTO createUserGroup(UserGroupForm userGroupForm, Long userId) throws EntityNotFoundException {
    	Set<User> retrievedUsers = retrieveUserListFromEmails(userGroupForm.getGroupUsersEmails());
    	
    	UserGroup userGroup = UserGroup.builder()
    								   .groupName(userGroupForm.getGroupName())
    								   .groupUsers(retrievedUsers).build();
    	
    	UserGroup savedUserGroup = userGroupDao.saveOrUpdateUserGroup(userGroup);
		
    	if (savedUserGroup != null) {
			log.info("Grupo criado com sucesso");
			notifyGroup(savedUserGroup, userId);
			return UserGroupDTO.buildDTOWithChildrenByEntity(savedUserGroup);
		}
    	
		log.error("Erro ao criar grupo");
        return null;
    }
    
    @Transactional
	public UserGroupDTO editUserGroupName(Long userGroupId, String groupName) throws ExistingEntityException, EntityNotFoundException {
    	if (userGroupId == null || (groupName == null || "".equals(groupName))) {
    		log.info("Dados inválidos de entrada, userGroupId ou grupo de groupName nulo");
    		return null;
    	}
    	
		UserGroup userGroup = getUserGroupEntityById(userGroupId);
		if (userGroup == null) {
			throw new EntityNotFoundException("Erro ao buscar grupo. Grupo com id " + userGroupId + " não foi encontrado.");
		}
		
		if (!groupName.equals(userGroup.getGroupName()) && !validateExistingGroupName(groupName)) {
			log.info("Grupo com o nome {} já existe", groupName);
			throw new ExistingEntityException("Grupo com o nome " + groupName + " já existe");
		}
		
		userGroup.setGroupName(groupName);
		UserGroup savedUserGroup = userGroupDao.saveOrUpdateUserGroup(userGroup);
		
		if (savedUserGroup != null) {
			log.info("Nome do grupo atualizado com sucesso para {}", groupName);
			return UserGroupDTO.buildDTOWithChildrenByEntity(savedUserGroup);
		}
		
		log.error("Erro ao editar nome do grupo de usuários.");
		return null;
	}
	
	private Boolean validateExistingGroupName(String groupName) {
		log.info("Validando se existe grupo com o nome {}", groupName);
		UserGroupDTO userGoupDTO = getUserGroupByGroupName(groupName);
		return userGoupDTO == null;
	}
    
	@Transactional
	public UserGroupDTO removeUserFromUserGroup(Long userGroupId, Long userId) throws EntityNotFoundException {
		if (userGroupId == null || userId == null) {
    		log.info("Dados inválidos de entrada, userGroupId ou grupo de userId nulo");
    		return null;
    	}
		
		UserGroup userGroup = getUserGroupEntityById(userGroupId);
		if (userGroup == null) {
			log.error("Grupo com id {} não encontrado", userGroupId);
    		throw new EntityNotFoundException("UserGroup de id " + userGroupId + "não encontrado");
    	}
		
		userGroup.removeUserFromGroupUsers(userId);
		UserGroup savedUserGroup = userGroupDao.saveOrUpdateUserGroup(userGroup);
		if (savedUserGroup != null) {
			log.info("Usuário de id {}, removido do grupo de id {}", userId, userGroupId);
			return UserGroupDTO.buildDTOWithChildrenByEntity(savedUserGroup);
		}
		
		log.error("Erro ao remover usuário de id {} do grupo de id {}", userId, userGroupId);
		return null;
	}
    
	@Transactional
	public UserGroupDTO addUsersToUserGroup(Long userGroupId, List<String> emails, Long userId) throws EntityNotFoundException {
		if (userGroupId == null || (emails == null || emails.isEmpty())) {
			log.info("Dados inválidos de entrada, userGroupId ou grupo de usuários nulo");
			return null;
		}
		
		UserGroup userGroup = getUserGroupEntityById(userGroupId);
		
		if (userGroup == null) {
			log.error("Erro, grupo de id {} não encontrado", userGroupId);
    		throw new EntityNotFoundException("UserGroup de id " + userGroupId + "não encontrado");
    	}
		
		addUsersToUserGroup(emails, userGroup);
		
		UserGroup savedUserGroup = userGroupDao.saveOrUpdateUserGroup(userGroup);
		if (savedUserGroup != null) {
			log.info("Usuário adicionados com sucesso ao grupo de id {}", userGroupId);
			
			notifyGroupUsers(savedUserGroup, emails, userId);
			return UserGroupDTO.buildDTOWithChildrenByEntity(savedUserGroup);
		}
		
		log.error("Erro ao adicionar usuários ao grupo de id {}", userGroupId);
		return null;
	}
	
	public List<UserGroupDTO> getAllUserGroupsJson(String queryString, Long userId) {
		log.info("Buscando lista de grupos de usuários pelo filtro: queryString = {}", queryString);
		return UserGroupDTO.convertEntityListNoChildToDTOList(userGroupDao.findAllByQueryString(queryString, userId));
	}

	private void addUsersToUserGroup(List<String> emails, UserGroup userGroup) throws EntityNotFoundException {
		for (String email : emails) {
			User retrievedUser = userService.getUserEntityByEmail(email);
			
			if (retrievedUser == null) {
				throw new EntityNotFoundException("Usuário de email " + email + "não encontrado");
			}
			
			userGroup.addUserToUserGroup(retrievedUser);
		}
	}
	
	private void notifyGroup(UserGroup userGroup, Long userId) {
		User userAgent = userService.getUserEntityById(userId);
		if (userAgent == null) {
			log.warn("Usuario agente da acao, de userId {}, nao encontrado", userId);
			return;
		}
		
		notificationMessageSender.sendMessage(NotificationMessageDTO.builder()
																	.actionDate(new Date())
																	.messageBody(("O usuário <b>").concat(userAgent.getUsername())
																							      .concat("</b> associou você ao grupo: <b>")
																							      .concat(userGroup.getGroupName())
																							      .concat("</b>"))
																	.messageTitle(("Você foi adicionado ao grupo - ").concat(userGroup.getGroupName().substring(0, userGroup.getGroupName().length() > 10 ? 10 : userGroup.getGroupName().length()))
																													 .concat(userGroup.getGroupName().length() > 10 ? "..." : ""))
																	.messageLink("/user-group/all-user-groups?myOwn=true")
																	.userGroupId(userGroup.getUserGroupId())
																	.userAgentId(userAgent.getUserId())
																	.notificationType(NotificationType.MESSAGE)
																	.build());
	}
    
	private void notifyGroupUsers(UserGroup userGroup, List<String> emails, Long userId) {
		User userAgent = userService.getUserEntityById(userId);
		if (userAgent == null) {
			log.warn("Usuario agente da acao, de userId {}, nao encontrado", userId);
			return;
		}
		
		for (String email : emails) {
			User groupUser = userGroup.getGroupUserByEmail(email);
			notificationMessageSender.sendMessage(NotificationMessageDTO.builder()
																		.actionDate(new Date())
																		.messageBody(("O usuário <b>").concat(userAgent.getUsername())
																								      .concat("</b> associou você ao grupo: <b>")
																								      .concat(userGroup.getGroupName())
																								      .concat("</b>"))
																		.messageTitle(("Você foi adicionado ao grupo - ").concat(userGroup.getGroupName().substring(0, userGroup.getGroupName().length() > 10 ? 10 : userGroup.getGroupName().length()))
																														 .concat("..."))
																		.messageLink("/user-group/all-user-groups?myOwn=true")
																		.userSubjectId(groupUser.getUserId())
																		.userAgentId(userAgent.getUserId())
																		.notificationType(NotificationType.MESSAGE)
																		.build());
		}
	}
	
    private Set<User> retrieveUserListFromEmails(List<String> emails) throws EntityNotFoundException {
    	log.info("Buscando usuários por email");
    	Set<User> retrievedUsers = new HashSet<User>();
    	
    	if (emails == null || emails.isEmpty()) {
    		log.info("Lista de nomes nula, impossível realizar a busca");
    		return retrievedUsers;
    	}
    	
    	for (String email : emails) {
    		if (email != null && !"".equals(email)) {
    			User retrievedUser = userService.getUserEntityByEmail(email);
    			
    			if (retrievedUser == null) {
    				log.error("Erro, usuáro com email {} não encontrado", email);
    				throw new EntityNotFoundException("User " + email + "não encontrado");
    			}
    			if (!containUser(retrievedUser, retrievedUsers)) {
    				log.info("Adicionando usuário de email {} a lista de usuário do grupo", email);
    				retrievedUsers.add(retrievedUser);
    			}
    		}
    	}
    	
    	log.info("Retornando lista de usuários buscadas do banco");
    	return retrievedUsers;
    }
    
	private Boolean containUser(User retrievedUser, Set<User> retrievedUsers) {
		log.info("Verificando se lista de usuários já contem usuário de username {}", retrievedUser.getUsername());
		for (User user : retrievedUsers) {
			if (user.getUserId().equals(retrievedUser.getUserId())) {
				return Boolean.TRUE;
			}
		}
		
		return Boolean.FALSE;
	}
	
	public void disassociateUserFromUserGroup(UserGroup userGroup, Long userId) {
		log.info("Disacodiando usuario de id {} do grupo de id {}", userId, userGroup.getUserGroupId());
		if (userGroup.removeUserFromGroupUsers(userId)) {
			updateUserGroup(userGroup);
		}
	}
	
	@Transactional
	public void updateUserGroup(UserGroup userGroup) {
		log.info("Atualizando grupo");
		userGroupDao.saveOrUpdateUserGroup(userGroup);
	}
	
	public List<UserGroup> getAllUserGroupsEntityOfUser(Long userId) {
		log.info("Buscando grupos do usuário de id {}", userId);
		return userGroupDao.findAlluserGroupsOfUser(userId);
	}
	
	public List<Long> getAllUserGroupsIdsOfUser(Long userId) {
		log.info("Buscando ids de grupos do usuário de id {}", userId);
		List<UserGroup> userGroups = getAllUserGroupsEntityOfUser(userId);
		List<Long> userGroupIds = new ArrayList<Long>();
		
		for (UserGroup userGroup : userGroups) {
			userGroupIds.add(userGroup.getUserGroupId());
		}
		
		return userGroupIds;
	}
	
	@Transactional
	public void removeUserGroup(Long userGroupId, Long userId) {
		UserGroup userGroup = userGroupDao.findById(userGroupId);
		
		if (userGroup == null) {
			log.error("Grupo de usuários de id {} não encontrado.", userGroupId);
			return;
		}
		
		disassociateFromTasks(userGroupId, userId);
		
		userGroupDao.delete(userGroup);
	}
	
	private void disassociateFromTasks(Long userGroupId, Long userId) {
		List<Task> tasksOfGroup = taskService.getAllTasksEntityOfUserGroup(userGroupId);
		
		if (tasksOfGroup != null && !tasksOfGroup.isEmpty()) {
			
			for (Task task : tasksOfGroup) {
				taskService.disassociateUserGroupFromTask(task, userGroupId, userId);
			}
			
		}
	}
	
	public Map<String, Integer> countDependencies(Long userGroupId) {
		Map<String, Integer> dependencies = new HashMap<String, Integer>();
		
		dependencies.put("workflows", workflowService.countAllWorkflowsOfUserGroup(userGroupId));
		
		return dependencies;
	}
	
	public Integer getPageSize() {
		return userGroupDao.getPaginationParameterConfig().getMaxResultsPerPage();
	}

}