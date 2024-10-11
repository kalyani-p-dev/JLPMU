package com.jlp.pmu.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jlp.pmu.constant.Constant;
import com.jlp.pmu.dto.LoginResponse;
import com.jlp.pmu.service.LoginService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@RestController
@CrossOrigin("*")
@RequestMapping("/pmu")
@Log4j2
public class LoginController {

	@Autowired
	LoginService loginService;

	@GetMapping("/loginResponseUI")
	public ResponseEntity<LoginResponse> loginWithUI(HttpServletRequest payload, HttpServletResponse response) {
		LoginResponse loginResponse = new LoginResponse();
		String code = payload.getParameter("code");
		if (code != null && !code.isEmpty()) {
			loginResponse = loginService.LoginProcessRedirect(code);
		} else {
			loginResponse = new LoginResponse();
			loginResponse.setErroMessage("code is not valid,please enter the valid code");
			return new ResponseEntity<LoginResponse>(loginResponse, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<LoginResponse>(loginResponse, HttpStatus.OK);
	}

	@GetMapping("/loginprocessor")
	public void getLoginPage(HttpServletRequest payload, HttpServletResponse response) {
		System.out.println("getLoginPage");
		LoginResponse loginResponse = new LoginResponse();
		try {
			String code = payload.getParameter("code");
			if (code != null && !code.isEmpty()) {
				System.out.println("code :" + code);
				// loginResponse = loginService.LoginProcessRedirect(code);
				String URL = "http://jlp-printmgmt-dev.acceptance.co.uk:3000/?code=" + code;
				response.addHeader(code, code);
				response.sendRedirect(URL);
			} else {
				response.sendRedirect("/pmu/authenticate");
			}
		} catch (Exception e) {
			loginResponse.setErroMessage("error occured in login process:" + e.getMessage());
			// return new ResponseEntity<LoginResponse>(loginResponse,
			// HttpStatus.BAD_REQUEST);
		}
		// return new ResponseEntity<LoginResponse>(loginResponse, HttpStatus.OK);
	}

	@GetMapping("/v1/api/logout")
	public ResponseEntity logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//
		// Authentication authentication =
		// SecurityContextHolder.getContext().getAuthentication();
		// new SecurityContextLogoutHandler().logout(request, response, authentication);
		String errorMessage = (String) request.getAttribute(Constant.ERROR);
		if (errorMessage != null) {
			return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
		} else {
			String jwtToken = request.getAttribute(Constant.JWTTOKEN).toString();
			loginService.callSessionRevoke(jwtToken);
			return new ResponseEntity<String>("success", HttpStatus.OK);
		}
	}

	@GetMapping("/v1/api/refreshJWTTokens")
	public String refreshJWTTokens(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("IN refreshJWTTokens");
		String refreshToken = null;
		String jwtToken = request.getAttribute(Constant.JWTTOKEN).toString();
		Claims testclaims = (Claims) request.getAttribute("claims");
		refreshToken = loginService.refreshJWTTokens(jwtToken, testclaims);
		return refreshToken;
	}

}
