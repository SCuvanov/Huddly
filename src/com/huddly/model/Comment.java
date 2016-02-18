package com.huddly.model;

import java.io.Serializable;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Comment")
public class Comment extends ParseObject implements Serializable {

	public Comment() {
		// TODO Auto-generated constructor stub
	}

	public String getComment() {
		return getString("comment");
	}

	public void setComment(String comment) {
		put("comment", comment);
	}
	
	public String getUsername() {
		return getString("username");
	}

	public void setUsername(String username) {
		put("username", username);
	}

	public ParseUser getUser() {
		return getParseUser("user");
	}

	public void setUser(ParseUser value) {
		put("user", value);
	}
	

}
