package com.uff.workflow.invoker.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name = "WORKFLOW")
public class Workflow {
	
	@Id
	@SequenceGenerator(name = "workflow_id", sequenceName = "workflow_id")
	@GeneratedValue(generator = "workflow_id", strategy = GenerationType.AUTO)
	@Column(name = "ID_WORKFLOW", nullable = false)
	private Long workflowId;
	
	@Column(name = "NM_NOME_WORKFLOW")
	private String workflowName;
	
	@Column(name = "NM_SWFMS")
	private String swfms;
	
	@Column(name = "NM_VERSAO")
	private String currentVersion = "1.0";
	
	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "ID_GRUPO")
	private UserGroup responsibleGroup;
	
	@Transient
	private Boolean isEditablebyUser = Boolean.FALSE;
	
	public Workflow() {}
	
	public static Workflow buildEmptyWorkflow() {
		return new Workflow();
	}

	public Workflow(WorkflowBuilder workflowBuilder) {
		this.workflowId = workflowBuilder.workflowId;
		this.workflowName = workflowBuilder.workflowName;
		this.swfms = workflowBuilder.swfms;
		this.currentVersion = workflowBuilder.currentVersion;
		this.responsibleGroup = workflowBuilder.responsibleGroup;
		this.isEditablebyUser = workflowBuilder.isEditablebyUser;
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
	
	public UserGroup getResponsibleGroup() {
		return responsibleGroup;
	}

	public void setResponsibleGroup(UserGroup responsibleGroup) {
		this.responsibleGroup = responsibleGroup;
	}
	
	public Boolean getIsEditablebyUser() {
		return isEditablebyUser;
	}

	public void setIsEditablebyUser(Boolean isEditablebyUser) {
		this.isEditablebyUser = isEditablebyUser;
	}

	public static WorkflowBuilder builder() {
		return new WorkflowBuilder();
	}
	
	public static class WorkflowBuilder {
		
		private Long workflowId;
		private String workflowName;
		private String swfms;
		private String currentVersion = "1.0";
		private UserGroup responsibleGroup;
		private Boolean isEditablebyUser = Boolean.FALSE;

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
		
		public WorkflowBuilder responsibleGroup(UserGroup responsibleGroup) {
			this.responsibleGroup = responsibleGroup;
			return this;
		}
		
		public WorkflowBuilder isEditablebyUser(Boolean isEditablebyUser) {
			this.isEditablebyUser = isEditablebyUser;
			return this;
		}
		
		public Workflow build() {
			return new Workflow(this);
		}
	}

}