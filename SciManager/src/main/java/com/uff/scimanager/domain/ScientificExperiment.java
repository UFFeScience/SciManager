package com.uff.scimanager.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.uff.scimanager.domain.form.ScientificExperimentForm;

@Entity
@Table(name = "EXPERIMENTO_CIENTIFICO")
public class ScientificExperiment implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "experimento_cientifico_id", sequenceName = "experimento_cientifico_id")
	@GeneratedValue(generator = "experimento_cientifico_id", strategy = GenerationType.AUTO)
	@Column(name = "ID_EXPERIMENTO_CIENTIFICO", nullable = false)
	private Long scientificExperimentId;
	
	@Column(name = "NM_SLUG", updatable = false, length = 32)
	private String slug;
	
	@Column(name = "NM_NOME_EXPERIMENTO", unique = true)
	private String experimentName;
	
	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "ID_PROJETO_CIENTIFICO")
	private ScientificProject scientificProject;
	
	@OneToMany(mappedBy = "scientificExperiment")
	@Cascade(CascadeType.ALL)
	private List<Workflow> workflows;
	
	public ScientificExperiment() {}
	
	public ScientificExperiment(ScientificExperimentBuilder scientificExperimentBuilder) {
		this.slug = scientificExperimentBuilder.slug;
		this.scientificExperimentId = scientificExperimentBuilder.scientificExperimentId;
		this.experimentName = scientificExperimentBuilder.experimentName;
		this.scientificProject = scientificExperimentBuilder.scientificProject;
		this.workflows = scientificExperimentBuilder.workflows;
	}
	
	public static ScientificExperiment buildEmptyScientificExperiment() {
		return new ScientificExperiment();
	}
	
	public ScientificExperiment(ScientificExperimentForm scientificExperimentForm) {
		if (scientificExperimentForm != null) {
			setExperimentName(scientificExperimentForm.getExperimentName());
		}
	}
	
	public List<Workflow> getWorkflows() {
		if (workflows == null) {
			workflows = new ArrayList<Workflow>();
		}
		
		return workflows;
	}

	public Long getScientificExperimentId() {
		return scientificExperimentId;
	}

	public void setScientificExperimentId(Long scientificExperimentId) {
		this.scientificExperimentId = scientificExperimentId;
	}
	
	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getExperimentName() {
		return experimentName;
	}

	public void setExperimentName(String experimentName) {
		this.experimentName = experimentName;
	}

	public ScientificProject getScientificProject() {
		return scientificProject;
	}

	public void setScientificProject(ScientificProject scientificProject) {
		this.scientificProject = scientificProject;
	}

	public void setWorkflows(List<Workflow> workflows) {
		this.workflows = workflows;
	}
	
	public Workflow getWorkflowByWorkflowName(String workflowName) {
		for (Workflow workflow : getWorkflows()) {
			if (workflow.getWorkflowName().equals(workflowName)) {
				return workflow;
			}
		}
		
		return null;
	}
	
	public static ScientificExperimentBuilder builder() {
		return new ScientificExperimentBuilder();
	}
	
	public static class ScientificExperimentBuilder {
		
		private Long scientificExperimentId;
		private String slug;
		private String experimentName;
		private ScientificProject scientificProject;
		private List<Workflow> workflows;
		
		public ScientificExperimentBuilder slug(String slug) {
			this.slug = slug;
			return this;
		}
		
		public ScientificExperimentBuilder scientificExperimentId(Long scientificExperimentId) {
			this.scientificExperimentId = scientificExperimentId;
			return this;
		}
		
		public ScientificExperimentBuilder scientificProject(ScientificProject scientificProject) {
			this.scientificProject = scientificProject;
			return this;
		}
		
		public ScientificExperimentBuilder experimentName(String experimentName) {
			this.experimentName = experimentName;
			return this;
		}
		
		public ScientificExperimentBuilder workflows(List<Workflow> workflows) {
			this.workflows = workflows;
			return this;
		}
		
		public ScientificExperiment build() {
			return new ScientificExperiment(this);
		}
	}

}