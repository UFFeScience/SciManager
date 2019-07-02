package com.uff.system.notification.processor.service.user.group;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uff.system.notification.processor.dao.user.group.UserGroupDao;
import com.uff.system.notification.processor.domain.UserGroup;

@Service
public class UserGroupService {
	
	private static final Logger log = LoggerFactory.getLogger(UserGroupService.class);
	
	private final UserGroupDao userGroupDao;
	
	@Autowired
	public UserGroupService(UserGroupDao userGroupDao) {
		this.userGroupDao = userGroupDao;
	}
    
    public UserGroup getUserGroupById(long userGroupId) {
    	UserGroup userGroup = userGroupDao.findById(userGroupId);
    	
    	if (userGroup != null) {
    		log.info("Grupo com id {} buscado com sucesso", userGroupId);
    		return userGroup;
    	}
    	
    	log.info("Grupo com id {}, não encontrado", userGroupId);
        return null;
    }
    
}