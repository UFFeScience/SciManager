package com.uff.scimanager.domain.form.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.uff.scimanager.domain.ScientificExperiment;
import com.uff.scimanager.domain.form.ScientificExperimentForm;
import com.uff.scimanager.service.scientific.experiment.ScientificExperimentService;

@Component
public class ScientificExperimentFormValidator implements Validator {
	
	@Autowired
	ScientificExperimentService scientificExperimentService;
	
	@Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(ScientificExperimentForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
    	ScientificExperimentForm form = (ScientificExperimentForm) target;
        validateExistingExperimentName(errors, form);
    }

	private void validateExistingExperimentName(Errors errors, ScientificExperimentForm form) {
		ScientificExperiment scientificExperimentFound = scientificExperimentService.getByExperimentName(form.getExperimentName());
		
		if (scientificExperimentFound != null) {
			errors.reject("scientific_experiment.exists", "Já existe um experimento científico com esse nome.");
		}
	}
	
}