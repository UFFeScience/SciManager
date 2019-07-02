package com.uff.scimanager.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name = "IMAGEM_PERFIL")
public class ProfileImage implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "imagem_perfil_id", sequenceName = "imagem_perfil_id")
	@GeneratedValue(generator = "imagem_perfil_id", strategy = GenerationType.AUTO)
	@Column(name = "ID_IMAGEM_PERFIL", nullable = false)
	private Long profileImageId;
	
	@Lob
	@Column(name = "NM_IMAGEM_PERFIL_CONTEUDO")
	private byte[] profileImageContent;

	@OneToOne
	@JoinColumn(name = "ID_USUARIO", referencedColumnName = "ID_USUARIO", nullable = true)
	@Cascade(CascadeType.SAVE_UPDATE)
	private User user;
	
	public ProfileImage() {}
	
	public ProfileImage(ProfileImageBuilder profileImageBuilder) {
		this.profileImageId = profileImageBuilder.profileImageId;
		this.profileImageContent = profileImageBuilder.profileImageContent;
		this.user = profileImageBuilder.user;
	}

	
	public Long getProfileImageId() {
		return profileImageId;
	}

	public void setProfileImageId(Long profileImageId) {
		this.profileImageId = profileImageId;
	}

	public byte[] getProfileImageContent() {
		return profileImageContent;
	}

	public void setProfileImageContent(byte[] profileImageContent) {
		this.profileImageContent = profileImageContent;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public static ProfileImageBuilder builder() {
		return new ProfileImageBuilder();
	}
	
	public static class ProfileImageBuilder {
		
		private Long profileImageId;
		private byte[] profileImageContent;
		private User user;
		
		public ProfileImageBuilder profileImageId(Long profileImageId) {
			this.profileImageId = profileImageId;
			return this;
		}
		
		public ProfileImageBuilder profileImageContent(byte[] profileImageContent) {
			this.profileImageContent = profileImageContent;
			return this;
		}
		
		public ProfileImageBuilder user(User user) {
			this.user = user;
			return this;
		}
		
		public ProfileImage build() {
			return new ProfileImage(this);
		}
	}

}