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

@Entity
@Table(name = "FASE")
public class Phase implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "fase_id", sequenceName = "fase_id")
	@GeneratedValue(generator = "fase_id", strategy = GenerationType.AUTO)
	@Column(name = "ID_FASE", nullable = false)
	private Long phaseId;
	
	@Column(name = "NM_SLUG", length = 32)
	private String slug;
	
	@Column(name = "NM_NOME_FASE")
	private String phaseName;
	
	@Column(name = "BO_HABILITA_EXECUCAO")
	private Boolean allowExecution = Boolean.FALSE;
	
	@ManyToOne
	@JoinColumn(name = "ID_PROJETO_CIENTIFICO", referencedColumnName = "ID_PROJETO_CIENTIFICO", nullable=true)
	@Cascade(CascadeType.SAVE_UPDATE)
	private ScientificProject scientificProject;
	
	public Phase() {}
	
	public Phase(PhaseBuilder phaseBuilder) {
		this.phaseId = phaseBuilder.phaseId;
		this.slug = phaseBuilder.slug;
		this.phaseName = phaseBuilder.phaseName;
		this.allowExecution = phaseBuilder.allowExecution;
		this.scientificProject = phaseBuilder.scientificProject;
	}
	
	public Long getPhaseId() {
		return phaseId;
	}

	public void setPhaseId(Long phaseId) {
		this.phaseId = phaseId;
	}
	
	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getPhaseName() {
		return phaseName;
	}

	public void setPhaseName(String phaseName) {
		this.phaseName = phaseName;
	}
	
	public Boolean getAllowExecution() {
		return allowExecution;
	}

	public void setAllowExecution(Boolean allowExecution) {
		this.allowExecution = allowExecution;
	}

	public ScientificProject getScientificProject() {
		return scientificProject;
	}

	public void setScientificProject(ScientificProject scientificProject) {
		this.scientificProject = scientificProject;
	}
	
	public static PhaseBuilder builder() {
		return new PhaseBuilder();
	}
	
	public static class PhaseBuilder {
		
		private Long phaseId;
		private String slug;
		private String phaseName;
		private Boolean allowExecution = Boolean.FALSE;
		private ScientificProject scientificProject;
		
		public PhaseBuilder slug(String slug) {
			this.slug = slug;
			return this;
		}
		
		public PhaseBuilder phaseId(Long phaseId) {
			this.phaseId = phaseId;
			return this;
		}
		
		public PhaseBuilder phaseName(String phaseName) {
			this.phaseName = phaseName;
			return this;
		}
		
		public PhaseBuilder allowExecution(Boolean allowExecution) {
			this.allowExecution = allowExecution;
			return this;
		}
		
		public PhaseBuilder scientificProject(ScientificProject scientificProject) {
			this.scientificProject = scientificProject;
			return this;
		}
		
		public Phase build() {
			return new Phase(this);
		}
	}
	
}