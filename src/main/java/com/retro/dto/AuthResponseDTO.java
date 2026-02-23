package com.retro.dto;

public class AuthResponseDTO {
	
	private String token;
	
	public AuthResponseDTO(String token)
	{
		this.setToken(token);
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
