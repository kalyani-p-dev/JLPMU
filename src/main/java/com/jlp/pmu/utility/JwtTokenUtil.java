package com.jlp.pmu.utility;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlp.pmu.pojo.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable {

	private static final long serialVersionUID = -2550185165626007488L;

	public static final long JWT_TOKEN_VALIDITY = 5*60*60;

	@Value("${jwt.secret}")
	private String secret;
	

	//retrieve username from jwt token
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	//retrieve expiration date from jwt token
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token,null);
		return claimsResolver.apply(claims);
	}
    //for retrieveing any information from token we will need the secret key
	public Claims getAllClaimsFromToken(String token, String code) {
		try {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
		//return 	Jwts.parser().setSigningKey(secret).build().parseClaimsJws(token).getBody();
		}catch(Exception e) {
				System.out.println("Exception in getAllClaim:"+e.getMessage());
				throw e;
		}
		
		
	}

	//check if the token has expired
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	//generate token for user
	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("userdetails", userDetails);
		return doGenerateToken(claims, userDetails.getUserName());
	}

	private String doGenerateToken(Map<String, Object> claims, String subject) {

		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 5))
				.signWith(SignatureAlgorithm.HS384, secret).compact();
	}

	//validate token
	public Boolean validateToken(String token) {
		final String username = getUsernameFromToken(token);
		return (username.equals(username) && !isTokenExpired(token));
	}
	
	public String refreshToken(String jwtToken,Claims testclaims) {
		ObjectMapper mapper = new ObjectMapper();
		UserDetails userdetails = mapper.convertValue(testclaims.get("userdetails"), UserDetails.class);
		return doGenerateToken(testclaims, userdetails.getUserName());
			}
	
	
}
