package com.jlp.pmu.service.impl;

import java.io.File;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.tools.ant.types.Environment;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jlp.pmu.constant.Constant;
import com.jlp.pmu.dto.TemplateDTO;
import com.jlp.pmu.dto.TemplateDeleteResponse;
//import com.itextpdf.text.pdf.parser.clipper.Paths;
import com.jlp.pmu.dto.TemplateRequest;
import com.jlp.pmu.dto.TemplateResponse;
import com.jlp.pmu.dto.TemplateUpdateRequest;
import com.jlp.pmu.dto.TemplateUpdateResponse;
import com.jlp.pmu.enums.ObjectType;
import com.jlp.pmu.exception.TemplateNotFoundException;
import com.jlp.pmu.exception.UserNotFoundException;
import com.jlp.pmu.models.Activity;
import com.jlp.pmu.models.Template;
import com.jlp.pmu.models.User;
import com.jlp.pmu.repository.ActivityRepository;
import com.jlp.pmu.repository.TemplateRepository;
import com.jlp.pmu.repository.UserRepository;
import com.jlp.pmu.service.TemplateService;
import com.jlp.pmu.utility.Utility;

import lombok.extern.log4j.Log4j2;

@Service@Log4j2
public class TemplateServiceImpl implements  TemplateService{
	
	@Autowired
	TemplateRepository templateRepository;
	
	@Autowired
	ActivityRepository activityRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ModelMapper modelMapper;
	
//	@Autowired
//	private Environment env;
	
//	@Value("${SourceDirectoryOfTemplate}")
//	private String uploadDir; 
	
	@Autowired
	private com.jlp.pmu.utility.PMUUtility PMUUtility;
	public static Properties prop = null;
	Utility utilObj =null;
	
	
	  
	
@Override
public TemplateDeleteResponse softDeleteTemplate(int tempId) {
	Optional<Template> optionalTemplate = templateRepository.findById(tempId);
    if (optionalTemplate.isPresent()) {
        Template template = optionalTemplate.get();
        template.setStatus(false); 
        templateRepository.save(template);
        return new TemplateDeleteResponse(Constant.TEMPLATE_SOFT_DELETED_SUCCESSFULLY);
    } else {
        log.error(Constant.TEMPLATE_NOT_FOUND_WITHID, tempId);
        throw new TemplateNotFoundException(Constant.TEMPLATE_NOT_FOUND,Constant.TEMPLATE_NOT_FOUND_WITH_ID + tempId);
    }
}


@Override
public TemplateResponse uploadTemplate(String description,MultipartFile file,String userId) {
	
	try {
		User user = userRepository.findById(userId)
	            .orElseThrow(() -> new UserNotFoundException(Constant.USER_NOT_FOUND_WITH_ID + userId,Constant.USER_NOT_FOUND));
		
	Template template = new Template();
    template.setTemplateDescription(description);
    LocalDateTime currentDateTime= LocalDateTime.now();
    template.setDateTime(currentDateTime);
    template.setTemplateName(file.getOriginalFilename());
    template.setFileSIze(file.getSize());
    
    template.setLastupdatedtime(LocalDateTime.now());
    template.setStatus(true);
    template.setCreatedBy(user.getFirstName()+ " " + user.getLastName());
    template.setUpdatedBy(user.getFirstName()+ " " + user.getLastName());
    template.setComment(Constant.ADDED_BY + user.getFirstName() + " " + user.getLastName());
    templateRepository.save(template);
    Activity activity = new Activity();

    prop=Utility.readPropertiesFile();
    String uploadDir = prop.getProperty(Constant.SOURCEDIRECTORYOFTEMPLATE);

	activity.setCreatedBy(user.getFirstName() + " " + user.getLastName());
	activity.setUpdatedBy(user.getFirstName() + " " + user.getLastName());
	activity.setLastupdatedtime(LocalDateTime.now());
	activity.setComment(Constant.ADDED_BY + user.getFirstName() + " " + user.getLastName());
	activity.setObjectType(ObjectType.TEMPLATE);
	activity.setObjectID(userId);
	activityRepository.save(activity);
    String filePath = uploadDir + file.getOriginalFilename();
    File destFile = new File(filePath);
    file.transferTo(destFile);
    
    

    // Prepare response DTO
    TemplateResponse responseDTO = new TemplateResponse();
    responseDTO.setTempId(template.getTempId());
    responseDTO.setTemplateName(template.getTemplateName());
    responseDTO.setFileSize(template.getFileSIze());
    responseDTO.setDate(currentDateTime.toLocalDate().toString());
    responseDTO.setTime(currentDateTime.toLocalTime().toString());
    responseDTO.setTemplateDescription(template.getTemplateDescription());

    return responseDTO;
	}
    catch(IOException e) {
    	e.printStackTrace();
    }
    throw new RuntimeException(Constant.FAILED_TO_SAVE_FILE);
}

//Implement method to get user details by userId
private User getUserById(String userId) {
 
 return userRepository.findById(userId).orElseThrow(() -> new RuntimeException(Constant.USER_NOT_FOUND));
}


@Override
public List<TemplateDTO> getAllTemplates() {
	List<Template> allTemplates = templateRepository.findAll();

    // Filter templates based on file existence
    List<TemplateDTO> validTemplates = allTemplates.stream()
            .filter(this::isTemplateFilePresent)
            .map(this::mapToTemplateDTO)
            .collect(Collectors.toList());
	return validTemplates;

}

//private boolean isTemplateFilePresent(Template template) {
	 boolean isTemplateFilePresent(Template template) {
	
    // Logic to check if the template file exists on the server
	 prop=Utility.readPropertiesFile();
	    String uploadDir = prop.getProperty(Constant.SOURCEDIRECTORYOFTEMPLATE);
    String filePath = uploadDir + template.getTemplateName(); 
    File file = new File(filePath);
    return file.exists();
}

private TemplateDTO mapToTemplateDTO(Template template) {
	TemplateDTO dto = new TemplateDTO();
    dto.setTempId(template.getTempId());
    dto.setTemplateName(template.getTemplateName());
    dto.setFileSize(template.getFileSIze());
    dto.setDateTime(template.getDateTime());
    dto.setTemplateDescription(template.getTemplateDescription());
    
    return dto;
}



@Override
public org.springframework.core.io.Resource downloadTemplate(int tempId) {
	// Fetch the template from the repository
    Template template = templateRepository.findById(tempId)
            .orElseThrow(() -> new TemplateNotFoundException(Constant.TEMPLATE_NOT_FOUND_WITH_ID + tempId, Constant.TEMPLATE_NOT_FOUND));

    // Construct the file path
    prop=Utility.readPropertiesFile();
    String uploadDir = prop.getProperty(Constant.SOURCEDIRECTORYOFTEMPLATE);
    Path filePath = Paths.get(uploadDir + template.getTemplateName());
    

    return new FileSystemResource(filePath);
}




@Override
public String getOriginalFilename(int tempId) {
	// Fetch the template from the repository
    Template template = templateRepository.findById(tempId)
            .orElseThrow(() -> new TemplateNotFoundException(Constant.TEMPLATE_NOT_FOUND_WITH_ID + tempId, Constant.TEMPLATE_NOT_FOUND));

    return template.getTemplateName();
}



@Override
public TemplateUpdateResponse updateTemplateDescription(int tempId, TemplateUpdateRequest requestDTO,String userId) {
	Template template = templateRepository.findById(tempId)
            .orElseThrow(() -> new TemplateNotFoundException(Constant.TEMPLATE_NOT_FOUND_WITH_ID + tempId,Constant.TEMPLATE_NOT_FOUND));
	User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(Constant.USER_NOT_FOUND_WITH_ID + userId,Constant.USER_NOT_FOUND));
	
	template.setLastupdatedtime(LocalDateTime.now());
    template.setStatus(true);
    template.setUpdatedBy(user.getFirstName()+ " " + user.getLastName());
    template.setComment(Constant.ADDED_BY + user.getFirstName() + " " + user.getLastName());
	
    template.setTemplateDescription(requestDTO.getNewDescription());
    
    templateRepository.save(template);
    
    Activity activity = new Activity();

	activity.setCreatedBy(user.getFirstName() + " " + user.getLastName());
	activity.setUpdatedBy(user.getFirstName() + " " + user.getLastName());
	activity.setLastupdatedtime(LocalDateTime.now());
	activity.setComment(Constant.ADDED_BY + user.getFirstName() + " " + user.getLastName());
	activity.setObjectType(ObjectType.TEMPLATE);
	activity.setObjectID(userId);
	activityRepository.save(activity);
    

    return mapToTemplateUpdateResponse(template);
}

private TemplateUpdateResponse mapToTemplateUpdateResponse(Template template) {
    TemplateUpdateResponse responseDTO = new TemplateUpdateResponse();
    responseDTO.setTempId(template.getTempId());
    responseDTO.setTemplateName(template.getTemplateName());
    responseDTO.setTemplateDescription(template.getTemplateDescription());
    responseDTO.setFileSize(template.getFileSIze());
    responseDTO.setDateTime(template.getDateTime());
    return responseDTO;
}

}


    
















    

        
		
	


