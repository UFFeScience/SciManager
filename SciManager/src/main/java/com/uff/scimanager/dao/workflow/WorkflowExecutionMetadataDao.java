package com.uff.scimanager.dao.workflow;

import java.math.BigInteger;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uff.scimanager.component.PaginationParameterConfig;
import com.uff.scimanager.dao.GenericDao;
import com.uff.scimanager.domain.swfms.scicumulus.WorkflowExecutionMetadata;

@Repository
@Transactional
public class WorkflowExecutionMetadataDao extends GenericDao<WorkflowExecutionMetadata> {
	
	private static final Logger log = LoggerFactory.getLogger(WorkflowExecutionMetadataDao.class);
	
	@Autowired
	public WorkflowExecutionMetadataDao(PaginationParameterConfig paginationParameterConfig, 
					   					@Qualifier("sciCumulus") SessionFactory sessionFactory) {
		
		super(paginationParameterConfig, sessionFactory);
	}

	@SuppressWarnings("unchecked")
	public List<WorkflowExecutionMetadata> getExecutionMetadataByExecTag(Integer pageNumber, String execTag) {
		log.info("Processando busca de metadados da execução do workflow de execTag {}", execTag);
		
		StringBuilder sqlQuery = new StringBuilder("SELECT DISTINCT w.tagexec AS \"execTag\", a.tag AS \"tag\", ea.workspace AS \"workspace\", ea.status AS \"status\", ")
										   .append("ea.starttime AS \"startTime\", ea.endtime AS \"endTime\", ea.processor AS \"processors\", em.address AS \"address\", ")
										   .append("em.type AS \"machineType\", em.hostname AS \"hostname\", em.financial_cost AS \"financialCost\", ea.failure_tries AS \"failureTries\" ")
										   .append("FROM EWORKFLOW w, EACTIVITY a, EACTIVATION ea, EMACHINE em ")
										   .append("WHERE w.ewkfid = a.wkfid ")
										   .append("AND a.actid = ea.actid ")
										   .append("AND ea.machineid = em.machineid ")
										   .append("AND w.tagexec = :execTag ")
										   .append("ORDER BY ea.endtime, ea.starttime DESC ")
										   .append("LIMIT :limit OFFSET :offset");
		
		Query hqlQuery = session().createSQLQuery(sqlQuery.toString());
		hqlQuery.setParameter("execTag", execTag);
		hqlQuery.setParameter("limit", paginationParameterConfig.getMaxMetadataResultsPerPage());
		hqlQuery.setParameter("offset", paginationParameterConfig.calculateActualPageFirstResultIndex(pageNumber));

		return hqlQuery.setResultTransformer(Transformers.aliasToBean(WorkflowExecutionMetadata.class)).list();
	}
	
	public Integer countExecutionMetadataByExecTag(String execTag) {
		log.info("Processando contagem de metadados da execução do workflow de execTag {}", execTag);
		
		StringBuilder sqlQuery = new StringBuilder("SELECT COUNT (*)")
										   .append("FROM EWORKFLOW w, EACTIVITY a, EACTIVATION ea, EMACHINE em ")
										   .append("WHERE w.ewkfid = a.wkfid ")
										   .append("AND a.actid = ea.actid ")
										   .append("AND ea.machineid = em.machineid ")
										   .append("AND w.tagexec = :execTag ");
		
		Query hqlQuery = session().createSQLQuery(sqlQuery.toString());
		hqlQuery.setParameter("execTag", execTag);

		return ((BigInteger)  hqlQuery.uniqueResult()).intValue();
	}
}