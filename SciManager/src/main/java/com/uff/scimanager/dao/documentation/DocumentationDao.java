package com.uff.scimanager.dao.documentation;

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
import com.uff.scimanager.domain.Documentation;

@Repository
@Transactional
public class DocumentationDao extends GenericDao<Documentation> {
	
	private static final Logger log = LoggerFactory.getLogger(DocumentationDao.class);
	
	@Autowired
	public DocumentationDao(PaginationParameterConfig paginationParameterConfig, 
						    @Qualifier("sciManager") SessionFactory sessionFactory) {
		
		super(paginationParameterConfig, sessionFactory);
	}
	
	public void delete(Documentation entity) {
		log.info("Deletando documentação");
		session().delete(session().merge(entity));
		session().flush();
	}

	public Documentation saveOrUpdateDocumentation(Documentation entity) {
		log.info("Salvando/atualizando documentação");
		return super.saveOrUpdate(entity);
	}

	public Documentation getDocumentationByWorkflowId(Long workflowId) {
		log.info("Processando busca por documentação de workflow por filtro");
		Criteria criteria = session().createCriteria(Documentation.class, "documentation");
		
		criteria.createAlias("documentation.workflow", "workflow");
		criteria.add(Restrictions.eq("workflow.workflowId", workflowId));
		
		return (Documentation) criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).uniqueResult();
	}
	
	public Documentation getDocumentationByScientificProjectId(Long scientificProjectId) {
		log.info("Processando busca por documentação de projeto científico por filtro");
		Criteria criteria = session().createCriteria(Documentation.class, "documentation");
		
		criteria.createAlias("documentation.scientificProject", "scientificProject");
		criteria.add(Restrictions.eq("scientificProject.scientificProjectId", scientificProjectId));
		
		return (Documentation) criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).uniqueResult();
	}
	
	public Documentation getDocumentationByScientificExperimentId(Long scientificExperimentId) {
		log.info("Processando busca por documentação de experimento científico por filtro");
		Criteria criteria = session().createCriteria(Documentation.class, "documentation");
		
		criteria.createAlias("documentation.scientificExperiment", "scientificExperiment");
		criteria.add(Restrictions.eq("scientificExperiment.scientificExperimentId", scientificExperimentId));
		
		return (Documentation) criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).uniqueResult();
	}
	
}