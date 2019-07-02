package com.uff.workflow.invoker.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "USUARIO")
public class User {
	
	@Id
	@SequenceGenerator(name = "usuario_id", sequenceName = "usuario_id")
	@GeneratedValue(generator = "usuario_id", strategy = GenerationType.AUTO)
	@Column(name = "ID_USUARIO", nullable = false)
	private Long userId;
	
	@Column(name = "NM_NOME")
	private String username;
	
	@Column(name = "NM_EMAIL", unique = true)
	private String email;
	
	@Column(name = "NM_PERFIL")
	@Enumerated(EnumType.STRING)
	private Role userRole;
	
	public User() {}
	
	public static User buildEmptyUser() {
		return new User();
	}
	
	public User(UserBuilder userBuilder) {
		this.userId = userBuilder.userId;
		this.username = userBuilder.username;
		this.email = userBuilder.email;
		this.userRole = userBuilder.userRole;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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

	public Role getUserRole() {
		return userRole;
	}

	public void setUserRole(Role userRole) {
		this.userRole = userRole;
	}
	
	public static UserBuilder builder() {
		return new UserBuilder();
	}
	
	public static class UserBuilder {
		
		private Long userId;
		private String username;
		private String email;
		private Role userRole;
		
		public UserBuilder userId(Long userId) {
			this.userId = userId;
			return this;
		}
		
		public UserBuilder username(String username) {
			this.username = username;
			return this;
		}
		
		public UserBuilder email(String email) {
			this.email = email;
			return this;
		}
		
		public UserBuilder userRole(Role userRole) {
			this.userRole = userRole;
			return this;
		}
		
		public User build() {
			return new User(this);
		}
	}

}