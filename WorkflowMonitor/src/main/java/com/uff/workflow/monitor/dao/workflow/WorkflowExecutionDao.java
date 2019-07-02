package com.uff.workflow.monitor.dao.workflow;

import java.util.List;

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

import com.uff.workflow.monitor.dao.GenericDao;
import com.uff.workflow.monitor.domain.WorkflowExecution;
import com.uff.workflow.monitor.domain.WorkflowExecutionStatus;

@Repository
@Transactional
public class WorkflowExecutionDao extends GenericDao<WorkflowExecution> {
	
	private static final Logger log = LoggerFactory.getLogger(WorkflowExecutionDao.class);
	
	@Autowired
	public WorkflowExecutionDao(@Qualifier("sciManager") SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public WorkflowExecution saveOrUpdateWorkflowExecution(WorkflowExecution entity) {
		log.info("Salvando/atualizando workflowExecution");
		return super.saveOrUpdate(entity);
	}
	
	@SuppressWarnings("unchecked")
	public List<WorkflowExecution> findAllRunningExecutions() {
		log.info("Processando busca pagina de workflow Executions com status {}", WorkflowExecutionStatus.RUNNING.name());
		Criteria criteria = session().createCriteria(WorkflowExecution.class, "workflowExecution");
		
		criteria.add(Restrictions.eq("workflowExecution.executionStatus", WorkflowExecutionStatus.RUNNING));
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	public Integer updateExecutionsByExectag(List<String> executionsExecTagToUpdate, WorkflowExecutionStatus status) {
		log.info("Iniciando atualização de execução pelos exectags {} e status {}", executionsExecTagToUpdate, status.name());

		StringBuilder sqlQuery = new StringBuilder("UPDATE EXECUCAO_WORKFLOW SET NM_STATUS = :status ")
										   .append("WHERE NM_EXECTAG IN :execTags");

		Query hqlQuery = session().createSQLQuery(sqlQuery.toString());
		hqlQuery.setParameterList("execTags", executionsExecTagToUpdate);
		hqlQuery.setParameter("status", status);
		
		return hqlQuery.executeUpdate();
	}

}