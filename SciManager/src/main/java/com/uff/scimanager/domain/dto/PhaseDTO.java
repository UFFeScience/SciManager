package com.uff.scimanager.domain.dto;

import java.util.ArrayList;
import java.util.List;

import com.uff.scimanager.domain.Phase;
import com.uff.scimanager.util.EncrypterUtils;

public class PhaseDTO {
	
	private Long phaseId;
	private String phaseName;
	private String encodedPhaseName;
	private Boolean allowExecution = Boolean.FALSE;
	
	public PhaseDTO() {}
	
	public PhaseDTO(PhaseDTOBuilder phaseDTOBuilder) {
		this.phaseId = phaseDTOBuilder.phaseId;
		this.phaseName = phaseDTOBuilder.phaseName;
		this.encodedPhaseName = phaseDTOBuilder.encodedPhaseName;
		this.allowExecution = phaseDTOBuilder.allowExecution;
	}
	
	public static PhaseDTO buildDTOByEntity(Phase phase) {
		if (phase == null) {
			return PhaseDTO.buildEmptyPhaseDTO();
		}
		
		return PhaseDTO.builder()
					   .phaseId(phase.getPhaseId())
					   .phaseName(phase.getPhaseName())
					   .encodedPhaseName(EncrypterUtils.encodeValueToURL(phase.getPhaseName()))
					   .allowExecution(phase.getAllowExecution()).build();
	}
	
	private static PhaseDTO buildEmptyPhaseDTO() {
		return new PhaseDTO();
	}
	
	public Long getPhaseId() {
		return phaseId;
	}

	public void setPhaseId(Long phaseId) {
		this.phaseId = phaseId;
	}

	public String getPhaseName() {
		return phaseName;
	}

	public void setPhaseName(String phaseName) {
		this.phaseName = phaseName;
	}
	
	public String getEncodedPhaseName() {
		return encodedPhaseName;
	}

	public void setEncodedPhaseName(String encodedPhaseName) {
		this.encodedPhaseName = encodedPhaseName;
	}

	public Boolean getAllowExecution() {
		return allowExecution;
	}

	public void setAllowExecution(Boolean allowExecution) {
		this.allowExecution = allowExecution;
	}

	public static PhaseDTOBuilder builder() {
		return new PhaseDTOBuilder();
	}
	
	public static class PhaseDTOBuilder {
	
		private Long phaseId;
		private String phaseName;
		private String encodedPhaseName;
		private Boolean allowExecution = Boolean.FALSE;
		
		public PhaseDTOBuilder phaseId(Long phaseId) {
			this.phaseId = phaseId;
			return this;
		}
		
		public PhaseDTOBuilder phaseName(String phaseName) {
			this.phaseName = phaseName;
			return this;
		}
		
		public PhaseDTOBuilder encodedPhaseName(String encodedPhaseName) {
			this.encodedPhaseName = encodedPhaseName;
			return this;
		}
		
		public PhaseDTOBuilder allowExecution(Boolean allowExecution) {
			this.allowExecution = allowExecution;
			return this;
		}
		
		public PhaseDTO build() {
			return new PhaseDTO(this);
		}
	}

	public static List<PhaseDTO> convertEntityListToDTOList(List<Phase> phases) {
		List<PhaseDTO> phasesDTO = new ArrayList<PhaseDTO>();
		
		if (phases != null) {
			for (Phase phase : phases) {
				phasesDTO.add(PhaseDTO.buildDTOByEntity(phase));
			}
		}
		
		return phasesDTO;
	}

}