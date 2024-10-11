package com.jlp.pmu.service.impl;

import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties.Function;
import org.springframework.stereotype.Service;

import com.jlp.pmu.models.User;
import com.jlp.pmu.pojo.UserDetails;
import com.jlp.pmu.service.JWTService;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTServiceImpl implements JWTService {

	@Value("${jwt.secret}")
	private String secret;

	public UserDetails generateUserFromToken(String token) {
		UserDetails userDetails = new UserDetails();
		try {
			Map<String, Object> userDetailsMap = getUserDetailsFromToken(token);
			userDetails = getUserDetailsFromClaimMap(userDetailsMap);
		} catch (Exception e) {
			System.out.println("Exception in getUserDetailsFromToken :" + e);
		}
		return userDetails;
	}

	public Map<String, Object> getUserDetailsFromToken(String token) throws Exception {
		JWT jwt = JWTParser.parse(token);
		Map<String, Object> claims = jwt.getJWTClaimsSet().getClaims();
		return claims;

	}

	public UserDetails getUserDetailsFromClaimMap(Map<String, Object> payLoadMap) {
		UserDetails userDetails = new UserDetails();
		for (String key : payLoadMap.keySet()) {
			System.out.printf("%s: %s\n", key, payLoadMap.get(key));
		}
		userDetails.setClientID(payLoadMap.get("client_id")!= null?payLoadMap.get("client_id").toString():"");
		userDetails.setFirstName(payLoadMap.get("First Name")!= null?payLoadMap.get("First Name").toString():"");
		userDetails.setLastName(payLoadMap.get("Last Name")!= null?payLoadMap.get("Last Name").toString():"");
		userDetails.setUserName(payLoadMap.get("Username")!= null?payLoadMap.get("Username").toString():"");
		userDetails.setEmailID(payLoadMap.get("Email")!= null?payLoadMap.get("Email").toString():"");
		userDetails.setPingSRI(payLoadMap.get("pi.sri")!= null?payLoadMap.get("pi.sri").toString():"");
		Object obj = payLoadMap.get("memberOf");
		ArrayList<String> adGroupList = new ArrayList<String>();
		if(obj != null) {
			ArrayList<String> memberList = (ArrayList) payLoadMap.get("memberOf");
			
			System.out.println("memberList :" + memberList);
			for (String member : memberList) {
				System.out.println(member+":::::");
				String[] myArray = member.split(",");
		//		ArrayList<String> tempList = (ArrayList<String>) Arrays.asList(myArray);
				List<String> tempList = Arrays.asList(myArray);
				for (String temp : tempList) {
					if(temp.contains("PMU")) {
						String adGroup = temp.substring(temp.indexOf("=DEV_PMU_")+9).toUpperCase();
						adGroupList.add(adGroup);
						
					}
				}
				/*
				 * if(member.contains("CN=PMU")) { //String adroup =
				 * member.substring(member.indexOf("CN=PMU"), 0) }
				 */
			}
			userDetails.setAdLists(adGroupList);
		}
		
		return userDetails;

	}

	public String generateJWTToken(UserDetails userDetails) {
		String jwtToken = Jwts.builder().setSubject(userDetails.getUserName())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() * 1000 * 60 * 24))
				.signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
		return jwtToken;
	}

	private Key getSignKey() {
		byte[] key = Decoders.BASE64.decode(secret);
		return Keys.hmacShaKeyFor(key);
	}

	public Boolean validateToken(String token, String username) {
		final String usernameInToken = extractUsername(token);
		return (usernameInToken.equals(username) && !isTokenExpired(token));
	}

	public String extractUsername(String token) {
		return extractClaims(token, Claims::getSubject);
	}

	private Date extractExpiration(String token) {
		return extractClaims(token, Claims::getExpiration);
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private <T> T extractClaims(String jwtToken, java.util.function.Function<Claims, T> claimsResolvers) {
		final Claims claims = extractAllClaims(jwtToken);
		return claimsResolvers.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
	}

}
