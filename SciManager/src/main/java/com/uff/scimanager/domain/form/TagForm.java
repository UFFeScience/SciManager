package com.uff.scimanager.domain.form;

import org.hibernate.validator.constraints.NotEmpty;

import com.uff.scimanager.domain.Tag;

public class TagForm {
	
	@NotEmpty(message = "O Nome da tag não pode ser vazio.")
	private String tagName;
	
	public TagForm() {}
	
	public TagForm(Tag tag) {
		if (tag != null) {
			this.tagName = tag.getTagName();
		}
	}
	
	public static TagForm buildEmptyTagForm() {
		return new TagForm();
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	
}