package com.uff.scimanager.domain.form;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

public class UserFormUpdate {
	
	@NotNull(message = "UserId não pode ser nulo.")
	private Long userId;
	
	@NotNull(message = "O username não pode ser nulo.")
	@NotEmpty(message = "O Nome de usuário não pode ser vazio.")
	private String username;
	
	@NotNull(message = "O email não pode ser nulo.")
	@NotEmpty(message = "O email não pode ser vazio.")
	private String email;
	
	@NotNull(message = "A instituição não pode ser nula.")
	private String institution;
	
	@NotNull(message = "A senha não pode ser nula.")
	@NotEmpty(message = "A senha não pode ser vazio.")
	private String password;
	
	@NotNull(message = "A repetição senha não pode ser nula.")
	@NotEmpty(message = "A repetição senha não pode ser vazio.")
	private String repeatedPassword;

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

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRepeatedPassword() {
		return repeatedPassword;
	}

	public void setRepeatedPassword(String repeatedPassword) {
		this.repeatedPassword = repeatedPassword;
	}
	
}
