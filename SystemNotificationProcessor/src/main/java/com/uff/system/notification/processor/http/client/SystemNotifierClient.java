package com.uff.system.notification.processor.http.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Throwables;

@Component
public class SystemNotifierClient {
	
	private static final Logger log = LoggerFactory.getLogger(SystemNotifierClient.class);
	
	@Value("${system.notifier.url}")
	private String systemNotifierUrl;
	
	public void notifyNewMessage(Long userId, String notificationType) {
		String finalUrl = systemNotifierUrl + "/" + notificationType + "/notify/" + userId;
		
		try  {
			log.info("Iniciando chamada http para {}", finalUrl);
			
	        new RestTemplate().getForObject(finalUrl, String.class);
	        
	        log.info("Chamada http realizada com sucesso para {}", finalUrl);
		}
		catch (Exception e) {
			log.error("Erro ao realizar chamada http para {}\n{}", finalUrl, Throwables.getStackTraceAsString(e));
		}
    }
	
}