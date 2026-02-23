package com.retro.util;

import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	private final String Secret="mysecretkeymysecretkeymysecretkey";
	
	public  String generatedToken(Long id,String name,String email)
	{
		return Jwts.builder()
				.setSubject(email)
				.claim("id",id)
				.claim("name",name)
				.claim("email",email)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis()+1000*60*60))
				.signWith(Keys.hmacShaKeyFor(Secret.getBytes()))
				.compact();
	}
	public Claims extractAllClaims(String token)
	{
		return Jwts.parserBuilder()
                .setSigningKey(Secret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
	}
	public Long extractId(String token)
	{
		return extractAllClaims(token).get("id",Long.class);
	}
	public String extractName(String token)
	{
		return extractAllClaims(token).get("name",String.class);
	}
	public String extractEmail(String token)
	{
		return extractAllClaims(token).get("email",String.class);
	}
	

}
