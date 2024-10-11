package com.jlp.pmu.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.Filter;
import jakarta.servlet.http.Cookie;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain filterChain(HttpSecurity http,ClientRegistrationRepository clientRegRepo) throws Exception {
        System.out.println("test login");
        http.authorizeHttpRequests(authz -> authz
                .requestMatchers(HttpMethod.GET, "/pmu/v1/api/**", "/pmu/loginprocessor","/swagger-ui/**","/pmu/loginResponseUI","/v3/**","/logout").permitAll()
                .requestMatchers(HttpMethod.POST, "/pmu/v1/api/**", "/pmu/loginprocessor","/swagger-ui/**","/pmu/loginResponseUI","/v3/**").permitAll()
                .requestMatchers(HttpMethod.PUT, "/pmu/v1/api/**", "/pmu/loginprocessor","/swagger-ui/**","/pmu/loginResponseUI","/v3/**").permitAll()   
                .requestMatchers("/pmu/authenticate").authenticated()).oauth2Login();
        http.csrf(AbstractHttpConfigurer::disable);
        return http.build();

    }
    private LogoutHandler cookiesClearlogoutHandler() {
    	//String access_token = "kjxncjxcnxkjcnxjcnxjkcnxjkcnxjkcnxjcnxcn";
    	//logoutAPI(access_token);
    	 return (request, response, authentication) -> {
             // Clear cookies
             Cookie[] cookies = request.getCookies();
             if (cookies != null) {
                 for (Cookie cookie : cookies) {
                     cookie.setMaxAge(0);
                     cookie.setValue(null);
                     cookie.setPath("/");
                     response.addCookie(cookie);
                 }
             }
         };
    }
    
    private LogoutSuccessHandler oidcLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
        OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
            new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("https://jlp-logon-dev.johnlewis.co.uk/idp/init_logout.openid");
        return oidcLogoutSuccessHandler;
    }

    
    public void logoutAPI(String code) {
		//prop = PMUUtility.getAllProperties();
    	try {
		String accessToken = "";
		String tokenURL ="https://jlp-logon-dev.johnlewis.co.uk/as/revoke_token.oauth2";
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<String, String>();
		formData.add("token", code);
		formData.add("token_type_hint", "access_token");
		String auth = "PMU"+":"+"5eDCyf3nVieniYrqRSnZOnVLMnSBZUPUMMrrs550Yn2vTkTEibnUc9jo3PyBQH8b";
		byte[] encodeAuth = java.util.Base64.getEncoder().encode(auth.getBytes());
		String authHeader = "Basic "+new String(encodeAuth);
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", authHeader);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> requestEntity =  new HttpEntity<>(formData,headers);
		RestTemplate restTemplate = new RestTemplate();
		 ResponseEntity<String> responseEnity = restTemplate.exchange(tokenURL, HttpMethod.POST,requestEntity,String.class);
		 HttpStatusCode statusCode = responseEnity.getStatusCode();
		System.out.println("statusCode :"+statusCode);
    	}catch (Exception e) {
    		System.out.println("Exception occured logoutAPI:"+e.getMessage());
			// TODO: handle exception
		}
	}
}