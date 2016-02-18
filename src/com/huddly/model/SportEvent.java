package com.huddly.model;

import java.io.Serializable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("SportEvent")
public class SportEvent extends ParseObject implements Serializable {

	public SportEvent() {

	}

	public String getLatitude() {
		return getString("latitude");
	}

	public void setLatitude(String latitude) {
		put("latitude", latitude);
	}

	public String getLongitude() {
		return getString("longitude");
	}

	public void setLongitude(String longitude) {
		put("longitude", longitude);
	}

	public String getTitle() {
		return getString("title");
	}

	public void setTitle(String title) {
		put("title", title);
	}

	public String getSport() {
		return getString("sport");
	}

	public void setSport(String sport) {
		put("sport", sport);
	}

	public String getDesc() {
		return getString("desc");
	}

	public void setDesc(String desc) {
		put("desc", desc);
	}

	public String getTime() {
		return getString("time");
	}

	public void setTime(String time) {
		put("time", time);
	}

	public int getZipcode() {
		return getInt("zipcode");
	}

	public void setZipcode(int zipcode) {
		put("time", zipcode);
	}

	public String getDate() {
		return getString("date");
	}

	public void setDate(String date) {
		put("date", date);
	}

	public ParseUser getUser() {
		return getParseUser("user");
	}

	public void setUser(ParseUser value) {
		put("user", value);
	}

	public ParseFile getPhotoFile() {
		return getParseFile("photo");
	}

	public void setPhotoFile(ParseFile file) {
		put("photo", file);
	}

	public ParseGeoPoint getGeoPoint() {
		return getParseGeoPoint("location");
	}

	public void setGeoPoint(ParseGeoPoint point) {
		put("location", point);
	}

	public static ParseQuery<SportEvent> getQuery() {
		return ParseQuery.getQuery(SportEvent.class);
	}

}
