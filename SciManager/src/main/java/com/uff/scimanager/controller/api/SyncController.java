package com.uff.scimanager.controller.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.uff.scimanager.domain.ScientificProject;
import com.uff.scimanager.domain.User;
import com.uff.scimanager.service.scientific.project.ScientificProjectService;
import com.uff.scimanager.service.user.UserService;

@RestController("/api/phenomanager/sync")
public class SyncController {
	
	private static final Logger log = LoggerFactory.getLogger(SyncController.class);
	
	@Autowired
    private ScientificProjectService scientificProjectService;
	
	@Autowired
    private UserService userService;
    
	@RequestMapping(value = "/scientific-project", method = RequestMethod.POST)
    public ResponseEntity<Object> syncScientificProject(@RequestHeader("Authorization") String authorization,
    		@RequestBody ScientificProject scientificProject) {
    	
		log.info("Sincronizando projetos entre SciManager e PhenoManager");
		try {
			scientificProjectService.syncScientificProject(scientificProject);
			return new ResponseEntity<>(HttpStatus.OK);
			
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	
	@RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity<Object> syncUser(@RequestHeader("Authorization") String authorization,
    		@RequestBody User user) {
    	
		log.info("Sincronizando projetos entre SciManager e PhenoManager");
		try {
			userService.syncUser(user);
			return new ResponseEntity<>(HttpStatus.OK);
			
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
    
}