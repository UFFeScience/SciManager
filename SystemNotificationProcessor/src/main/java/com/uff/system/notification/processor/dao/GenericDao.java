package com.uff.system.notification.processor.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericDao<T> {

	private static final Logger log = LoggerFactory.getLogger(GenericDao.class);
	
	private final SessionFactory sessionFactory;
	
	public GenericDao(SessionFactory sessionFactory) {
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

		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<T> findByParamsPaginatedDesc(Class<T> persistenceClass, List<Criterion> restrictions, Integer pageNumber, String orderColumn)
			throws HibernateException {
		
		log.info("Processando busca por parâmetros");
		Criteria criteria = session().createCriteria(persistenceClass);
		
		for (Criterion restriction : restrictions) {
			criteria.add(restriction);
		}

		if (!"".equals(orderColumn) && orderColumn != null) {
			criteria.addOrder(Property.forName(orderColumn).desc());
		}
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
}