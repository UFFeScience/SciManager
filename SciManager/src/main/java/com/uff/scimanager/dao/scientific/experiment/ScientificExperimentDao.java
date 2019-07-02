package com.uff.scimanager.dao.scientific.experiment;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uff.scimanager.component.PaginationParameterConfig;
import com.uff.scimanager.dao.GenericDao;
import com.uff.scimanager.domain.ScientificExperiment;
import com.uff.scimanager.util.MapUtils;

@Repository
@Transactional
public class ScientificExperimentDao extends GenericDao<ScientificExperiment> {
	
	private static final Logger log = LoggerFactory.getLogger(ScientificExperimentDao.class);
	
	@Autowired
	public ScientificExperimentDao(PaginationParameterConfig paginationParameterConfig, 
								   @Qualifier("sciManager") SessionFactory sessionFactory) {
		
		super(paginationParameterConfig, sessionFactory);
	}

	public ScientificExperiment saveOrUpdateScientificExperiment(ScientificExperiment entity) {
		log.info("Salvando/atualizando experimento científico");
		session().flush();
		session().clear();
		return super.saveOrUpdate(entity);
	}

	public void delete(ScientificExperiment entity) {
		log.info("Deletando experimento científico do banco");
		super.delete(entity);
	}

	public ScientificExperiment findById(Long id) {
		log.info("Buscando experimento científico por id {}", id);
		return super.findById(ScientificExperiment.class, id);
	}
	
	public ScientificExperiment findScientificExperimentBySlug(String slug) {
		log.info("Processando busca por experimento científico pelo slug {}", slug);
		List<Criterion> restrictions = new ArrayList<Criterion>();
		restrictions.add(Restrictions.like("slug", slug));
		
		List<ScientificExperiment> queryResultList =  findByParams(ScientificExperiment.class, restrictions);
		
		if (queryResultList.isEmpty()) {
			return null;
		}
		
		return queryResultList.get(0);
	}
	
	@SuppressWarnings("unchecked")
	public List<ScientificExperiment> findAllPaginated(Integer pageNumber, String queryString, Long scientificProjectId, Long userId) {
		log.info("Processando busca pagina de experimento científico por filtro");
		Criteria criteria = session().createCriteria(ScientificExperiment.class, "scientificExperiment");
		
		if (scientificProjectId != null) {
			criteria.createAlias("scientificExperiment.scientificProject", "scientificProject");
			criteria.add(Restrictions.eq("scientificProject.scientificProjectId", scientificProjectId));
		}
		
		if (userId != null) {
			criteria.createAlias("scientificExperiment.workflows", "workflow");
			criteria.createAlias("workflow.responsibleGroup", "responsibleGroup");
			criteria.createAlias("responsibleGroup.groupUsers", "user");
			criteria.add(Restrictions.eq("user.userId", userId));
		}
		
		if (!"".equals(queryString) && queryString != null) {
			criteria.add(Restrictions.ilike("experimentName", "%" + queryString + "%"));
		}
		
		if (PaginationParameterConfig.QueryOrder.asc.name().equals(paginationParameterConfig.getOrder())) {
			criteria.addOrder(Property.forName("experimentName").asc());
		}
		
		if (PaginationParameterConfig.QueryOrder.desc.name().equals(paginationParameterConfig.getOrder())) {
			criteria.addOrder(Property.forName("experimentName").desc());
		}
		
		criteria.setFirstResult(paginationParameterConfig.calculateActualPageFirstResultIndex(pageNumber));
		criteria.setMaxResults(paginationParameterConfig.getMaxResultsPerPage());
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<ScientificExperiment> findAllRelevantPaginated(Integer pageNumber, Long scientificProjectId, Long userId) {
		log.info("Processando busca de experimentos científicos relevantes por filtro");
		Criteria criteria = session().createCriteria(ScientificExperiment.class, "scientificExperiment");
		
		if (scientificProjectId != null) {
			criteria.createAlias("scientificExperiment.scientificProject", "scientificProject");
			criteria.add(Restrictions.eq("scientificProject.scientificProjectId", scientificProjectId));
		}
		
		List<String> relevantExperimentNames = findRelevantExperimentsNames(scientificProjectId, userId);
		
		if (relevantExperimentNames != null && !relevantExperimentNames.isEmpty()) {
			criteria.add(Restrictions.in("experimentName", relevantExperimentNames));
		}
		
		if (PaginationParameterConfig.QueryOrder.asc.name().equals(paginationParameterConfig.getOrder())) {
			criteria.addOrder(Property.forName("experimentName").asc());
		}
		
		if (PaginationParameterConfig.QueryOrder.desc.name().equals(paginationParameterConfig.getOrder())) {
			criteria.addOrder(Property.forName("experimentName").desc());
		}
		
		criteria.setFirstResult(paginationParameterConfig.calculateActualPageFirstResultIndex(pageNumber));
		criteria.setMaxResults(paginationParameterConfig.getMaxFilterResults());
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	@SuppressWarnings("unchecked")
	private List<String> findRelevantExperimentsNames(Long scientificProjectId, Long userId) {
		log.info("Processando busca de experimentos relevantes do scientificProjectId {} e do userId {}", scientificProjectId, userId);
		
		StringBuilder sqlQuery = new StringBuilder("SELECT DISTINCT nome_experimento FROM ")
										   .append("((SELECT 1, experimento_cientifico.nm_nome_experimento AS \"nome_experimento\", ")
										   .append("COUNT(tarefa.id_tarefa) AS \"total_tarefas\" ")
										   .append("FROM experimento_cientifico ")
										   .append("LEFT OUTER JOIN workflow ")
										   .append("ON workflow.id_experimento_cientifico = experimento_cientifico.id_experimento_cientifico ")
										   .append("LEFT OUTER JOIN tarefa ")
										   .append("ON tarefa.id_workflow = workflow.id_workflow ")
										   .append("LEFT OUTER JOIN usuario_tarefa ")
										   .append("ON tarefa.id_tarefa = usuario_tarefa.id_tarefa ")
										   .append("LEFT OUTER JOIN usuario ")
										   .append("ON usuario_tarefa.id_usuario = usuario.id_usuario ")
										   .append("WHERE tarefa.id_projeto_cientifico = :scientificProjectId ")
										   .append("AND usuario.id_usuario = :userId ")
										   .append("OR tarefa.id_tarefa IN ")
										   .append("(SELECT tarefa.id_tarefa FROM tarefa ")
										   .append("LEFT OUTER JOIN grupo ")
										   .append("ON tarefa.id_grupo = grupo.id_grupo ")
										   .append("LEFT OUTER JOIN grupo_usuario ")
										   .append("ON grupo.id_grupo = grupo_usuario.id_grupo ")
										   .append("LEFT OUTER JOIN usuario ")
										   .append("ON grupo_usuario.id_usuario = usuario.id_usuario ")
										   .append("WHERE usuario.id_usuario = :userId ")
										   .append("AND tarefa.id_projeto_cientifico = :scientificProjectId) ")
										   .append("GROUP BY nome_experimento ")
										   .append("ORDER BY total_tarefas DESC ")
										   .append("LIMIT :limit) ")
										   .append("UNION ")
										   .append("(SELECT 2, experimento_cientifico.nm_nome_experimento AS \"nome_experimento\", 0 AS \"total_tarefas\" ")
										   .append("FROM experimento_cientifico ")
										   .append("WHERE experimento_cientifico.id_projeto_cientifico = :scientificProjectId ")
										   .append("LIMIT :limit)) AS \"experimentos\" ")
										   .append("ORDER BY 1 ")
										   .append("LIMIT :limit");
		
		Query hqlQuery = session().createSQLQuery(sqlQuery.toString());
		hqlQuery.setParameter("scientificProjectId", scientificProjectId);
		hqlQuery.setParameter("userId", userId);
		hqlQuery.setParameter("limit", paginationParameterConfig.getMaxFilterResults());
		
		return MapUtils.mapListMapToStringList(hqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list());
	}

	public ScientificExperiment findByExperimentName(String experimentName) {
		log.info("Processando busca pagina de experimento científico por experimentName {}", experimentName);
		List<Criterion> restrictions = new ArrayList<Criterion>();
		
		if (!"".equals(experimentName) && experimentName != null) {
			restrictions.add(Restrictions.eq("experimentName", experimentName.toLowerCase()).ignoreCase());
		}
		
		List<ScientificExperiment> queryResultList = findByParams(ScientificExperiment.class, restrictions);
		
		if (queryResultList.isEmpty()) {
			return null;
		}
		
		return queryResultList.get(0);
	}
	
	public Integer getAllScientificExperimentCount(String queryString, Long scientificProjectId, Long userId) throws HibernateException {
		log.info("Processando busca da contagem de experimentos científicos por filtro");
		Criteria criteria = session().createCriteria(ScientificExperiment.class, "scientificExperiment");
		
		if (scientificProjectId != null) {
			criteria.createAlias("scientificExperiment.scientificProject", "scientificProject");
			criteria.add(Restrictions.eq("scientificProject.scientificProjectId", scientificProjectId));
		}
		
		if (userId != null) {
			criteria.createAlias("scientificExperiment.workflows", "workflow");
			criteria.createAlias("workflow.responsibleGroup", "responsibleGroup");
			criteria.createAlias("responsibleGroup.groupUsers", "user");
			criteria.add(Restrictions.eq("user.userId", userId));
		}
		
		if (!"".equals(queryString) && queryString != null) {
			criteria.add(Restrictions.ilike("experimentName", "%" + queryString + "%"));
		}
		
		return ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}
	
	public Integer getAllScientificExperimentCount(String queryString, Long scientificProjectId) throws HibernateException {
		log.info("Processando busca da contagem de experimentos científicos por filtro");
		Criteria criteria = session().createCriteria(ScientificExperiment.class, "scientificExperiment");
		
		if (scientificProjectId != null) {
			criteria.createAlias("scientificExperiment.scientificProject", "scientificProject");
			criteria.add(Restrictions.eq("scientificProject.scientificProjectId", scientificProjectId));
		}
		
		if (!"".equals(queryString) && queryString != null) {
			criteria.add(Restrictions.ilike("experimentName", "%" + queryString + "%"));
		}
		
		return ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	public void deleteByScientificProjectId(Long scientificProjectId) {
		log.info("Processando remoção de experimentos científicos por filtro");
		
		Query childrenDocumentationQuery = session().createSQLQuery("DELETE FROM DOCUMENTACAO WHERE ID_EXPERIMENTO_CIENTIFICO IN " + 
																	"(SELECT ID_EXPERIMENTO_CIENTIFICO FROM EXPERIMENTO_CIENTIFICO WHERE " + 
																	"ID_PROJETO_CIENTIFICO = :scientificProjectId)");
		
		childrenDocumentationQuery.setParameter("scientificProjectId", scientificProjectId);
		childrenDocumentationQuery.executeUpdate();
		
		Query query = session().createSQLQuery("DELETE FROM EXPERIMENTO_CIENTIFICO WHERE ID_PROJETO_CIENTIFICO = :scientificProjectId");
		query.setParameter("scientificProjectId", scientificProjectId);
		query.executeUpdate();
		
		session().flush();
	}

}