package com.uff.scimanager.service.tag;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uff.scimanager.dao.tag.TagDao;
import com.uff.scimanager.domain.Tag;
import com.uff.scimanager.domain.Task;
import com.uff.scimanager.domain.dto.TagDTO;
import com.uff.scimanager.domain.form.TagForm;
import com.uff.scimanager.exception.EntityNotFoundException;
import com.uff.scimanager.exception.ExistingEntityException;
import com.uff.scimanager.service.task.TaskService;

@Service
public class TagService {

	private static final Logger log = LoggerFactory.getLogger(TagService.class);

	private final TagDao tagDao;

	@Autowired
	private TaskService taskService;
	
    @Autowired
	public TagService(TagDao tagDao) {
		this.tagDao = tagDao;
	}
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<TagDTO> getAllTags(Integer pageNumber, String queryString) {
		log.info("Buscando lista de tags pelo filtro: pageNumber = {}, queryString = {}", pageNumber, queryString);
		return TagDTO.convertEntityListToDTOList(tagDao.findAllPaginated(pageNumber, queryString));
	}
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<String> getAllTagsNamesJson(String queryString) {
		log.info("Buscando lista de todas as tags");
		List<String> tagNames = new ArrayList<String>();
		List<Tag> tags = tagDao.findAllByQueryString(queryString);
		
		if (tags != null && !tags.isEmpty()) {
			for (Tag tag : tags) {
				tagNames.add(tag.getTagName());
			}
		}
		
		return tagNames;
	}
    
    public Integer countAllTags(String queryString) {
		log.info("Contando lista de tags pelo filtro: queryString = {}", queryString);
        return tagDao.getAllTagsCount(queryString);
    }
    
    @Transactional
    public TagDTO createTag(TagForm tagForm) {
    	log.info("Processando criação de tag");
    	return TagDTO.buildDTOByEntity(tagDao.saveOrUpdateTag(Tag.buildTagFromTagForm(tagForm)));
    }
    
    @Transactional
	public Tag createFromNameTag(String tagName) {
		log.info("Processando criação de tag");
    	return tagDao.saveOrUpdateTag(Tag.builder()
    			                         .tagName(tagName).build());
	}
    
    @Transactional
    public TagDTO editTag(Long tagId, String tagName) throws EntityNotFoundException, ExistingEntityException {
    	if (tagId == null || (tagName == null || "".equals(tagName))) {
    		log.info("Dados inválidos de entrada, parâmetros de edição não podem ser vazios.");
    		return null;
    	}
    	
		Tag tag = tagDao.findById(tagId);
		if (tag == null) {
			throw new EntityNotFoundException("Erro ao buscar tag. Tag com o id " + tagId + " não foi encontrada.");
		}
		
		if (!tagName.equals(tag.getTagName()) && !validateExistingTagName(tagName)) {
			log.info("Tag com a tagName {} já existe", tagName);
			throw new ExistingEntityException("Tag com a tagName " + tagName + " já existe");
		}
		
		tag.setTagName(tagName);
		Tag savedTag = tagDao.saveOrUpdateTag(tag);
		
		if (savedTag != null) {
			log.info("Nome da tag atualizado com sucesso para {}", tagName);
			return TagDTO.buildDTOByEntity(savedTag);
		}
		
		log.error("Erro ao editar nome da tag.");
		return null;
    }
    
    private Boolean validateExistingTagName(String tagName) {
		log.info("Validando se existe tag com o nome {}", tagName);
		TagDTO tagDTO = getTagByTagName(tagName);
		return tagDTO == null;
	}

    public TagDTO getTagByTagName(String tagName) {
    	Tag tag = tagDao.getTagByTagName(tagName);
    	
    	if (tag != null) {
    		log.info("Tag de tagName {}, buscado com sucesso", tagName);
    		return TagDTO.buildDTOByEntity(tag);
    	}
    	
    	log.info("Tag não pode ser encontrado");
        return null;
    }
    
    public Tag getTagEntityByTagName(String tagName) {
    	Tag tag = tagDao.getTagByTagName(tagName);
    	
    	if (tag != null) {
    		log.info("Tag de tagName {}, buscado com sucesso", tagName);
    		return tag;
    	}
    	
    	log.info("Tag não pode ser encontrado");
        return null;
    }
    
    @Transactional
	public void removeTag(Long tagIdValue, Long userId) {
		log.info("Removendo tag de id {}", tagIdValue);
		Tag tag = tagDao.findById(tagIdValue);
		
		if (tag == null) {
			log.error("Tag de id {} não encontrada", tagIdValue);
			return;
		}
		
		disassociateFromTasks(tag, userId);
		
		tagDao.delete(tag);
	}

	private void disassociateFromTasks(Tag tag, Long userId) {
		List<Task> tasksWithTag = taskService.getAllTasksOfTag(tag.getTagId());
		
		if (tasksWithTag != null && !tasksWithTag.isEmpty()) {
			
			for (Task task : tasksWithTag) {
				taskService.disassociateTagFromTask(task, tag.getTagId(), userId);
			}
			
		}
	}

	public Integer getPageSize() {
		return tagDao.getPaginationParameterConfig().getMaxResultsPerPage();
	}

}