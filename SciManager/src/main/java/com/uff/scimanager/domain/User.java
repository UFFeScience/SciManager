package com.uff.scimanager.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.uff.scimanager.domain.dto.UserDTO;
import com.uff.scimanager.domain.form.UserForm;
import com.uff.scimanager.util.EncrypterUtils;

@Entity
@Table(name = "USUARIO")
public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "usuario_id", sequenceName = "usuario_id")
	@GeneratedValue(generator = "usuario_id", strategy = GenerationType.AUTO)
	@Column(name = "ID_USUARIO", nullable = false)
	private Long userId;
	
	@Column(name = "NM_SLUG", updatable = false, length = 32)
	private String slug;
	
	@Column(name = "NM_NOME")
	private String username;
	
	@Column(name = "NM_INSTITUICAO")
	private String institution;
	
	@Column(name = "BO_TEM_IMAGEM_PERFIL")
	private Boolean hasProfileImage = Boolean.FALSE;

	@Column(name = "NM_EMAIL", unique = true)
	private String email;
	
	@Column(name = "NM_SENHA")
	private String password;
	
	@Column(name = "NM_PERFIL")
	@Enumerated(EnumType.STRING)
	private Role userRole;
	
	@Transient
	private ProfileImage profileImage;
	
	public User() {}
	
	public static User buildEmptyUser() {
		return new User();
	}
	
	public static User buildUserFromUserForm(UserForm userForm) {
		if (userForm != null) {
			return User.builder()
					   .username(userForm.getUsername())
					   .email(userForm.getEmail())
					   .institution(userForm.getInstitution())
					   .password(EncrypterUtils.encryptPassword(userForm.getPassword()))
					   .hasProfileImage(Boolean.FALSE)
					   .userRole(userForm.getUserRole()).build();
		}
		
		return User.buildEmptyUser();
	}
	
	public User(UserBuilder userBuilder) {
		this.slug = userBuilder.slug;
		this.userId = userBuilder.userId;
		this.username = userBuilder.username;
		this.hasProfileImage = userBuilder.hasProfileImage;
		this.email = userBuilder.email;
		this.institution = userBuilder.institution;
		this.password = userBuilder.password;
		this.userRole = userBuilder.userRole;
		this.profileImage = userBuilder.profileImage;
	}
	
	public ProfileImage getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(ProfileImage profileImage) {
		this.profileImage = profileImage;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Boolean getHasProfileImage() {
		return hasProfileImage;
	}

	public void setHasProfileImage(Boolean hasProfileImage) {
		this.hasProfileImage = hasProfileImage;
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
	
	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}
	
	public UserDTO buildUserSimpleDTO() {
		return UserDTO.builder().userId(getUserId())
							   .username(getUsername())
							   .email(getEmail()).build();
	}

	public static UserBuilder builder() {
		return new UserBuilder();
	}
	
	public static class UserBuilder {
		
		private Long userId;
		private String username;
		private Boolean hasProfileImage;
		private String email;
		private String institution;
		private String password;
		private Role userRole;
		private String slug;
		private ProfileImage profileImage;
		
		public UserBuilder profileImage(ProfileImage profileImage) {
			this.profileImage = profileImage;
			return this;
		}
		
		public UserBuilder slug(String slug) {
			this.slug = slug;
			return this;
		}
		
		public UserBuilder userId(Long userId) {
			this.userId = userId;
			return this;
		}
		
		public UserBuilder username(String username) {
			this.username = username;
			return this;
		}
		
		public UserBuilder hasProfileImage(Boolean hasProfileImage) {
			this.hasProfileImage = hasProfileImage;
			return this;
		}
		
		public UserBuilder email(String email) {
			this.email = email;
			return this;
		}
		
		public UserBuilder institution(String institution) {
			this.institution = institution;
			return this;
		}
		
		public UserBuilder password(String password) {
			this.password = password;
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