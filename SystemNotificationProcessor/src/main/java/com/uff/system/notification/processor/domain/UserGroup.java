package com.uff.system.notification.processor.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "GRUPO")
public class UserGroup {
	
	@Id
	@SequenceGenerator(name = "grupo_id", sequenceName = "grupo_id")
	@GeneratedValue(generator = "grupo_id", strategy = GenerationType.AUTO)
	@Column(name = "ID_GRUPO", nullable = false)
	private Long userGroupId;
	
	@ManyToMany
    @JoinTable(name = "GRUPO_USUARIO", joinColumns =
    {@JoinColumn(name = "ID_GRUPO")}, inverseJoinColumns =
   	{@JoinColumn(name = "ID_USUARIO")})
	@Fetch(FetchMode.SUBSELECT)
	private List<User> groupUsers; 
	
	public UserGroup() {}
	
	public UserGroup(UserGroupBuilder userGroupBuilder) {
		this.userGroupId = userGroupBuilder.userGroupId;
		this.groupUsers = userGroupBuilder.groupUsers;
	}

	public Long getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(Long userGroupId) {
		this.userGroupId = userGroupId;
	}

	public List<User> getGroupUsers() {
		if (groupUsers == null) {
			groupUsers = new ArrayList<User>();
		}
		
		return groupUsers;
	}

	public void setGroupUsers(List<User> groupUsers) {
		this.groupUsers = groupUsers;
	}

	public static UserGroupBuilder builder() {
		return new UserGroupBuilder();
	}
	
	public static class UserGroupBuilder {
		
		private Long userGroupId;
		private List<User> groupUsers; 
		
		public UserGroupBuilder userGroupId(Long userGroupId) {
			this.userGroupId = userGroupId;
			return this;
		}
		
		public UserGroupBuilder groupUsers(List<User> groupUsers) {
			this.groupUsers = groupUsers;
			return this;
		}
		
		public UserGroup build() {
			return new UserGroup(this);
		}
	}

}