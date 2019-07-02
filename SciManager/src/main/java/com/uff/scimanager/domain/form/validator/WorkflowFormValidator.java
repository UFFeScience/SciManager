package com.uff.scimanager.domain.form.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.uff.scimanager.domain.Workflow;
import com.uff.scimanager.domain.form.WorkflowForm;
import com.uff.scimanager.service.workflow.WorkflowService;

@Component
public class WorkflowFormValidator implements Validator {
	
	@Autowired
	WorkflowService workflowService;
	
	@Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(WorkflowForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
    	WorkflowForm form = (WorkflowForm) target;
        validateExistingWorkflowName(errors, form);
    }

	private void validateExistingWorkflowName(Errors errors, WorkflowForm form) {
		Workflow workflowFound = workflowService.getByWorkflowName(form.getWorkflowName());
		
		if (workflowFound != null) {
			errors.reject("workflow.exists", "Já existe um workflow com esse nome.");
		}
	}
	
}