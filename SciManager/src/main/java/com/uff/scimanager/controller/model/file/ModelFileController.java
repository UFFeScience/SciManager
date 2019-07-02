package com.uff.scimanager.controller.model.file;

import java.io.File;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Throwables;
import com.uff.scimanager.component.PaginationInfo;
import com.uff.scimanager.component.ResponseMessage;
import com.uff.scimanager.domain.dto.ModelFileDTO;
import com.uff.scimanager.domain.dto.filter.FilterDTO;
import com.uff.scimanager.domain.dto.filter.FilterFieldDTO;
import com.uff.scimanager.exception.ExistingEntityException;
import com.uff.scimanager.exception.InvalidEntityException;
import com.uff.scimanager.service.model.file.ModelFileService;
import com.uff.scimanager.service.workflow.WorkflowService;
import com.uff.scimanager.util.CalendarDateUtils;
import com.uff.scimanager.util.FileUtils;
import com.uff.scimanager.util.ResponseMessageUtil;

@Controller
@RequestMapping("/workflow/model-file")
public class ModelFileController {
	
	private static final Logger log = LoggerFactory.getLogger(ModelFileController.class);
	
    private final ModelFileService modelFileService;
    private final WorkflowService workflowService;
	
	@Autowired
	public ModelFileController(ModelFileService modelFileService, WorkflowService workflowService) {
		this.modelFileService = modelFileService;
		this.workflowService = workflowService;
	}
	
	@PreAuthorize("@workflowService.canAccessWorkflow(principal, #workflowId)")
	@RequestMapping(value = "/{workflowId}/download-file/{modelFileId}", method = RequestMethod.GET, produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public HttpEntity<byte[]> getWorkflowModelFile(@PathVariable Long workflowId, @PathVariable Long modelFileId, HttpServletResponse response) {
    	log.info("Carregando arquivo modelo de id {} do workflow do id {}", modelFileId, workflowId);
    	
    	File currentFile = modelFileService.getModelFileContentOfWorkflowById(workflowId, modelFileId);
    	
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    	response.setHeader("Content-Disposition", "attachment; filename=" + currentFile.getName());
    	
    	try {
    		return new HttpEntity<byte[]>(FileUtils.getBytesArray(currentFile), headers); 
    	}
    	catch (Exception e) {
    		log.error("Erro ao recuperar o arquivo modelo de id {} do workflow de id {}\n{}", modelFileId, workflowId, Throwables.getStackTraceAsString(e));
    	}
    	
    	return null;
    }
	
	@PreAuthorize("@workflowService.canAccessWorkflow(principal, #workflowId)")
	@RequestMapping(value = "/{workflowId}/json", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getWorkflowModelFileContentJson(@PathVariable Long workflowId, HttpServletResponse response) {
    	log.info("Carregando arquivo modelo atual do workflow do id {}", workflowId);
    	return modelFileService.getModelFileContentAsJsonOfWorkflowById(workflowId);
    }
	
	@PreAuthorize("@workflowService.canAccessWorkflow(principal, #workflowId)")
	@RequestMapping(value = "/{workflowId}/entity", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ModelFileDTO getWorkflowModelFileEntityJson(@PathVariable Long workflowId, HttpServletResponse response) {
    	log.info("Carregando arquivo modelo atual do workflow do id {}", workflowId);
    	return modelFileService.getCurrentModelFileOfWorkflow(workflowId);
    }
	
	@PreAuthorize("@workflowService.canAccessWorkflow(principal, #workflowId)")
	@RequestMapping(value = "/{workflowId}/dashboard/model-files", method = RequestMethod.GET)
    public String getDashboardModelFilesList(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
								    		 @PathVariable Long workflowId,
								    		 Model model) {
    	
    	log.info("Carregando página de listagem de arquivos modelo");
    	
    	model.addAttribute("workflowDTO", workflowService.getWorkflowDTOById(workflowId));
        model.addAttribute("modelFileDTOs", modelFileService.getAllDashboardModelFiles(pageNumber, workflowId));
        model.addAttribute("modelFilesTotal", modelFileService.countAllModelFiles(workflowId));
        model.addAttribute("searchModelFilesListUrl", "/workflow/model-file/" + workflowId + "/dashboard/model-files");
        
        return "fragments/model-file/workflow-model-file-list-fragment";
    }
	
	@PreAuthorize("@workflowService.canAccessWorkflow(principal, #workflowId)")
	@RequestMapping(value = "/{workflowId}/model-files-list", method = RequestMethod.GET)
    public String getModelFilesList(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
								    @RequestParam(value = "initialDate", required = false) String initialDate,
								    @RequestParam(value = "finalDate", required = false) String finalDate,
								    @PathVariable Long workflowId,
								    Model model) {
    	
    	log.info("Carregando página de listagem de arquivos modelo");
    	model.addAttribute("workflowDTO", workflowService.getWorkflowDTOById(workflowId));
        model.addAttribute("modelFileDTOs", modelFileService.getAllModelFiles(pageNumber, workflowId, initialDate, finalDate));
        
        PaginationInfo pagination = PaginationInfo.builder()
		        								  .totalEntities(modelFileService.countAllModelFiles(workflowId, initialDate, finalDate))
		        								  .actualPageNumber(pageNumber)
		        								  .pageSize(modelFileService.getPageSize())
		        								  .baseUrlLink("/workflow/model-file/" + workflowId + "/model-file-list").build();
        
        pagination.addUrlParameter("initialDate", CalendarDateUtils.formatDateString(initialDate));
        pagination.addUrlParameter("finalDate", CalendarDateUtils.formatDateString(finalDate));
        
        FilterDTO filter = FilterDTO.builder().build();
        filter.addFilterField(FilterFieldDTO.builder().name("initialDate").value(initialDate).label("Data inicial").build())
        	  .addFilterField(FilterFieldDTO.builder().name("finalDate").value(finalDate).label("Data final").build());
        
        model.addAttribute("filter", filter);
        model.addAttribute("pagination", pagination);
        model.addAttribute("searchListUrl", pagination.getBaseUrlLink());
        
        return "fragments/model-file/model-file-list-fragment";
    }
	
	@PreAuthorize("@workflowService.canAccessWorkflow(principal, #workflowId)")
	@RequestMapping(value = "/alter-current-file", method = RequestMethod.POST)
	@ResponseBody
    public ResponseMessage setCurrentModelFile(@RequestParam(value="modelFileId", required = true) Long modelFileId,
    								  		   @RequestParam(value="workflowId", required = true) Long workflowId, HttpServletRequest request) {
    	
    	log.info("Setando novo arquivo modelo de id {} como atual para o workflow de id {}", modelFileId, workflowId);
        ModelFileDTO modelFileDTO = null;
        
		try {
			modelFileDTO = modelFileService.setNewCurrentFileModel(modelFileId, workflowId);
		} 
		catch (Exception e) {
        	log.error("Erro ao setar novo arquivo modelo de id {} como atual para o workflow de id {}\n{}", modelFileId, workflowId, Throwables.getStackTraceAsString(e));
    	}
        
		return ResponseMessageUtil.handleResponseMessage(modelFileDTO, "Arquivo modelo alterado com sucesso.", "Erro inesperado ao alterar arquivo modelo.");
    }
	
	@PreAuthorize("@workflowService.canAccessWorkflow(principal, #workflowId)")
    @RequestMapping(value = "/upload-model-file", method = RequestMethod.POST)
	@ResponseBody
    public ResponseMessage uploadModelFile(@RequestParam(value="modelFile", required = true) MultipartFile newFile,
									       @RequestParam(value="workflowId", required = true) Long workflowId,
									       @RequestParam(value="userId", required = true) Long userId, HttpServletRequest request) {
    	
    	log.info("Salvando arquivo modelo carregado para o workflow de id {} e usuário de id {}", workflowId, userId);
        ModelFileDTO modelFileDTO = null;
        
		try {
			modelFileDTO = modelFileService.uploadNewModelFile(newFile, workflowId, userId);
		} 
		catch (ExistingEntityException e) {
			log.error("Erro ao salvar arquivo modelo, já existe modelo com exectag informada.\n{}", Throwables.getStackTraceAsString(e));
			return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Não foi possível fazer upload do arquivo, pois já existe arquivo modelo com o exectag informado.").build();
		} 
		catch (InvalidEntityException e) {
			log.error("Erro ao salvar arquivo modelo. Arquivo modelo deve conter atributo 'workflow_exectag'.\n{}", Throwables.getStackTraceAsString(e));
			return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro ao salvar arquivo modelo. Arquivo modelo deve conter atributo 'workflow_exectag'").build();
		}
		catch (Exception e) {
        	log.error("Erro ao salvar arquivo modelo para o workflow de id {} e usuário de id {}\n{}", workflowId, userId, Throwables.getStackTraceAsString(e));
    	}
		
		return ResponseMessageUtil.handleResponseMessage(modelFileDTO, "Arquivo modelo salvo com sucesso.", "Erro inesperado ao salvar arquivo modelo.");
    }
	
	@PreAuthorize("@modelFileService.canAccessModelFile(principal, #modelFileId)")
    @RequestMapping(value = "/remove-model-file", method = RequestMethod.POST)
	@ResponseBody
	public ResponseMessage handleRemoveModelFile(@RequestParam(value = "modelFileId", required = true) Long modelFileId, HttpServletRequest request) {
		log.info("Processando remocao de modelFile de id {}", modelFileId);
		try {
    		modelFileService.removeModelFile(modelFileId);
    	} 
    	catch (InvalidEntityException e) {
        	log.error("Erro ao excluir arquivo modelo de id {}, não é possível excluir arquivo modelo atual de um workflow\n{}", modelFileId, Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro, não é possível excluir arquivo modelo atual de um workflow.").build();
    	}
        catch (Exception e) {
        	log.error("Erro ao excluir arquivo modelo de id {}\n{}", modelFileId, Throwables.getStackTraceAsString(e));
        	return ResponseMessage.builder()
								  .type(ResponseMessage.ERROR_MESSAGE_LABEL)
								  .text("Erro inesperado ao excluir arquivo modelo.").build();
    	}
    	
    	return ResponseMessage.builder()
							  .type(ResponseMessage.SUCCESS_MESSAGE_LABEL)
							  .text("Arquivo modelo excluído com sucesso.").build();
	}
    
	@PreAuthorize("@modelFileService.canAccessModelFile(principal, #modelFileId)")
    @RequestMapping(value = "/count-dependencies/{modelFileId}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Integer> countDependencies(@PathVariable Long modelFileId) {
        log.info("Contando dependências do arquivo modelo de id {}", modelFileId);
    	return modelFileService.countExecutionDependencies(modelFileId);
    }
    
}