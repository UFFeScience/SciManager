package com.uff.scimanager.repository.notification.message;

import org.springframework.data.repository.CrudRepository;

import com.uff.scimanager.domain.TaskHistory;

public interface NotificationMessageRepository extends CrudRepository<TaskHistory, Long>, NotificationMessageRepositoryCustom {}