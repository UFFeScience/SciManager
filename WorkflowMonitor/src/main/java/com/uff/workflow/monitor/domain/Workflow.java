package com.uff.workflow.monitor.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

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
	
	@Column(name = "ID_GRUPO")
	private Long userGroupId;
	
	public Workflow() {}
	
	public static Workflow buildEmptyWorkflow() {
		return new Workflow();
	}

	public Workflow(WorkflowBuilder workflowBuilder) {
		this.workflowId = workflowBuilder.workflowId;
		this.workflowName = workflowBuilder.workflowName;
		this.userGroupId = workflowBuilder.userGroupId;
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
	
	public Long getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(Long userGroupId) {
		this.userGroupId = userGroupId;
	}

	public static WorkflowBuilder builder() {
		return new WorkflowBuilder();
	}
	
	public static class WorkflowBuilder {
		
		private Long workflowId;
		private String workflowName;
		private Long userGroupId;

		public WorkflowBuilder workflowId(Long workflowId) {
			this.workflowId = workflowId;
			return this;
		}
		
		public WorkflowBuilder workflowName(String workflowName) {
			this.workflowName = workflowName;
			return this;
		}
		
		public WorkflowBuilder userGroupId(Long userGroupId) {
			this.userGroupId = userGroupId;
			return this;
		}
		
		public Workflow build() {
			return new Workflow(this);
		}
	}

}