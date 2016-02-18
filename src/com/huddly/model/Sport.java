package com.huddly.model;

public class Sport {

	String sportName;
	int resourceId;

	public Sport(String sportName, int resourceId) {
		// TODO Auto-generated constructor stub
		this.sportName = sportName;
		this.resourceId = resourceId;
	}

	public String getSportName() {
		return sportName;
	}

	public void setSportName(String sportName) {
		this.sportName = sportName;
	}

	public int getResourceId() {
		return resourceId;
	}

	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}

}
