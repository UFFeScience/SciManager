package com.uff.scimanager.dao.notification.message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.uff.scimanager.component.PaginationParameterConfig;
import com.uff.scimanager.domain.NotificationMessage;
import com.uff.scimanager.repository.notification.message.NotificationMessageRepositoryCustom;

public class NotificationMessageRepositoryImpl implements NotificationMessageRepositoryCustom {
	
	private static final Logger log = LoggerFactory.getLogger(NotificationMessageRepositoryImpl.class);
	
	private final MongoTemplate mongoTemplate;

	@Autowired
	public NotificationMessageRepositoryImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
	@Override
	public List<NotificationMessage> getNotificationMessagesOfUser(Long userId, Integer pageNumber, Integer pageSize, Boolean visualized) {
		log.info("Iniciando busca por notificacoes do usuario de id {}", userId);
		
		List<Criteria> filter = new ArrayList<Criteria>();
		
		if (visualized != null) {
			filter.add(Criteria.where("userSubjectId").is(userId).and("userAgentId").ne(userId).and("visualized").is(visualized));
		}
		else {
			filter.add(Criteria.where("userSubjectId").is(userId).and("userAgentId").ne(userId));
		}
		
		Criteria criteria = new Criteria().andOperator(filter.toArray(new Criteria[filter.size()]));
		Query find = new Query(criteria).limit(pageSize).skip(PaginationParameterConfig.calculateFirstResultIndex(pageNumber, pageSize));
		find.with(new Sort(Sort.Direction.DESC, "actionDate"));
		
		find.fields().include("notificationMessageId").include("messageTitle").include("messageBody")
					 .include("messageLink").include("actionDate").include("visualizedDate")
					 .include("visualized").include("userSubjectId")
					 .include("userAgentId");
		
        return mongoTemplate.find(find, NotificationMessage.class);
	}
	
	@Override
	public Long countNotificationMessagesOfUser(Long userId) {
		log.info("Iniciando busca por notificacoes do usuario de id {}", userId);
		
		List<Criteria> filter = new ArrayList<Criteria>();
		filter.add(Criteria.where("userSubjectId").is(userId).and("userAgentId").ne(userId));
		
		Criteria criteria = new Criteria().andOperator(filter.toArray(new Criteria[filter.size()]));
		Query find = new Query(criteria);
		
        return mongoTemplate.count(find, NotificationMessage.class);
	}

	@Override
	public Long countNewNotificationMessagesOfUser(Long userId) {
		log.info("Iniciando contagem por notificacoes nao visualizadas do usuario de id {}", userId);
		
		List<Criteria> filter = new ArrayList<Criteria>();
		filter.add(Criteria.where("userSubjectId").is(userId).and("userAgentId").ne(userId).and("visualized").is(Boolean.FALSE));
		
		Criteria criteria = new Criteria().andOperator(filter.toArray(new Criteria[filter.size()]));
		Query find = new Query(criteria);
		
        return mongoTemplate.count(find, NotificationMessage.class);
	}

	@Override
	public void updateNotificationsToVisualized(Long userId, String notificationMessageId) {
		log.info("Atualizando para visualizadas notificacoes do userId {}", userId);
		
		Update update = new Update();
		update.set("visualized", Boolean.TRUE);
		update.set("visualizedDate", new Date());
		Criteria criteria = null;
		
		if (notificationMessageId != null) {
			criteria = Criteria.where("_id").is(new ObjectId(notificationMessageId));
		}
		else {
			List<Criteria> filter = new ArrayList<Criteria>();
			filter.add(Criteria.where("userSubjectId").is(userId).and("userAgentId").ne(userId).and("visualized").is(Boolean.FALSE));
		
			criteria = new Criteria().andOperator(filter.toArray(new Criteria[filter.size()]));
		}
		
		Query find = new Query(criteria);
		
        mongoTemplate.updateMulti(find, update, NotificationMessage.class);
	}

}