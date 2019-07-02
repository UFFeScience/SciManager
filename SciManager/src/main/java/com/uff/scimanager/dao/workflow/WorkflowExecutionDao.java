package com.uff.scimanager.dao.workflow;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uff.scimanager.component.PaginationParameterConfig;
import com.uff.scimanager.dao.GenericDao;
import com.uff.scimanager.domain.WorkflowExecution;
import com.uff.scimanager.domain.WorkflowExecutionStatus;
import com.uff.scimanager.domain.form.WorkflowExecutionFormFilter;
import com.uff.scimanager.util.CalendarDateUtils;

@Repository
@Transactional
public class WorkflowExecutionDao extends GenericDao<WorkflowExecution> {
	
	private static final Logger log = LoggerFactory.getLogger(WorkflowExecutionDao.class);
	
	@Autowired
	public WorkflowExecutionDao(PaginationParameterConfig paginationParameterConfig, 
					   			@Qualifier("sciManager") SessionFactory sessionFactory) {
		
		super(paginationParameterConfig, sessionFactory);
	}

	public WorkflowExecution saveOrUpdateWorkflowExecution(WorkflowExecution entity) {
		log.info("Salvando/atualizando workflowExecution");
		return super.saveOrUpdate(entity);
	}

	public void delete(WorkflowExecution entity) {
		log.info("Deletando workflowExecution do banco");
		super.delete(entity);
	}

	public WorkflowExecution findById(Long id) {
		log.info("Buscando workflowExecution por id {}", id);
		return super.findById(WorkflowExecution.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<WorkflowExecution> findAllrelevantPaginated(WorkflowExecutionFormFilter workflowExecutionFormFilter) {
		
		log.info("Processando busca pagina de workflowExecutions por filtro");
		Criteria criteria = session().createCriteria(WorkflowExecution.class, "workflowExecution");
		
		if (workflowExecutionFormFilter.getWorkflowId() != null) {
			criteria.createAlias("workflowExecution.workflow", "workflow");
			criteria.add(Restrictions.eq("workflow.workflowId", workflowExecutionFormFilter.getWorkflowId()));
		}
		
		if (workflowExecutionFormFilter.getUserId() != null) {
			criteria.createAlias("workflowExecution.workflow", "workflow");
			criteria.createAlias("workflow.responsibleGroup", "responsibleGroup");
			criteria.createAlias("responsibleGroup.groupUsers", "user");
			criteria.add(Restrictions.eq("user.userId", workflowExecutionFormFilter.getUserId()));
		}
		
		criteria.addOrder(Order.desc("executionDate"));
		
		criteria.setFirstResult(paginationParameterConfig.calculateActualPageFirstResultIndex(1));
		criteria.setMaxResults(paginationParameterConfig.getMaxFilterResults());
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<WorkflowExecution> findAllPaginated(WorkflowExecutionFormFilter workflowExecutionFormFilter) {
		
		log.info("Processando busca pagina de workflowExecutions por filtro");
		Criteria criteria = session().createCriteria(WorkflowExecution.class, "workflowExecution");
		
		if (workflowExecutionFormFilter.getWorkflowId() != null || (workflowExecutionFormFilter.getWorkflowName() != null && 
				!"".equals(workflowExecutionFormFilter.getWorkflowName()))) {
			
			criteria.createAlias("workflowExecution.workflow", "workflow");
			
			if (workflowExecutionFormFilter.getWorkflowId() != null) {
				criteria.add(Restrictions.eq("workflow.workflowId", workflowExecutionFormFilter.getWorkflowId()));
			} 
			else {
				criteria.add(Restrictions.eq("workflow.workflowName", workflowExecutionFormFilter.getWorkflowName().toLowerCase()).ignoreCase());
			}
			
			if (workflowExecutionFormFilter.getQueryString() != null && !"".equals(workflowExecutionFormFilter.getQueryString())) {
				criteria.createAlias("workflowExecution.userAgent", "userAgent");
				criteria.add(Restrictions.disjunction().add(Restrictions.ilike("workflowExecution.execTag", "%" + workflowExecutionFormFilter.getQueryString() + "%"))
												       .add(Restrictions.ilike("workflowExecution.swfms", "%" + workflowExecutionFormFilter.getQueryString() + "%"))
												       .add(Restrictions.ilike("userAgent.username", "%" + workflowExecutionFormFilter.getQueryString() + "%"))
												       .add(Restrictions.ilike("userAgent.email", "%" + workflowExecutionFormFilter.getQueryString() + "%")));
			}
		}
		else if (workflowExecutionFormFilter.getQueryString() != null && !"".equals(workflowExecutionFormFilter.getQueryString())) {
			criteria.createAlias("workflowExecution.workflow", "workflow");
			criteria.createAlias("workflowExecution.userAgent", "userAgent");
			criteria.add(Restrictions.disjunction().add(Restrictions.ilike("workflowExecution.execTag", "%" + workflowExecutionFormFilter.getQueryString() + "%"))
											       .add(Restrictions.ilike("workflowExecution.swfms", "%" + workflowExecutionFormFilter.getQueryString() + "%"))
											       .add(Restrictions.ilike("workflow.workflowName", "%" + workflowExecutionFormFilter.getQueryString() + "%"))
											       .add(Restrictions.ilike("userAgent.username", "%" + workflowExecutionFormFilter.getQueryString() + "%"))
											       .add(Restrictions.ilike("userAgent.email", "%" + workflowExecutionFormFilter.getQueryString() + "%")));
		}
		
		if (workflowExecutionFormFilter.getExecutionStatus() != null && !"".equals(workflowExecutionFormFilter.getExecutionStatus())) {
			criteria.add(Restrictions.eq("workflowExecution.executionStatus",
									     WorkflowExecutionStatus.getExecutionStatusFromString(workflowExecutionFormFilter.getExecutionStatus())));
		}
		
		if (workflowExecutionFormFilter.getInitialDate() != null && !"".equals(workflowExecutionFormFilter.getInitialDate())) {
			criteria.add(Restrictions.ge("workflowExecution.executionDate", CalendarDateUtils.createCalendarFromString(workflowExecutionFormFilter.getInitialDate())));
		}
		
		if (workflowExecutionFormFilter.getFinalDate() != null && !"".equals(workflowExecutionFormFilter.getFinalDate())) {
			criteria.add(Restrictions.le("workflowExecution.executionDate", CalendarDateUtils.createCalendarFromString(workflowExecutionFormFilter.getFinalDate())));
		}
		
		if (workflowExecutionFormFilter.getMyOwn() != null && Boolean.TRUE.equals(workflowExecutionFormFilter.getMyOwn())) {
			criteria.add(Restrictions.eq("workflowExecution.userAgent.userId", workflowExecutionFormFilter.getUserId()));
		}
		
		if (workflowExecutionFormFilter.getUserId() != null) {
			if ((workflowExecutionFormFilter.getQueryString() == null || "".equals(workflowExecutionFormFilter.getQueryString())) && 
				(workflowExecutionFormFilter.getWorkflowId() == null && (workflowExecutionFormFilter.getWorkflowName() == null ||
				"".equals(workflowExecutionFormFilter.getWorkflowName())))) {
				
				criteria.createAlias("workflowExecution.workflow", "workflow");
			}
			
			criteria.createAlias("workflow.responsibleGroup", "responsibleGroup");
			criteria.createAlias("responsibleGroup.groupUsers", "user");
			criteria.add(Restrictions.eq("user.userId", workflowExecutionFormFilter.getUserId()));
		}
		
		criteria.addOrder(Order.desc("executionDate"));
		
		criteria.setFirstResult(paginationParameterConfig.calculateActualPageFirstResultIndex(workflowExecutionFormFilter.getPageNumber()));
		criteria.setMaxResults(paginationParameterConfig.getMaxResultsPerPage());
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<WorkflowExecution> findAllRelevantPaginated(WorkflowExecutionFormFilter workflowExecutionFormFilter) {
		
		log.info("Processando busca pagina de workflowExecutions por filtro");
		Criteria criteria = session().createCriteria(WorkflowExecution.class, "workflowExecution");
		
		if (workflowExecutionFormFilter.getWorkflowId() != null) {
			criteria.createAlias("workflowExecution.workflow", "workflow");
			criteria.add(Restrictions.eq("workflow.workflowId", workflowExecutionFormFilter.getWorkflowId()));
		}
		
		if (workflowExecutionFormFilter.getMyOwn() != null && Boolean.TRUE.equals(workflowExecutionFormFilter.getMyOwn())) {
			criteria.add(Restrictions.eq("workflowExecution.userAgent.userId", workflowExecutionFormFilter.getUserId()));
		}
		
		if (workflowExecutionFormFilter.getUserId() != null) {
			if ((workflowExecutionFormFilter.getQueryString() == null || "".equals(workflowExecutionFormFilter.getQueryString())) && 
				(workflowExecutionFormFilter.getWorkflowId() == null)) {
				
				criteria.createAlias("workflowExecution.workflow", "workflow");
			}
			
			criteria.createAlias("workflow.responsibleGroup", "responsibleGroup");
			criteria.createAlias("responsibleGroup.groupUsers", "user");
			criteria.add(Restrictions.eq("user.userId", workflowExecutionFormFilter.getUserId()));
		}
		
		criteria.addOrder(Order.desc("executionDate"));
		
		criteria.setFirstResult(paginationParameterConfig.calculateActualPageFirstResultIndex(workflowExecutionFormFilter.getPageNumber()));
		criteria.setMaxResults(paginationParameterConfig.getMaxFilterResults());
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	public Integer getAllWorkflowExecutionsCount(String execTag) {
		WorkflowExecutionFormFilter workflowExecutionFormFilter = WorkflowExecutionFormFilter.builder().execTag(execTag).build();
		return getAllWorkflowExecutionsCount(workflowExecutionFormFilter);
	}
	
	public Integer getAllWorkflowExecutionsCount(WorkflowExecutionFormFilter workflowExecutionFormFilter) {

		log.info("Processando busca da contagem de workflowExecutions por filtro");
		Criteria criteria = session().createCriteria(WorkflowExecution.class, "workflowExecution");
		
		if (workflowExecutionFormFilter.getWorkflowId() != null || (workflowExecutionFormFilter.getWorkflowName() != null && 
				!"".equals(workflowExecutionFormFilter.getWorkflowName()))) {
			
			criteria.createAlias("workflowExecution.workflow", "workflow");
			
			if (workflowExecutionFormFilter.getWorkflowId() != null) {
				criteria.add(Restrictions.eq("workflow.workflowId", workflowExecutionFormFilter.getWorkflowId()));
			} 
			else {
				criteria.add(Restrictions.eq("workflow.workflowName", workflowExecutionFormFilter.getWorkflowName().toLowerCase()).ignoreCase());
			}
			
			if (workflowExecutionFormFilter.getQueryString() != null && !"".equals(workflowExecutionFormFilter.getQueryString())) {
				criteria.createAlias("workflowExecution.userAgent", "userAgent");
				criteria.add(Restrictions.disjunction().add(Restrictions.ilike("workflowExecution.execTag", "%" + workflowExecutionFormFilter.getQueryString() + "%"))
												       .add(Restrictions.ilike("workflowExecution.swfms", "%" + workflowExecutionFormFilter.getQueryString() + "%"))
												       .add(Restrictions.ilike("userAgent.username", "%" + workflowExecutionFormFilter.getQueryString() + "%"))
												       .add(Restrictions.ilike("userAgent.email", "%" + workflowExecutionFormFilter.getQueryString() + "%")));
			}
		}
		else if (workflowExecutionFormFilter.getQueryString() != null && !"".equals(workflowExecutionFormFilter.getQueryString())) {
			criteria.createAlias("workflowExecution.workflow", "workflow");
			criteria.createAlias("workflowExecution.userAgent", "userAgent");
			criteria.add(Restrictions.disjunction().add(Restrictions.ilike("workflowExecution.execTag", "%" + workflowExecutionFormFilter.getQueryString() + "%"))
											       .add(Restrictions.ilike("workflowExecution.swfms", "%" + workflowExecutionFormFilter.getQueryString() + "%"))
											       .add(Restrictions.ilike("workflow.workflowName", "%" + workflowExecutionFormFilter.getQueryString() + "%"))
											       .add(Restrictions.ilike("userAgent.username", "%" + workflowExecutionFormFilter.getQueryString() + "%"))
											       .add(Restrictions.ilike("userAgent.email", "%" + workflowExecutionFormFilter.getQueryString() + "%")));
		}
		
		if (workflowExecutionFormFilter.getExecutionStatus() != null && !"".equals(workflowExecutionFormFilter.getExecutionStatus())) {
			criteria.add(Restrictions.eq("workflowExecution.executionStatus",
		    WorkflowExecutionStatus.getExecutionStatusFromString(workflowExecutionFormFilter.getExecutionStatus())));
		}
		
		if (workflowExecutionFormFilter.getInitialDate() != null && !"".equals(workflowExecutionFormFilter.getInitialDate())) {
			criteria.add(Restrictions.ge("workflowExecution.executionDate", CalendarDateUtils.createCalendarFromString(workflowExecutionFormFilter.getInitialDate())));
		}
		
		if (workflowExecutionFormFilter.getFinalDate() != null && !"".equals(workflowExecutionFormFilter.getFinalDate())) {
			criteria.add(Restrictions.le("workflowExecution.executionDate", CalendarDateUtils.createCalendarFromString(workflowExecutionFormFilter.getFinalDate())));
		}
		
		if (workflowExecutionFormFilter.getExecTag() != null && !"".equals(workflowExecutionFormFilter.getExecTag())) {
			criteria.add(Restrictions.eq("workflowExecution.execTag", workflowExecutionFormFilter.getExecTag()));
		}
		
		if (workflowExecutionFormFilter.getUserId() != null) {
			criteria.add(Restrictions.eq("workflowExecution.userAgent.userId", workflowExecutionFormFilter.getUserId()));
		}
		
		if (workflowExecutionFormFilter.getMyOwn() != null && Boolean.TRUE.equals(workflowExecutionFormFilter.getMyOwn())) {
			if ((workflowExecutionFormFilter.getQueryString() == null || "".equals(workflowExecutionFormFilter.getQueryString())) && 
				(workflowExecutionFormFilter.getWorkflowId() == null && (workflowExecutionFormFilter.getWorkflowName() == null ||
				"".equals(workflowExecutionFormFilter.getWorkflowName())))) {
				
				criteria.createAlias("workflowExecution.workflow", "workflow");
			}
			
			criteria.createAlias("workflow.responsibleGroup", "responsibleGroup");
			criteria.createAlias("responsibleGroup.groupUsers", "user");
			criteria.add(Restrictions.eq("user.userId", workflowExecutionFormFilter.getUserId()));
		}
		
		criteria.setProjection(Projections.countDistinct("workflowExecution.workflowExecutionId"));
			
		return ((Long) criteria.uniqueResult()).intValue();
	}

	public Integer countAllOfModelFile(Long modelFileId) {
		log.info("Processando busca contagem de workflowExecutions por filtro");
		Criteria criteria = session().createCriteria(WorkflowExecution.class, "workflowExecution");
		
		if (modelFileId != null) {
			criteria.createAlias("workflowExecution.modelFile", "modelFile");
			criteria.add(Restrictions.eq("modelFile.modelFileId", modelFileId));
		}

		criteria.setProjection(Projections.countDistinct("workflowExecution.workflowExecutionId"));
	    
	    return ((Long) criteria.uniqueResult()).intValue();
	}
	
}