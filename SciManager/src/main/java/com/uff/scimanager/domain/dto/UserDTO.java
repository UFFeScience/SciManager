package com.uff.scimanager.domain.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.uff.scimanager.domain.Role;
import com.uff.scimanager.domain.User;

public class UserDTO {
	
	private Long userId;
	private String username;
	private String email;
	
	@JsonIgnore
	private String password;
	
	private String institution;
	private Boolean hasProfileImage;
	private Role userRole;
	private List<UserGroupDTO> userGroups;
	private List<ScientificProjectDTO> scientificProjects;
	private List<WorkflowDTO> workflows;
	
	public UserDTO() {}

	public UserDTO(UserDTOBuilder userDTOBuilder) {
		this.userId = userDTOBuilder.userId;
		this.username = userDTOBuilder.username;
		this.hasProfileImage = userDTOBuilder.hasProfileImage;
		this.email = userDTOBuilder.email;
		this.institution = userDTOBuilder.institution;
		this.password = userDTOBuilder.password;
		this.userRole = userDTOBuilder.userRole;
		this.scientificProjects = userDTOBuilder.scientificProjects;
		this.userGroups = userDTOBuilder.userGroups;
		this.workflows = userDTOBuilder.workflows;
	}
	
	public static UserDTO buildEmptyDTO() {
		return new UserDTO();
	}
	
	public static UserDTO buildDTOByEntity(User user) {
		if (user == null) {
			return buildEmptyDTO();
		}
		
		return UserDTO.builder()
					 .userId(user.getUserId())
					 .username(user.getUsername())
					 .hasProfileImage(user.getHasProfileImage())
					 .email(user.getEmail())
					 .institution(user.getInstitution())
					 .password(user.getPassword())
					 .userRole(user.getUserRole()).build();
	}
	
	public Boolean getHasProfileImage() {
		return hasProfileImage;
	}

	public void setHasProfileImage(Boolean hasProfileImage) {
		this.hasProfileImage = hasProfileImage;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public Role getUserRole() {
		return userRole;
	}

	public void setUserRole(Role userRole) {
		this.userRole = userRole;
	}

	public List<UserGroupDTO> getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(List<UserGroupDTO> userGroups) {
		this.userGroups = userGroups;
	}

	public List<ScientificProjectDTO> getScientificProjects() {
		return scientificProjects;
	}

	public void setScientificProjects(List<ScientificProjectDTO> scientificProjects) {
		this.scientificProjects = scientificProjects;
	}

	public List<WorkflowDTO> getWorkflows() {
		return workflows;
	}

	public void setWorkflows(List<WorkflowDTO> workflows) {
		this.workflows = workflows;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public static List<UserDTO> convertEntityListToDTOList(Set<User> users) {
		List<UserDTO> usersDTO = new ArrayList<UserDTO>();
		
		if (users != null) {
			for (User user : users){
				usersDTO.add(UserDTO.buildDTOByEntity(user));
			}
		}
		
		return usersDTO;
	}
	
	public static List<UserDTO> convertEntityListToDTOList(List<User> users) {
		List<UserDTO> usersDTO = new ArrayList<UserDTO>();
		
		if (users != null) {
			for (User user : users){
				usersDTO.add(UserDTO.buildDTOByEntity(user));
			}
		}
		
		return usersDTO;
	}
	
	public static UserDTOBuilder builder() {
		return new UserDTOBuilder();
	}
	
	public static class UserDTOBuilder {
		
		private Long userId;
		private String username;
		private String email;
		private String password;
		private String institution;
		private Boolean hasProfileImage;
		private Role userRole;
		private List<UserGroupDTO> userGroups;
		private List<ScientificProjectDTO> scientificProjects;
		private List<WorkflowDTO> workflows;
		
		public UserDTOBuilder userId(Long userId) {
			this.userId = userId;
			return this;
		}
		
		public UserDTOBuilder username(String username) {
			this.username = username;
			return this;
		}
		
		public UserDTOBuilder hasProfileImage(Boolean hasProfileImage) {
			this.hasProfileImage = hasProfileImage;
			return this;
		}
		
		public UserDTOBuilder email(String email) {
			this.email = email;
			return this;
		}
		
		public UserDTOBuilder institution(String institution) {
			this.institution = institution;
			return this;
		}
		
		public UserDTOBuilder password(String password) {
			this.password = password;
			return this;
		}
		
		public UserDTOBuilder userRole(Role userRole) {
			this.userRole = userRole;
			return this;
		}
		
		public UserDTOBuilder userGroups(List<UserGroupDTO> userGroups) {
			this.userGroups = userGroups;
			return this;
		}
		
		public UserDTOBuilder scientificProjects(List<ScientificProjectDTO> scientificProjects) {
			this.scientificProjects = scientificProjects;
			return this;
		}
		
		public UserDTOBuilder workflows(List<WorkflowDTO> workflows) {
			this.workflows = workflows;
			return this;
		}
		
		public UserDTO build() {
			return new UserDTO(this);
		}
	}
	
}