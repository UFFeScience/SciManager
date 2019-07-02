package com.uff.scimanager.dao.phase;

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
import com.uff.scimanager.domain.Phase;

@Repository
@Transactional
public class PhaseDao extends GenericDao<Phase> {
	
	private static final Logger log = LoggerFactory.getLogger(PhaseDao.class);
	
	@Autowired
	public PhaseDao(PaginationParameterConfig paginationParameterConfig, 
		    		@Qualifier("sciManager") SessionFactory sessionFactory) {
			
		super(paginationParameterConfig, sessionFactory);
	}

	public Phase saveOrUpdatePhase(Phase entity) {
		log.info("Salvando/atualizando fase");
		return super.saveOrUpdate(entity);
	}

	public void delete(Phase entity) {
		log.info("Deletando fase do banco");
		super.delete(entity);
	}
	
	public Phase findById(Long id) {
		log.info("Buscando fase por id {}", id);
		return super.findById(Phase.class, id);
	}
	
	public Phase findPhaseBySlug(String slug) {
		log.info("Processando busca por fase pelo slug {}", slug);
		List<Criterion> restrictions = new ArrayList<Criterion>();
		restrictions.add(Restrictions.like("slug", slug));
		
		List<Phase> queryResultList =  findByParams(Phase.class, restrictions);
		
		if (queryResultList.isEmpty()) {
			return null;
		}
		
		return queryResultList.get(0);
	}
	
	public Phase getPhaseOfProjectByPhaseName(Long scientificProjectId, String phaseName) throws HibernateException {
		log.info("Buscando fase pela phaseName {}", phaseName);
		Criteria criteria = session().createCriteria(Phase.class);
		
		criteria.add(Restrictions.eq("phaseName", phaseName.toLowerCase()).ignoreCase());
		criteria.add(Restrictions.eq("scientificProject.scientificProjectId", scientificProjectId));

		return (Phase) criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).uniqueResult();
	}

	public List<Phase> findAllDashboardPaginatedByProjectId(Long scientificProjectId, Integer pageNumber, String queryString) {
		log.info("Processando busca pagina de fases por filtro");
		List<Criterion> restrictions = new ArrayList<Criterion>();
		
		restrictions.add(Restrictions.eq("scientificProject.scientificProjectId", scientificProjectId));
		
		if (!"".equals(queryString) && queryString != null) {
			restrictions.add(Restrictions.ilike("phaseName", "%" + queryString + "%"));
		}
		
		return findByParamsPaginated(Phase.class, restrictions, pageNumber, getPaginationParameterConfig().getMaxFilterResults(), 
									 "phaseId", PaginationParameterConfig.QueryOrder.desc.name());
	}
	
	public List<Phase> findAllPaginatedByProjectId(Long scientificProjectId, Integer pageNumber, String queryString) {
		log.info("Processando busca pagina de fases por filtro");
		List<Criterion> restrictions = new ArrayList<Criterion>();
		
		restrictions.add(Restrictions.eq("scientificProject.scientificProjectId", scientificProjectId));
		
		if (!"".equals(queryString) && queryString != null) {
			restrictions.add(Restrictions.ilike("phaseName", "%" + queryString + "%"));
		}
		
		return findByParamsPaginated(Phase.class, restrictions, pageNumber, "phaseName");
	}
	
	public Integer getAllPhasesCount(Long scientificProjectId, String queryString) throws HibernateException {
		log.info("Processando busca da contagem de fases por filtro");
		
		Query countQuery = session().createSQLQuery(buildCountQuery(queryString));
		setQueryParameters(countQuery, scientificProjectId, queryString);
		
		return ((BigInteger) countQuery.list().get(0)).intValue();
	}

	private String buildCountQuery(String queryString) {
		StringBuilder getCountQuery = new StringBuilder("SELECT COUNT(ID_FASE) FROM FASE WHERE ID_PROJETO_CIENTIFICO = :scientificProjectId");
		
		if (!"".equals(queryString) && queryString != null) {
			getCountQuery.append(" AND upper(NM_NOME_FASE) like :queryString"); 
		}
		
		return getCountQuery.toString();
	}
	
	private void setQueryParameters(Query countQuery, Long scientificProjectId, String queryString) {
		countQuery.setParameter("scientificProjectId", scientificProjectId);
		
		if (!"".equals(queryString) && queryString != null) {
			countQuery.setParameter("queryString", '%' + queryString.toUpperCase() + '%');
		}
	}
	
	public void deleteByScientificProjectId(Long scientificProjectId) {
		log.info("Processando remoção de fases por filtro");
		
		Query childrenUserQuery = session().createSQLQuery("DELETE FROM TAREFA WHERE ID_FASE IN (SELECT ID_FASE FROM FASE WHERE ID_PROJETO_CIENTIFICO = :scientificProjectId)");
		childrenUserQuery.setParameter("scientificProjectId", scientificProjectId);
		childrenUserQuery.executeUpdate();
		
		Query query = session().createSQLQuery("DELETE FROM FASE WHERE ID_PROJETO_CIENTIFICO = :scientificProjectId");
		query.setParameter("scientificProjectId", scientificProjectId);
		query.executeUpdate();
		
		session().flush();
	}

}