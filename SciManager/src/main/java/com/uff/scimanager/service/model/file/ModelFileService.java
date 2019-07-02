package com.uff.scimanager.service.model.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Throwables;
import com.uff.scimanager.dao.model.file.ModelFileDao;
import com.uff.scimanager.domain.ModelFile;
import com.uff.scimanager.domain.User;
import com.uff.scimanager.domain.Workflow;
import com.uff.scimanager.domain.dto.ModelFileDTO;
import com.uff.scimanager.domain.dto.WorkflowDTO;
import com.uff.scimanager.domain.security.CurrentUser;
import com.uff.scimanager.exception.ExistingEntityException;
import com.uff.scimanager.exception.InvalidEntityException;
import com.uff.scimanager.service.user.UserService;
import com.uff.scimanager.service.workflow.WorkflowService;
import com.uff.scimanager.util.CalendarDateUtils;
import com.uff.scimanager.util.XmlParser;

@Service
public class ModelFileService {

	private static final Logger log = LoggerFactory.getLogger(ModelFileService.class);
	
	private final ModelFileDao modelFileDao;
	private final UserService userService;
	
	@Autowired
	private WorkflowService workflowService;
	
    @Autowired
	public ModelFileService(ModelFileDao modelFileDao, UserService userService) {
		this.modelFileDao = modelFileDao;
		this.userService = userService;
	}
    
	public List<ModelFileDTO> getAllModelFiles(Integer pageNumber, Long workflowId, String initialDate, String finalDate) {
		log.info("Buscando lista de arquivos modelo pelo filtro: pageNumber = {}, initialDate = {}, finalDate = {}, workflowId = {}", 
				 pageNumber, initialDate, finalDate, workflowId);
		
		return ModelFileDTO.convertEntityListToDTOList(modelFileDao.findAllPaginated(pageNumber, workflowId, 
				                          										     CalendarDateUtils.parseStringDateCorrectFormat(initialDate), 
				                          										     CalendarDateUtils.parseStringDateCorrectFormat(finalDate)));
	}
	
	public List<ModelFileDTO> getAllDashboardModelFiles(Integer pageNumber, Long workflowId) {
		log.info("Buscando lista de arquivos modelo relevantes pelo filtro: pageNumber = {}, workflowId = {}", pageNumber, workflowId);
		return ModelFileDTO.convertEntityListToDTOList(modelFileDao.findAllDashboardPaginated(pageNumber, workflowId));
	}
	
	public ModelFile getModelFileById(Long modelFileId) {
		log.info("Buscando modelFile por id {}", modelFileId);
		return modelFileDao.findById(modelFileId);
	}
   
	public Integer countAllModelFiles(Long workflowId, String initialDate, String finalDate) {
		log.info("Contando lista de arquivos modelo pelo filtro: initialDate = {}, finalDate = {}, workflowId = {}", 
				 initialDate, finalDate, workflowId);
		
		return modelFileDao.getAllModelFilesCount(workflowId, CalendarDateUtils.parseStringDateCorrectFormat(initialDate), 
    		                                                  CalendarDateUtils.parseStringDateCorrectFormat(finalDate));
	}
	
	public Integer countAllModelFiles(Long workflowId) {
		log.info("Contando lista de arquivos modelo pelo filtro: workflowId = {}", workflowId);
		return countAllModelFiles(workflowId, null, null);
	}
	
	public File getModelFileContentOfWorkflowById(Long workflowId, Long modelFileId) {
		log.info("Buscando arquivo modelo de id {} do workflow de id {}", modelFileId, workflowId);
		
		ModelFile modelFile = modelFileDao.findModelFileByWorkflowIdAndModelFileId(workflowId, modelFileId);
		File tempFile = null;
		
		try {
			tempFile = File.createTempFile("arquivo_modelo_" + modelFileId + workflowId, ".xml", null);
			
			FileOutputStream fos = new FileOutputStream(tempFile);
			fos.write(modelFile.getFileContent());
			
			fos.close();
		} catch (IOException e) {
			log.error("Erro ao buscar arquivo modelo\n{}", Throwables.getStackTraceAsString(e));
		}
		
		return tempFile;
	}
	
	public String getModelFileContentAsJsonOfWorkflowById(Long workflowId) {
		log.info("Buscando conteudo parseado em json do arquivo modelo atual do workflow de id {}", workflowId);
		
		ModelFile modelFile = modelFileDao.findModelFileCurrentByWorkflowId(workflowId);
		
		String xmlFileContent = null;
		JSONObject jsonFileContent = null;
		String jsonString = null;
		
		try {
			xmlFileContent = new String(modelFile.getFileContent());
			jsonFileContent = XML.toJSONObject(xmlFileContent);
			jsonString = jsonFileContent.toString();
			
		} catch (Exception e) {
			log.error("Erro ao parsear arquivo modelo xml em json\n{}", Throwables.getStackTraceAsString(e));
		}
		
		return jsonString;
	}

	public ModelFileDTO getCurrentModelFileOfWorkflow(Long workflowId) {
		log.info("Buscando arquivo modelo atual do workflow de id {}", workflowId);
		ModelFile modelFile = modelFileDao.findModelFileCurrentByWorkflowId(workflowId);
		
		if (modelFile != null) {
			return ModelFileDTO.buildDTOByEntity(modelFile);
		}
		
		log.info("Nenhum arquivo modelo atual encontrado para o workflow de id {}", workflowId);
		return null;
	}
	
	public ModelFile getCurrentModelFileEntityOfWorkflow(Long workflowId) {
		log.info("Buscando arquivo modelo atual do workflow de id {}", workflowId);
		ModelFile modelFile = modelFileDao.findModelFileCurrentByWorkflowId(workflowId);
		
		if (modelFile != null) {
			return modelFile;
		}
		
		log.info("Nenhum arquivo modelo atual encontrado para o workflow de id {}", workflowId);
		return null;
	}

	public ModelFileDTO uploadNewModelFile(MultipartFile newFile, Long workflowId, Long userId) throws IOException, ExistingEntityException, InvalidEntityException {
		log.info("Iniciando processo de salvamento de novo arquivo modelo");
		
		if (newFile == null || workflowId == null || userId == null) {
			log.error("Erro, inputs de criação de arquivo modelo nulos.");
			return null;
		}
		
		String execTag = XmlParser.getXmlAttributeValue("workflow_exectag", "constraint", 
														new String(newFile.getBytes(), StandardCharsets.UTF_8));
		
		if (modelFileDao.getAllModelFilesCountByExecTag(execTag) > 0) {
			log.error("Erro, já existe modelFile com a execTag {}", execTag);
			throw new ExistingEntityException(String.format("Já existe modelFile com a execTag [%s]", execTag));
		}
		
		ModelFile currentModelFile = modelFileDao.findModelFileCurrentByWorkflowId(workflowId);
		
		if (currentModelFile != null) {
			currentModelFile.setIsCurrentFile(Boolean.FALSE);
			modelFileDao.saveOrUpdateModelFile(currentModelFile);
		}
		
		Workflow workflow = workflowService.getWorkflowById(workflowId);
		
		if (workflow == null) {
			return null;
		}
		
		User uploader = userService.getUserEntityById(userId);
		
		if (uploader == null) {
			return null;
		}
		
		ModelFile modelFile = ModelFile.builder()
				                       .isCurrentFile(Boolean.TRUE)
				                       .submissionDate(Calendar.getInstance(TimeZone.getTimeZone(CalendarDateUtils.DATE_LOCALE)))
				                       .fileContent(newFile.getBytes())
				                       .execTag(execTag)
				                       .uploader(uploader)
				                       .workflow(workflow).build();
		
		return ModelFileDTO.buildDTOByEntity(modelFileDao.saveOrUpdateModelFile(modelFile));
	}
	
	public ModelFileDTO setNewCurrentFileModel(Long modelFileId, Long workflowId) {
		log.info("Iniciando processo de salvamento de novo arquivo modelo");
		
		if (workflowId == null || modelFileId == null) {
			log.error("Erro, inputs de alteração de arquivo modelo nulos.");
			return null;
		}
		
		ModelFile currentModelFile = modelFileDao.findModelFileCurrentByWorkflowId(workflowId);
		
		if (currentModelFile != null) {
			currentModelFile.setIsCurrentFile(Boolean.FALSE);
			modelFileDao.saveOrUpdateModelFile(currentModelFile);
		}
		
		Workflow workflow = workflowService.getWorkflowById(workflowId);
		
		if (workflow == null) {
			return null;
		}
		
		ModelFile modelFile = modelFileDao.findModelFileByWorkflowIdAndModelFileId(workflowId, modelFileId);
		modelFile.setIsCurrentFile(Boolean.TRUE);
		
		return ModelFileDTO.buildDTOByEntity(modelFileDao.saveOrUpdateModelFile(modelFile));
	}
	
	public Map<String, Integer> countExecutionDependencies(Long modelFileId) {
		Map<String, Integer> dependencies = new HashMap<String, Integer>();
		
		dependencies.put("workflowExecutions", workflowService.countAllWorkflowsExecutionByModelFileId(modelFileId));
		
		return dependencies;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void removeModelFile(Long modelFileId) throws InvalidEntityException {
		ModelFile modelFile = modelFileDao.findById(modelFileId);
		
		if (modelFile == null) {
			log.error("Arquivo modelo de id {} não encontrado.", modelFileId);
			return;
		}
		
		if (modelFile.getIsCurrentFile()) {
			throw new InvalidEntityException("Não é possível remover um arquivo modelo atual de um workflow.");
		}
		
		modelFileDao.delete(modelFile);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Boolean canAccessModelFile(CurrentUser currentUser, Long modelFileId) {
		log.info("Checando se usuário {} tem acesso ao arquivo modelo de id {}", currentUser, modelFileId);
		
		ModelFile modelFile = modelFileDao.findById(modelFileId);
		
		if (modelFile == null) {
			return Boolean.FALSE;
	    } 
		else {
			log.info("Arquivo modelo com id {} buscado com sucesso", modelFile);
			initializeEntities(modelFile);
		}
		
		WorkflowDTO workflow = WorkflowDTO.buildDTOByEntity(modelFile.getWorkflow());
		
		return workflowService.canAccessWorkflow(currentUser.getUserId(), workflow);
    }
	
	private void initializeEntities(ModelFile modelFile) {
		if (modelFile.getWorkflow() != null) {
			modelFile.getWorkflow().getResponsibleGroup().getGroupUsers().size();
		}
	}
	
	public Integer getPageSize() {
		return modelFileDao.getPaginationParameterConfig().getMaxResultsPerPage();
	}

}