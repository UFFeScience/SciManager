package com.uff.scimanager.repository.history;

import org.springframework.data.repository.CrudRepository;

import com.uff.scimanager.domain.TaskHistory;

public interface TaskHistoryRepository extends CrudRepository<TaskHistory, Long>, TaskHistoryRepositoryCustom {}