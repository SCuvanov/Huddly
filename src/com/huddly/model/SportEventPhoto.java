package com.huddly.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("SportEventPhoto")
public class SportEventPhoto extends ParseObject {

	public SportEventPhoto() {
		// TODO Auto-generated constructor stub
	}

	public String getDesc() {
		return getString("description");
	}

	public void setDesc(String desc) {
		put("description", desc);
	}

	public ParseUser getUser() {
		return getParseUser("user");
	}

	public void setUser(ParseUser value) {
		put("user", value);
	}

	public boolean isDraft() {
		return getBoolean("isDraft");
	}

	public void setDraft(boolean isDraft) {
		put("isDraft", isDraft);
	}

	public ParseFile getPhotoFile() {
		return getParseFile("photo");
	}

	public void setPhotoFile(ParseFile file) {
		put("photo", file);
	}

	public static ParseQuery<SportEventPhoto> getQuery() {
		return ParseQuery.getQuery(SportEventPhoto.class);
	}

}
