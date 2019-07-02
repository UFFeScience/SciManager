package com.uff.scimanager.domain.form.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.uff.scimanager.domain.form.TagForm;
import com.uff.scimanager.service.tag.TagService;

@Component
public class TagFormValidator implements Validator {
	
	@Autowired
	TagService tagService;
	
	@Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(TagForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
    	TagForm form = (TagForm) target;
        validateTagNameRepeated(errors, form);
    }
	
	private void validateTagNameRepeated(Errors errors, TagForm form) {
        if (tagService.getTagByTagName(form.getTagName()) != null) {
            errors.reject("tag.exists", "Já existe uma tag com esse nome.");
        }
    }
	
}