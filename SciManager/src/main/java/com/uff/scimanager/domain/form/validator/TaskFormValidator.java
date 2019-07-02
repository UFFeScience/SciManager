package com.uff.scimanager.domain.form.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.uff.scimanager.domain.form.TaskForm;

@Component
public class TaskFormValidator implements Validator {
	
	@Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(TaskForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {}

}