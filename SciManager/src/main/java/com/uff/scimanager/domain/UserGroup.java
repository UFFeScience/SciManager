package com.uff.scimanager.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uff.scimanager.domain.dto.UserGroupDTO;

@Entity
@Table(name = "GRUPO")
public class UserGroup implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(UserGroup.class);
	
	@Id
	@SequenceGenerator(name = "grupo_id", sequenceName = "grupo_id")
	@GeneratedValue(generator = "grupo_id", strategy = GenerationType.AUTO)
	@Column(name = "ID_GRUPO", nullable = false)
	private Long userGroupId;
	
	@Column(name = "NM_SLUG", updatable = false, length = 32)
	private String slug;
	
	@Column(name = "NM_NOME_GRUPO", unique = true)
	private String groupName;
	
	@ManyToMany
    @JoinTable(name = "GRUPO_USUARIO", joinColumns =
    {@JoinColumn(name = "ID_GRUPO")}, inverseJoinColumns =
   	{@JoinColumn(name = "ID_USUARIO")})
	@Fetch(FetchMode.SUBSELECT)
	private Set<User> groupUsers; 
	
	public UserGroup() {}
	
	public UserGroup(UserGroupBuilder userGroupBuilder) {
		this.slug = userGroupBuilder.slug;
		this.userGroupId = userGroupBuilder.userGroupId;
		this.groupName = userGroupBuilder.groupName;
		this.groupUsers = userGroupBuilder.groupUsers;
	}
	
	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
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

	public Set<User> getGroupUsers() {
		if (groupUsers == null) {
			groupUsers = new HashSet<User>();
		}
		
		return groupUsers;
	}

	public void setGroupUsers(Set<User> groupUsers) {
		this.groupUsers = groupUsers;
	}

	public List<String> getGroupUsersEmails() {
		List<String> emails = new ArrayList<String>();
		
		for (User user : getGroupUsers()) {
			emails.add(user.getEmail());
		}
		
		return emails;
	}

	public void addUserToUserGroup(User user) {
		if (!containUser(user)) {
			getGroupUsers().add(user);
		}
		
		log.error("Erro ao adicionar usuário ao grupo. O usuário já se encontra no grupo");
	}

	private Boolean containUser(User newUser) {
		for (User user : getGroupUsers()) {
			if (user.getUserId().equals(newUser.getUserId())) {
				return Boolean.TRUE;
			}
		}
		
		return Boolean.FALSE;
	}

	public Boolean removeUserFromGroupUsers(Long userId) {
		for (Iterator<User> i = getGroupUsers().iterator(); i.hasNext();) {
			User user = i.next();
			
			if (user.getUserId().equals(userId)) {
				getGroupUsers().remove(user);
				return Boolean.TRUE;
			}
		}
		
		return Boolean.FALSE;
	}
	
	public Set<User> getUsersByEmails(List<String> usersInTask) {
		Set<User> usersResponse = new HashSet<User>();
		
		for (String email : usersInTask) {
			for (User user : getGroupUsers()) {
				if (email.equals(user.getEmail())) {
					usersResponse.add(user);
				}
			}
		}
		
		return usersResponse;
	}
	
	public User getGroupUserByEmail(String email) {
		for (User user : getGroupUsers()) {
			if (email.equals(user.getEmail())) {
				return user;
			}
		}
		
		return null;
	}
	
	public List<Long> getGroupUsersIds() {
		List<Long> userIds = new ArrayList<Long>();
		
		for (User user : getGroupUsers()) {
			userIds.add(user.getUserId());
		}
		
		return userIds;
	}

	public UserGroupDTO buildUserGroupSimpleDTO() {
		return UserGroupDTO.builder().userGroupId(getUserGroupId())
									.groupName(getGroupName()).build();
	}
	
	public static UserGroupBuilder builder() {
		return new UserGroupBuilder();
	}
	
	public static class UserGroupBuilder {
		
		private Long userGroupId;
		private String slug;
		private String groupName;
		private Set<User> groupUsers; 
		
		public UserGroupBuilder slug(String slug) {
			this.slug = slug;
			return this;
		}
		
		public UserGroupBuilder userGroupId(Long userGroupId) {
			this.userGroupId = userGroupId;
			return this;
		}
		
		public UserGroupBuilder groupName(String groupName) {
			this.groupName = groupName;
			return this;
		}
		
		public UserGroupBuilder groupUsers(Set<User> groupUsers) {
			this.groupUsers = groupUsers;
			return this;
		}
		
		public UserGroup build() {
			return new UserGroup(this);
		}
	}
	
}