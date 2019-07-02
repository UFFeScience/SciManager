package com.uff.scimanager.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.uff.scimanager.domain.form.TagForm;

@Entity
@Table(name = "TAG")
public class Tag {
	
	@Id
	@SequenceGenerator(name = "tag_id", sequenceName = "tag_id")
	@GeneratedValue(generator = "tag_id", strategy = GenerationType.AUTO)
	@Column(name = "ID_TAG", nullable = false)
	private Long tagId;
	
	@Column(name = "NM_NOME_TAG")
	private String tagName;
	
	public Tag() {}

	public Tag(TagBuilder tagBuilder) {
		this.tagId = tagBuilder.tagId;
		this.tagName = tagBuilder.tagName;
	}
	
	public static Tag buildEmptyTag() {
		return new Tag();
	}
	
	public static Tag buildTagFromTagForm(TagForm tagForm) {
		if (tagForm != null) {
			return Tag.builder()
					  .tagName(tagForm.getTagName()).build();
		}
		
		return Tag.buildEmptyTag();
	}

	public Long getTagId() {
		return tagId;
	}

	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public static TagBuilder builder() {
		return new TagBuilder();
	}
	
	public static class TagBuilder {
		
		private Long tagId;
		private String tagName;
		
		public TagBuilder tagId(Long tagId) {
			this.tagId = tagId;
			return this;
		}
		
		public TagBuilder tagName(String tagName) {
			this.tagName = tagName;
			return this;
		}
		
		public Tag build() {
			return new Tag(this);
		}
	}
	
}