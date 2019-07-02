package com.uff.scimanager.repository.history;

import java.util.Calendar;
import java.util.List;

import com.uff.scimanager.domain.TaskHistory;
import com.uff.scimanager.domain.TaskHistoryType;
import com.uff.scimanager.domain.form.TaskChartFormFilter;

public interface TaskHistoryRepositoryCustom {
	
	List<TaskHistory> getTasksHistoryByFilter(Calendar initialDate, Calendar finalDate, TaskChartFormFilter taskChartFormFilter, TaskHistoryType taskHistoryType);
	
}