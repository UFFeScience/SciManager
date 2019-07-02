package com.uff.scimanager.domain.dto;

import java.util.ArrayList;
import java.util.List;

import com.uff.scimanager.domain.Tag;

public class TagDTO {
	
	private Long tagId;
	private String tagName;
	
	public TagDTO() {}
	
	public TagDTO(TagDTOBuilder tagDTOBuilder) {
		this.tagId = tagDTOBuilder.tagId;
		this.tagName = tagDTOBuilder.tagName;
	}
	
	public static TagDTO buildDTOByEntity(Tag tag) {
		if (tag == null) {
			return TagDTO.buildEmptyTagDTO();
		}
		
		return TagDTO.builder()
				    .tagId(tag.getTagId())
				    .tagName(tag.getTagName()).build();
	}
	
	private static TagDTO buildEmptyTagDTO() {
		return new TagDTO();
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
	
	public void settagName(String tagName) {
		this.tagName = tagName;
	}
	
	public static TagDTOBuilder builder() {
		return new TagDTOBuilder();
	}
	
	public static class TagDTOBuilder {
	
		private Long tagId;
		private String tagName;
		
		public TagDTOBuilder tagId(Long tagId) {
			this.tagId = tagId;
			return this;
		}
		
		public TagDTOBuilder tagName(String tagName) {
			this.tagName = tagName;
			return this;
		}
		
		public TagDTO build() {
			return new TagDTO(this);
		}
	}

	public static List<TagDTO> convertEntityListToDTOList(List<Tag> tags) {
		List<TagDTO> tagsDTO = new ArrayList<TagDTO>();
		
		if (tags != null) {
			for (Tag tag : tags) {
				tagsDTO.add(TagDTO.buildDTOByEntity(tag));
			}
		}
		
		return tagsDTO;
	}

}