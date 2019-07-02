package com.uff.scimanager.dao.user.group;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uff.scimanager.component.PaginationParameterConfig;
import com.uff.scimanager.dao.GenericDao;
import com.uff.scimanager.domain.UserGroup;

@Repository
@Transactional
public class UserGroupDao extends GenericDao<UserGroup> {
	
	private static final Logger log = LoggerFactory.getLogger(UserGroupDao.class);
	
	@Autowired
	public UserGroupDao(PaginationParameterConfig paginationParameterConfig, 
						@Qualifier("sciManager") SessionFactory sessionFactory) {
		
		super(paginationParameterConfig, sessionFactory);
	}

	public UserGroup saveOrUpdateUserGroup(UserGroup entity) {
		log.info("Salvando/atualizando grupo");
		return super.saveOrUpdate(entity);
	}

	public void delete(UserGroup entity) {
		log.info("Deletando grupo do banco");
		super.delete(entity);
	}

	public UserGroup findById(Long id) {
		log.info("Buscando grupo pelo id {} no banco", id);
		return super.findById(UserGroup.class, id);
	}
	
	public UserGroup findUserGroupBySlug(String slug) {
		log.info("Processando busca por grupo pelo slug {}", slug);
		List<Criterion> restrictions = new ArrayList<Criterion>();
		restrictions.add(Restrictions.like("slug", slug));
		
		List<UserGroup> queryResultList =  findByParams(UserGroup.class, restrictions);
		
		if (queryResultList.isEmpty()) {
			return null;
		}
		
		return queryResultList.get(0);
	}
	
	public UserGroup getUserGroupByGroupName(String groupName) throws HibernateException {
		log.info("Buscando grupo pelo groupName {}", groupName);
		List<Criterion> restrictions = new ArrayList<Criterion>();
		restrictions.add(Restrictions.like("groupName", groupName));
		
		List<UserGroup> queryResultList =  findByParams(UserGroup.class, restrictions);
		
		if (queryResultList.isEmpty()) {
			return null;
		}
		
		return queryResultList.get(0);
	}
	
	public List<UserGroup> findAll(String queryString) {
		log.info("Buscando grupos que contenham o texto {} no groupName", queryString);
		List<Criterion> restrictions = new ArrayList<Criterion>();
		
		if (!"".equals(queryString) && queryString != null) {
			restrictions.add(Restrictions.ilike("groupName", "%" + queryString + "%"));
		}
		
		return findByParams(UserGroup.class, restrictions);
	}
	
	@SuppressWarnings("unchecked")
	public List<UserGroup> findAllPaginated(Integer pageNumber, String queryString, Long userId) {
		log.info("Realizando busca páginada de grupos por filtro");
		Criteria criteria = session().createCriteria(UserGroup.class, "group");

		if (userId != null) {
			criteria.createAlias("group.groupUsers", "user");
			criteria.add(Restrictions.eq("user.userId", userId));
		}
		
		if (!"".equals(queryString) && queryString != null) {
			criteria.add(Restrictions.ilike("groupName", "%" + queryString + "%"));
		}

		if (PaginationParameterConfig.QueryOrder.asc.name().equals(paginationParameterConfig.getOrder())) {
			criteria.addOrder(Property.forName("groupName").asc());
		}
		
		if (PaginationParameterConfig.QueryOrder.desc.name().equals(paginationParameterConfig.getOrder())) {
			criteria.addOrder(Property.forName("groupName").desc());
		}
		
		criteria.setFetchMode("groupUsers", FetchMode.SELECT);
		criteria.setFirstResult(paginationParameterConfig.calculateActualPageFirstResultIndex(pageNumber));
		criteria.setMaxResults(paginationParameterConfig.getMaxResultsPerPage());
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<UserGroup> findAlluserGroupsOfUser(Long userId) {
		log.info("Realizando busca páginada de grupos por filtro");
		Criteria criteria = session().createCriteria(UserGroup.class, "group");

		if (userId != null) {
			criteria.createAlias("group.groupUsers", "user");
			criteria.add(Restrictions.eq("user.userId", userId));
		}
		
		criteria.setFetchMode("groupUsers", FetchMode.SELECT);
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<UserGroup> findAllByQueryString(String queryString, Long userId) {
		log.info("Processando busca por todos os grupos de usuários que contenham o texto {} no groupName", queryString);
		Criteria criteria = session().createCriteria(UserGroup.class, "group");

		if (userId != null) {
			criteria.createAlias("group.groupUsers", "user");
			criteria.add(Restrictions.eq("user.userId", userId));
		}
		
		if (!"".equals(queryString) && queryString != null) {
			criteria.add(Restrictions.ilike("groupName", "%" + queryString + "%"));
		}
		
		criteria.setMaxResults(paginationParameterConfig.getMaxFilterResults());
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	public Integer getAllUserGroupsCount(String queryString, Long userId) throws HibernateException {
		log.info("Realizando busca de contagem de grupos por filtro");
		Criteria criteria = session().createCriteria(UserGroup.class, "group");

		if (userId != null) {
			criteria.createAlias("group.groupUsers", "user");
			criteria.add(Restrictions.eq("user.userId", userId));
		}
		
		if (!"".equals(queryString) && queryString != null) {
			criteria.add(Restrictions.ilike("groupName", "%" + queryString + "%"));
		}
		
		return ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

}