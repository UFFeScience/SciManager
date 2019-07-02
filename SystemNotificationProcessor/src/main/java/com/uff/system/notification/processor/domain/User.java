package com.uff.system.notification.processor.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "USUARIO")
public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "usuario_id", sequenceName = "usuario_id")
	@GeneratedValue(generator = "usuario_id", strategy = GenerationType.AUTO)
	@Column(name = "ID_USUARIO", nullable = false)
	private Long userId;
	
	@Column(name = "NM_EMAIL", unique = true)
	private String email;
	
	public User() {}
	
	public User(UserBuilder userBuilder) {
		this.userId = userBuilder.userId;
		this.email = userBuilder.email;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public static UserBuilder builder() {
		return new UserBuilder();
	}
	
	public static class UserBuilder {
		
		private Long userId;
		private String email;
		
		public UserBuilder userId(Long userId) {
			this.userId = userId;
			return this;
		}
		
		public UserBuilder email(String email) {
			this.email = email;
			return this;
		}
		
		public User build() {
			return new User(this);
		}
	}

}