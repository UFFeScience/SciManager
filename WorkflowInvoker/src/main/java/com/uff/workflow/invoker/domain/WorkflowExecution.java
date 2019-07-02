package com.uff.workflow.invoker.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name = "EXECUCAO_WORKFLOW")
public class WorkflowExecution {
	
	@Id
	@SequenceGenerator(name = "execucao_workflow_id", sequenceName = "execucao_workflow_id")
	@GeneratedValue(generator = "execucao_workflow_id", strategy = GenerationType.AUTO)
	@Column(name = "ID_EXECUCAO_WORKFLOW", nullable = false)
	private Long workflowExecutionId;
	
	@Column(name = "NM_SWFMS")
	private String swfms;
	
	@Column(name = "NM_WORKFLOW_VERSAO")
	private String workflowVersion;
	
	@Column(name = "NM_EXECTAG")
	private String execTag;
	
	@Column(name = "DT_DATA_EXECUCAO")
	private Calendar executionDate;
	
	@Column(name = "NM_STATUS")
	@Enumerated(EnumType.STRING)
	private WorkflowExecutionStatus executionStatus;
	
	@Column(name = "NM_LOG_EXECUCAO")
	private String executionLog;
	
	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "ID_WORKFLOW")
	private Workflow workflow;
	
	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "ID_ARQUIVO_MODELO")
	private ModelFile modelFile;
	
	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "ID_USUARIO")
	private User userAgent;
	
	public WorkflowExecution() {}
	
	public WorkflowExecution(WorkflowExecutionBuilder workflowExecutionBuilder) {
		this.workflowExecutionId = workflowExecutionBuilder.workflowExecutionId;
		this.swfms = workflowExecutionBuilder.swfms;
		this.workflowVersion = workflowExecutionBuilder.currentVersion;
		this.execTag = workflowExecutionBuilder.execTag;
		this.executionDate = workflowExecutionBuilder.executionDate;
		this.executionStatus = workflowExecutionBuilder.executionStatus;
		this.executionLog = workflowExecutionBuilder.executionLog;
		this.workflow = workflowExecutionBuilder.workflow;
		this.modelFile = workflowExecutionBuilder.modelFile;
		this.userAgent = workflowExecutionBuilder.user;
	}
	
	public Long getWorkflowExecutionId() {
		return workflowExecutionId;
	}

	public void setWorkflowExecutionId(Long workflowExecutionId) {
		this.workflowExecutionId = workflowExecutionId;
	}

	public String getSwfms() {
		return swfms;
	}

	public void setSwfms(String swfms) {
		this.swfms = swfms;
	}

	public String getWorkflowVersion() {
		return workflowVersion;
	}

	public void setWorkflowVersion(String workflowVersion) {
		this.workflowVersion = workflowVersion;
	}

	public String getExecTag() {
		return execTag;
	}

	public void setExecTag(String execTag) {
		this.execTag = execTag;
	}
	
	public Calendar getExecutionDate() {
		return executionDate;
	}

	public void setExecutionDate(Calendar executionDate) {
		this.executionDate = executionDate;
	}

	public WorkflowExecutionStatus getExecutionStatus() {
		return executionStatus;
	}

	public void setExecutionStatus(WorkflowExecutionStatus executionStatus) {
		this.executionStatus = executionStatus;
	}
	
	public String getExecutionLog() {
		return executionLog;
	}

	public void setExecutionLog(String executionLog) {
		this.executionLog = executionLog;
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

	public ModelFile getModelFile() {
		return modelFile;
	}

	public void setModelFile(ModelFile modelFile) {
		this.modelFile = modelFile;
	}

	public User getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(User userAgent) {
		this.userAgent = userAgent;
	}

	public static WorkflowExecutionBuilder builder() {
		return new WorkflowExecutionBuilder();
	}
	
	public static class WorkflowExecutionBuilder {
		
		private Long workflowExecutionId;
		private String swfms;
		private String currentVersion;
		private String execTag;
		private Calendar executionDate;
		private WorkflowExecutionStatus executionStatus;
		private String executionLog;
		private Workflow workflow;
		private ModelFile modelFile;
		private User user;
		
		public WorkflowExecutionBuilder workflowExecutionId(Long workflowExecutionId) {
			this.workflowExecutionId = workflowExecutionId;
			return this;
		}
		
		public WorkflowExecutionBuilder swfms(String swfms) {
			this.swfms = swfms;
			return this;
		}
		
		public WorkflowExecutionBuilder workflowVersion(String workflowVersion) {
			this.currentVersion = workflowVersion;
			return this;
		}
		
		public WorkflowExecutionBuilder execTag(String execTag) {
			this.execTag = execTag;
			return this;
		}
		
		public WorkflowExecutionBuilder executionDate(Calendar executionDate) {
			this.executionDate = executionDate;
			return this;
		}
		
		public WorkflowExecutionBuilder executionStatus(WorkflowExecutionStatus executionStatus) {
			this.executionStatus = executionStatus;
			return this;
		}
		
		public WorkflowExecutionBuilder executionLog(String executionLog) {
			this.executionLog = executionLog;
			return this;
		}
		
		public WorkflowExecutionBuilder workflow(Workflow workflow) {
			this.workflow = workflow;
			return this;
		}
		
		public WorkflowExecutionBuilder modelFile(ModelFile modelFile) {
			this.modelFile = modelFile;
			return this;
		}
		
		public WorkflowExecutionBuilder userAgent(User userAgent) {
			this.user = userAgent;
			return this;
		}
		
		public WorkflowExecution build() {
			return new WorkflowExecution(this);
		}
	}

}