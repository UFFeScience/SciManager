package com.uff.scimanager.domain.dto.swfms.scicumuls;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.uff.scimanager.domain.swfms.scicumulus.SciCumulusExecutionStatus;
import com.uff.scimanager.domain.swfms.scicumulus.WorkflowExecutionMetadata;
import com.uff.scimanager.util.CalendarDateUtils;

public class WorkflowExecutionMetadataDTO {
	
	private String execTag;
	private String tag;
	private String workspace;
	private SciCumulusExecutionStatus status;
	private String startTime;
	private String endTime;
	private Integer processors;
	private String address;
	private String machineType;
	private String hostname;
	private BigDecimal financialCost;
	private Integer failureTries;
	
	public WorkflowExecutionMetadataDTO() {}
	
	public WorkflowExecutionMetadataDTO(WorkflowExecutionMetadataDTOBuilder workflowExecutionMetadataDTOBuilder) {
		this.execTag = workflowExecutionMetadataDTOBuilder.execTag;
		this.tag = workflowExecutionMetadataDTOBuilder.tag;
		this.workspace = workflowExecutionMetadataDTOBuilder.workspace;
		this.status = workflowExecutionMetadataDTOBuilder.status;
		this.startTime = workflowExecutionMetadataDTOBuilder.startTime;
		this.status = workflowExecutionMetadataDTOBuilder.status;
		this.endTime = workflowExecutionMetadataDTOBuilder.endTime;
		this.processors = workflowExecutionMetadataDTOBuilder.processors;
		this.address = workflowExecutionMetadataDTOBuilder.address;
		this.machineType = workflowExecutionMetadataDTOBuilder.machineType;
		this.hostname = workflowExecutionMetadataDTOBuilder.hostname;
		this.financialCost = workflowExecutionMetadataDTOBuilder.financialCost;
		this.failureTries = workflowExecutionMetadataDTOBuilder.failureTries;
	}
	
	public static WorkflowExecutionMetadataDTO buildEmptyWorkflowExecutionMetadataDTO() {
		return new WorkflowExecutionMetadataDTO();
	}
	
	public static WorkflowExecutionMetadataDTO buildDTOFromEntity(WorkflowExecutionMetadata workflowExecutionMetadata) {
		if (workflowExecutionMetadata != null) {
			return WorkflowExecutionMetadataDTO.builder()
											   .execTag(workflowExecutionMetadata.getExecTag())
											   .tag(workflowExecutionMetadata.getTag())
											   .workspace(workflowExecutionMetadata.getWorkspace())
											   .status(SciCumulusExecutionStatus.getSciCumulusExecutionStatusFromString(workflowExecutionMetadata.getStatus()))
											   .startTime(CalendarDateUtils.formatExecutionDate(workflowExecutionMetadata.getStartTime()))
											   .endTime(CalendarDateUtils.formatExecutionDate(workflowExecutionMetadata.getEndTime()))
											   .processors(workflowExecutionMetadata.getProcessors())
											   .address(workflowExecutionMetadata.getAddress())
											   .machineType(workflowExecutionMetadata.getMachineType())
											   .hostname(workflowExecutionMetadata.getHostname())
											   .financialCost(BigDecimal.valueOf(workflowExecutionMetadata.getFinancialCost()))
											   .failureTries(workflowExecutionMetadata.getFailureTries())
											   .build();
		}
		
		return WorkflowExecutionMetadataDTO.buildEmptyWorkflowExecutionMetadataDTO();
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

	public SciCumulusExecutionStatus getStatus() {
		return status;
	}

	public void setStatus(SciCumulusExecutionStatus status) {
		this.status = status;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
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

	public BigDecimal getFinancialCost() {
		return financialCost;
	}

	public void setFinancialCost(BigDecimal financialCost) {
		this.financialCost = financialCost;
	}

	public Integer getFailureTries() {
		return failureTries;
	}

	public void setFailureTries(Integer failureTries) {
		this.failureTries = failureTries;
	}
	
	public static List<WorkflowExecutionMetadataDTO> convertEntityListNoChildToDTOList(List<WorkflowExecutionMetadata> WorkflowExecutionsMetadata) {
		List<WorkflowExecutionMetadataDTO> workflowExecutionsMetadataDTO = new ArrayList<WorkflowExecutionMetadataDTO>();
		
		if (WorkflowExecutionsMetadata != null) {
			for (WorkflowExecutionMetadata workflowExecutionMetadata : WorkflowExecutionsMetadata) {
				workflowExecutionsMetadataDTO.add(WorkflowExecutionMetadataDTO.buildDTOFromEntity(workflowExecutionMetadata));
			}
		}
		
		return workflowExecutionsMetadataDTO;
	}

	public static WorkflowExecutionMetadataDTOBuilder builder() {
		return new WorkflowExecutionMetadataDTOBuilder();
	}

	public static class WorkflowExecutionMetadataDTOBuilder {
		
		private String execTag;
		private String tag;
		private String workspace;
		private SciCumulusExecutionStatus status;
		private String startTime;
		private String endTime;
		private Integer processors;
		private String address;
		private String machineType;
		private String hostname;
		private BigDecimal financialCost;
		private Integer failureTries;
		
		public WorkflowExecutionMetadataDTOBuilder execTag(String execTag) {
            this.execTag = execTag;
            return this;
        }
		
		public WorkflowExecutionMetadataDTOBuilder tag(String tag) {
            this.tag = tag;
            return this;
        }
		
		public WorkflowExecutionMetadataDTOBuilder workspace(String workspace) {
            this.workspace = workspace;
            return this;
        }
		
		public WorkflowExecutionMetadataDTOBuilder status(SciCumulusExecutionStatus status) {
            this.status = status;
            return this;
        }
		
		public WorkflowExecutionMetadataDTOBuilder startTime(String startTime) {
            this.startTime = startTime;
            return this;
        }
		
		public WorkflowExecutionMetadataDTOBuilder endTime(String endTime) {
            this.endTime = endTime;
            return this;
        }
		
		public WorkflowExecutionMetadataDTOBuilder processors(Integer processors) {
            this.processors = processors;
            return this;
        }
		
		public WorkflowExecutionMetadataDTOBuilder address(String address) {
            this.address = address;
            return this;
        }
		
		public WorkflowExecutionMetadataDTOBuilder machineType(String machineType) {
            this.machineType = machineType;
            return this;
        }
		
		public WorkflowExecutionMetadataDTOBuilder hostname(String hostname) {
            this.hostname = hostname;
            return this;
        }
		
		public WorkflowExecutionMetadataDTOBuilder financialCost(BigDecimal financialCost) {
            this.financialCost = financialCost;
            return this;
        }
		
		public WorkflowExecutionMetadataDTOBuilder failureTries(Integer failureTries) {
            this.failureTries = failureTries;
            return this;
        }

        public WorkflowExecutionMetadataDTO build() {
            return new WorkflowExecutionMetadataDTO(this);
        }
    }
	
}