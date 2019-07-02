package com.uff.scimanager.dao.model.file;

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
import com.uff.scimanager.domain.ModelFile;
import com.uff.scimanager.util.CalendarDateUtils;

@Repository
@Transactional
public class ModelFileDao extends GenericDao<ModelFile> {
	
	private static final Logger log = LoggerFactory.getLogger(ModelFileDao.class);
	
	@Autowired
	public ModelFileDao(PaginationParameterConfig paginationParameterConfig, 
					    @Qualifier("sciManager") SessionFactory sessionFactory) {
		
		super(paginationParameterConfig, sessionFactory);
	}
	
	public ModelFile findById(Long id) {
		log.info("Buscando modelFile por id {}", id);
		return super.findById(ModelFile.class, id);
	}
	
	public void delete(ModelFile entity) {
		log.info("Deletando arquivo modelo do banco");
		super.delete(entity);
	}

	public ModelFile saveOrUpdateModelFile(ModelFile entity) {
		log.info("Salvando arquivo modelo");
		return super.saveOrUpdate(entity);
	}

	public ModelFile findModelFileByWorkflowIdAndModelFileId(Long workflowId, Long modelFileId) {
		log.info("Processando busca por arquivo modelo de workflow por filtro");
		Criteria criteria = session().createCriteria(ModelFile.class, "modelFile");
		
		criteria.createAlias("modelFile.workflow", "workflow");
		criteria.add(Restrictions.eq("modelFile.modelFileId", modelFileId));
		criteria.add(Restrictions.eq("workflow.workflowId", workflowId));
		
		return (ModelFile) criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).uniqueResult();
	}

	public ModelFile findModelFileCurrentByWorkflowId(Long workflowId) {
		log.info("Processando busca por arquivo modelo atual de workflow por filtro");
		Criteria criteria = session().createCriteria(ModelFile.class, "modelFile");
		
		criteria.createAlias("modelFile.workflow", "workflow");
		criteria.add(Restrictions.eq("modelFile.isCurrentFile", Boolean.TRUE));
		criteria.add(Restrictions.eq("workflow.workflowId", workflowId));
		
		return (ModelFile) criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<ModelFile> findAllDashboardPaginated(Integer pageNumber, Long workflowId) {
		log.info("Processando busca de pagina de arquivos modelos relevantes");
		Criteria criteria = session().createCriteria(ModelFile.class, "modelFile");
		
		criteria.add(Restrictions.eq("workflow.workflowId", workflowId));
		
		criteria.setFirstResult(paginationParameterConfig.calculateActualPageFirstResultIndex(pageNumber));
		criteria.setMaxResults(paginationParameterConfig.getMaxFilterResults());
		
		criteria.addOrder(Order.desc("submissionDate"));
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	@SuppressWarnings("unchecked")
	public List<ModelFile> findAllPaginated(Integer pageNumber, Long workflowId, String initialDate, String finalDate) {
		log.info("Processando busca de pagina de arquivos modelos por filtro");
		Criteria criteria = session().createCriteria(ModelFile.class, "modelFile");
		
		criteria.add(Restrictions.eq("workflow.workflowId", workflowId));
		
		if (initialDate != null) {
			criteria.add(Restrictions.ge("initialDate", CalendarDateUtils.createCalendarFromString(initialDate)));
		}
		
		if (finalDate != null) {
			criteria.add(Restrictions.le("finalDate", CalendarDateUtils.createCalendarFromString(finalDate)));
		}
		
		criteria.setFirstResult(paginationParameterConfig.calculateActualPageFirstResultIndex(pageNumber));
		criteria.setMaxResults(paginationParameterConfig.getMaxResultsPerPage());
		
		criteria.addOrder(Order.desc("submissionDate"));
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	public Integer getAllModelFilesCountByExecTag(String execTag) {
		return getAllModelFilesCount(null, null, null, execTag);
	}
	
	public Integer getAllModelFilesCount(Long workflowId, String initialDate, String finalDate) {
		return getAllModelFilesCount(workflowId, initialDate, finalDate, null);
	}
	
	public Integer getAllModelFilesCount(Long workflowId, String initialDate, String finalDate, String execTag) {
		log.info("Processando contagem de arquivos modelos por filtro");
		Criteria criteria = session().createCriteria(ModelFile.class, "modelFile");
		
		criteria.add(Restrictions.eq("workflow.workflowId", workflowId));
		
		if (initialDate != null) {
			criteria.add(Restrictions.ge("initialDate", CalendarDateUtils.createCalendarFromString(initialDate)));
		}
		
		if (finalDate != null) {
			criteria.add(Restrictions.le("finalDate", CalendarDateUtils.createCalendarFromString(finalDate)));
		}
		
		if (execTag != null && !"".equals(execTag)) {
			criteria.add(Restrictions.eq("execTag", execTag));
		}
		
		criteria.setProjection(Projections.countDistinct("modelFile.modelFileId"));
		
		return ((Long) criteria.uniqueResult()).intValue();
	}

}