package com.uff.workflow.invoker.dao.workflow;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uff.workflow.invoker.dao.GenericDao;
import com.uff.workflow.invoker.domain.WorkflowExecution;

@Repository
@Transactional
public class WorkflowExecutionDao extends GenericDao<WorkflowExecution> {
	
	private static final Logger log = LoggerFactory.getLogger(WorkflowExecutionDao.class);
	
	@Autowired
	public WorkflowExecutionDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public WorkflowExecution saveOrUpdateWorkflowExecution(WorkflowExecution entity) {
		log.info("Salvando/atualizando workflowExecution");
		return super.saveOrUpdate(entity);
	}

	public WorkflowExecution findById(Long id) {
		log.info("Buscando workflowExecution por id {}", id);
		return super.findById(WorkflowExecution.class, id);
	}
	
}