package com.retro.dto;

import java.util.List;

public class TeamDTO {
    private Long id;
    private String name;
    private Long createdBy; 
    private List<Long> members;
    
    public TeamDTO(Long id, String name, Long createdBy, List<Long> members) {
		super();
		this.id = id;
		this.name = name;
		this.createdBy = createdBy;
		this.members = members;
	}
    
    
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Long getCreatedBy() {
		return createdBy;
	}


	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}


	public List<Long> getMembers() {
		return members;
	}


	public void setMembers(List<Long> members) {
		this.members = members;
	}


	
    

   }