package com.uff.system.notification.processor.service.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uff.system.notification.processor.dao.user.UserDao;
import com.uff.system.notification.processor.domain.User;

@Service
public class UserService {
	
	private static final Logger log = LoggerFactory.getLogger(UserService.class);
	
	private final UserDao userDao;
	
	@Autowired
	public UserService(UserDao userDao) {
		this.userDao = userDao;
	}
    
    public User getUserById(long userId) {
    	User user = userDao.findById(userId);
    	
    	if (user != null) {
    		log.info("Usuário com id {} buscado com sucesso", userId);
    		return user;
    	}
    	
    	log.info("Usuário com id {}, não encontrado", userId);
        return null;
    }
    
}