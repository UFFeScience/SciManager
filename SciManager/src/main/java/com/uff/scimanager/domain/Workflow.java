package com.uff.scimanager.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.uff.scimanager.domain.dto.WorkflowDTO;
import com.uff.scimanager.domain.form.WorkflowForm;

@Entity
@Table(name = "WORKFLOW")
public class Workflow implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "workflow_id", sequenceName = "workflow_id")
	@GeneratedValue(generator = "workflow_id", strategy = GenerationType.AUTO)
	@Column(name = "ID_WORKFLOW", nullable = false)
	private Long workflowId;
	
	@Column(name = "NM_SLUG", updatable = false, length = 32)
	private String slug;
	
	@Column(name = "NM_NOME_WORKFLOW")
	private String workflowName;
	
	@Column(name = "NM_SWFMS")
	private String swfms;
	
	@Column(name = "NM_VERSAO")
	private String currentVersion = "1.0";
	
	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "ID_EXPERIMENTO_CIENTIFICO")
	private ScientificExperiment scientificExperiment;
	
	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "ID_PROJETO_CIENTIFICO")
	private ScientificProject scientificProject;
	
	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "ID_GRUPO")
	private UserGroup responsibleGroup;
	
	public Workflow() {}
	
	public static Workflow buildEmptyWorkflow() {
		return new Workflow();
	}

	public Workflow(WorkflowBuilder workflowBuilder) {
		this.slug = workflowBuilder.slug;
		this.workflowId = workflowBuilder.workflowId;
		this.workflowName = workflowBuilder.workflowName;
		this.swfms = workflowBuilder.swfms;
		this.currentVersion = workflowBuilder.currentVersion;
		this.scientificExperiment = workflowBuilder.scientificExperiment;
		this.scientificProject = workflowBuilder.scientificProject;
		this.responsibleGroup = workflowBuilder.responsibleGroup;
	}
	
	public static Workflow buildWorkflowFromWorkflowForm(WorkflowForm workflowForm) {
		if (workflowForm != null) {
			return Workflow.builder()
						   .workflowName(workflowForm.getWorkflowName())
						   .swfms(workflowForm.getSwfms()).build();
		}
		
		return Workflow.buildEmptyWorkflow();
	}
	
	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public Long getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(Long workflowId) {
		this.workflowId = workflowId;
	}

	public String getWorkflowName() {
		return workflowName;
	}

	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	public String getSwfms() {
		return swfms;
	}

	public void setSwfms(String swfms) {
		this.swfms = swfms;
	}
	
	public String getCurrentVersion() {
		return currentVersion;
	}

	public void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
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

	public UserGroup getResponsibleGroup() {
		return responsibleGroup;
	}

	public void setResponsibleGroup(UserGroup responsibleGroup) {
		this.responsibleGroup = responsibleGroup;
	}
	
	public WorkflowDTO buildWorkflowSimpleDTO() {
		return WorkflowDTO.builder().workflowId(workflowId)
								   .workflowName(workflowName).build();
	}
	
	public static WorkflowBuilder builder() {
		return new WorkflowBuilder();
	}
	
	public static class WorkflowBuilder {
		
		private String slug;
		private Long workflowId;
		private String workflowName;
		private String swfms;
		private String currentVersion = "1.0";
		private ScientificExperiment scientificExperiment;
		private ScientificProject scientificProject;
		private UserGroup responsibleGroup;
		
		public WorkflowBuilder slug(String slug) {
			this.slug = slug;
			return this;
		}
		
		public WorkflowBuilder workflowId(Long workflowId) {
			this.workflowId = workflowId;
			return this;
		}
		
		public WorkflowBuilder workflowName(String workflowName) {
			this.workflowName = workflowName;
			return this;
		}
		
		public WorkflowBuilder swfms(String swfms) {
			this.swfms = swfms;
			return this;
		}
		
		public WorkflowBuilder currentVersion(String currentVersion) {
			this.currentVersion = currentVersion;
			return this;
		}
		
		public WorkflowBuilder scientificExperiment(ScientificExperiment scientificExperiment) {
			this.scientificExperiment = scientificExperiment;
			return this;
		}
		
		public WorkflowBuilder scientificProject(ScientificProject scientificProject) {
			this.scientificProject = scientificProject;
			return this;
		}
		
		public WorkflowBuilder responsibleGroup(UserGroup responsibleGroup) {
			this.responsibleGroup = responsibleGroup;
			return this;
		}
		
		public Workflow build() {
			return new Workflow(this);
		}
	}

}