package com.uff.system.notification.processor.repository;

import org.springframework.data.repository.CrudRepository;

import com.uff.system.notification.processor.domain.NotificationMessage;

public interface NotificationMessageRepository extends CrudRepository<NotificationMessage, Long> {}