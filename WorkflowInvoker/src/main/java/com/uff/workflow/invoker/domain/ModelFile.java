package com.uff.workflow.invoker.domain;

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
	
	@Column(name = "BO_ARQUIVO_ATUAL")
	private Boolean isCurrentFile;
	
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
	
	public byte[] getFileContent() {
		return fileContent;
	}

	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}
	
	public String getFileContenAsString() {
		return new String(fileContent, StandardCharsets.UTF_8);
	}

	public static ModelFileBuilder builder() {
		return new ModelFileBuilder();
	}

	public static class ModelFileBuilder {
		
        private byte[] fileContent;
        private Calendar submissionDate;
        private Boolean isCurrentFile;
        private Workflow workflow;
        private Long modelFileId;

        public ModelFileBuilder fileContent(byte[] fileContent) {
            this.fileContent = fileContent;
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
	
        public ModelFileBuilder modelFileId(Long modelFileId) {
            this.modelFileId = modelFileId;
            return this;
        }

        public ModelFile build() {
            return new ModelFile(this);
        }
    }
	
}