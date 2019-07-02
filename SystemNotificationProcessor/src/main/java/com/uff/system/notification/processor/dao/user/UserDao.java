package com.uff.system.notification.processor.dao.user;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uff.system.notification.processor.dao.GenericDao;
import com.uff.system.notification.processor.domain.User;

@Repository
@Transactional
public class UserDao extends GenericDao<User> {
	
	private static final Logger log = LoggerFactory.getLogger(UserDao.class);
	
	@Autowired
	public UserDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public User findById(Long id) {
		log.info("Buscando usuário por id {}", id);
		return super.findById(User.class, id);
	}
	
}