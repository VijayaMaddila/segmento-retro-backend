package com.retro.dto;

public class RoomResponseDTO {
	 private Long id;
	    private String name;
	    private String joinCode;
	    private String status;
	    private Integer timerDuration;
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
		public String getJoinCode() {
			return joinCode;
		}
		public void setJoinCode(String joinCode) {
			this.joinCode = joinCode;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public Integer getTimerDuration() {
			return timerDuration;
		}
		public void setTimerDuration(Integer timerDuration) {
			this.timerDuration = timerDuration;
		}


}
