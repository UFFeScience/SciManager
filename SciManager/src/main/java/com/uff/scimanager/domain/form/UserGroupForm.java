package com.uff.scimanager.domain.form;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import com.uff.scimanager.domain.UserGroup;

public class UserGroupForm {
	
	@NotEmpty(message = "O Nome do grupo de usuários não pode ser vazio.")
	private String groupName;
	private List<String> groupUsersEmails;
	
	public UserGroupForm() {}
	
	public UserGroupForm(UserGroup userGroup) {
		if (userGroup != null) {
			this.groupName = userGroup.getGroupName();
		}
	}
	
	public static UserGroupForm buildEmptyUserGroupForm() {
		return new UserGroupForm();
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<String> getGroupUsersEmails() {
		return groupUsersEmails;
	}

	public void setGroupUsersEmails(List<String> groupUsersNames) {
		this.groupUsersEmails = groupUsersNames;
	}
	
}