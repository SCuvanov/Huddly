package com.huddly.model;

public class SportItem {
	private String _title;
	private String _image;
	private String _desc;

	public SportItem() {
	}

	public SportItem(String title, String image, String desc) {

		this._title = title;
		this._image = image;
		this._desc = desc;

	}

	public String get_title() {
		return _title;
	}

	public String get_image() {
		return _image;
	}

	public String get_desc() {
		return _desc;
	}

}
