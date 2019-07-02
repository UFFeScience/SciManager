package com.uff.scimanager.domain.swfms.scicumulus;

import java.util.Date;

public class WorkflowExecutionMetadata {
	
	private String execTag;
	private String tag;
	private String workspace;
	private String status;
	private Date startTime;
	private Date endTime;
	private Integer processors;
	private String address;
	private String machineType;
	private String hostname;
	private Double financialCost;
	private Integer failureTries;
	
	public WorkflowExecutionMetadata() {}
	
	public WorkflowExecutionMetadata(WorkflowExecutionMetadataBuilder workflowExecutionMetadataBuilder) {
		this.execTag = workflowExecutionMetadataBuilder.execTag;
		this.tag = workflowExecutionMetadataBuilder.tag;
		this.workspace = workflowExecutionMetadataBuilder.workspace;
		this.status = workflowExecutionMetadataBuilder.status;
		this.startTime = workflowExecutionMetadataBuilder.startTime;
		this.status = workflowExecutionMetadataBuilder.status;
		this.endTime = workflowExecutionMetadataBuilder.endTime;
		this.processors = workflowExecutionMetadataBuilder.processors;
		this.address = workflowExecutionMetadataBuilder.address;
		this.machineType = workflowExecutionMetadataBuilder.machineType;
		this.hostname = workflowExecutionMetadataBuilder.hostname;
		this.financialCost = workflowExecutionMetadataBuilder.financialCost;
		this.failureTries = workflowExecutionMetadataBuilder.failureTries;
	}
	
	public String getExecTag() {
		return execTag;
	}

	public void setExecTag(String execTag) {
		this.execTag = execTag;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getWorkspace() {
		return workspace;
	}

	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}

	public Date getStartTime() {
		return startTime;
	}
	
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}
	
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Integer getProcessors() {
		return processors;
	}

	public void setProcessors(Integer processors) {
		this.processors = processors;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMachineType() {
		return machineType;
	}

	public void setMachineType(String machineType) {
		this.machineType = machineType;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public Double getFinancialCost() {
		return financialCost;
	}
	
	public void setFinancialCost(Double financialCost) {
		this.financialCost = financialCost;
	}

	public Integer getFailureTries() {
		return failureTries;
	}

	public void setFailureTries(Integer failureTries) {
		this.failureTries = failureTries;
	}

	public static WorkflowExecutionMetadataBuilder builder() {
		return new WorkflowExecutionMetadataBuilder();
	}

	public static class WorkflowExecutionMetadataBuilder {
		
		private String execTag;
		private String tag;
		private String workspace;
		private String status;
		private Date startTime;
		private Date endTime;
		private Integer processors;
		private String address;
		private String machineType;
		private String hostname;
		private Double financialCost;
		private Integer failureTries;
		
		public WorkflowExecutionMetadataBuilder execTag(String execTag) {
            this.execTag = execTag;
            return this;
        }
		
		public WorkflowExecutionMetadataBuilder tag(String tag) {
            this.tag = tag;
            return this;
        }
		
		public WorkflowExecutionMetadataBuilder workspace(String workspace) {
            this.workspace = workspace;
            return this;
        }
		
		public WorkflowExecutionMetadataBuilder status(String status) {
            this.status = status;
            return this;
        }
		
		public WorkflowExecutionMetadataBuilder startTime(Date startTime) {
            this.startTime = startTime;
            return this;
        }
		
		public WorkflowExecutionMetadataBuilder endTime(Date endTime) {
            this.endTime = endTime;
            return this;
        }
		
		public WorkflowExecutionMetadataBuilder processors(Integer processors) {
            this.processors = processors;
            return this;
        }
		
		public WorkflowExecutionMetadataBuilder address(String address) {
            this.address = address;
            return this;
        }
		
		public WorkflowExecutionMetadataBuilder machineType(String machineType) {
            this.machineType = machineType;
            return this;
        }
		
		public WorkflowExecutionMetadataBuilder hostname(String hostname) {
            this.hostname = hostname;
            return this;
        }
		
		public WorkflowExecutionMetadataBuilder financialCost(Double financialCost) {
            this.financialCost = financialCost;
            return this;
        }
		
		public WorkflowExecutionMetadataBuilder failureTries(Integer failureTries) {
            this.failureTries = failureTries;
            return this;
        }

        public WorkflowExecutionMetadata build() {
            return new WorkflowExecutionMetadata(this);
        }
    }
	
}