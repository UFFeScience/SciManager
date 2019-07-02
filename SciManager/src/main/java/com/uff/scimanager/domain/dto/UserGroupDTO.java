package com.uff.scimanager.domain.dto;

import java.util.ArrayList;
import java.util.List;

import com.uff.scimanager.domain.UserGroup;

public class UserGroupDTO {
	
	private Long userGroupId;
	private String groupName;
	private List<UserDTO> groupUsers;
	private List<ScientificProjectDTO> scientificProjects;
	private List<WorkflowDTO> workflows;
	
	public UserGroupDTO() {}
	
	public UserGroupDTO(UserGroupDTOBuilder userGroupDTOBuilder) {
		this.userGroupId = userGroupDTOBuilder.userGroupId;
		this.groupName = userGroupDTOBuilder.groupName;
		this.groupUsers = userGroupDTOBuilder.groupUsers;
		this.scientificProjects = userGroupDTOBuilder.scientificProjects;
		this.workflows = userGroupDTOBuilder.workflows;
	}
	
	public static UserGroupDTO buildEmptyDTO() {
		return new UserGroupDTO();
	}

	public static UserGroupDTO buildDTOWithChildrenByEntity(UserGroup userGroup) {
		if (userGroup != null) {
			return UserGroupDTO.builder()
							  .userGroupId(userGroup.getUserGroupId())
							  .groupName(userGroup.getGroupName())
							  .groupUsers(UserDTO.convertEntityListToDTOList(userGroup.getGroupUsers())).build();
		}
		
		return UserGroupDTO.buildEmptyDTO();
	}
	
	private static UserGroupDTO buildDTOByEntity(UserGroup userGroup) {
		if (userGroup != null) {
			return UserGroupDTO.builder()
							  .userGroupId(userGroup.getUserGroupId())
							  .groupName(userGroup.getGroupName()).build();
		}
		
		return UserGroupDTO.buildEmptyDTO();
	}

	public Long getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(Long userGroupId) {
		this.userGroupId = userGroupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
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

	public List<UserDTO> getGroupUsers() {
		if (groupUsers == null) {
			groupUsers = new ArrayList<UserDTO>();
		}
		
		return groupUsers;
	}

	public void setGroupUsers(List<UserDTO> groupUsers) {
		this.groupUsers = groupUsers;
	}
	
	public static List<UserGroupDTO> convertEntityListNoChildToDTOList(List<UserGroup> userGroups) {
		List<UserGroupDTO> userGroupsDTO = new ArrayList<UserGroupDTO>();
		
		if (userGroups != null) {
			for (UserGroup userGroup : userGroups) {
				userGroupsDTO.add(UserGroupDTO.buildDTOByEntity(userGroup));
			}
		}
		
		return userGroupsDTO;
	}

	public static List<UserGroupDTO> convertEntityListToDTOList(List<UserGroup> userGroups) {
		List<UserGroupDTO> userGroupsDTO = new ArrayList<UserGroupDTO>();
		
		for (UserGroup userGroup : userGroups){
			userGroupsDTO.add(UserGroupDTO.buildDTOWithChildrenByEntity(userGroup));
		}
		
		return userGroupsDTO;
	}
	
	public static UserGroupDTOBuilder builder() {
		return new UserGroupDTOBuilder();
	}
	
	public static class UserGroupDTOBuilder {
		
		private Long userGroupId;
		private String groupName;
		private List<UserDTO> groupUsers;
		private List<ScientificProjectDTO> scientificProjects;
		private List<WorkflowDTO> workflows;
		
		public UserGroupDTOBuilder userGroupId(Long userGroupId) {
			this.userGroupId = userGroupId;
			return this;
		}
		
		public UserGroupDTOBuilder groupName(String groupName) {
			this.groupName = groupName;
			return this;
		}
		
		public UserGroupDTOBuilder groupUsers(List<UserDTO> groupUsers) {
			this.groupUsers = groupUsers;
			return this;
		}
		
		public UserGroupDTOBuilder scientificProjects(List<ScientificProjectDTO> scientificProjects) {
			this.scientificProjects = scientificProjects;
			return this;
		}
		
		public UserGroupDTOBuilder workflows(List<WorkflowDTO> workflows) {
			this.workflows = workflows;
			return this;
		}
		
		public UserGroupDTO build() {
			return new UserGroupDTO(this);
		}
	}

}