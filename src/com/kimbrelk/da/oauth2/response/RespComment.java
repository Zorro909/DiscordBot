package com.kimbrelk.da.oauth2.response;

import org.json.JSONException;
import org.json.JSONObject;

import com.kimbrelk.da.oauth2.struct.Comment;

public class RespComment extends Response {
	private Comment mResult;
	
	public RespComment(JSONObject json) throws JSONException {
		super();
		mResult = new Comment(json);
	}
	
	public final Comment getComment() {
		return mResult;
	}
}