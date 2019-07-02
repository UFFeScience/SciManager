package com.uff.scimanager.domain;

import java.io.UnsupportedEncodingException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.uff.scimanager.util.EncrypterUtils;

@Entity
@Table(name = "DOCUMENTACAO")
public class Documentation {
	
	@Id
	@SequenceGenerator(name = "documentacao_id", sequenceName = "documentacao_id")
	@GeneratedValue(generator = "documentacao_id", strategy = GenerationType.AUTO)
	@Column(name = "ID_DOCUMENTACAO", nullable = false)
	private Long documentationId;
	
	@Lob
	@Column(name = "NM_HTML_DOCUMENTACAO")
	private byte[] htmlDocumentation;
	
	@OneToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "ID_WORKFLOW")
	private Workflow workflow;
	
	@OneToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "ID_EXPERIMENTO_CIENTIFICO")
	private ScientificExperiment scientificExperiment;
	
	@OneToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "ID_PROJETO_CIENTIFICO")
	private ScientificProject scientificProject;
	
	public Documentation() {}

	public Documentation(DocumentationBuilder documentationBuilder) {
		this.documentationId = documentationBuilder.documentationId;
		this.htmlDocumentation = documentationBuilder.htmlDocumentation;
		this.workflow = documentationBuilder.workflow;
		this.scientificExperiment = documentationBuilder.scientificExperiment;
		this.scientificProject = documentationBuilder.scientificProject;
	}

	public Long getDocumentationId() {
		return documentationId;
	}

	public void setDocumentationId(Long documentationId) {
		this.documentationId = documentationId;
	}

	public byte[] getHtmlDocumentation() {
		return htmlDocumentation;
	}
	
	public String getHtmlDocumentationAsString() {
		try {
			return new String(htmlDocumentation, EncrypterUtils.DEFAULT_ENCODING);
		} 
		catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public void setHtmlDocumentation(byte[] htmlDocumentation) {
		this.htmlDocumentation = htmlDocumentation;
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}
	
	public ScientificExperiment getScientificExperiment() {
		return scientificExperiment;
	}

	public void setScientificExperiment(ScientificExperiment scientificExperiment) {
		this.scientificExperiment = scientificExperiment;
	}

	public ScientificProject getScientificProject() {
		return scientificProject;
	}

	public void setScientificProject(ScientificProject scientificProject) {
		this.scientificProject = scientificProject;
	}

	public static DocumentationBuilder builder() {
		return new DocumentationBuilder();
	}
	
	public static class DocumentationBuilder {
		
		private Long documentationId;
		private byte[] htmlDocumentation;
		private Workflow workflow;
		private ScientificExperiment scientificExperiment;
		private ScientificProject scientificProject;
		
		public DocumentationBuilder documentationId(Long documentationId) {
			this.documentationId = documentationId;
			return this;
		}
		
		public DocumentationBuilder htmlDocumentation(byte[] htmlDocumentation) {
			this.htmlDocumentation = htmlDocumentation;
			return this;
		}
		
		public DocumentationBuilder workflow(Workflow workflow) {
			this.workflow = workflow;
			return this;
		}
		
		public DocumentationBuilder scientificExperiment(ScientificExperiment scientificExperiment) {
			this.scientificExperiment = scientificExperiment;
			return this;
		}
		
		public DocumentationBuilder scientificProject(ScientificProject scientificProject) {
			this.scientificProject = scientificProject;
			return this;
		}
		
		public Documentation build() {
			return new Documentation(this);
		}
	}
	
}