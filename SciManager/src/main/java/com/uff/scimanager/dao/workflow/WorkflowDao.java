package com.uff.scimanager.dao.workflow;

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
import com.uff.scimanager.domain.Workflow;
import com.uff.scimanager.domain.form.WorkflowFormFilter;
import com.uff.scimanager.util.MapUtils;

@Repository
@Transactional
public class WorkflowDao extends GenericDao<Workflow> {
	
	private static final Logger log = LoggerFactory.getLogger(WorkflowDao.class);
	
	@Autowired
	public WorkflowDao(PaginationParameterConfig paginationParameterConfig, 
					   @Qualifier("sciManager") SessionFactory sessionFactory) {
		
		super(paginationParameterConfig, sessionFactory);
	}

	public Workflow saveOrUpdateWorkflow(Workflow entity) {
		log.info("Salvando/atualizando workflow");
		return super.saveOrUpdate(entity);
	}

	public void delete(Workflow entity) {
		log.info("Deletando workflow do banco");
		super.delete(entity);
	}
	
	public Workflow findWorkflowBySlug(String slug) {
		log.info("Processando busca por workflow pelo slug {}", slug);
		List<Criterion> restrictions = new ArrayList<Criterion>();
		restrictions.add(Restrictions.like("slug", slug));
		
		List<Workflow> queryResultList =  findByParams(Workflow.class, restrictions);
		
		if (queryResultList.isEmpty()) {
			return null;
		}
		
		return queryResultList.get(0);
	}

	public Workflow findById(Long id) {
		log.info("Buscando workflow por id {}", id);
		return super.findById(Workflow.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<Workflow> findAllRelevantPaginated(Integer pageNumber, Long scientificExperimentId, Long userId) {
		log.info("Processando busca de lista de workflow relevantes por filtro");
		Criteria criteria = session().createCriteria(Workflow.class, "workflow");
		
		if (scientificExperimentId != null) {
			criteria.createAlias("workflow.scientificExperiment", "scientificExperiment");
			criteria.add(Restrictions.eq("scientificExperiment.scientificExperimentId", scientificExperimentId));
		}
		
		List<String> relevantWorkflowNames = findRelevantWorkflowNames(scientificExperimentId, userId);
		
		if (relevantWorkflowNames != null && !relevantWorkflowNames.isEmpty()) {
			criteria.add(Restrictions.in("workflowName", relevantWorkflowNames));
		}
		
		if (PaginationParameterConfig.QueryOrder.asc.name().equals(paginationParameterConfig.getOrder())) {
			criteria.addOrder(Property.forName("workflowName").asc());
		}
		
		if (PaginationParameterConfig.QueryOrder.desc.name().equals(paginationParameterConfig.getOrder())) {
			criteria.addOrder(Property.forName("workflowName").desc());
		}
		
		criteria.setFirstResult(paginationParameterConfig.calculateActualPageFirstResultIndex(pageNumber));
		criteria.setMaxResults(paginationParameterConfig.getMaxFilterResults());
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	@SuppressWarnings("unchecked")
	private List<String> findRelevantWorkflowNames(Long scientificExperimentId, Long userId) {
		log.info("Processando busca de workflows relevantes do scientificExperimentId {} e do userId {}", scientificExperimentId, userId);
		
		StringBuilder sqlQuery = new StringBuilder("SELECT DISTINCT nome_workflow FROM ")
										   .append("((SELECT 1, workflow.nm_nome_workflow AS \"nome_workflow\", ")
										   .append("COUNT(tarefa.id_tarefa) AS \"total_tarefas\" ")
										   .append("FROM workflow ")
										   .append("LEFT OUTER JOIN experimento_cientifico ")
										   .append("ON workflow.id_workflow = experimento_cientifico.id_experimento_cientifico ")
										   .append("LEFT OUTER JOIN tarefa ")
										   .append("ON tarefa.id_workflow = workflow.id_workflow ")
										   .append("LEFT OUTER JOIN usuario_tarefa ")
										   .append("ON tarefa.id_tarefa = usuario_tarefa.id_tarefa ")
										   .append("LEFT OUTER JOIN usuario ")
										   .append("ON usuario_tarefa.id_usuario = usuario.id_usuario ")
										   .append("WHERE experimento_cientifico.id_experimento_cientifico = :scientificExperimentId ")
										   .append("AND usuario.id_usuario = :userId ")
										   .append("OR tarefa.id_tarefa IN ")
										   .append("(SELECT tarefa.id_tarefa FROM tarefa ")
										   .append("LEFT OUTER JOIN grupo ")
										   .append("ON tarefa.id_grupo = grupo.id_grupo ")
										   .append("LEFT OUTER JOIN grupo_usuario ")
										   .append("ON grupo.id_grupo = grupo_usuario.id_grupo ")
										   .append("LEFT OUTER JOIN usuario ")
										   .append("ON grupo_usuario.id_usuario = usuario.id_usuario ")
										   .append("LEFT OUTER JOIN experimento_cientifico ")
										   .append("ON tarefa.id_workflow = experimento_cientifico.id_experimento_cientifico ")
										   .append("WHERE usuario.id_usuario = :userId ")
										   .append("AND experimento_cientifico.id_experimento_cientifico = :scientificExperimentId) ")
										   .append("GROUP BY nome_workflow ")
										   .append("ORDER BY total_tarefas DESC ")
										   .append("LIMIT :limit) ")
										   .append("UNION ")
										   .append("(SELECT 2, workflow.nm_nome_workflow AS \"nome_workflow\", 0 AS \"total_tarefas\" ")
										   .append("FROM workflow ")
										   .append("WHERE workflow.id_experimento_cientifico = :scientificExperimentId ")
										   .append("LIMIT :limit)) AS \"workflows\" ")
										   .append("ORDER BY 1 ")
										   .append("LIMIT :limit");
		
		Query hqlQuery = session().createSQLQuery(sqlQuery.toString());
		hqlQuery.setParameter("scientificExperimentId", scientificExperimentId);
		hqlQuery.setParameter("userId", userId);
		hqlQuery.setParameter("limit", paginationParameterConfig.getMaxFilterResults());
		
		return MapUtils.mapListMapToStringList(hqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list());
	}
	
	@SuppressWarnings("unchecked")
	public List<Workflow> findAllPaginated(WorkflowFormFilter workflowFormFilter) {
		log.info("Processando busca pagina de workflows por filtro");
		Criteria criteria = session().createCriteria(Workflow.class, "workflow");
		
		if (workflowFormFilter.getScientificProjectId() != null) {
			criteria.createAlias("workflow.scientificProject", "scientificProject");
			criteria.add(Restrictions.eq("scientificProject.scientificProjectId", workflowFormFilter.getScientificProjectId()));
		}
		
		if (workflowFormFilter.getScientificExperimentId() != null) {
			criteria.createAlias("workflow.scientificExperiment", "scientificExperiment");
			criteria.add(Restrictions.eq("scientificExperiment.scientificExperimentId", workflowFormFilter.getScientificExperimentId()));
		}
		
		if (!"".equals(workflowFormFilter.getResponsibleGroupName()) && workflowFormFilter.getResponsibleGroupName() != null) {
			criteria.createAlias("workflow.responsibleGroup", "responsibleGroup");
			criteria.add(Restrictions.eq("responsibleGroup.groupName", workflowFormFilter.getResponsibleGroupName().toLowerCase()).ignoreCase());
		}
		
		if (workflowFormFilter.getMyOwn() != null && Boolean.TRUE.equals(workflowFormFilter.getMyOwn())) {
			if ("".equals(workflowFormFilter.getResponsibleGroupName()) || workflowFormFilter.getResponsibleGroupName() == null) {
				criteria.createAlias("workflow.responsibleGroup", "responsibleGroup");
			}
			
			criteria.createAlias("responsibleGroup.groupUsers", "user");
			criteria.add(Restrictions.eq("user.userId", workflowFormFilter.getUserId()));
		}
		
		if (!"".equals(workflowFormFilter.getQueryString()) && workflowFormFilter.getQueryString() != null) {
			criteria.add(Restrictions.ilike("workflowName", "%" + workflowFormFilter.getQueryString() + "%"));
		}
		
		if (PaginationParameterConfig.QueryOrder.asc.name().equals(paginationParameterConfig.getOrder())) {
			criteria.addOrder(Property.forName("workflowName").asc());
		}
		
		if (PaginationParameterConfig.QueryOrder.desc.name().equals(paginationParameterConfig.getOrder())) {
			criteria.addOrder(Property.forName("workflowName").desc());
		}
		
		criteria.setFirstResult(paginationParameterConfig.calculateActualPageFirstResultIndex(workflowFormFilter.getPageNumber()));
		criteria.setMaxResults(paginationParameterConfig.getMaxResultsPerPage());
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Workflow> findAllOfUser(Long userId) {
		log.info("Processando busca pagina de workflows do dashboard do usuario por filtro");
		Criteria criteria = session().createCriteria(Workflow.class, "workflow");
		
		List<String> relevantWorkflowNames = findRelevantWorkflowNames(userId);
		
		if (relevantWorkflowNames != null && !relevantWorkflowNames.isEmpty()) {
			criteria.add(Restrictions.in("workflowName", relevantWorkflowNames));
		}
		
		criteria.setFirstResult(paginationParameterConfig.calculateActualPageFirstResultIndex(1));
		criteria.setMaxResults(paginationParameterConfig.getMaxResultsPerPage());
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	@SuppressWarnings("unchecked")
	private List<String> findRelevantWorkflowNames(Long userId) {
		log.info("Processando busca de workflows relevantes do userId {}", userId);
		
		StringBuilder sqlQuery = new StringBuilder("SELECT DISTINCT nome_workflow FROM ")
										   .append("((SELECT 1, workflow.nm_nome_workflow AS \"nome_workflow\", ")
										   .append("COUNT(tarefa.id_tarefa) AS \"total_tarefas\" ")
										   .append("FROM workflow ")
										   .append("LEFT OUTER JOIN tarefa ")
										   .append("ON tarefa.id_workflow = workflow.id_workflow ")
										   .append("LEFT OUTER JOIN usuario_tarefa ")
										   .append("ON tarefa.id_tarefa = usuario_tarefa.id_tarefa ")
										   .append("LEFT OUTER JOIN usuario ")
										   .append("ON usuario_tarefa.id_usuario = usuario.id_usuario ")
										   .append("WHERE usuario.id_usuario = :userId ")
										   .append("OR tarefa.id_tarefa IN ")
										   .append("(SELECT tarefa.id_tarefa FROM tarefa ")
										   .append("LEFT OUTER JOIN grupo ")
										   .append("ON tarefa.id_grupo = grupo.id_grupo ")
										   .append("LEFT OUTER JOIN grupo_usuario ")
										   .append("ON grupo.id_grupo = grupo_usuario.id_grupo ")
										   .append("LEFT OUTER JOIN usuario ")
										   .append("ON grupo_usuario.id_usuario = usuario.id_usuario ")
										   .append("WHERE usuario.id_usuario = :userId) ")
										   .append("GROUP BY nome_workflow ")
										   .append("ORDER BY total_tarefas DESC ")
										   .append("LIMIT :limit) ")
										   .append("UNION ")
										   .append("(SELECT 2, workflow.nm_nome_workflow AS \"nome_workflow\", 0 AS \"total_tarefas\" ")
										   .append("FROM workflow ")
										   .append("LEFT OUTER JOIN grupo ")
										   .append("ON workflow.id_grupo = grupo.id_grupo ")
										   .append("LEFT OUTER JOIN grupo_usuario ")
										   .append("ON grupo.id_grupo = grupo_usuario.id_grupo ")
										   .append("LEFT OUTER JOIN usuario ")
										   .append("ON grupo_usuario.id_usuario = usuario.id_usuario ")
										   .append("WHERE usuario.id_usuario = :userId ")
										   .append("LIMIT :limit)) AS \"workflows\" ")
										   .append("ORDER BY 1 ")
										   .append("LIMIT :limit");
		
		Query hqlQuery = session().createSQLQuery(sqlQuery.toString());
		hqlQuery.setParameter("userId", userId);
		hqlQuery.setParameter("limit", paginationParameterConfig.getMaxResultsPerPage());
		
		return MapUtils.mapListMapToStringList(hqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list());
	}
	
	public Integer countAllOfUser(Long userId) {
		log.info("Processando busca contagem de workflows por filtro");
		Criteria criteria = session().createCriteria(Workflow.class, "workflow");
		
		if (userId != null) {
			criteria.createAlias("workflow.responsibleGroup", "responsibleGroup");
			criteria.createAlias("responsibleGroup.groupUsers", "user");
			criteria.add(Restrictions.eq("user.userId", userId));
		}

		criteria.setProjection(Projections.countDistinct("workflow.workflowId"));
	    
	    return ((Long) criteria.uniqueResult()).intValue();
	}
	
	public Integer countAllOfUserGroup(Long userGroupId) {
		log.info("Processando busca contagem de workflows por filtro");
		Criteria criteria = session().createCriteria(Workflow.class, "workflow");
		
		if (userGroupId != null) {
			criteria.createAlias("workflow.responsibleGroup", "responsibleGroup");
			criteria.add(Restrictions.eq("responsibleGroup.userGroupId", userGroupId));
		}

		criteria.setProjection(Projections.countDistinct("workflow.workflowId"));
	    
	    return ((Long) criteria.uniqueResult()).intValue();
	}
	
	public Integer countAllOfScientificExperiment(Long scientificExperimentId) {
		log.info("Processando busca contagem de workflows por filtro");
		Criteria criteria = session().createCriteria(Workflow.class, "workflow");
		
		if (scientificExperimentId != null) {
			criteria.createAlias("workflow.scientificExperiment", "scientificExperiment");
			criteria.add(Restrictions.eq("scientificExperiment.scientificExperimentId", scientificExperimentId));
		}

		criteria.setProjection(Projections.countDistinct("workflow.workflowId"));
	    
	    return ((Long) criteria.uniqueResult()).intValue();
	}
	
	public Workflow findByWorkflowName(String workflowName) {
		log.info("Processando busca pagina de workflows por workflowName {}", workflowName);
		List<Criterion> restrictions = new ArrayList<Criterion>();
		
		if (!"".equals(workflowName) && workflowName != null) {
			restrictions.add(Restrictions.eq("workflowName", workflowName.toLowerCase()).ignoreCase());
		}
		
		List<Workflow> queryResultList = findByParams(Workflow.class, restrictions);
		
		if (queryResultList.isEmpty()) {
			return null;
		}
		
		return queryResultList.get(0);
	}
	
	public Integer getAllWorkflowsCount(WorkflowFormFilter workflowFormFilter) throws HibernateException {
		log.info("Processando busca da contagem de workflows por filtro");
		Criteria criteria = session().createCriteria(Workflow.class, "workflow");
		
		if (workflowFormFilter.getScientificProjectId() != null) {
			criteria.createAlias("workflow.scientificProject", "scientificProject");
			criteria.add(Restrictions.eq("scientificProject.scientificProjectId", workflowFormFilter.getScientificProjectId()));
		}
		
		if (workflowFormFilter.getScientificExperimentId() != null) {
			criteria.createAlias("workflow.scientificExperiment", "scientificExperiment");
			criteria.add(Restrictions.eq("scientificExperiment.scientificExperimentId", workflowFormFilter.getScientificExperimentId()));
		}
		
		if (!"".equals(workflowFormFilter.getResponsibleGroupName()) && workflowFormFilter.getResponsibleGroupName() != null) {
			criteria.createAlias("workflow.responsibleGroup", "responsibleGroup");
			criteria.add(Restrictions.eq("responsibleGroup.groupName", workflowFormFilter.getResponsibleGroupName().toLowerCase()).ignoreCase());
		}
		
		if (workflowFormFilter.getMyOwn() != null && Boolean.TRUE.equals(workflowFormFilter.getMyOwn())) {
			if ("".equals(workflowFormFilter.getResponsibleGroupName()) || workflowFormFilter.getResponsibleGroupName() == null) {
				criteria.createAlias("workflow.responsibleGroup", "responsibleGroup");
			}
			
			criteria.createAlias("responsibleGroup.groupUsers", "user");
			criteria.add(Restrictions.eq("user.userId", workflowFormFilter.getUserId()));
		}
		
		if (!"".equals(workflowFormFilter.getQueryString()) && workflowFormFilter.getQueryString() != null) {
			criteria.add(Restrictions.ilike("workflowName", "%" + workflowFormFilter.getQueryString() + "%"));
		}
		
		return ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	public void deleteByScientificProjectId(Long scientificProjectId) {
		log.info("Processando remoção de workflows por filtro");
		
		Query childrenGraphQuery = session().createSQLQuery("DELETE FROM GRAFO_WORKFLOW WHERE ID_WORKFLOW IN (SELECT ID_WORKFLOW FROM WORKFLOW WHERE ID_PROJETO_CIENTIFICO = :scientificProjectId)");
		childrenGraphQuery.setParameter("scientificProjectId", scientificProjectId);
		childrenGraphQuery.executeUpdate();
		
		Query childrenDocumentationQuery = session().createSQLQuery("DELETE FROM DOCUMENTACAO WHERE ID_WORKFLOW IN (SELECT ID_WORKFLOW FROM WORKFLOW WHERE ID_PROJETO_CIENTIFICO = :scientificProjectId)");
		childrenDocumentationQuery.setParameter("scientificProjectId", scientificProjectId);
		childrenDocumentationQuery.executeUpdate();
		
		Query query = session().createSQLQuery("DELETE FROM WORKFLOW WHERE ID_PROJETO_CIENTIFICO = :scientificProjectId");
		query.setParameter("scientificProjectId", scientificProjectId);
		query.executeUpdate();
		
		session().flush();
	}
	
	@SuppressWarnings("unchecked")
	public List<Workflow> findAllByQueryString(String queryString, Long userId) {
		log.info("Processando busca por todos os workflows que contenham o texto {} no workflowName", queryString);
		Criteria criteria = session().createCriteria(Workflow.class, "workflow");
		
		if (userId != null) {
			criteria.createAlias("workflow.responsibleGroup", "responsibleGroup");
			criteria.createAlias("responsibleGroup.groupUsers", "user");
			criteria.add(Restrictions.eq("user.userId", userId));
		}
		
		if (!"".equals(queryString) && queryString != null) {
			criteria.add(Restrictions.ilike("workflowName", "%" + queryString + "%"));
		}
		
		criteria.setMaxResults(paginationParameterConfig.getMaxFilterResults());
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	public void deleteByScientificExperimentId(Long scientificExperimentId) {
		log.info("Processando remoção de workflows por filtro");
		
		Query childrenGraphQuery = session().createSQLQuery("DELETE FROM GRAFO_WORKFLOW WHERE ID_WORKFLOW IN (SELECT ID_WORKFLOW FROM WORKFLOW WHERE ID_EXPERIMENTO_CIENTIFICO = :scientificExperimentId)");
		childrenGraphQuery.setParameter("scientificExperimentId", scientificExperimentId);
		childrenGraphQuery.executeUpdate();
		
		Query childrenDocumentationQuery = session().createSQLQuery("DELETE FROM DOCUMENTACAO WHERE ID_WORKFLOW IN (SELECT ID_WORKFLOW FROM WORKFLOW WHERE ID_EXPERIMENTO_CIENTIFICO = :scientificExperimentId)");
		childrenDocumentationQuery.setParameter("scientificExperimentId", scientificExperimentId);
		childrenDocumentationQuery.executeUpdate();
		
		Query query = session().createSQLQuery("DELETE FROM WORKFLOW WHERE ID_EXPERIMENTO_CIENTIFICO = :scientificExperimentId");
		query.setParameter("scientificExperimentId", scientificExperimentId);
		query.executeUpdate();
		
		session().flush();
	}

}