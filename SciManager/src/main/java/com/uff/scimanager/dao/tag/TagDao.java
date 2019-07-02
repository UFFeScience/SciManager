package com.uff.scimanager.dao.tag;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uff.scimanager.component.PaginationParameterConfig;
import com.uff.scimanager.dao.GenericDao;
import com.uff.scimanager.domain.Tag;

@Repository
@Transactional
public class TagDao extends GenericDao<Tag> {
	
	private static final Logger log = LoggerFactory.getLogger(TagDao.class);
	
	@Autowired
	public TagDao(PaginationParameterConfig paginationParameterConfig, 
    			  @Qualifier("sciManager") SessionFactory sessionFactory) {
		
		super(paginationParameterConfig, sessionFactory);
	}

	public Tag saveOrUpdateTag(Tag entity) {
		log.info("Salvando/atualizando tag");
		return super.saveOrUpdate(entity);
	}

	public void delete(Tag entity) {
		log.info("Deletando tag do banco");
		super.delete(entity);
	}
	
	public Tag findById(Long id) {
		log.info("Buscando tag por id {}", id);
		return super.findById(Tag.class, id);
	}
	
	public Tag getTagByTagName(String tagName) throws HibernateException {
		log.info("Buscando tag pela tagName {}", tagName);
		List<Criterion> restrictions = new ArrayList<Criterion>();
		restrictions.add(Restrictions.eq("tagName", tagName.toLowerCase()).ignoreCase());
		
		List<Tag> queryResultList = findByParams(Tag.class, restrictions);
		
		if (queryResultList.isEmpty()) {
			return null;
		}
		
		return queryResultList.get(0);
	}

	public List<Tag> findAllPaginated(Integer pageNumber, String queryString) {
		log.info("Processando busca pagina de tags por filtro");
		List<Criterion> restrictions = new ArrayList<Criterion>();
		
		if (!"".equals(queryString) && queryString != null) {
			restrictions.add(Restrictions.ilike("tagName", "%" + queryString + "%"));
		}
		
		return findByParamsPaginated(Tag.class, restrictions, pageNumber, "tagName");
	}
	
	@SuppressWarnings("unchecked")
	public List<Tag> findAllByQueryString(String queryString) {
		log.info("Processando busca por todas as tags que contenham o texto {} na tagName", queryString);
		Criteria criteria = session().createCriteria(Tag.class, "tag");
		
		if (!"".equals(queryString) && queryString != null) {
			criteria.add(Restrictions.ilike("tagName", "%" + queryString + "%"));
		}
		
		criteria.setMaxResults(paginationParameterConfig.getMaxFilterResults());
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	public Integer getAllTagsCount(String queryString) throws HibernateException {
		log.info("Processando busca da contagem de tags por filtro");
		
		Query countQuery = session().createSQLQuery(buildCountQuery(queryString));
		setQueryParameters(countQuery, queryString);
		
		return ((BigInteger) countQuery.list().get(0)).intValue();
	}

	private String buildCountQuery(String queryString) {
		StringBuilder getCountQuery = new StringBuilder("SELECT COUNT(ID_TAG) FROM TAG");
		
		if (!"".equals(queryString) && queryString != null) {
			getCountQuery.append(" WHERE upper(NM_NOME_TAG) like :queryString"); 
		}
		
		return getCountQuery.toString();
	}
	
	private void setQueryParameters(Query countQuery, String queryString) {
		if (!"".equals(queryString) && queryString != null) {
			countQuery.setParameter("queryString", '%' + queryString.toUpperCase() + '%');
		}
	}

}