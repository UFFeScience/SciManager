package com.uff.scimanager.domain.form.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.uff.scimanager.domain.form.ScientificProjectFrom;
import com.uff.scimanager.service.scientific.project.ScientificProjectService;

@Component
public class ScientificProjectFormValidator implements Validator {
	
	@Autowired
	ScientificProjectService scientificProjectService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(ScientificProjectFrom.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
    	ScientificProjectFrom form = (ScientificProjectFrom) target;
        validateRepeatedProjectName(errors, form);
    }

    private void validateRepeatedProjectName(Errors errors, ScientificProjectFrom form) {
    	if (scientificProjectService.getScientificProjectByProjectName(form.getProjectName()) != null) {
    		errors.reject("scientific_project.exists", "Já existe um projeto científico com esse nome.");
    	}
	}
	
}