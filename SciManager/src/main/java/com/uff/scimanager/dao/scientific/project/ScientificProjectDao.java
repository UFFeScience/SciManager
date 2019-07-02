package com.uff.scimanager.dao.scientific.project;

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
import com.uff.scimanager.domain.Role;
import com.uff.scimanager.domain.ScientificProject;
import com.uff.scimanager.domain.security.CurrentUser;
import com.uff.scimanager.util.MapUtils;

@Repository
@Transactional
public class ScientificProjectDao extends GenericDao<ScientificProject> {
	
	private static final Logger log = LoggerFactory.getLogger(ScientificProjectDao.class);
	
	@Autowired
	public ScientificProjectDao(PaginationParameterConfig paginationParameterConfig, 
							    @Qualifier("sciManager") SessionFactory sessionFactory) {
		
		super(paginationParameterConfig, sessionFactory);
	}

	public ScientificProject saveOrUpdateScientificProject(ScientificProject entity) {
		log.info("Salvando/atualizando projeto científico");
		return super.saveOrUpdate(entity);
	}

	public void delete(ScientificProject entity) {
		log.info("Deletando projeto científico do banco");
		super.delete(entity);
	}

	public ScientificProject findById(Long id) {
		log.info("Buscando projeto científico por id {}", id);
		return super.findById(ScientificProject.class, id);
	}
	
	public ScientificProject findScientificProjectByProjectName(String projectName) {
		log.info("Processando busca por projeto científico pelo nome {}", projectName);
		List<Criterion> restrictions = new ArrayList<Criterion>();
		restrictions.add(Restrictions.like("projectName", projectName));
		
		List<ScientificProject> queryResultList =  findByParams(ScientificProject.class, restrictions);
		
		if (queryResultList.isEmpty()) {
			return null;
		}
		
		return queryResultList.get(0);
	}
	
	public ScientificProject findScientificProjectBySlug(String slug) {
		log.info("Processando busca por projeto científico pelo slug {}", slug);
		List<Criterion> restrictions = new ArrayList<Criterion>();
		restrictions.add(Restrictions.like("slug", slug));
		
		List<ScientificProject> queryResultList =  findByParams(ScientificProject.class, restrictions);
		
		if (queryResultList.isEmpty()) {
			return null;
		}
		
		return queryResultList.get(0);
	}
	
	@SuppressWarnings("unchecked")
	public List<ScientificProject> findAllPaginated(Integer pageNumber, String queryString, Long userId) {
		log.info("Processando busca pagina de projetos científicos por filtro");
		Criteria criteria = session().createCriteria(ScientificProject.class, "scientificProject");
		
		if (userId != null) {
			criteria.createAlias("scientificProject.workflows", "workflow");
			criteria.createAlias("workflow.responsibleGroup", "responsibleGroup");
			criteria.createAlias("responsibleGroup.groupUsers", "user");
			criteria.add(Restrictions.eq("user.userId", userId));
		}
		
		if (!"".equals(queryString) && queryString != null) {
			criteria.add(Restrictions.ilike("projectName", "%" + queryString + "%"));
		}
		
		if (PaginationParameterConfig.QueryOrder.asc.name().equals(paginationParameterConfig.getOrder())) {
			criteria.addOrder(Property.forName("projectName").asc());
		}
		
		if (PaginationParameterConfig.QueryOrder.desc.name().equals(paginationParameterConfig.getOrder())) {
			criteria.addOrder(Property.forName("projectName").desc());
		}
		
		criteria.setFirstResult(paginationParameterConfig.calculateActualPageFirstResultIndex(pageNumber));
		criteria.setMaxResults(paginationParameterConfig.getMaxResultsPerPage());
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<ScientificProject> findAllBoardsPaginated(Integer pageNumber, String queryString, Long userId) {
		log.info("Processando busca pagina de projetos científicos por filtro");
		Criteria criteria = session().createCriteria(ScientificProject.class, "scientificProject");
		
		if (userId != null) {
			criteria.createAlias("scientificProject.workflows", "workflow");
			criteria.createAlias("workflow.responsibleGroup", "responsibleGroup");
			criteria.createAlias("responsibleGroup.groupUsers", "user");
			criteria.add(Restrictions.eq("user.userId", userId));
		}
		
		if (!"".equals(queryString) && queryString != null) {
			criteria.add(Restrictions.ilike("projectName", "%" + queryString + "%"));
		}
		
		if (PaginationParameterConfig.QueryOrder.asc.name().equals(paginationParameterConfig.getOrder())) {
			criteria.addOrder(Property.forName("projectName").asc());
		}
		
		if (PaginationParameterConfig.QueryOrder.desc.name().equals(paginationParameterConfig.getOrder())) {
			criteria.addOrder(Property.forName("projectName").desc());
		}
		
		criteria.setFirstResult(paginationParameterConfig.calculateActualPageFirstResultIndex(pageNumber));
		criteria.setMaxResults(paginationParameterConfig.getMaxResultsPerPage());
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<ScientificProject> findAllRelevantPaginated(CurrentUser currentUser) {
		log.info("Processando busca de projetos científicos relevantes por filtro");
		Criteria criteria = session().createCriteria(ScientificProject.class, "scientificProject");
		
		List<String> relevantProjectNames = findRelevantProjectsNames(currentUser);
		
		if (relevantProjectNames != null && !relevantProjectNames.isEmpty()) {
			criteria.add(Restrictions.in("projectName", relevantProjectNames));
		}
		
		criteria.setFirstResult(paginationParameterConfig.calculateActualPageFirstResultIndex(1));
		criteria.setMaxResults(paginationParameterConfig.getMaxResultsPerPage());
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	@SuppressWarnings("unchecked")
	private List<String> findRelevantProjectsNames(CurrentUser currentUser) {
		log.info("Processando busca de experimentos relevantes do userId {}", currentUser.getUserId());
		
		StringBuilder sqlQuery = new StringBuilder("SELECT DISTINCT nome_projeto FROM ")
										   .append("((SELECT 1, projeto_cientifico.nm_nome_projeto AS \"nome_projeto\", ")
										   .append("COUNT(tarefa.id_tarefa) AS \"total_tarefas\" ")
										   .append("FROM projeto_cientifico ")
										   .append("LEFT OUTER JOIN tarefa ")
										   .append("ON tarefa.id_projeto_cientifico = projeto_cientifico.id_projeto_cientifico ")
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
										   .append("GROUP BY nome_projeto ")
										   .append("ORDER BY total_tarefas DESC ")
										   .append("LIMIT :limit) ")
										   .append("UNION ");
		
		if (currentUser.getUserRole().equals(Role.ADMIN)) {
			sqlQuery.append("(SELECT 2, projeto_cientifico.nm_nome_projeto AS \"nome_projeto\", 0 AS \"total_tarefas\" ")
					.append("FROM projeto_cientifico ")
					.append("LIMIT :limit)) AS \"projetos\" ")
					.append("ORDER BY 1 ")
					.append("LIMIT :limit");
		}
		else {
			sqlQuery.append("(SELECT 2, projeto_cientifico.nm_nome_projeto AS \"nome_projeto\", 0 AS \"total_tarefas\" ")
					.append("FROM projeto_cientifico ")
					.append("LEFT OUTER JOIN workflow ")
					.append("ON workflow.id_projeto_cientifico = projeto_cientifico.id_projeto_cientifico ")
					.append("LEFT OUTER JOIN grupo ")
					.append("ON workflow.id_grupo = grupo.id_grupo ")
					.append("LEFT OUTER JOIN grupo_usuario ")
					.append("ON grupo.id_grupo = grupo_usuario.id_grupo ")
					.append("LEFT OUTER JOIN usuario ")
					.append("ON grupo_usuario.id_usuario = usuario.id_usuario ")
					.append("WHERE usuario.id_usuario = :userId ")
					.append("LIMIT :limit)) AS \"projetos\" ")
					.append("ORDER BY 1 ")
					.append("LIMIT :limit");
		}
										   
		Query hqlQuery = session().createSQLQuery(sqlQuery.toString());
		hqlQuery.setParameter("userId", currentUser.getUserId());
		hqlQuery.setParameter("limit", paginationParameterConfig.getMaxResultsPerPage());
		
		return MapUtils.mapListMapToStringList(hqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list());
	}
	
	@SuppressWarnings("unchecked")
	public List<ScientificProject> findAllByQueryString(String queryString, Long  userId) {
		log.info("Processando busca por todos os projetos científicos que contenham o texto {} no projectName", queryString);
		Criteria criteria = session().createCriteria(ScientificProject.class, "scientificProject");
		
		if (userId != null) {
			criteria.createAlias("scientificProject.workflows", "workflow");
			criteria.createAlias("workflow.responsibleGroup", "responsibleGroup");
			criteria.createAlias("responsibleGroup.groupUsers", "user");
			criteria.add(Restrictions.eq("user.userId", userId));
		}
		
		if (!"".equals(queryString) && queryString != null) {
			criteria.add(Restrictions.ilike("projectName", "%" + queryString + "%"));
		}
		
		criteria.setMaxResults(paginationParameterConfig.getMaxFilterResults());
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	public Integer getAllScientificProjectsCount(String queryString, Long userId) throws HibernateException {
		log.info("Processando busca da contagem de projetos científicos por filtro");
		Criteria criteria = session().createCriteria(ScientificProject.class, "scientificProject");
		
		if (userId != null) {
			criteria.createAlias("scientificProject.workflows", "workflow");
			criteria.createAlias("workflow.responsibleGroup", "responsibleGroup");
			criteria.createAlias("responsibleGroup.groupUsers", "user");
			criteria.add(Restrictions.eq("user.userId", userId));
		}
		
		if (!"".equals(queryString) && queryString != null) {
			criteria.add(Restrictions.ilike("projectName", "%" + queryString + "%"));
		}
		
		criteria.setProjection(Projections.countDistinct("projectName"));
	    
	    return ((Long) criteria.uniqueResult()).intValue();
	}
	
	public Integer getAllScientificProjectsBoardsCount(String queryString, Long userId) throws HibernateException {
		log.info("Processando busca da contagem de projetos científicos por filtro");
		Criteria criteria = session().createCriteria(ScientificProject.class, "scientificProject");
		
		if (userId != null) {
			criteria.createAlias("scientificProject.workflows", "workflow");
			criteria.createAlias("workflow.responsibleGroup", "responsibleGroup");
			criteria.createAlias("responsibleGroup.groupUsers", "user");
			criteria.add(Restrictions.eq("user.userId", userId));
		}
		
		if (!"".equals(queryString) && queryString != null) {
			criteria.add(Restrictions.ilike("projectName", "%" + queryString + "%"));
		}
		
		criteria.setProjection(Projections.countDistinct("projectName"));
	    
	    return ((Long) criteria.uniqueResult()).intValue();
	}

}