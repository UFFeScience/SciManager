package com.uff.scimanager.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name = "GRAFO_WORKFLOW")
public class WorkflowGraph {
	
	@Id
	@SequenceGenerator(name = "grafo_workflow_id", sequenceName = "grafo_workflow_id")
	@GeneratedValue(generator = "grafo_workflow_id", strategy = GenerationType.AUTO)
	@Column(name = "ID_GRAFO_WORKFLOW", nullable = false)
	private Long workflowGraphId;
	
	@Column(name = "NM_CODIGO_GRAFO")
	private String graphCode;
	
	@Column(name = "BO_GRAFO_DETALHADO")
	private Boolean isDetailed = Boolean.FALSE;
	
	@OneToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "ID_WORKFLOW")
	private Workflow workflow;
	
	public WorkflowGraph() {}

	public WorkflowGraph(WorkflowGraphBuilder workflowGraphBuilder) {
		this.workflowGraphId = workflowGraphBuilder.workflowGraphId;
		this.graphCode = workflowGraphBuilder.graphCode;
		this.workflow = workflowGraphBuilder.workflow;
		this.isDetailed = workflowGraphBuilder.isDetailed;
	}

	public Long getWorkflowGraphId() {
		return workflowGraphId;
	}

	public void setWorkflowGraphId(Long workflowGraphId) {
		this.workflowGraphId = workflowGraphId;
	}

	public String getGraphCode() {
		return graphCode;
	}

	public void setGraphCode(String graphCode) {
		this.graphCode = graphCode;
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}
	
	public Boolean getIsDetailed() {
		return isDetailed;
	}

	public void setIsDetailed(Boolean isDetailed) {
		this.isDetailed = isDetailed;
	}

	public static WorkflowGraphBuilder builder() {
		return new WorkflowGraphBuilder();
	}
	
	public static class WorkflowGraphBuilder {
		
		private Long workflowGraphId;
		private String graphCode;
		private Workflow workflow;
		private Boolean isDetailed;
		
		public WorkflowGraphBuilder workflowGraphId(Long workflowGraphId) {
			this.workflowGraphId = workflowGraphId;
			return this;
		}
		
		public WorkflowGraphBuilder workflowGraphId(String graphCode) {
			this.graphCode = graphCode;
			return this;
		}
		
		public WorkflowGraphBuilder isDetailed(Boolean isDetailed) {
			this.isDetailed = isDetailed;
			return this;
		}
		
		public WorkflowGraphBuilder workflow(Workflow workflow) {
			this.workflow = workflow;
			return this;
		}
		
		public WorkflowGraph build() {
			return new WorkflowGraph(this);
		}
	}
	
}