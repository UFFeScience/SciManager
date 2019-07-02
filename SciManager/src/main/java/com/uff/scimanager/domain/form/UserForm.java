package com.uff.scimanager.domain.form;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.uff.scimanager.domain.Role;

public class UserForm {
	
	@NotEmpty(message = "Nome de usuário não pode ser vazio.")
	private String username;
	
	@NotEmpty(message = "Campo e-mail não pode ser vazio.")
	private String email;
	
	@NotEmpty(message = "Senha de usuário não pode ser vazia.")
	private String password;
	
	@NotEmpty(message = "Senha repetida de usuário não pode ser vazia.")
	private String repeatedPassword;

	@NotNull(message = "Perfil não pode ser vazio.")
	private Role userRole = Role.USER;
	
	private String institution;
	
	public static UserForm BuildEmptyUserForm() {
		return new UserForm();
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

	public String getRepeatedPassword() {
		return repeatedPassword;
	}

	public void setRepeatedPassword(String repeatedPassword) {
		this.repeatedPassword = repeatedPassword;
	}

	public Role getUserRole() {
		return userRole;
	}

	public void setUserRole(Role userRole) {
		this.userRole = userRole;
	}

	public String getInstitution() {
		if (institution != null && !"".equals(institution)) {
			return institution;
		}
		
		return null;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}
	
}