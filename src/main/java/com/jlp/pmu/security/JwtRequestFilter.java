package com.jlp.pmu.security;

import java.io.IOException;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jlp.pmu.constant.Constant;
import com.jlp.pmu.utility.JwtTokenUtil;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request,
			jakarta.servlet.http.HttpServletResponse response, jakarta.servlet.FilterChain chain)
			throws jakarta.servlet.ServletException, IOException {
		System.out.println("test 1:" + new Date(System.currentTimeMillis()));
		System.out.println("test 2:" + new Date(System.currentTimeMillis() + 5 * 60 * 60 * 5));
		final String requestTokenHeader = request.getHeader("Authorization");
		System.out.println("requestTokenHeader :"+requestTokenHeader);
		String path = request.getRequestURI();
		System.out.println("path:" + path);
		if (path.contains("swagger-ui") || path.contains("/v3")) {
			chain.doFilter(request, response);
		} else {
			if (path.contains("pmu/authenticate") || path.contains("/pmu/loginprocessor")) {
				chain.doFilter(request, response);
			} else {
				String username = null;
				String jwtToken = null;
				// JWT Token is in the form "Bearer token". Remove Bearer word and get only the
				// Token
				try {
					if (requestTokenHeader != null && requestTokenHeader.startsWith(Constant.BEARER)) {
						jwtToken = requestTokenHeader.substring(7);
						try {
							if (jwtTokenUtil.validateToken(jwtToken)) {
								request.setAttribute(Constant.JWTTOKEN, jwtToken);
								response.setHeader(Constant.JWTTOKEN, jwtToken);

							} else {
								request.setAttribute(Constant.ERROR, "token is not validate");
								response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
								response.setContentType("application/json");
								logger.warn("JWT Token does not begin with Bearer String");
							}
						} catch (ExpiredJwtException e) {
							String isRefreshToken = request.getHeader(Constant.ISREFRESHTOKEN);
							if (isRefreshToken != null && isRefreshToken.equals("true")) {
								request.setAttribute(Constant.JWTTOKEN, jwtToken);
								request.setAttribute(Constant.CLAIMS, e.getClaims());
								chain.doFilter(request, response);
							} else {
								request.setAttribute(Constant.ERROR, "token is expired");
								response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
								response.setContentType("application/json");
								logger.warn("JWT Token does not begin with Bearer String");
							}

						}

					} else {
						request.setAttribute(Constant.ERROR, "token is not valid,please enter valid token");
						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						response.setContentType("application/json");
						logger.warn("JWT Token does not begin with Bearer String");
					}
				} catch (Exception e) {
					request.setAttribute(Constant.ERROR, "exception in validate token");
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					response.setContentType("application/json");
				}
				chain.doFilter(request, response);
			}
		}

	}

}
