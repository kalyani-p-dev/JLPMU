package com.jlp.pmu.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jlp.pmu.constant.Constant;
import com.jlp.pmu.dto.PrinterResponse;
import com.jlp.pmu.dto.ReportResponse;
import com.jlp.pmu.dto.TemplateDTO;
import com.jlp.pmu.dto.TemplateDeleteResponse;
import com.jlp.pmu.dto.TemplateRequest;
import com.jlp.pmu.dto.TemplateResponse;
import com.jlp.pmu.dto.TemplateUpdateRequest;
import com.jlp.pmu.dto.TemplateUpdateResponse;
import com.jlp.pmu.models.Template;
import com.jlp.pmu.repository.TemplateRepository;
import com.jlp.pmu.service.TemplateService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.io.Resource;


@RestController
@CrossOrigin("*")
@RequestMapping("/pmu/v1/api/template")
public class TemplateController {
	@Autowired
	TemplateService templateService;
	
	@PostMapping("/upload/{userId}")
    public ResponseEntity uploadTemplate(
    		@PathVariable String userId,
            @RequestParam("description") String description,
            @RequestParam("file") MultipartFile file,HttpServletRequest httpRequest,
			HttpServletResponse httpResponse)
             {
		String errorMessage = (String) httpRequest.getAttribute(Constant.ERROR);
		if(errorMessage != null) {
			int reponseCode = httpResponse.getStatus();
			if(reponseCode==401) {
			 return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
			}else {
				 return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
			}
		}else {
			 try {
		            TemplateResponse responseDTO = templateService.uploadTemplate( description,file, userId );
		            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
		        } catch (Exception e) {
		            e.printStackTrace();
		            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		        }
		}
		
       
    }
	
	@PostMapping("/delete/{tempId}")
    public ResponseEntity softDeleteTemplate(@PathVariable int tempId,HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		String errorMessage = (String) httpRequest.getAttribute(Constant.ERROR);
		if(errorMessage != null) {
			int reponseCode = httpResponse.getStatus();
			if(reponseCode==401) {
			 return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
			}else {
				 return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
			}
		}else {
			TemplateDeleteResponse response = templateService.softDeleteTemplate(tempId);
	        return ResponseEntity.ok(response);
		}
    }
	
	
	@GetMapping("/get-all-template")
    public ResponseEntity getAllTemplates(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		String errorMessage = (String) httpRequest.getAttribute(Constant.ERROR);
		if(errorMessage != null) {
			int reponseCode = httpResponse.getStatus();
			if(reponseCode==401) {
			 return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
			}else {
				 return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
			}
		}else {
			 List<TemplateDTO> templates = templateService.getAllTemplates();
		        
		        if (templates.isEmpty()) {
		            return ResponseEntity.status( HttpStatus.NOT_FOUND)
		            		.body(templates);
		        } else {
		            return ResponseEntity.ok(templates);
		        }
		}
		
		
       
    }
	
	@GetMapping("/download/{tempId}")
	public ResponseEntity downloadTemplate(@PathVariable int tempId,HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		String errorMessage = (String) httpRequest.getAttribute(Constant.ERROR);
		if(errorMessage != null) {
			int reponseCode = httpResponse.getStatus();
			if(reponseCode==401) {
			 return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
			}else {
				 return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
			}
		}else {
			  // Download the template
		    Resource templateFile = templateService.downloadTemplate(tempId);

		    // Get the original filename from the template entity
		    String originalFilename = templateService.getOriginalFilename(tempId); // Implement this method in your service

		    // Set content type and original filename
		    HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.TEXT_HTML);
		    headers.setContentDispositionFormData("attachment", originalFilename);

		    // Return the file as a ResponseEntity
		    return ResponseEntity.ok()
		            .headers(headers)
		            .body(templateFile);
		}	
		
	  
	}
	
	@PutMapping("/update/{tempId}/{userId}")
    public ResponseEntity updateTemplateDescription(@PathVariable int tempId, @RequestBody TemplateUpdateRequest requestDTO,@PathVariable String userId,HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		String errorMessage = (String) httpRequest.getAttribute(Constant.ERROR);
		if(errorMessage != null) {
			int reponseCode = httpResponse.getStatus();
			if(reponseCode==401) {
			 return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
			}else {
				 return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
			}
		}else {
			 TemplateUpdateResponse responseDTO = templateService.updateTemplateDescription(tempId, requestDTO,userId);
		        return ResponseEntity.ok(responseDTO);
		}	
       
    }
	
	}
