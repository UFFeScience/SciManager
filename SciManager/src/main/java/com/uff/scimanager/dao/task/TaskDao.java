package com.uff.scimanager.dao.task;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.hibernate.Criteria;
import org.hibernate.NullPrecedence;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uff.scimanager.component.PaginationParameterConfig;
import com.uff.scimanager.dao.GenericDao;
import com.uff.scimanager.domain.Task;
import com.uff.scimanager.domain.TaskStatus;
import com.uff.scimanager.domain.UserGroup;
import com.uff.scimanager.domain.form.TaskFormFilter;
import com.uff.scimanager.util.CalendarDateUtils;

@Repository
@Transactional
public class TaskDao extends GenericDao<Task> {
	
	private static final Logger log = LoggerFactory.getLogger(TaskDao.class);
	
	@Autowired
	public TaskDao(PaginationParameterConfig paginationParameterConfig, 
				   @Qualifier("sciManager") SessionFactory sessionFactory) {
		
		super(paginationParameterConfig, sessionFactory);
	}

	public Task saveOrUpdateTask(Task entity) {
		log.info("Salvando/atualizando tarefa");
		return super.saveOrUpdate(entity);
	}
	
	public Task mergeTask(Task entity) {
		log.info("Salvando/atualizando tarefa");
		return super.merge(entity);
	}

	public void delete(Task entity) {
		log.info("Deletando tarefa do banco");
		super.delete(entity);
	}
	
	public Task findById(Long id) {
		log.info("Buscando tarefa por id {}", id);
		return super.findById(Task.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<Task> getAllTasksWithTag(Long tagId) {
		log.info("Processando busca de tarefas da tag de id {}", tagId);
		Criteria criteria = session().createCriteria(Task.class, "task");
		
		if (tagId != null) {
			criteria.createAlias("task.tags", "tag", JoinType.INNER_JOIN);
			criteria.add(Restrictions.eq("tag.tagId", tagId));
		}
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Task> getAllTasksOfUserOrCreatedByUser(Long userId) {
		log.info("Processando busca pagina de tarefas do usuário de id {}", userId);
		Criteria criteria = session().createCriteria(Task.class, "task");
		
		if (userId != null) {
			criteria.createAlias("task.userGroupInTask", "userGroupInTask", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("userGroupInTask.groupUsers", "userOfGroup", JoinType.LEFT_OUTER_JOIN);
		
			criteria.add(Restrictions.or(Restrictions.eq("task.userCreator.userId", userId),
								     	 Subqueries.propertyIn("task.taskId", createTaskSubQueryForTasksOfUser(userId))));
		}
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Task> getAllTasksOfUserGroup(Long userGroupId) {
		log.info("Processando busca pagina de tarefas do grupo de usuários de id {}", userGroupId);
		Criteria criteria = session().createCriteria(Task.class, "task");
		
		if (userGroupId != null) {
			criteria.createAlias("task.userGroupInTask", "userGroupInTask", JoinType.LEFT_OUTER_JOIN);
			criteria.add(Restrictions.eq("userGroupInTask.userGroupId", userGroupId));
		}
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Task> getAllTasksOpenForUser(Long userId) {
		log.info("Processando busca pagina de tarefas do usuário de id {}", userId);
		Criteria criteria = session().createCriteria(Task.class, "task");
		
		criteria.add(Restrictions.ne("task.status", TaskStatus.DONE));
		
		if (userId != null) {
			criteria.createAlias("task.userGroupInTask", "userGroupInTask", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("userGroupInTask.groupUsers", "userOfGroup", JoinType.LEFT_OUTER_JOIN);
		
			criteria.add(Restrictions.or(Restrictions.eq("userOfGroup.userId", userId),
								     	 Subqueries.propertyIn("task.taskId", createTaskSubQueryForTasksOfUser(userId))));
		}
		
		criteria.setFirstResult(paginationParameterConfig.calculateActualPageFirstResultIndex(1));
		criteria.setMaxResults(paginationParameterConfig.getMaxResultsPerPage());
		
		criteria.addOrder(Order.asc("deadline").nulls(NullPrecedence.LAST));
		criteria.addOrder(Order.desc("creationDate"));
		criteria.addOrder(Order.desc("estimatedTime"));
		criteria.addOrder(Order.asc("taskTitle"));
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	public Integer countTasksByPhaseId(Long phaseId) {
		log.info("Contagem de tarefas da fase de id {}", phaseId);
		Criteria criteria = session().createCriteria(Task.class, "task");
		
		criteria.add(Restrictions.ne("task.status", TaskStatus.DONE));
		
		if (phaseId != null) {
			criteria.createAlias("task.phase", "phase");
			criteria.add(Restrictions.eq("phase.phaseId", phaseId));
		}
		
		criteria.setProjection(Projections.countDistinct("task.taskId"));
	    
	    return ((Long) criteria.uniqueResult()).intValue();
	}
	
	public Integer countTasksByWorkflowId(Long workflowId) {
		log.info("Contagem de tarefas do workflow de id {}", workflowId);
		Criteria criteria = session().createCriteria(Task.class, "task");
		
		criteria.add(Restrictions.ne("task.status", TaskStatus.DONE));
		
		if (workflowId != null) {
			criteria.createAlias("task.workflow", "workflow");
			criteria.add(Restrictions.eq("workflow.workflowId", workflowId));
		}
		
		criteria.setProjection(Projections.countDistinct("task.taskId"));
	    
	    return ((Long) criteria.uniqueResult()).intValue();
	}
	
	public Integer countTasksByWorkflowIdAndGroup(Long workflowId, UserGroup userGroup) {
		log.info("Contagem de tarefas do workflow de id {} associadas ao grupo de id {}", workflowId, userGroup.getUserGroupId());
		Criteria criteria = session().createCriteria(Task.class, "task");
		
		criteria.add(Restrictions.ne("task.status", TaskStatus.DONE));
		
		if (workflowId != null) {
			criteria.createAlias("task.workflow", "workflow");
			criteria.add(Restrictions.eq("workflow.workflowId", workflowId));
		}
		
		if (userGroup != null) {
			criteria.createAlias("task.userGroupInTask", "userGroupInTask", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("userGroupInTask.groupUsers", "userOfGroup", JoinType.LEFT_OUTER_JOIN);
			
			criteria.add(Restrictions.or(Restrictions.eq("userGroupInTask.userGroupId", userGroup.getUserGroupId()),
										 Subqueries.propertyIn("task.taskId", createTaskSubQueryForTasksOfGroup(workflowId, userGroup))));
		}
		
		criteria.setProjection(Projections.countDistinct("task.taskId"));
	    
	    return ((Long) criteria.uniqueResult()).intValue();
	}
	
	private DetachedCriteria createTaskSubQueryForTasksOfGroup(Long workflowId, UserGroup userGroup) {

		DetachedCriteria userSubquery = DetachedCriteria.forClass(Task.class, "taskOfUser");
		
		userSubquery.add(Restrictions.ne("taskOfUser.status", TaskStatus.DONE));
		
		if (workflowId != null) {
			userSubquery.createAlias("taskOfUser.workflow", "workflow");
			userSubquery.add(Restrictions.eq("workflow.workflowId", workflowId));
		}
		
		if (userGroup != null) {
			userSubquery.createAlias("taskOfUser.usersInTask", "user", JoinType.LEFT_OUTER_JOIN);
			userSubquery.add(Restrictions.in("user.userId", userGroup.getGroupUsersIds()));
		}
		
		return userSubquery.setProjection(Projections.property("taskId"));
	}
	
	public Integer countTasksOpenForUserCount(Long userId) {
		log.info("Contagem de tarefas do usuário de id {}", userId);
		Criteria criteria = session().createCriteria(Task.class, "task");
		
		criteria.add(Restrictions.ne("task.status", TaskStatus.DONE));
		
		if (userId != null) {
			criteria.createAlias("task.userGroupInTask", "userGroupInTask", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("userGroupInTask.groupUsers", "userOfGroup", JoinType.LEFT_OUTER_JOIN);
	
			criteria.add(Restrictions.or(Restrictions.eq("userOfGroup.userId", userId),
									     Subqueries.propertyIn("task.taskId", createTaskSubQueryForTasksOfUser(userId))));
		}
		criteria.setProjection(Projections.countDistinct("task.taskId"));
	    
	    return ((Long) criteria.uniqueResult()).intValue();
	}
	
	private DetachedCriteria createTaskSubQueryForTasksOfUser(Long userId) {

		DetachedCriteria userSubquery = DetachedCriteria.forClass(Task.class, "taskOfUser");
		
		userSubquery.add(Restrictions.ne("taskOfUser.status", TaskStatus.DONE));
		
		userSubquery.createAlias("taskOfUser.usersInTask", "userOfTask", JoinType.LEFT_OUTER_JOIN);
		userSubquery.add(Restrictions.eq("userOfTask.userId", userId));
		
		userSubquery.addOrder(Order.asc("deadline").nulls(NullPrecedence.LAST));
		userSubquery.addOrder(Order.desc("creationDate"));
		userSubquery.addOrder(Order.desc("estimatedTime"));
		userSubquery.addOrder(Order.asc("taskTitle"));
		
		return userSubquery.setProjection(Projections.property("taskId"));
	}
	
	@SuppressWarnings("unchecked")
	public List<Task> findAllRelevantPaginated(Long scientificProjectId, TaskFormFilter taskFormFilter) {
		log.info("Processando busca pagina de tarefas relevantes por filtro");
		
		Criteria criteria = session().createCriteria(Task.class, "task");
		criteria.add(Restrictions.ne("task.status", TaskStatus.DONE));

		criteria.createAlias("task.scientificProject", "scientificProject");
		criteria.add(Restrictions.eq("scientificProject.scientificProjectId", scientificProjectId));
		
		processRelevantFilters(scientificProjectId, taskFormFilter, criteria);
		
		criteria.setFirstResult(paginationParameterConfig.calculateActualPageFirstResultIndex(taskFormFilter.getPageNumber()));
		criteria.setMaxResults(paginationParameterConfig.getMaxResultsPerPage());
		
		criteria.addOrder(Order.asc("deadline").nulls(NullPrecedence.LAST));
		criteria.addOrder(Order.desc("creationDate"));
		criteria.addOrder(Order.desc("estimatedTime"));
		criteria.addOrder(Order.asc("taskTitle"));
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	@SuppressWarnings("unchecked")
	public List<Task> findAllPaginated(Long scientificProjectId, TaskFormFilter taskFormFilter) {

		log.info("Processando busca pagina de tarefas por filtro");
		Criteria criteria = session().createCriteria(Task.class, "task");
		
		criteria.createAlias("task.scientificProject", "scientificProject");
		criteria.add(Restrictions.eq("scientificProject.scientificProjectId", scientificProjectId));
		
		processFilters(scientificProjectId, taskFormFilter, criteria);
		
		criteria.setFirstResult(paginationParameterConfig.calculateActualPageFirstResultIndex(taskFormFilter.getPageNumber()));
		criteria.setMaxResults(paginationParameterConfig.getMaxTasksResultsPerPage());
		
		criteria.addOrder(Order.asc("deadline").nulls(NullPrecedence.LAST));
		criteria.addOrder(Order.desc("creationDate"));
		criteria.addOrder(Order.desc("estimatedTime"));
		criteria.addOrder(Order.asc("taskTitle"));
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	public Integer getAllTasksLateCount(Long scientificProjectId, TaskFormFilter taskFormFilter) {
		log.info("Processando busca da contagem de tarefas atrasadas por filtro");
		Criteria criteria = session().createCriteria(Task.class, "task");
		
		criteria.createAlias("task.scientificProject", "scientificProject");
		criteria.add(Restrictions.eq("scientificProject.scientificProjectId", scientificProjectId));
		criteria.add(Restrictions.lt("task.deadline", Calendar.getInstance(TimeZone.getTimeZone(CalendarDateUtils.DATE_LOCALE))));
		
		if (taskFormFilter.getUserId() != null) {
			criteria.createAlias("task.workflow", "workflow", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("workflow.responsibleGroup", "workflowResponsibleGroup", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("workflowResponsibleGroup.groupUsers", "userOfWorkflow", JoinType.LEFT_OUTER_JOIN);
			criteria.add(Restrictions.eq("userOfWorkflow.userId", taskFormFilter.getUserId()));
		}
		
		criteria.setProjection(Projections.countDistinct("task.taskId"));
		
		return ((Long) criteria.uniqueResult()).intValue();
	}
	
	public Integer getAllTasksCount(Long scientificProjectId, TaskFormFilter taskFormFilter) {
		
		log.info("Processando busca da contagem de tarefas por filtro");
		Criteria criteria = session().createCriteria(Task.class, "task");
		
		criteria.createAlias("task.scientificProject", "scientificProject");
		criteria.add(Restrictions.eq("scientificProject.scientificProjectId", scientificProjectId));
		
		processFilters(scientificProjectId, taskFormFilter, criteria);
		
		criteria.setProjection(Projections.countDistinct("task.taskId"));
	    
	    return ((Long) criteria.uniqueResult()).intValue();
	}
	
	private void processRelevantFilters(Long scientificProjectId, TaskFormFilter taskFormFilter, Criteria criteria) {
		
		if (taskFormFilter.getUserId() != null) {
			criteria.createAlias("task.workflow", "workflow", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("workflow.responsibleGroup", "workflowResponsibleGroup", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("workflowResponsibleGroup.groupUsers", "userOfWorkflow", JoinType.LEFT_OUTER_JOIN);
			criteria.add(Restrictions.or(Restrictions.eq("userOfWorkflow.userId", taskFormFilter.getUserId()),
										 Restrictions.isNull("workflow")));
		}
		
		if (taskFormFilter.getMyOwn() != null && Boolean.TRUE.equals(taskFormFilter.getMyOwn())) {
			criteria.createAlias("task.userGroupInTask", "userGroupInTask", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("userGroupInTask.groupUsers", "userOfGroup", JoinType.LEFT_OUTER_JOIN);

			criteria.add(Restrictions.or(Restrictions.eq("userOfGroup.userId", taskFormFilter.getUserId()),
										 Subqueries.propertyIn("task.taskId", createRelevantTaskSubQuery(scientificProjectId, taskFormFilter))));
		}
	}
	
	private DetachedCriteria createRelevantTaskSubQuery(Long scientificProjectId, TaskFormFilter taskFormFilter) {

		DetachedCriteria userSubquery = DetachedCriteria.forClass(Task.class, "taskOfUser");
		
		userSubquery.createAlias("taskOfUser.scientificProject", "scientificProject");
		userSubquery.add(Restrictions.eq("scientificProject.scientificProjectId", scientificProjectId));
		userSubquery.createAlias("taskOfUser.workflow", "workflow", JoinType.LEFT_OUTER_JOIN);
		
		if (taskFormFilter.getUserId() != null) {
			userSubquery.createAlias("workflow.responsibleGroup", "workflowResponsibleGroup", JoinType.LEFT_OUTER_JOIN);
			userSubquery.createAlias("workflowResponsibleGroup.groupUsers", "userOfWorkflow", JoinType.LEFT_OUTER_JOIN);
			userSubquery.add(Restrictions.eq("userOfWorkflow.userId", taskFormFilter.getUserId()));
			userSubquery.add(Restrictions.or(Restrictions.eq("userOfWorkflow.userId", taskFormFilter.getUserId()),
					 					 	 Restrictions.isNull("workflow")));
			
			if (taskFormFilter.getMyOwn() != null && Boolean.TRUE.equals(taskFormFilter.getMyOwn())) {
				userSubquery.createAlias("taskOfUser.usersInTask", "userOfTask", JoinType.LEFT_OUTER_JOIN);
				userSubquery.add(Restrictions.eq("userOfTask.userId", taskFormFilter.getUserId()));
			}
		}
		
		userSubquery.addOrder(Order.asc("deadline").nulls(NullPrecedence.LAST));
		userSubquery.addOrder(Order.desc("creationDate"));
		userSubquery.addOrder(Order.desc("estimatedTime"));
		userSubquery.addOrder(Order.asc("taskTitle"));
		
		return userSubquery.setProjection(Projections.property("taskId"));
	}
	
	private void processFilters(Long scientificProjectId, TaskFormFilter taskFormFilter, Criteria criteria) {
		
		criteria.createAlias("task.workflow", "workflow", JoinType.LEFT_OUTER_JOIN);
		if (!"".equals(taskFormFilter.getWorkflowName()) && taskFormFilter.getWorkflowName() != null) {
			criteria.add(Restrictions.eq("workflow.workflowName", taskFormFilter.getWorkflowName().toLowerCase()).ignoreCase());
		}
		
		if (!"".equals(taskFormFilter.getPhaseName()) && taskFormFilter.getPhaseName() != null) {
			criteria.createAlias("task.phase", "phase");
			criteria.add(Restrictions.eq("phase.phaseName", taskFormFilter.getPhaseName().toLowerCase()).ignoreCase());
		}
		
		if (!"".equals(taskFormFilter.getTaskStatus()) && taskFormFilter.getTaskStatus() != null) {
			criteria.add(Restrictions.eq("task.status", TaskStatus.getStatusFromString(taskFormFilter.getTaskStatus())));
		}
		
		if (!"".equals(taskFormFilter.getQueryString()) && taskFormFilter.getQueryString() != null) {
			criteria.createAlias("task.tags", "tag", JoinType.LEFT_OUTER_JOIN);
			criteria.add(Restrictions.disjunction().add(Restrictions.ilike("task.taskTitle", "%" + taskFormFilter.getQueryString() + "%"))
											       .add(Restrictions.ilike("task.description", "%" + taskFormFilter.getQueryString() + "%"))
											       .add(Restrictions.ilike("tag.tagName", "%" + taskFormFilter.getQueryString() + "%")));
		}
		
		if (taskFormFilter.getUserId() != null) {
			criteria.createAlias("workflow.responsibleGroup", "workflowResponsibleGroup", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("workflowResponsibleGroup.groupUsers", "userOfWorkflow", JoinType.LEFT_OUTER_JOIN);
			criteria.add(Restrictions.or(Restrictions.eq("userOfWorkflow.userId", taskFormFilter.getUserId()),
	 	 								 Restrictions.isNull("workflow")));
		}
		
		if (taskFormFilter.getInitialDate() != null && !"".equals(taskFormFilter.getInitialDate())) {
			criteria.add(Restrictions.ge("task.creationDate", CalendarDateUtils.createCalendarFromString(taskFormFilter.getInitialDate())));
		}
		
		if (taskFormFilter.getFinalDate() != null && !"".equals(taskFormFilter.getFinalDate())) {
			criteria.add(Restrictions.le("task.creationDate", CalendarDateUtils.createCalendarFromString(taskFormFilter.getFinalDate())));
		}
		
		if (taskFormFilter.getMyOwn() != null && taskFormFilter.getMyOwn()) {
			criteria.createAlias("task.userGroupInTask", "userGroupInTask", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("userGroupInTask.groupUsers", "userOfGroup", JoinType.LEFT_OUTER_JOIN);

			criteria.add(Restrictions.or(Restrictions.eq("userOfGroup.userId", taskFormFilter.getUserId()),
										 Subqueries.propertyIn("task.taskId", createTaskSubQuery(scientificProjectId, taskFormFilter))));
		}
	}
	
	private DetachedCriteria createTaskSubQuery(Long scientificProjectId, TaskFormFilter taskFormFilter) {

		DetachedCriteria userSubquery = DetachedCriteria.forClass(Task.class, "taskOfUser");
		
		userSubquery.createAlias("taskOfUser.scientificProject", "scientificProject");
		userSubquery.add(Restrictions.eq("scientificProject.scientificProjectId", scientificProjectId));
		userSubquery.createAlias("taskOfUser.workflow", "workflow", JoinType.LEFT_OUTER_JOIN);
		
		if (!"".equals(taskFormFilter.getWorkflowName()) && taskFormFilter.getWorkflowName() != null) {
			userSubquery.add(Restrictions.eq("workflow.workflowName", taskFormFilter.getWorkflowName().toLowerCase()).ignoreCase());
		}
		
		if (!"".equals(taskFormFilter.getPhaseName()) && taskFormFilter.getPhaseName() != null) {
			userSubquery.createAlias("taskOfUser.phase", "phase");
			userSubquery.add(Restrictions.eq("phase.phaseName", taskFormFilter.getPhaseName().toLowerCase()).ignoreCase());
		}
		
		if (!"".equals(taskFormFilter.getTaskStatus()) && taskFormFilter.getTaskStatus() != null) {
			userSubquery.add(Restrictions.eq("taskOfUser.status", TaskStatus.getStatusFromString(taskFormFilter.getTaskStatus())));
		}
		
		if (!"".equals(taskFormFilter.getQueryString()) && taskFormFilter.getQueryString() != null) {
			userSubquery.createAlias("taskOfUser.tags", "tag", JoinType.LEFT_OUTER_JOIN);
			userSubquery.add(Restrictions.disjunction().add(Restrictions.ilike("taskOfUser.taskTitle", "%" + taskFormFilter.getQueryString() + "%"))
				        .add(Restrictions.ilike("taskOfUser.description", "%" + taskFormFilter.getQueryString() + "%"))
				        .add(Restrictions.ilike("tag.tagName", "%" + taskFormFilter.getQueryString() + "%")));
		}
		
		if (taskFormFilter.getUserId() != null) {
			userSubquery.createAlias("workflow.responsibleGroup", "workflowResponsibleGroup", JoinType.LEFT_OUTER_JOIN);
			userSubquery.createAlias("workflowResponsibleGroup.groupUsers", "userOfWorkflow", JoinType.LEFT_OUTER_JOIN);
			userSubquery.add(Restrictions.or(Restrictions.eq("userOfWorkflow.userId", taskFormFilter.getUserId()),
						 				 	 Restrictions.isNull("workflow")));
		}
		
		if (taskFormFilter.getInitialDate() != null && !"".equals(taskFormFilter.getInitialDate())) {
			userSubquery.add(Restrictions.ge("taskOfUser.creationDate", CalendarDateUtils.createCalendarFromString(taskFormFilter.getInitialDate())));
		}
		
		if (taskFormFilter.getFinalDate() != null && !"".equals(taskFormFilter.getFinalDate())) {
			userSubquery.add(Restrictions.le("taskOfUser.creationDate", CalendarDateUtils.createCalendarFromString(taskFormFilter.getFinalDate())));
		}
		
		if (taskFormFilter.getMyOwn() != null && taskFormFilter.getMyOwn()) {
			userSubquery.createAlias("taskOfUser.usersInTask", "userOfTask", JoinType.LEFT_OUTER_JOIN);
			userSubquery.add(Restrictions.eq("userOfTask.userId", taskFormFilter.getUserId()));
		}
		
		userSubquery.addOrder(Order.asc("deadline").nulls(NullPrecedence.LAST));
		userSubquery.addOrder(Order.desc("creationDate"));
		userSubquery.addOrder(Order.desc("estimatedTime"));
		userSubquery.addOrder(Order.asc("taskTitle"));
		
		return userSubquery.setProjection(Projections.property("taskId"));
	}
	
	public void deleteByScientificProjectId(Long scientificProjectId) {
		log.info("Processando remoção de tarefas por filtro");
		
		Query childrenUserQuery = session().createSQLQuery("DELETE FROM USUARIO_TAREFA WHERE ID_TAREFA IN (SELECT ID_TAREFA FROM TAREFA WHERE ID_PROJETO_CIENTIFICO = :scientificProjectId)");
		childrenUserQuery.setParameter("scientificProjectId", scientificProjectId);
		childrenUserQuery.executeUpdate();
		
		Query childrenTagQuery = session().createSQLQuery("DELETE FROM TAG_TAREFA WHERE ID_TAREFA IN (SELECT ID_TAREFA FROM TAREFA WHERE ID_PROJETO_CIENTIFICO = :scientificProjectId)");
		childrenTagQuery.setParameter("scientificProjectId", scientificProjectId);
		childrenTagQuery.executeUpdate();
		
		Query query = session().createSQLQuery("DELETE FROM TAREFA WHERE ID_PROJETO_CIENTIFICO = :scientificProjectId");
		query.setParameter("scientificProjectId", scientificProjectId);
		query.executeUpdate();
		
		session().flush();
	}
	
	public Integer getAllTasksForChartCount(Long scientificProjectId, Long userId, TaskFormFilter taskFormFilter) {
		
		log.info("Processando busca da contagem de tarefas por filtro");
		Criteria criteria = session().createCriteria(Task.class, "task");
		
		criteria.createAlias("task.scientificProject", "scientificProject");
		criteria.add(Restrictions.eq("scientificProject.scientificProjectId", scientificProjectId));
		
		processChartFilters(scientificProjectId, userId, taskFormFilter, criteria);
		
		criteria.setProjection(Projections.countDistinct("task.taskId"));
		
		return ((Long) criteria.uniqueResult()).intValue();
	}

	private void processChartFilters(Long scientificProjectId, Long userId, TaskFormFilter taskFormFilter, Criteria criteria) {

		if (taskFormFilter.getWorkflowId() != null) {
			criteria.createAlias("task.workflow", "workflow");
			criteria.add(Restrictions.eq("workflow.workflowId", taskFormFilter.getWorkflowId()));
		}
		
		if (taskFormFilter.getPhaseId() != null) {
			criteria.createAlias("task.phase", "phase");
			criteria.add(Restrictions.eq("phase.phaseId", taskFormFilter.getPhaseId()));
		}
		
		if (!"".equals(taskFormFilter.getTaskStatus()) && taskFormFilter.getTaskStatus() != null) {
			criteria.add(Restrictions.eq("task.status", TaskStatus.getStatusFromString(taskFormFilter.getTaskStatus())));
		}
		
		if (!"".equals(taskFormFilter.getQueryString()) && taskFormFilter.getQueryString() != null) {
			criteria.createAlias("task.tags", "tag", JoinType.LEFT_OUTER_JOIN);
			criteria.add(Restrictions.disjunction().add(Restrictions.ilike("task.taskTitle", "%" + taskFormFilter.getQueryString() + "%"))
											       .add(Restrictions.ilike("task.description", "%" + taskFormFilter.getQueryString() + "%"))
											       .add(Restrictions.ilike("tag.tagName", "%" + taskFormFilter.getQueryString() + "%")));
		}
		
		if (taskFormFilter.getInitialDate() != null && !"".equals(taskFormFilter.getInitialDate())) {
			criteria.add(Restrictions.ge("task.creationDate", CalendarDateUtils.createCalendarFromString(taskFormFilter.getInitialDate())));
		}
		
		if (taskFormFilter.getFinalDate() != null && !"".equals(taskFormFilter.getFinalDate())) {
			criteria.add(Restrictions.le("task.creationDate", CalendarDateUtils.createCalendarFromString(taskFormFilter.getFinalDate())));
		}
		
		if (userId != null) {
			criteria.createAlias("task.userGroupInTask", "userGroupInTask", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("userGroupInTask.groupUsers", "userOfGroup", JoinType.LEFT_OUTER_JOIN);
			
			criteria.add(Restrictions.or(Restrictions.eq("userOfGroup.userId", userId),
						 Subqueries.propertyIn("task.taskId", createChartTaskSubQuery(scientificProjectId, userId,taskFormFilter))));
		}
	}

	private DetachedCriteria createChartTaskSubQuery(Long scientificProjectId, Long userId, TaskFormFilter taskFormFilter) {
	
		DetachedCriteria userSubquery = DetachedCriteria.forClass(Task.class, "taskOfUser");
		
		userSubquery.createAlias("taskOfUser.scientificProject", "scientificProject");
		userSubquery.add(Restrictions.eq("scientificProject.scientificProjectId", scientificProjectId));
		
		if (taskFormFilter.getWorkflowId() != null) {
			userSubquery.createAlias("taskOfUser.workflow", "workflow");
			userSubquery.add(Restrictions.eq("workflow.workflowId", taskFormFilter.getWorkflowId()));
		}
		
		if (taskFormFilter.getPhaseId() != null) {
			userSubquery.createAlias("taskOfUser.phase", "phase");
			userSubquery.add(Restrictions.eq("phase.phaseId", taskFormFilter.getPhaseId()));
		}
		
		if (!"".equals(taskFormFilter.getTaskStatus()) && taskFormFilter.getTaskStatus() != null) {
			userSubquery.add(Restrictions.eq("taskOfUser.status", TaskStatus.getStatusFromString(taskFormFilter.getTaskStatus())));
		}
		
		if (!"".equals(taskFormFilter.getQueryString()) && taskFormFilter.getQueryString() != null) {
			userSubquery.createAlias("taskOfUser.tags", "tag", JoinType.LEFT_OUTER_JOIN);
			userSubquery.add(Restrictions.disjunction().add(Restrictions.ilike("taskOfUser.taskTitle", "%" + taskFormFilter.getQueryString() + "%"))
						.add(Restrictions.ilike("taskOfUser.description", "%" + taskFormFilter.getQueryString() + "%"))
						.add(Restrictions.ilike("tag.tagName", "%" + taskFormFilter.getQueryString() + "%")));
		}
		
		if (taskFormFilter.getInitialDate() != null && !"".equals(taskFormFilter.getInitialDate())) {
			userSubquery.add(Restrictions.ge("taskOfUser.creationDate", CalendarDateUtils.createCalendarFromString(taskFormFilter.getInitialDate())));
		}
		
		if (taskFormFilter.getFinalDate() != null && !"".equals(taskFormFilter.getFinalDate())) {
			userSubquery.add(Restrictions.le("taskOfUser.creationDate", CalendarDateUtils.createCalendarFromString(taskFormFilter.getFinalDate())));
		}
		
		if (userId != null) {
			userSubquery.createAlias("taskOfUser.usersInTask", "userOfTask", JoinType.LEFT_OUTER_JOIN);
			userSubquery.add(Restrictions.eq("userOfTask.userId", userId));
		}
		
		userSubquery.addOrder(Order.asc("deadline").nulls(NullPrecedence.LAST));
		userSubquery.addOrder(Order.desc("creationDate"));
		userSubquery.addOrder(Order.desc("estimatedTime"));
		userSubquery.addOrder(Order.asc("taskTitle"));
		
		return userSubquery.setProjection(Projections.property("taskId"));
	}

}