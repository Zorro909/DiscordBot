package com.kimbrelk.da.oauth2.response;

import org.json.JSONException;
import org.json.JSONObject;

import com.kimbrelk.da.oauth2.struct.User;

public class RespUser extends Response {
	private User mUser;
	
	public RespUser(JSONObject json) throws JSONException {
		mUser = new User(json);
	}
	
	public final User getUser() {
		return mUser;
	}
}