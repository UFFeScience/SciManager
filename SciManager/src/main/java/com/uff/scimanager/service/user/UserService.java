package com.uff.scimanager.service.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uff.scimanager.amqp.NotificationMessageSender;
import com.uff.scimanager.dao.user.UserDao;
import com.uff.scimanager.domain.NotificationType;
import com.uff.scimanager.domain.ProfileImage;
import com.uff.scimanager.domain.Role;
import com.uff.scimanager.domain.Task;
import com.uff.scimanager.domain.User;
import com.uff.scimanager.domain.UserGroup;
import com.uff.scimanager.domain.dto.ProfileImageDTO;
import com.uff.scimanager.domain.dto.UserDTO;
import com.uff.scimanager.domain.dto.amqp.NotificationMessageDTO;
import com.uff.scimanager.domain.form.UserForm;
import com.uff.scimanager.domain.form.UserFormUpdate;
import com.uff.scimanager.exception.EntityNotFoundException;
import com.uff.scimanager.exception.ExistingEntityException;
import com.uff.scimanager.service.notification.MailService;
import com.uff.scimanager.service.task.TaskService;
import com.uff.scimanager.service.user.group.UserGroupService;
import com.uff.scimanager.util.EncrypterUtils;
import com.uff.scimanager.util.FileUtils;

@Service
public class UserService {
	
	private static final Logger log = LoggerFactory.getLogger(UserService.class);
	
	private final UserDao userDao;
	private final ProfileImageService profileImageService;
	private final NotificationMessageSender notificationMessageSender;
	private final MailService mailService;
	
	@Autowired
	private UserGroupService userGroupService;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	public UserService(UserDao userDao, ProfileImageService profileImageService, 
					   NotificationMessageSender notificationMessageSender, MailService mailService) {
		
		this.userDao = userDao;
		this.profileImageService = profileImageService;
		this.notificationMessageSender = notificationMessageSender;
		this.mailService = mailService;
	}
	
    public UserDTO getUserById(long userId) {
    	User user = userDao.findById(userId);
    	
    	if (user != null) {
    		log.info("Usuário com id {} buscado com sucesso", userId);
    		return UserDTO.buildDTOByEntity(user);
    	}
    	
    	log.info("Usuário com id {}, não encontrado", userId);
        return null;
    }
    
    public User getUserEntityById(long userId) {
    	User user = userDao.findById(userId);
    	
    	if (user != null) {
    		log.info("Usuário com id {} buscado com sucesso", userId);
    		return user;
    	}
    	
    	log.info("Usuário com id {}, não encontrado", userId);
        return null;
    }
    
    public UserDTO getUserByEmail(String email) {
    	User user = userDao.getUserByEmail(email);
    	
    	if (user != null) {
    		log.info("Usuário buscado pelo email {}, encontrado com sucesso", email);
    		return UserDTO.buildDTOByEntity(user);
    	}
    	
    	log.info("Usuário com email {}, não encontrado", email);
        return null;
    }
    
    public User getUserEntityByEmail(String email) {
    	User user = userDao.getUserByEmail(email);
    	
    	if (user != null) {
    		log.info("Usuário buscado pelo email {}, encontrado com sucesso", email);
    		return user;
    	}
    	
    	log.info("Usuário com email {}, não encontrado", email);
        return null;
    }
    
    public User getUserEntityBySlug(String slug) {
    	User user = userDao.findUserBySlug(slug);
    	
    	if (user != null) {
    		log.info("Usuário buscado pelo slug {}, encontrado com sucesso", slug);
    		return user;
    	}
    	
    	log.info("Usuário com email {}, não encontrado", slug);
        return null;
    }
    
    public List<User> getUsersEntitiesByEmails(List<String> emails) {
    	List<User> users = userDao.getUsersByEmails(emails);
    	
    	if (users != null && !users.isEmpty()) {
    		log.info("Usuários buscado pelos emails {}, encontrados com sucesso", emails);
    		return users;
    	}
    	
    	log.info("Usuários com emails {}, não encontrados", emails);
        return null;
    }

	public List<UserDTO> getAllUsers(Integer pageNumber, String letter, String queryString) {
		log.info("Buscando lista de usuários pelo filtro: pageNumber = {}, letter = {}, queryString = {}", pageNumber, letter, queryString);
		return UserDTO.convertEntityListToDTOList(userDao.findAllPaginated(pageNumber, letter, queryString));
	}
	
	public List<UserDTO> getAllUsersJson(String queryString) {
		log.info("Buscando lista de usuários pelo filtro: queryString = {}", queryString);
		return UserDTO.convertEntityListToDTOList(userDao.findAllByQueryString(queryString));
	}
	
	@Transactional
	public List<UserDTO> getAllUsersJsonNotInGroup(String queryString, Long userGroupId) {
		log.info("Buscando lista de usuários que não estão no grupo de id {} pelo filtro: queryString = {}", userGroupId, queryString);
		return UserDTO.convertEntityListToDTOList(userDao.findAllNotInGroup(queryString, userGroupService.getUserGroupEntityById(userGroupId)));
	}
    
    public Integer countAllUsers(String letter, String queryString) {
		log.info("Contando lista de usuários pelo filtro: letter = {}, queryString = {}", letter, queryString);
        return userDao.getAllUsersSearchedCount(letter, queryString);
    }
    
    @Transactional
    public UserDTO createUser(UserForm userForm, Long userId) {
    	log.info("Processando criação de usuário");
    	
    	UserDTO createdUser = UserDTO.buildDTOByEntity(userDao.saveOrUpdateUser(User.buildUserFromUserForm(userForm)));
    	notifyUser(userForm, createdUser, userId);
    	
        return createdUser;
    }
    
    private void notifyUser(UserForm userForm, UserDTO userSubject, Long userId) {
		User userAgent = getUserEntityById(userId);
		if (userAgent == null) {
			log.warn("Usuario agente da acao, de userId {}, nao encontrado", userId);
			return;
		}
		
		notificationMessageSender.sendMessage(NotificationMessageDTO.builder()
																	.actionDate(new Date())
																	.messageBody(("O usuário <b>").concat(userAgent.getUsername())
																							      .concat("</b> criou uma conta para você no sistema de gerênciamento de projetos SciManager.<br/>")
																							      .concat("Seus dados de acesso são:<br /><br />Login: <b>")
																							      .concat(userForm.getEmail())
																							      .concat("</b><br />Senha: <b>")
																							      .concat(userForm.getPassword())
																							      .concat("</b>"))
																	.messageTitle("Você teve uma conta criada no SciManager")
																	.messageLink("")
																	.userSubjectId(userSubject.getUserId())
																	.userAgentId(userAgent.getUserId())
																	.notificationType(NotificationType.MESSAGE)
																	.build());
	}

	public UserDTO editUser(UserFormUpdate userFormUpdate) throws EntityNotFoundException, ExistingEntityException {
		User user = getUserEntityById(userFormUpdate.getUserId());
		
		if (user == null) {
			throw new EntityNotFoundException("Erro ao buscar usuário. Usuário com id " + userFormUpdate.getUserId() + " não foi encontrado.");
		}
		
		User userByEmail = getUserEntityByEmail(userFormUpdate.getEmail());
		
		if (userByEmail != null && !user.getUserId().equals(userByEmail.getUserId())) {
			throw new ExistingEntityException("Usuário com o email " + userFormUpdate.getEmail() + " já existe");
		}
		
		user.setEmail(userFormUpdate.getEmail());
		user.setUsername(userFormUpdate.getUsername());
		user.setInstitution(userFormUpdate.getInstitution());
		
		if (userFormUpdate.getPassword() != null && !"".equals(userFormUpdate.getPassword())) {
			user.setPassword(EncrypterUtils.encryptPassword(userFormUpdate.getPassword()));
		}
		
		return UserDTO.buildDTOByEntity(userDao.saveOrUpdateUser(user));
	}
	
	public UserDTO edituserRole(Long userId, String role) throws EntityNotFoundException {
		User user = getUserEntityById(userId);
		
		if (user == null) {
			throw new EntityNotFoundException("Erro ao buscar usuário. Usuário com id " + userId + " não foi encontrado.");
		}
		
		user.setUserRole(Role.getRoleFromString(role));
		
		return UserDTO.buildDTOByEntity(userDao.saveOrUpdateUser(user));
	}

	@Transactional
	public UserDTO uploadImage(Long userId, String imageString) throws IOException {
		if (userId == null || imageString == null) {
			log.info("Dados inválidos de entrada, parâmetros de edição não podem ser vazios.");
			return null;
		}
		
		User user = getUserEntityById(userId);
		
		if (user == null) {
			log.info("Usuário de id {} não encontrado.", userId);
			return null;
		}
		
		ProfileImage profileImage = ProfileImage.builder()
												.profileImageContent(FileUtils.processImageData(imageString))
											    .user(user).build();
		
		ProfileImageDTO persistedProfileImage = profileImageService.saveProfileImage(profileImage);
		
		if (persistedProfileImage == null) {
			log.error("Erro ao persistir imagem de perfil");
			return null;
		}

		user.setHasProfileImage(Boolean.TRUE);
		userDao.saveOrUpdateUser(user);
		
		log.info("Imagem de perfil salva com sucesso");
		return UserDTO.buildDTOByEntity(user);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void removeUser(Long userId) {
		User user = userDao.findById(userId);
		
		if (user == null) {
			log.error("Usuário de id {} não encontrado.", userId);
			return;
		}
		
		disassociateFromTasks(userId, user);
		disassociatesFromGroup(userId);
		
		userDao.delete(user);
	}

	private void disassociateFromTasks(Long userId, User user) {
		List<Task> tasksOfUser = taskService.getAllTasksEntityOfUserOrCreatedByUser(userId);
		
		if (tasksOfUser != null && !tasksOfUser.isEmpty()) {
			
			for (Task task : tasksOfUser) {
				taskService.disassociateUserFromTask(task, user);
			}
			
		}
	}

	private void disassociatesFromGroup(Long userId) {
		List<UserGroup> groupsOfUser = userGroupService.getAllUserGroupsEntityOfUser(userId);
		
		if (groupsOfUser != null && !groupsOfUser.isEmpty()) {
			
			for (UserGroup userGroup : groupsOfUser) {
				userGroupService.disassociateUserFromUserGroup(userGroup, userId);
			}
			
		}
	}

	public UserDTO editUserPassword(String email, String newPassword, String repeatedNewPassword) throws EntityNotFoundException {
		if (email == null || "".equals(email) || newPassword == null || "".equals(newPassword) || 
			repeatedNewPassword == null || "".equals(repeatedNewPassword) ||
		   (newPassword != null && !"".equals(newPassword) && repeatedNewPassword != null && !"".equals(repeatedNewPassword) && 
		   !repeatedNewPassword.equals(newPassword))) {
			
			log.error("Dados inválidos para edição, email {}, senha {}, confirmação de senha {}.", email, newPassword, repeatedNewPassword);
			return null;
		}
		
		User user = getUserEntityByEmail(email);
		
		if (user == null) {
			throw new EntityNotFoundException("Erro ao buscar usuário. Usuário com email " + email + " não foi encontrado.");
		}
		
		user.setPassword(EncrypterUtils.encryptPassword(newPassword));
		
		return UserDTO.buildDTOByEntity(userDao.saveOrUpdateUser(user));
	}

	public List<List<UserDTO>> divideUsersByRow(List<UserDTO> allUsers) {
		List<List<UserDTO>> rows = new ArrayList<List<UserDTO>>();	
		
		if (allUsers == null || allUsers.isEmpty()) {
			return rows;
		}
		
		List<UserDTO> row = new ArrayList<UserDTO>();
		
		for (int i = 0; i < allUsers.size(); i++) {
			if ((i % 4) == 0) {
				if (i > 0) {
					rows.add(row);
				}
				row = new ArrayList<UserDTO>();
			}
			row.add(allUsers.get(i));
		}
		
		rows.add(row);
		
		return rows;
	}
	
	public ProfileImageDTO getUserProfileImage(Long userId) {
		log.info("Buscando imagem de perfil do usuario de id {}", userId);
		return profileImageService.getProfileImageByUserId(userId);
	}
	
	public Integer getPageSize() {
		return userDao.getPaginationParameterConfig().getMaxResultsPerPage();
	}

	public void processPasswordRenewalRequest(String email) throws EntityNotFoundException {
		UserDTO userDTO = getUserByEmail(email);
		
		if (userDTO == null) {
			log.warn("Usuário de email {} não encontrado, não é possível processar requisição de reset de senha", email);
			throw new EntityNotFoundException("User of email " + email + " not found]");
		}
		
		mailService.sendPasswordRenewalEmail(userDTO.getUserId(), userDTO.getEmail());
	}

	public User syncUser(User user) {
		User userDatabase = getUserEntityBySlug(user.getSlug());
		
		if (userDatabase == null) {
			userDatabase = getUserEntityByEmail(user.getEmail());
		}
		
		if (userDatabase == null) {
			userDatabase = userDao.saveOrUpdateUser(User.builder()
					.email(user.getEmail())
					.institution(user.getInstitution())
					.password(user.getPassword())
					.userRole(user.getUserRole())
					.username(user.getUsername())
					.build());
		} else {
			userDatabase.setSlug(user.getSlug());
			userDatabase = userDao.saveOrUpdateUser(userDatabase);
		}
		
		profileImageService.syncProfileImage(user.getProfileImage(), userDatabase);
		
		return userDatabase;
	}

}