package com.uff.scimanager.service.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uff.scimanager.dao.user.ProfileImageDao;
import com.uff.scimanager.domain.ProfileImage;
import com.uff.scimanager.domain.User;
import com.uff.scimanager.domain.dto.ProfileImageDTO;

@Service
public class ProfileImageService {
	
	private static final Logger log = LoggerFactory.getLogger(ProfileImageService.class);
	
	private final ProfileImageDao profileImgageDao;
	
	@Autowired
	public ProfileImageService(ProfileImageDao profileImgageDao) {
		this.profileImgageDao = profileImgageDao;
	}
	
	@Transactional
	public ProfileImageDTO saveProfileImage(ProfileImage profileImage) {
    	log.info("Salvando imagem de perifl para usuario de id {}", profileImage.getUser().getUserId());
        return ProfileImageDTO.buildDTOByEntity(profileImgageDao.saveOrUpdateUser(profileImage));
    }
	
    public ProfileImageDTO getProfileImageByUserId(long userId) {
    	log.info("Buscando imagem de perfil de userId {}", userId);
        return ProfileImageDTO.buildDTOByEntity(profileImgageDao.findByUserId(userId));
    }
    
    @Transactional
	public ProfileImage syncProfileImage(ProfileImage profileImage, User user) {
    	log.info("Salvando imagem de perifl para usuario de id {}", user.getUserId());
    	
    	if (user == null || profileImage == null || profileImage.getProfileImageContent() == null) {
    		log.info("Não é possível salvar image de perfil nula");
    		return null;
    	}
    	
    	ProfileImage profileImageDatabase = profileImgageDao.findByUserId(user.getUserId());
    	
    	if (profileImageDatabase != null) {
    		return profileImageDatabase;
    	}
    	
    	profileImageDatabase = new ProfileImage();
    	profileImageDatabase.setUser(user);
    	profileImageDatabase.setProfileImageContent(profileImage.getProfileImageContent());
    	
        return profileImgageDao.saveOrUpdateUser(profileImageDatabase);
    }
    
}