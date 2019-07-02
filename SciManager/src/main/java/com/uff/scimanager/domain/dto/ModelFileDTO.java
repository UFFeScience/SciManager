package com.uff.scimanager.domain.dto;

import java.util.ArrayList;
import java.util.List;

import com.uff.scimanager.domain.ModelFile;
import com.uff.scimanager.util.CalendarDateUtils;

public class ModelFileDTO {
	
	private Long modelFileId;
	private String submissionDate;
	private String fileContent;
	private String execTag;
	private Boolean isCurrentFile;
	private String uploader;
	
	public ModelFileDTO() {}
	
	public ModelFileDTO(ModelFileDTOBuilder modelFileDTOBuilder) {
		this.modelFileId = modelFileDTOBuilder.modelFileId;
		this.submissionDate = modelFileDTOBuilder.submissionDate;
		this.fileContent = modelFileDTOBuilder.fileContent;
		this.execTag = modelFileDTOBuilder.execTag;
		this.isCurrentFile = modelFileDTOBuilder.isCurrentFile;
		this.uploader = modelFileDTOBuilder.uploader;
	}
	
	public static ModelFileDTO buildDTOByEntity(ModelFile modelFile) {
		if (modelFile == null) {
			return ModelFileDTO.buildEmptyModelFileDTO();
		}
		
		return ModelFileDTO.builder()
				          .modelFileId(modelFile.getModelFileId())
	                      .submissionDate(CalendarDateUtils.formatDate(modelFile.getSubmissionDate()))
	                      .fileContent(modelFile.getFileContenAsString())
	                      .execTag(modelFile.getExecTag())
	                      .uploader(modelFile.getUploader().getEmail())
	                      .isCurrentFile(modelFile.getIsCurrentFile()).build();
	}
	
	public static ModelFileDTO buildEmptyModelFileDTO() {
		return new ModelFileDTO();
	}
	
	public static List<ModelFileDTO> convertEntityListToDTOList(List<ModelFile> modelFiles) {
		List<ModelFileDTO> modelFilesDTO = new ArrayList<ModelFileDTO>();
		
		if (modelFiles != null) {
			for (ModelFile modelFile : modelFiles) {
				modelFilesDTO.add(ModelFileDTO.buildDTOByEntity(modelFile));
			}
		}
		
		return modelFilesDTO;
	}

	public Long getModelFileId() {
		return modelFileId;
	}

	public void setModelFileId(Long modelFileId) {
		this.modelFileId = modelFileId;
	}

	public String getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(String submissionDate) {
		this.submissionDate = submissionDate;
	}

	public String getFileContent() {
		return fileContent;
	}

	public void setFileContent(String fileContent) {
		this.fileContent = fileContent;
	}
	
	public String getExecTag() {
		return execTag;
	}

	public void setExecTag(String execTag) {
		this.execTag = execTag;
	}

	public String getUploader() {
		return uploader;
	}

	public void setUploader(String uploader) {
		this.uploader = uploader;
	}

	public Boolean getIsCurrentFile() {
		return isCurrentFile;
	}

	public void setIsCurrentFile(Boolean isCurrentFile) {
		this.isCurrentFile = isCurrentFile;
	}

	public static ModelFileDTOBuilder builder() {
		return new ModelFileDTOBuilder();
	}
	
	public static class ModelFileDTOBuilder {
	
		private Long modelFileId;
		private String submissionDate;
		private String fileContent;
		private String execTag;
		private Boolean isCurrentFile;
		private String uploader;
		
		public ModelFileDTOBuilder modelFileId(Long modelFileId) {
			this.modelFileId = modelFileId;
			return this;
		}
		
		public ModelFileDTOBuilder submissionDate(String submissionDate) {
			this.submissionDate = submissionDate;
			return this;
		}
		
		public ModelFileDTOBuilder fileContent(String fileContent) {
			this.fileContent = fileContent;
			return this;
		}
		
		public ModelFileDTOBuilder execTag(String execTag) {
			this.execTag = execTag;
			return this;
		}
		
		public ModelFileDTOBuilder uploader(String uploader) {
			this.uploader = uploader;
			return this;
		}
		
		public ModelFileDTOBuilder isCurrentFile(Boolean isCurrentFile) {
			this.isCurrentFile = isCurrentFile;
			return this;
		}
		
		public ModelFileDTO build() {
			return new ModelFileDTO(this);
		}
	}
	
}