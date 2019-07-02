package com.uff.system.notifier.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.google.common.base.Throwables;
import com.uff.system.notifier.domain.MessageContent;
import com.uff.system.notifier.domain.MessageEmitter;

@Controller
@CrossOrigin
@RequestMapping("/notification")
public class MessageNotificationController {
	
	private static final Logger log = LoggerFactory.getLogger(MessageNotificationController.class);
	
	private final List<MessageEmitter> emitters = Collections.synchronizedList(new ArrayList<MessageEmitter>());
    
	@RequestMapping(value = "/{channel}/notify/{userId}", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<String> handleNotification(@PathVariable String channel, @PathVariable Long userId) {
    	log.info("Iniciando processamento de envio de mensagem");
    	
    	if (channel == null || "".equals(channel) || userId == null) {
    		return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    	}
    	
    	synchronized (this.emitters) { 
        	MessageEmitter messageEmitterActive = MessageEmitter.getSseEmitterActive(emitters, channel, userId);
	        		
    		try {
    			if (messageEmitterActive != null) {
    				messageEmitterActive.getSseEmitter().send(MessageContent.builder().channel(channel).userId(userId).reload(Boolean.TRUE).build(), 
    											    	  	  MediaType.APPLICATION_JSON);
    			}
    		} 
    		catch (IOException e) {
    			messageEmitterActive.getSseEmitter().complete();
    			log.error("Erro ao emitir mensagem: channel={}; userId={}\n{}", channel, userId, Throwables.getStackTraceAsString(e)); 
    			
    			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
    		}
    	}
        
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "/{channel}/sse/{userId}", method = RequestMethod.GET)
    public SseEmitter getSseEmitter(@PathVariable String channel, @PathVariable Long userId, HttpServletRequest request) {
    	log.info("Iniciando emitter de mensagem para url: {}", request.getHeader("referer"));
    	
    	if (channel == null || "".equals(channel) || userId == null) {
			log.warn("Impossivel criar SseEmitter, dados inválidos: channel={}; userId={}", channel, userId); 
    		return null;
    	}
    	
    	MessageEmitter.unsubscribeExistingSse(emitters, channel, userId);

    	SseEmitter sseEmitter = new SseEmitter(0L);
    	final MessageEmitter emitter = MessageEmitter.builder().channel(channel).userId(userId).sseEmitter(sseEmitter).build();
    	
    	synchronized (this.emitters) { 
	    	emitters.add(emitter);
	        
	        sseEmitter.onCompletion(new Runnable() {
	            public void run() {
	            	synchronized (emitters) { 
	            		emitters.remove(emitter);
	            	}
	            }
	        });
    	}
        
        return sseEmitter;
    }
    
}