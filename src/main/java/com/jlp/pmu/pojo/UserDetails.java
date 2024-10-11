package com.jlp.pmu.pojo;

import java.util.List;
import java.util.Map;

public class UserDetails {
	
	private String clientID;
	
	private String firstName;
	
	private String lastName;
	
	private String emailID;
	
	private String userName;
	
	private String pingSRI;
	

	public String getPingSRI() {
		return pingSRI;
	}

	public void setPingSRI(String pingSRI) {
		this.pingSRI = pingSRI;
	}

	private List<String> adLists;

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmailID() {
		return emailID;
	}

	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<String> getAdLists() {
		return adLists;
	}

	public void setAdLists(List<String> adLists) {
		this.adLists = adLists;
	}

	
	
	
	
	

}
