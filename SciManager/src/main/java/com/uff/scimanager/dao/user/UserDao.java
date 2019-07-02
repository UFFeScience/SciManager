package com.uff.scimanager.dao.user;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uff.scimanager.component.PaginationParameterConfig;
import com.uff.scimanager.dao.GenericDao;
import com.uff.scimanager.domain.User;
import com.uff.scimanager.domain.UserGroup;

@Repository
@Transactional
public class UserDao extends GenericDao<User> {
	
	private static final Logger log = LoggerFactory.getLogger(UserDao.class);
	
	@Autowired
	public UserDao(PaginationParameterConfig paginationParameterConfig, 
				   @Qualifier("sciManager") SessionFactory sessionFactory) {
		
		super(paginationParameterConfig, sessionFactory);
	}

	public User saveOrUpdateUser(User entity) {
		log.info("Salvando/atualizando usuário");
		return super.saveOrUpdate(entity);
	}

	public void delete(User entity) {
		log.info("Deletando usuário do banco");
		super.delete(entity);
	}

	public User findById(Long id) {
		log.info("Buscando usuário por id {}", id);
		return super.findById(User.class, id);
	}
	
	public User findUserBySlug(String slug) {
		log.info("Processando busca por usuário pelo slug {}", slug);
		List<Criterion> restrictions = new ArrayList<Criterion>();
		restrictions.add(Restrictions.like("slug", slug));
		
		List<User> queryResultList =  findByParams(User.class, restrictions);
		
		if (queryResultList.isEmpty()) {
			return null;
		}
		
		return queryResultList.get(0);
	}
	
	public User getUserByEmail(String email) throws HibernateException {
		log.info("Processando busca por usuário pelo email {}", email);
		List<Criterion> restrictions = new ArrayList<Criterion>();
		restrictions.add(Restrictions.like("email", email));
		
		List<User> queryResultList =  findByParams(User.class, restrictions);
		
		if (queryResultList.isEmpty()) {
			return null;
		}
		
		return queryResultList.get(0);
	}
	
	public List<User> getUsersByEmails(List<String> emails) throws HibernateException {
		log.info("Processando busca por usuários pelos emails {}", emails);
		List<Criterion> restrictions = new ArrayList<Criterion>();
		restrictions.add(Restrictions.in("email", emails));
		
		List<User> queryResultList =  findByParams(User.class, restrictions);
		
		if (queryResultList.isEmpty()) {
			return null;
		}
		
		return queryResultList;
	}
	
	@SuppressWarnings("unchecked")
	public List<User> findAllByQueryString(String queryString) {
		log.info("Processando busca por todos os usuários que contenham o texto {} no username", queryString);
		Criteria criteria = session().createCriteria(User.class, "user");
		
		if (!"".equals(queryString) && queryString != null) {
			criteria.add(Restrictions.ilike("username", "%" + queryString + "%"));
		}
		
		criteria.setMaxResults(paginationParameterConfig.getMaxFilterResults());
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	public List<User> findAllNotInGroup(String queryString, UserGroup userGroup) {
		log.info("Processando busca por todos os usuários que contenham o texto {} no username e não esteja não grupo de id {}", queryString, userGroup.getUserGroupId());
		List<Criterion> restrictions = new ArrayList<Criterion>();
		
		if (userGroup.getGroupUsersEmails() != null && !userGroup.getGroupUsersEmails().isEmpty()) {
			restrictions.add(Restrictions.not(Restrictions.in("email", userGroup.getGroupUsersEmails())));
		}
		
		if (!"".equals(queryString) && queryString != null) {
			restrictions.add(Restrictions.ilike("username", "%" + queryString + "%"));
		}
		
		return findByParams(User.class, restrictions);
	}
	
	public List<User> findAllPaginated(Integer pageNumber, String letter, String queryString) {
		log.info("Processando busca pagina de usuários por filtro");
		List<Criterion> restrictions = new ArrayList<Criterion>();
		
		if (letter != null && !"".equals(letter)) {
			restrictions.add(Restrictions.ilike("username", letter + "%"));
		}
		
		if (!"".equals(queryString) && queryString != null) {
			restrictions.add(Restrictions.ilike("username", "%" + queryString + "%"));
			
			restrictions.add(Restrictions.disjunction().add(Restrictions.ilike("username", "%" + queryString + "%"))
				       							   	   .add(Restrictions.ilike("institution", "%" + queryString + "%"))
				       							       .add(Restrictions.ilike("email", "%" + queryString + "%")));
		}
		
		return findByParamsPaginated(User.class, restrictions, pageNumber, "username");
	}
	
	
	public Integer getAllUsersSearchedCount(String letter, String queryString) throws HibernateException {
		log.info("Processando busca da contagem de usuários por filtro");
		
		Query countQuery = session().createSQLQuery(buildCountQuery(letter, queryString));
		setQueryParameters(countQuery, letter, queryString);
		
		return ((BigInteger) countQuery.list().get(0)).intValue();
	}

	private String buildCountQuery(String letter, String queryString) {
		StringBuilder getCountQuery = new StringBuilder("SELECT COUNT(ID_USUARIO) FROM USUARIO");
		
		if (letter != null && !"".equals(letter)) {
			getCountQuery.append(" WHERE upper(NM_NOME) like :letter");
		}
		
		if (!"".equals(queryString) && queryString != null) {
			
			if ((letter != null && !"".equals(letter))) {
				getCountQuery.append(" AND (upper(NM_NOME) like :queryString"); 
			}
			else {
				getCountQuery.append(" WHERE (upper(NM_NOME) like :queryString"); 
			}
			
			getCountQuery.append(" OR upper(NM_INSTITUICAO) like :queryString"); 
			getCountQuery.append(" OR upper(NM_EMAIL) like :queryString)"); 
		}
		
		return getCountQuery.toString();
	}
	
	private void setQueryParameters(Query countQuery, String letter, String queryString) {
		if (letter != null && !"".equals(letter)) {
			countQuery.setParameter("letter", letter.toUpperCase() + '%');
		}
		
		if (!"".equals(queryString) && queryString != null) {
			countQuery.setParameter("queryString", '%' + queryString.toUpperCase() + '%');
		}
	}

}