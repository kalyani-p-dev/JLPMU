package com.jlp.pmu.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.jlp.pmu.dto.TemplateDTO;
import com.jlp.pmu.dto.TemplateDeleteResponse;
import com.jlp.pmu.dto.TemplateRequest;
import com.jlp.pmu.dto.TemplateResponse;
import com.jlp.pmu.dto.TemplateUpdateRequest;
import com.jlp.pmu.dto.TemplateUpdateResponse;

import jakarta.annotation.Resource;

public interface TemplateService {

	TemplateDeleteResponse softDeleteTemplate(int tempId);

	TemplateResponse uploadTemplate(String description, MultipartFile file,String userId)throws IOException;

	List<TemplateDTO> getAllTemplates();

	org.springframework.core.io.Resource downloadTemplate(int tempId);
	String getOriginalFilename(int tempId);

	TemplateUpdateResponse updateTemplateDescription(int tempId, TemplateUpdateRequest requestDTO,String userId);
}


