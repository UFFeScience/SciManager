package com.uff.scimanager.controller.notification.message;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uff.scimanager.component.PaginationInfo;
import com.uff.scimanager.component.ResponseMessage;
import com.uff.scimanager.domain.dto.NotificationMessageDTO;
import com.uff.scimanager.domain.dto.filter.FilterDTO;
import com.uff.scimanager.domain.dto.filter.FilterFieldDTO;
import com.uff.scimanager.service.notification.message.NotificationMessageService;

@Controller
@RequestMapping("/notification-message")
public class NotificationMessageController {
	
	private static final Logger log = LoggerFactory.getLogger(NotificationMessageController.class);
	
    private final NotificationMessageService notificationMessageService;
	
	@Autowired
	public NotificationMessageController(NotificationMessageService notificationMessageService) {
		this.notificationMessageService = notificationMessageService;
	}
	
	@PreAuthorize("@currentUserService.canAccessUser(principal, #userId)")
    @RequestMapping(value = "/notification-list/{userId}", method = RequestMethod.GET)
    public String getNotificationsList(@RequestParam(value = "pageSize", required = false, defaultValue = "12") Integer pageSize,
									   @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
									   @RequestParam(value = "notVisualized", required = false) Boolean notVisualized,
									   @PathVariable Long userId,
									   Model model) {
    	
    	log.info("Carregando página de notificacoes do usuario de userId {}", userId);
        
		model.addAttribute("notificationMessageDTOs", notificationMessageService.getAllUserNotifications(userId, pageNumber, pageSize, notVisualized));
        
        PaginationInfo pagination = PaginationInfo.builder()
			        							  .totalEntities(notificationMessageService.countAllUserNotifications(userId))
			        							  .actualPageNumber(pageNumber)
			        							  .pageSize(pageSize)
			        							  .baseUrlLink("/notification-message/notification-list/" + userId + "?pageSize=" + pageSize).build();
        
		pagination.addUrlParameter("notVisualized", notVisualized);
        
		FilterDTO filter = FilterDTO.builder().build();
        filter.addFilterField(FilterFieldDTO.builder().name("notVisualized").value(notVisualized).build());
        
        model.addAttribute("filter", filter);
        model.addAttribute("pagination", pagination);
        model.addAttribute("searchListUrl", pagination.getBaseUrlLink());
        
        return "fragments/notification-message/notification-list-fragment";
    }
	
	@PreAuthorize("@currentUserService.canAccessUser(principal, #userId)")
    @RequestMapping(value = "/all-notifications/{userId}", method = RequestMethod.GET)
    public String getNotificationsPage(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
									   @RequestParam(value = "notVisualized", required = false) Boolean notVisualized,
									   @PathVariable Long userId,
									   Model model) {
    	
    	log.info("Carregando página de notificacoes do usuario de userId {}", userId);

        FilterDTO filter = FilterDTO.builder().build();
        filter.addFilterField(FilterFieldDTO.builder().name("notVisualized").value(notVisualized).build());
        
        PaginationInfo pagination = PaginationInfo.builder()
        										  .actualPageNumber(pageNumber)
        										  .baseUrlLink("/notification-message/notification-list/" + userId).build();
        
        model.addAttribute("filter", filter);
        model.addAttribute("searchListUrl", pagination.buildParametersFromFilter(filter));
        
        return "notification-message/notification-list";
    }
	
	@PreAuthorize("@currentUserService.canAccessUser(principal, #userId)")
    @RequestMapping(value = "/update-visualized/{userId}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseMessage updateToVisualized(@RequestParam(value = "notificationMessageId", required = false) String notificationMessageId,
						  		   			  @PathVariable Long userId) {
    	
    	log.info("Atualizando para visualizadas notificacoes do userId {}", userId);
    	
    	try {
    		notificationMessageService.updateToVisualized(userId, notificationMessageId);
    	}
    	catch (Exception e) {
    		return ResponseMessage.buildErrorMessage("Erro inesperado ao atualizar notificacoes para visualizadas");
    	}
        
        return ResponseMessage.buildSuccessMessage("Notificações atualizadas para visualizadas com sucesso");
    }
	
    @RequestMapping(value = "/all/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public List<NotificationMessageDTO> getAllUserNotifications(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
    														    @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
    														    @PathVariable Long userId) {
    	
    	log.info("Carregando listagem de todas as notificacoes relacionadas ao usuario de id {}, pagina {}", userId, pageNumber);
        return notificationMessageService.getAllUserNotifications(userId, pageNumber, pageSize, null);
    }
    
    @RequestMapping(value = "/count/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public Integer countAllUserNotificationsNotVisualized(@PathVariable Long userId) {
    	log.info("Carregando contagem de todas as notificacoes relacionadas ao usuario de id {} nao visualizadas", userId);
        return notificationMessageService.countAllNewUserNotifications(userId);
    }
    
}