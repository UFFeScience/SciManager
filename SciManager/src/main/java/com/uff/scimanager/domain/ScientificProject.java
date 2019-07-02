package com.uff.scimanager.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.uff.scimanager.domain.form.ScientificProjectFrom;

@Entity
@Table(name = "PROJETO_CIENTIFICO")
public class ScientificProject implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "projeto_cientifico_id", sequenceName = "projeto_cientifico_id")
	@GeneratedValue(generator = "projeto_cientifico_id", strategy = GenerationType.AUTO)
	@Column(name = "ID_PROJETO_CIENTIFICO", nullable = false)
	private Long scientificProjectId;
	
	@Column(name = "NM_SLUG", updatable = false, length = 32)
	private String slug;
	
	@Column(name = "NM_NOME_PROJETO", unique = true)
	private String projectName;
	
	@OneToMany(mappedBy = "scientificProject")
	@Cascade(CascadeType.ALL)
	private List<ScientificExperiment> scientificExperiments;
	
	@OneToMany(mappedBy = "scientificProject")
	@Cascade(CascadeType.ALL)
	private List<Workflow> workflows;
	
	@OneToMany(mappedBy = "scientificProject")
	@Cascade(CascadeType.ALL)
	private List<Phase> phases;
	
	public ScientificProject() {}
	
	public ScientificProject(ScientificProjectBuilder scientificProjectBuilder) {
		this.slug = scientificProjectBuilder.slug;
		this.scientificProjectId = scientificProjectBuilder.scientificProjectId;
		this.projectName = scientificProjectBuilder.projectName;
		this.scientificExperiments = scientificProjectBuilder.scientificExperiments;
		this.workflows = scientificProjectBuilder.workflows;
		this.phases = scientificProjectBuilder.phases;
	}
	
	public static ScientificProject buildEmptyScientificProject() {
		return new ScientificProject();
	}
	
	public ScientificProject(ScientificProjectFrom scientificProjectForm) {
		if (scientificProjectForm != null) {
			setProjectName(scientificProjectForm.getProjectName());
		}
	}
	
	public Long getScientificProjectId() {
		return scientificProjectId;
	}

	public void setScientificProjectId(Long scientificProjectId) {
		this.scientificProjectId = scientificProjectId;
	}
	
	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public List<Phase> getPhases() {
		if (phases == null) {
			phases = new ArrayList<Phase>();
		}
		
		return phases;
	}

	public void setPhases(List<Phase> phases) {
		this.phases = phases;
	}
	
	public List<ScientificExperiment> getScientificExperiments() {
		if (scientificExperiments == null) {
			scientificExperiments = new ArrayList<ScientificExperiment>();
		}
		
		return scientificExperiments;
	}

	public void setScientificExperiments(List<ScientificExperiment> scientificExperiments) {
		this.scientificExperiments = scientificExperiments;
	}

	public List<Workflow> getWorkflows() {
		if (workflows == null) {
			workflows = new ArrayList<Workflow>();
		}
		
		return workflows;
	}

	public void setWorkflows(List<Workflow> workflows) {
		this.workflows = workflows;
	}
	
	public Phase getPhaseByPhaseName(String phaseName) {
		for (Phase phase : getPhases()) {
			if (phase.getPhaseName().equals(phaseName)) {
				return phase;
			}
		}
		
		return null;
	}
	
	public Workflow getWorkflowByWorkflowName(String workflowName) {
		for (Workflow workflow : getWorkflows()) {
			if (workflow.getWorkflowName().equals(workflowName)) {
				return workflow;
			}
		}
		
		return null;
	}
	
	public static ScientificProjectBuilder builder() {
		return new ScientificProjectBuilder();
	}
	
	public static class ScientificProjectBuilder {
		
		private Long scientificProjectId;
		private String slug;
		private String projectName;
		private List<ScientificExperiment> scientificExperiments;
		private List<Workflow> workflows;
		private List<Phase> phases;
		
		public ScientificProjectBuilder slug(String slug) {
			this.slug = slug;
			return this;
		}
		
		public ScientificProjectBuilder scientificProjectId(Long scientificProjectId) {
			this.scientificProjectId = scientificProjectId;
			return this;
		}
		
		public ScientificProjectBuilder projectName(String projectName) {
			this.projectName = projectName;
			return this;
		}
		
		public ScientificProjectBuilder scientificExperiments(List<ScientificExperiment> scientificExperiments) {
			this.scientificExperiments = scientificExperiments;
			return this;
		}
		
		public ScientificProjectBuilder workflows(List<Workflow> workflows) {
			this.workflows = workflows;
			return this;
		}
		
		public ScientificProjectBuilder phases(List<Phase> phases) {
			this.phases = phases;
			return this;
		}
		
		public ScientificProject build() {
			return new ScientificProject(this);
		}
	}

}