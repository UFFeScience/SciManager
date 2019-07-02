package com.uff.scimanager.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uff.scimanager.component.PaginationParameterConfig;

public class GenericDao<T> {

	private static final Logger log = LoggerFactory.getLogger(GenericDao.class);
	
	protected final PaginationParameterConfig paginationParameterConfig;
	private final SessionFactory sessionFactory;
	
	public GenericDao(PaginationParameterConfig paginationParameterConfig, SessionFactory sessionFactory) {
		this.paginationParameterConfig = paginationParameterConfig;
		this.sessionFactory = sessionFactory;
	}

	protected Session session() {
		log.info("Pegando sessão do banco corrente");
		return sessionFactory.getCurrentSession();
	}

	protected T saveOrUpdate(T entity) {
		log.info("Salvando entidade");
		session().saveOrUpdate(entity);
		session().flush();
		return entity;
	}
	
	protected T merge(T entity) {
		log.info("Salvando entidade");
		session().merge(entity);
		session().flush();
		return entity;
	}

	protected void delete(T entity) {
		log.info("Deletando entidade");
		session().delete(session().merge(entity));
	}

	@SuppressWarnings("unchecked")
	protected T findById(Class<T> persistenceClass, Long id) {
		log.info("Processando busca por id");
		return (T) session().get(persistenceClass, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<T> findByParams(Class<T> persistenceClass, List<Criterion> restrictions) throws HibernateException {
		log.info("Processando busca por parâmetros");
		Criteria criteria = session().createCriteria(persistenceClass);

		for (Criterion restriction : restrictions) {
			criteria.add(restriction);
		}

		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<T> findByParamsPaginated(Class<T> persistenceClass, List<Criterion> restrictions, Integer pageNumber, String orderColumn)
			throws HibernateException {
		
		log.info("Processando busca paginada por parâmetros");
		Criteria criteria = session().createCriteria(persistenceClass);
		
		for (Criterion restriction : restrictions) {
			criteria.add(restriction);
		}

		if (!"".equals(orderColumn) && orderColumn != null) {
			if (PaginationParameterConfig.QueryOrder.asc.name().equals(paginationParameterConfig.getOrder())) {
				criteria.addOrder(Property.forName(orderColumn).asc());
			}
			if (PaginationParameterConfig.QueryOrder.desc.name().equals(paginationParameterConfig.getOrder())) {
				criteria.addOrder(Property.forName(orderColumn).desc());
			}
		}
		
		criteria.setFirstResult(paginationParameterConfig.calculateActualPageFirstResultIndex(pageNumber));
		criteria.setMaxResults(paginationParameterConfig.getMaxResultsPerPage());
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<T> findByParamsPaginated(Class<T> persistenceClass, List<Criterion> restrictions, Integer pageNumber, 
										 Integer limit, String orderColumn, String order) throws HibernateException {
		
		log.info("Processando busca paginada por parâmetros");
		Criteria criteria = session().createCriteria(persistenceClass);
		
		for (Criterion restriction : restrictions) {
			criteria.add(restriction);
		}

		if (!"".equals(orderColumn) && orderColumn != null) {
			if (PaginationParameterConfig.QueryOrder.asc.name().equals(order)) {
				criteria.addOrder(Property.forName(orderColumn).asc());
			}
			if (PaginationParameterConfig.QueryOrder.desc.name().equals(order)) {
				criteria.addOrder(Property.forName(orderColumn).desc());
			}
		}
		
		criteria.setFirstResult(paginationParameterConfig.calculateActualPageFirstResultIndex(pageNumber));
		criteria.setMaxResults(limit);
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<T> findByParamsPaginatedDesc(Class<T> persistenceClass, List<Criterion> restrictions, Integer pageNumber, String orderColumn)
			throws HibernateException {
		
		log.info("Processando busca paginada por parâmetros");
		Criteria criteria = session().createCriteria(persistenceClass);
		
		for (Criterion restriction : restrictions) {
			criteria.add(restriction);
		}

		if (!"".equals(orderColumn) && orderColumn != null) {
			criteria.addOrder(Property.forName(orderColumn).desc());
		}
		
		criteria.setFirstResult(paginationParameterConfig.calculateActualPageFirstResultIndex(pageNumber));
		criteria.setMaxResults(paginationParameterConfig.getMaxResultsPerPage());
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	public PaginationParameterConfig getPaginationParameterConfig() {
		return paginationParameterConfig;
	}
	
}