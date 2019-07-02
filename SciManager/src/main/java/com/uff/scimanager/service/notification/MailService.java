package com.uff.scimanager.service.notification;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.google.common.base.Throwables;
import com.uff.scimanager.util.EncrypterUtils;

@Service
public class MailService {
	
	private static final Logger log = LoggerFactory.getLogger(MailService.class);
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Value("${system.url}")
	protected String systemUrl;
	
	public void sendPasswordRenewalEmail(Long userId, String email) {
		sendEmail(email,  "Você solicitou uma alteração de senha."
				.concat("<br /><br /><a target=\"_blank\" href=\"")
				.concat(systemUrl)
				.concat("/user/reset-password/new-password?token=")
				.concat(EncrypterUtils.generateUserResetPasswordToken(userId, email))
				.concat("\">Clique aqui para redefinir sua senha.</a>"),
				("[SciManager] ").concat("Email de alteração de senha"));
	}
	
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