package com.uff.scimanager.domain;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name = "ARQUIVO_MODELO")
public class ModelFile {

	@Id
	@SequenceGenerator(name = "arquivo_modelo_id", sequenceName = "arquivo_modelo_id")
	@GeneratedValue(generator = "arquivo_modelo_id", strategy = GenerationType.AUTO)
	@Column(name = "ID_ARQUIVO_MODELO", nullable = false)
	private Long modelFileId;
	
	@Column(name = "DT_DATA_SUBMISSAO")
	private Calendar submissionDate;
	
	@Lob
	@Column(name = "NM_CONTEUDO_ARQUIVO")
	private byte[] fileContent;
	
	@Column(name = "NM_EXECTAG")
	private String execTag;
	
	@Column(name = "BO_ARQUIVO_ATUAL")
	private Boolean isCurrentFile;
	
	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "ID_USUARIO")
	private User uploader;
	
	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "ID_WORKFLOW")
	private Workflow workflow;
	
	public ModelFile() {}
	
	public ModelFile(ModelFileBuilder modelFileBuilder) {
		this.modelFileId = modelFileBuilder.modelFileId;
		this.submissionDate = modelFileBuilder.submissionDate;
		this.workflow = modelFileBuilder.workflow;
		this.isCurrentFile = modelFileBuilder.isCurrentFile;
		this.fileContent = modelFileBuilder.fileContent;
		this.execTag = modelFileBuilder.execTag;
		this.uploader = modelFileBuilder.uploader;
	}

	public Long getModelFileId() {
		return modelFileId;
	}

	public void setModelFileId(Long modelFileId) {
		this.modelFileId = modelFileId;
	}

	public Calendar getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(Calendar submissionDate) {
		this.submissionDate = submissionDate;
	}
	
	public Boolean getIsCurrentFile() {
		return isCurrentFile;
	}

	public void setIsCurrentFile(Boolean isCurrentFile) {
		this.isCurrentFile = isCurrentFile;
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}
	
	public User getUploader() {
		return uploader;
	}

	public void setUploader(User uploader) {
		this.uploader = uploader;
	}

	public byte[] getFileContent() {
		return fileContent;
	}

	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}
	
	public String getFileContenAsString() {
		return new String(fileContent, StandardCharsets.UTF_8);
	}
	
	public String getExecTag() {
		return execTag;
	}

	public void setExecTag(String execTag) {
		this.execTag = execTag;
	}

	public static ModelFileBuilder builder() {
		return new ModelFileBuilder();
	}

	public static class ModelFileBuilder {
		
        private byte[] fileContent;
        private String execTag;
        private Calendar submissionDate;
        private Boolean isCurrentFile;
        private Workflow workflow;
        private Long modelFileId;
        private User uploader;

        public ModelFileBuilder fileContent(byte[] fileContent) {
            this.fileContent = fileContent;
            return this;
        }
        
        public ModelFileBuilder execTag(String execTag) {
            this.execTag = execTag;
            return this;
        }

        public ModelFileBuilder submissionDate(Calendar submissionDate) {
            this.submissionDate = submissionDate;
            return this;
        }

        public ModelFileBuilder isCurrentFile(Boolean isCurrentFile) {
            this.isCurrentFile = isCurrentFile;
            return this;
        }

        public ModelFileBuilder workflow(Workflow workflow) {
            this.workflow = workflow;
            return this;
        }
        
        public ModelFileBuilder uploader(User uploader) {
            this.uploader = uploader;
            return this;
        }
	
        public ModelFileBuilder modelFileId(Long modelFileId) {
            this.modelFileId = modelFileId;
            return this;
        }

        public ModelFile build() {
            return new ModelFile(this);
        }
    }
	
}