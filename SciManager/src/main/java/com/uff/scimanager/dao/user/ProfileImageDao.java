package com.uff.scimanager.dao.user;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uff.scimanager.component.PaginationParameterConfig;
import com.uff.scimanager.dao.GenericDao;
import com.uff.scimanager.domain.ProfileImage;

@Repository
@Transactional
public class ProfileImageDao extends GenericDao<ProfileImage> {
	
	private static final Logger log = LoggerFactory.getLogger(ProfileImageDao.class);
	
	@Autowired
	public ProfileImageDao(PaginationParameterConfig paginationParameterConfig, 
				   		   @Qualifier("sciManager") SessionFactory sessionFactory) {
		
		super(paginationParameterConfig, sessionFactory);
	}

	public ProfileImage saveOrUpdateUser(ProfileImage entity) {
		log.info("Salvando/atualizando imagem de perfil");
		return super.saveOrUpdate(entity);
	}
	
	public ProfileImage findByUserId(Long userId) {
		log.info("Processando busca de imagem de perfil por user id {}", userId);
		Criteria criteria = session().createCriteria(ProfileImage.class, "profileImage");
		
		criteria.createAlias("profileImage.user", "user");
		criteria.add(Restrictions.eq("user.userId", userId));
		
		return (ProfileImage) criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).uniqueResult();
	}

}