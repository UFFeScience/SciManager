package com.uff.scimanager.domain.form.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.uff.scimanager.domain.form.UserForm;
import com.uff.scimanager.service.user.UserService;

@Component
public class UserFormValidator implements Validator {

	@Autowired
	UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(UserForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserForm form = (UserForm) target;

        validatePasswords(errors, form);
        validateEmailRepeated(errors, form);
    }

    private void validatePasswords(Errors errors, UserForm form) {
        if (!form.getPassword().equals(form.getRepeatedPassword())) {
            errors.reject("password.no_match", "As senhas tem que ser iguais.");
        }
    }
    
    private void validateEmailRepeated(Errors errors, UserForm form) {
        if (userService.getUserByEmail(form.getEmail()) != null) {
            errors.reject("email.exists", "Já existe um usuário com esse email.");
        }
    }
	
}