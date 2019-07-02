package com.uff.scimanager.domain.form.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.uff.scimanager.domain.form.PhaseForm;
import com.uff.scimanager.service.phase.PhaseService;

@Component
public class PhaseFormValidator implements Validator {
	
	@Autowired
	PhaseService phaseService;
	
	@Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(PhaseForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
    	PhaseForm form = (PhaseForm) target;
    	validateRepeatedPhaseName(errors, form);
    }

	private void validateRepeatedPhaseName(Errors errors, PhaseForm form) {
		if (phaseService.validateExistingPhaseName(form.getProjectId(), form.getPhaseName())) {
			errors.reject("phase.exists", "Já existe outra fase com esse nome.");
		}
	}
	
}