package com.uff.scimanager.domain.form.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.uff.scimanager.domain.form.UserGroupForm;
import com.uff.scimanager.service.user.group.UserGroupService;

@Component
public class UserGroupFormValidator implements Validator {
	
	@Autowired
	UserGroupService userGroupService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(UserGroupForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
    	UserGroupForm form = (UserGroupForm) target;
        validateRepeatedGroupName(errors, form);
    }

    private void validateRepeatedGroupName(Errors errors, UserGroupForm form) {
    	if (userGroupService.getUserGroupEntityByGroupName(form.getGroupName()) != null) {
    		errors.reject("user_group.exists", "Já existe um grupo de usuários com esse nome.");
    	}
	}

}