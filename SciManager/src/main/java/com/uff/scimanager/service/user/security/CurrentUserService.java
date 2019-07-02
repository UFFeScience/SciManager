package com.uff.scimanager.service.user.security;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.base.Throwables;
import com.uff.scimanager.domain.security.CurrentUser;

@Service
public class CurrentUserService {
	
    private static final Logger log = LoggerFactory.getLogger(CurrentUserService.class);

	public Boolean canAccessUser(CurrentUser currentUser, String userId) {
		log.info("Checando se usuário {} tem acesso ao usuario de id {}", currentUser.getUsername(), userId);
		Long userIdValue;
		
		try {
			userIdValue = Long.valueOf(userId);
		} 
		catch (Exception e) {
			log.error("Id inválido, impossível bucar usuário com Id {}\n{}", userId, Throwables.getStackTraceAsString(e));
			throw new EntityNotFoundException("Dado não encontrado");
		}
		
		log.info("Buscando usuário no banco pelo id {}.", userId);
        return currentUser != null && currentUser.getUserId().equals(userIdValue);
    }

}