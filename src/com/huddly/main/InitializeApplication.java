package com.huddly.main;

import android.app.Application;

import com.huddly.model.Comment;
import com.huddly.model.SportEvent;
import com.huddly.model.SportEventPhoto;
import com.parse.Parse;
import com.parse.ParseObject;

public class InitializeApplication extends Application {
	
	static final String TAG = "HUDDLY";
	
	@Override
	public void onCreate() {
		super.onCreate();

		
		ParseObject.registerSubclass(SportEvent.class);
		ParseObject.registerSubclass(SportEventPhoto.class);
		ParseObject.registerSubclass(Comment.class);
		Parse.initialize(this, "lWqmd3uznR6D3w6XIZuhZFCgZadgACJAy2uO50CN", "sqjLRZai45on9OmgaxEYiQLSpQJBTYxl6xHx24RO");
	}
}
