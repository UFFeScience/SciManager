package com.uff.system.notification.processor.service.notification;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.google.common.base.Throwables;

@Service
public class MailService {
	
	private static final Logger log = LoggerFactory.getLogger(MailService.class);
	
	@Autowired
	private JavaMailSender mailSender;
	
    public void sendEmail(String mailTo, String messageContent, String messageSubject) {
    	try {
    		log.info("Processando envio de email para o email: {}; subject: {}, messageContent", mailTo, messageSubject, messageContent);
    		
	        MimeMessage message = mailSender.createMimeMessage();
            message.setSubject(messageSubject);
            
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(mailTo);
            helper.setText(messageContent, true);
        
            mailSender.send(message);
            log.info("Email enviado com sucesso para {}", mailTo);
        } 
    	catch (Exception e) {
    		log.info("Erro ao enviar email para {}\n{}", mailTo, Throwables.getStackTraceAsString(e));
        }
    }
	
}