package com.uff.scimanager.domain.dto;

import com.uff.scimanager.domain.ProfileImage;
import com.uff.scimanager.domain.User;
import com.uff.scimanager.util.FileUtils;

public class ProfileImageDTO {
	
	private Long profileImageId;
	private String profileImageContent;
	private User user;
	
	public ProfileImageDTO() {}

	public ProfileImageDTO(ProfileImageDTOBuilder profileImageDTOBuilder) {
		this.profileImageId = profileImageDTOBuilder.profileImageId;
		this.profileImageContent = profileImageDTOBuilder.profileImageContent;
		this.user = profileImageDTOBuilder.user;
	}
	
	public static ProfileImageDTO buildEmptyProfileImageDTO() {
		return new ProfileImageDTO();
	}
	
	public static ProfileImageDTO buildDTOByEntity(ProfileImage profileImage) {
		if (profileImage == null) {
			return buildEmptyProfileImageDTO();
		}
		
		return ProfileImageDTO.builder()
							 .profileImageId(profileImage.getProfileImageId())
							 .profileImageContent(FileUtils.encodeBytes(profileImage.getProfileImageContent()))
							 .user(profileImage.getUser()).build();
	}
	
	public Long getProfileImageId() {
		return profileImageId;
	}

	public void setProfileImageId(Long profileImageId) {
		this.profileImageId = profileImageId;
	}

	public String getProfileImageContent() {
		return profileImageContent;
	}

	public void setProfileImageContent(String profileImageContent) {
		this.profileImageContent = profileImageContent;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public static ProfileImageDTOBuilder builder() {
		return new ProfileImageDTOBuilder();
	}
	
	public static class ProfileImageDTOBuilder {
		
		private Long profileImageId;
		private String profileImageContent;
		private User user;
		
		public ProfileImageDTOBuilder profileImageId(Long profileImageId) {
			this.profileImageId = profileImageId;
			return this;
		}
		
		public ProfileImageDTOBuilder profileImageContent(String profileImageContent) {
			this.profileImageContent = profileImageContent;
			return this;
		}
		
		public ProfileImageDTOBuilder user(User user) {
			this.user = user;
			return this;
		}
		
		public ProfileImageDTO build() {
			return new ProfileImageDTO(this);
		}
	}
	
}