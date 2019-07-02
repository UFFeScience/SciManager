package com.uff.scimanager.dao.workflow;

import org.hibernate.Criteria;
import org.hibernate.Query;
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
import com.uff.scimanager.domain.WorkflowGraph;

@Repository
@Transactional
public class WorkflowGraphDao extends GenericDao<WorkflowGraph> {
	
	private static final Logger log = LoggerFactory.getLogger(WorkflowGraphDao.class);
	
	@Autowired
	public WorkflowGraphDao(PaginationParameterConfig paginationParameterConfig, 
							@Qualifier("sciManager") SessionFactory sessionFactory) {
		
		super(paginationParameterConfig, sessionFactory);
	}

	public WorkflowGraph saveOrUpdateWorkflowGraph(WorkflowGraph entity) {
		log.info("Salvando/atualizando grafo de workflow");
		return super.saveOrUpdate(entity);
	}
	
	public WorkflowGraph getMacroGraphByWorkflowId(Long workflowId) {
		log.info("Processando busca por grafo de workflow por filtro");
		Criteria criteria = session().createCriteria(WorkflowGraph.class, "workflowGraph");
		
		criteria.createAlias("workflowGraph.workflow", "workflow");
		criteria.add(Restrictions.eq("workflow.workflowId", workflowId));
		criteria.add(Restrictions.eq("workflowGraph.isDetailed", Boolean.FALSE));
		
		return (WorkflowGraph) criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).uniqueResult();
	}
	
	public WorkflowGraph getDetailedGraphByWorkflowId(Long workflowId) {
		log.info("Processando busca por grafo de workflow por filtro");
		Criteria criteria = session().createCriteria(WorkflowGraph.class, "workflowGraph");
		
		criteria.createAlias("workflowGraph.workflow", "workflow");
		criteria.add(Restrictions.eq("workflow.workflowId", workflowId));
		criteria.add(Restrictions.eq("workflowGraph.isDetailed", Boolean.TRUE));
		
		return (WorkflowGraph) criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).uniqueResult();
	}
	
	public void deleteByWorkflowId(Long workflowId) {
		log.info("Processando remoção de tarefas por filtro");
		
		Query query = session().createSQLQuery("DELETE FROM GRAFO_WORKFLOW WHERE ID_WORKFLOW = :workflowId");
		query.setParameter("workflowId", workflowId);
		query.executeUpdate();
		
		session().flush();
	}
	
}