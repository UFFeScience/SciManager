package com.uff.scimanager.dao.history;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.uff.scimanager.domain.TaskHistory;
import com.uff.scimanager.domain.TaskHistoryType;
import com.uff.scimanager.domain.form.TaskChartFormFilter;
import com.uff.scimanager.repository.history.TaskHistoryRepositoryCustom;
import com.uff.scimanager.util.CalendarDateUtils;

public class TaskHistoryRepositoryImpl implements TaskHistoryRepositoryCustom {
	
	private static final Logger log = LoggerFactory.getLogger(TaskHistoryRepositoryImpl.class);
	
	private final MongoTemplate mongoTemplate;

	@Autowired
	public TaskHistoryRepositoryImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
	@Override
	public List<TaskHistory> getTasksHistoryByFilter(Calendar initialDate, Calendar finalDate, TaskChartFormFilter taskChartFormFilter, TaskHistoryType taskHistoryType) {
		log.info("Iniciando busca por historico de tarefas por filtro");
		
		List<Criteria> filter = new ArrayList<Criteria>();
		
		if (taskHistoryType != null) {
			filter.add(Criteria.where("historyType").is(taskHistoryType.name()));
		}
				
		if (initialDate != null) {
			filter.add(Criteria.where("actionDate").gte(CalendarDateUtils.getDayBefore(initialDate.getTime())));
		}
		
		if (finalDate != null) {
			filter.add(Criteria.where("actionDate").lte(CalendarDateUtils.getDayAfter(finalDate.getTime())));
		}
		
		processFieldFilter("scientificProjectId", taskChartFormFilter.getScientificProjectId(), filter);
		processFieldFilter("userAgentId", taskChartFormFilter.getUserId(), filter);
		processFieldFilter("workflowId", taskChartFormFilter.getWorkflowId(), filter);
		processFieldFilter("phaseId", taskChartFormFilter.getPhaseId(), filter);
		
		Criteria criteria = new Criteria().andOperator(filter.toArray(new Criteria[filter.size()]));
		
		Query find = new Query(criteria);
		
		if (TaskHistoryType.UPDATED.equals(taskHistoryType)) {
			find.fields().elemMatch("changeSet", Criteria.where("name").is("status"));
		}
		
		find.fields().include("workflowId").include("scientificProjectId").include("taskId")
					 .include("phaseId").include("creationDate").include("currentStatus")
					 .include("actionDate").include("historyType").include("userAgentId");
		
        return mongoTemplate.find(find, TaskHistory.class);
	}
	
	private void processFieldFilter(String fieldName, Object fieldValue, List<Criteria> filter) {
		if (fieldValue != null) {
			filter.add(Criteria.where(fieldName).is(fieldValue));
		}
	}

}