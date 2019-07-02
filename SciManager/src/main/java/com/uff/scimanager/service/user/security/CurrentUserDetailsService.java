package com.uff.scimanager.service.user.security;

import javax.security.sasl.AuthenticationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.uff.scimanager.domain.User;
import com.uff.scimanager.domain.form.UserFormUpdate;
import com.uff.scimanager.domain.security.CurrentUser;
import com.uff.scimanager.service.user.UserService;
import com.uff.scimanager.util.EncrypterUtils;

@Service
public class CurrentUserDetailsService implements UserDetailsService {
	
	private static final Logger log = LoggerFactory.getLogger(CurrentUserDetailsService.class);
	
	@Autowired
	private UserService userService;
	
    public CurrentUser loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.getUserEntityByEmail(email);
        
        if (user == null) {
        	log.error("Erro ao recuperar usuário com email {} do banco", email);
        	throw new UsernameNotFoundException(String.format("Usuário com email = %s não encontrado", email)); 
        }
        
        log.info("Usuário buscado com sucesso, retornando usuário com email {}", email);
        return new CurrentUser(user);
    }
    
	public void reauthenticateUser(UserFormUpdate userDTO) throws AuthenticationException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication == null) {
			throw new AuthenticationException("Erro, objeto de Authentication nulo.");
		}
		
		CurrentUser userDetails = (CurrentUser) authentication.getPrincipal();

		userDetails.getUser().setUsername(userDTO.getUsername());
		userDetails.getUser().setEmail(userDTO.getEmail());
		userDetails.getUser().setInstitution(userDTO.getInstitution());
		
		if (userDTO.getPassword() != null && !"".equals(userDTO.getPassword()) && 
			userDTO.getRepeatedPassword() != null && !"".equals(userDTO.getRepeatedPassword())) {
			
			userDetails.getUser().setPassword(EncrypterUtils.encryptPassword(userDTO.getPassword()));
		}
		
		Authentication newAuthentication = new UsernamePasswordAuthenticationToken(new CurrentUser(userDetails.getUser()), userDetails.getPassword(), userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(newAuthentication);
	}
    
}