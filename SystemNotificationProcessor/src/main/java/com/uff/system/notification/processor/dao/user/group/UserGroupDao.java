package com.uff.system.notification.processor.dao.user.group;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uff.system.notification.processor.dao.GenericDao;
import com.uff.system.notification.processor.domain.UserGroup;

@Repository
@Transactional
public class UserGroupDao extends GenericDao<UserGroup> {
	
	private static final Logger log = LoggerFactory.getLogger(UserGroupDao.class);
	
	@Autowired
	public UserGroupDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public UserGroup findById(Long id) {
		log.info("Buscando grupo por id {}", id);
		return super.findById(UserGroup.class, id);
	}
	
}