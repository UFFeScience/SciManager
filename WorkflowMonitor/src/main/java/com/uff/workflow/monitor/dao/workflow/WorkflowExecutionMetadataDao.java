package com.uff.workflow.monitor.dao.workflow;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uff.workflow.monitor.dao.GenericDao;
import com.uff.workflow.monitor.domain.swfms.scicumulus.SciCumulusExecutionStatus;

@Repository
@Transactional
public class WorkflowExecutionMetadataDao extends GenericDao<Object> {
	
	private static final Logger log = LoggerFactory.getLogger(WorkflowExecutionMetadataDao.class);
	
	@Autowired
	public WorkflowExecutionMetadataDao(@Qualifier("sciCumulus") SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@SuppressWarnings("unchecked")
	public List<String> findExecutionToUpdateSuccess(List<String> execTags) {
		log.info("Processando busca de metadado de execução de workflows de execTag {} e com execução finalizada", execTags);

		StringBuilder sqlQuery = new StringBuilder("SELECT DISTINCT w.tagexec as execTag ")
										   .append("FROM EWORKFLOW w, EACTIVITY a ")
										   .append("WHERE w.tagexec IN :execTags ")
										   .append("GROUP BY w.tagexec ")
										   .append("HAVING 0 = ( ")
										   .append("SELECT COUNT (act.actid) ")
										   .append("FROM EWORKFLOW wkf, EACTIVITY act ")
										   .append("WHERE wkf.tagexec = w.tagexec ")
										   .append("AND act.status != :finishedStatus AND act.status != :failureStatus)");

		Query hqlQuery = session().createSQLQuery(sqlQuery.toString());
		hqlQuery.setParameterList("execTags", execTags);
		hqlQuery.setParameter("finishedStatus", SciCumulusExecutionStatus.FINISHED.getExecutionStatusName());
		hqlQuery.setParameter("failureStatus", SciCumulusExecutionStatus.FAILURE.getExecutionStatusName());
		
		return hqlQuery.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> findExecutionToUpdateFailure(List<String> execTags) {
		log.info("Processando busca de metadado de execução de workflows de execTag {} e com execução finalizada com erro", execTags);

		StringBuilder sqlQuery = new StringBuilder("SELECT DISTINCT w.tagexec as execTag ")
										   .append("FROM EWORKFLOW w, EACTIVITY a ")
										   .append("WHERE w.tagexec IN :execTags ")
										   .append("GROUP BY w.tagexec ")
										   .append("HAVING 0 < ( ")
										   .append("SELECT COUNT (act.actid) ")
										   .append("FROM EWORKFLOW wkf, EACTIVITY act ")
										   .append("WHERE wkf.tagexec = w.tagexec ")
										   .append("AND act.status = :failureStatus)");

		Query hqlQuery = session().createSQLQuery(sqlQuery.toString());
		hqlQuery.setParameterList("execTags", execTags);
		hqlQuery.setParameter("failureStatus", SciCumulusExecutionStatus.FAILURE.getExecutionStatusName());
		
		return hqlQuery.list();
	}
	
}